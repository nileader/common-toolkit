#!/usr/bin/env bash
# git-sync.sh v3 —— submodule 指针一致性同步
#
# 核心原理（gitlink 偏移的两场景）：
#   Scenario X（远端已对齐，守纪律）：推进 submodule 的人同步更新了主仓库 gitlink。
#     → pull 主仓库 + submodule update 即全齐，不该 bump。
#   Scenario Y（远端漂移）：submodule 远端前进了但主仓库 gitlink 没跟上。
#     → 需 bump 主仓库 gitlink 到 submodule 远端实际值。
#   分类以"主仓库 gitlink(rec) vs submodule 远端 tip(rem)"为准（Step2 对齐后 rec==远端 gitlink）。
#   只在 Scenario Y 才 bump。
#
# 流程：
#   Step0 fetch（timeout+进度）
#   Step1 冲突预检：1a 内容冲突(merge-tree) + 1b 工作区脏文件∩远端要改文件
#   Step2 对齐主仓库到 origin/main（落后→ff-merge；分叉→rebase）
#   Step3 逐 submodule 四态分类：
#     [对齐]                 wd==gitlink==远端tip → 跳过
#     [待更新submodule]      gitlink==远端tip 但工作区落后 → submodule update（③校验）
#     [待更新主仓库gitlink]  gitlink≠远端tip（Scenario Y）→ ff 工作区+①校验+git add bump
#     [本地改动]             submodule 有未提交/未推送 → 递归 sync_repo 提交+push 再 bump
#   Step4 主仓库提交（bump + 本地变更）+ pull --rebase 整合
#   Step5 ② push 前独立复查 gitlink 目标可达  Step6 push（仅有未推送时）
#
# 三道保险：①bump 前目标 SHA 须在远端可达 ②push 前独立复查 ③submodule update 后 wd==gitlink
#
# 用法： git-sync.sh [--dry-run]

set -euo pipefail

DRY_RUN=0
[ "${1:-}" = "--dry-run" ] && DRY_RUN=1

# timeout 命令：Linux 用 timeout，macOS 用 gtimeout（需 brew install coreutils）；都没有则无超时
TIMEOUT_BIN=""
command -v timeout  >/dev/null 2>&1 && TIMEOUT_BIN=timeout
command -v gtimeout >/dev/null 2>&1 && [ -z "$TIMEOUT_BIN" ] && TIMEOUT_BIN=gtimeout

DIRS=(
  "$HOME"
)

# ── 颜色 / 输出 ──────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; CYAN='\033[0;36m'; NC='\033[0m'
info()   { printf "${BLUE}[INFO]${NC}  %s\n" "$*"; }
ok()     { printf "${GREEN}[OK]${NC}    %s\n" "$*"; }
warn()   { printf "${YELLOW}[WARN]${NC}  %s\n" "$*"; }
act()    { printf "${CYAN}[ACT]${NC}   %s\n" "$*"; }
header() { printf "\n${CYAN}══════════════════════════════════════════${NC}\n"; printf "${CYAN}  %s${NC}\n" "$*"; printf "${CYAN}══════════════════════════════════════════${NC}\n"; }

has_git() { [ -d "$1/.git" ] || { git -C "$1" rev-parse --git-dir &>/dev/null; }; }

die_or_skip() {
  local msg="$1" dir="${2:-}"
  [ -n "$dir" ] && msg="$msg  ($dir)"
  warn "$msg"
  while true; do
    read -rp "  [s]kip / [a]bort: " answer </dev/tty
    case "$answer" in
      s|S|skip|SKIP) return 0 ;;
      a|A|abort|ABORT) exit 1 ;;
    esac
  done
}

# settings.json 锁（BSD chflags 生效，Linux no-op）
lock_supported() { command -v chflags >/dev/null 2>&1; }
unlock_settings() { lock_supported && chflags nouchg "$HOME/.claude/settings.json" 2>/dev/null || true; }
lock_settings()   { lock_supported && chflags uchg   "$HOME/.claude/settings.json" 2>/dev/null || true; }

# ── submodule / SHA 辅助 ───────────────────────────────────
sub_paths() {
  git -C "$1" config --file "$1/.gitmodules" --get-regexp 'path$' 2>/dev/null | awk '{print $NF}' || true
}
has_upstream() { git -C "$1" rev-parse --abbrev-ref "@{upstream}" &>/dev/null; }
unpushed_first() {
  has_upstream "$1" && git -C "$1" log --oneline @{upstream}..HEAD 2>/dev/null | head -1 || true
}
sha_recorded() { git -C "$1" ls-tree HEAD -- "$2" 2>/dev/null | awk '{print $3}'; }
sha_workdir() { git -C "$1" rev-parse HEAD 2>/dev/null || true; }
sha_remote() {
  git -C "$1" rev-parse --verify -q origin/HEAD 2>/dev/null \
    || git -C "$1" rev-parse --verify -q origin/main 2>/dev/null \
    || git -C "$1" rev-parse --verify -q origin/master 2>/dev/null \
    || true
}
is_anc() { git -C "$1" merge-base --is-ancestor "$2" "$3" 2>/dev/null; }   # <dir> A B → A 是 B 的祖先?
remote_reachable() {
  local dir="$1" sha="$2" r
  r=$(sha_remote "$dir"); [ -z "$r" ] && return 1
  git -C "$dir" merge-base --is-ancestor "$sha" "$r" 2>/dev/null
}
is_dirty() { [ -n "$(git -C "$1" status --porcelain 2>/dev/null)" ]; }

# git merge-tree --write-tree 是否受支持（git>=2.38）；结果全局缓存，按需探测一次
_merge_tree_supported=""
merge_tree_ok() {
  [ "$_merge_tree_supported" = yes ] && return 0
  if [ -z "$_merge_tree_supported" ]; then
    if git -C "$1" merge-tree --write-tree HEAD HEAD >/dev/null 2>&1; then
      _merge_tree_supported=yes
    else
      _merge_tree_supported=no
    fi
  fi
  [ "$_merge_tree_supported" = yes ]
}

# ── Step1a 内容冲突预检（无侵入）──
# 优先 merge-tree --write-tree（git>=2.38，内容级）；旧 git 回退到文件级重叠检测
conflict_content() {
  local dir="$1" r out
  r=$(sha_remote "$dir"); [ -z "$r" ] && return 0
  if is_anc "$dir" "$r" HEAD || is_anc "$dir" HEAD "$r"; then return 0; fi   # 快进方向必无冲突
  if merge_tree_ok "$dir"; then
    out=$(git -C "$dir" merge-tree --write-tree --name-only HEAD "$r" 2>/dev/null | tail -n +2 || true)
    echo "$out"
    return
  fi
  # 回退：双方都改过的文件（文件级，非内容级，能挡住同文件双改的常见冲突）
  local rc lc
  rc=$(git -C "$dir" diff --name-only HEAD "$r" 2>/dev/null)
  lc=$(git -C "$dir" diff --name-only HEAD 2>/dev/null)
  if [ -z "$rc" ] || [ -z "$lc" ]; then return 0; fi
  comm -12 <(printf '%s\n' "$rc" | sort -u) <(printf '%s\n' "$lc" | sort -u) 2>/dev/null || true
}

# ── Step1b 工作区脏文件 ∩ 远端要改的文件（排除 submodule 路径）──
# 非空 → pull 会覆盖本地未提交改动，应中止让用户手动合并
wt_overlap() {
  local dir="$1" r rc lc subs rc_clean lc_clean
  r=$(sha_remote "$dir"); [ -z "$r" ] && return 0
  rc=$(git -C "$dir" diff --name-only HEAD "$r" 2>/dev/null)
  [ -z "$rc" ] && return 0
  lc=$(git -C "$dir" diff --name-only HEAD 2>/dev/null)
  [ -z "$lc" ] && return 0
  subs=$(sub_paths "$dir")
  if [ -n "$subs" ]; then
    rc_clean=$(printf '%s\n' "$rc" | grep -vxF -f <(printf '%s\n' "$subs") 2>/dev/null || true)
    lc_clean=$(printf '%s\n' "$lc" | grep -vxF -f <(printf '%s\n' "$subs") 2>/dev/null || true)
  else
    rc_clean="$rc"; lc_clean="$lc"
  fi
  if [ -z "$rc_clean" ] || [ -z "$lc_clean" ]; then return 0; fi
  comm -12 <(printf '%s\n' "$rc_clean") <(printf '%s\n' "$lc_clean") 2>/dev/null || true
}

# 带 timeout 的 fetch（进度输出到终端，避免静默卡死）；返回 fetch 退出码
_fetch_one() {
  local d="$1" t="${2:-60}"
  if [ -n "$TIMEOUT_BIN" ]; then
    "$TIMEOUT_BIN" "$t" git -C "$d" fetch --all
  else
    git -C "$d" fetch --all
  fi
}

# ── 主同步 ─────────────────────────────────────────────────
# sync_repo <dir> <is_home>
sync_repo() {
  local dir="$1" is_home="${2:-false}"
  local name; name=$(basename "$dir")
  local remote_url branch
  remote_url=$(git -C "$dir" remote get-url origin 2>/dev/null || echo "?")
  branch=$(git -C "$dir" rev-parse --abbrev-ref HEAD 2>/dev/null || echo "?")
  header "$name ($remote_url | $branch) — $dir"

  $is_home && unlock_settings
  _lock_back() { $is_home && lock_settings || true; }
  trap _lock_back EXIT RETURN

  # ── Step 0: fetch（只读，绝不动工作区）──
  local ft="${FETCH_TIMEOUT:-60}"
  local fetch_failed=""
  info "Step0 fetch 主仓库（timeout ${ft}s）..."
  _fetch_one "$dir" "${ft}" || die_or_skip "主仓库 fetch 失败" "$dir"
  local sub
  while IFS= read -r sub; do
    [ -z "$sub" ] && continue
    local sd="$dir/$sub"
    if [ ! -d "$sd" ] || ! has_git "$sd"; then continue; fi
    info "  fetch submodule: $sub"
    local rc=0
    _fetch_one "$sd" "${ft}" || rc=$?
    if [ "$rc" != 0 ]; then
      if [ "$rc" = 124 ]; then warn "  $sub fetch 超时(>${ft}s)，本轮跳过"
      else warn "  $sub fetch 失败(rc=$rc)，本轮跳过"; fi
      fetch_failed+="$sub"$'\n'
    fi
  done <<< "$(sub_paths "$dir")"

  # ── Step 1: 冲突预检（内容冲突 + 工作区脏文件重叠）──
  info "Step1 冲突预检..."
  local cf wo
  cf=$(conflict_content "$dir")
  if [ -n "$cf" ]; then
    warn "主仓库本地与远端内容冲突，需手动合并："
    echo "$cf" | sed 's/^/    /'
    return 1
  fi
  while IFS= read -r sub; do
    [ -z "$sub" ] && continue
    local sd="$dir/$sub"; [ -d "$sd" ] || continue
    cf=$(conflict_content "$sd")
    if [ -n "$cf" ]; then
      warn "submodule $sub 本地与远端内容冲突，需手动合并："
      echo "$cf" | sed 's/^/    /'
      return 1
    fi
  done <<< "$(sub_paths "$dir")"
  wo=$(wt_overlap "$dir")
  if [ -n "$wo" ]; then
    warn "以下文件本地和远端都改了，pull 会覆盖本地改动，请先手动合并："
    echo "$wo" | sed 's/^/    /'
    return 1
  fi
  ok "Step1 无冲突"

  # ── Step 2: 对齐主仓库到 origin/main（拿最新 gitlink 再分类）──
  #   落后 → ff-merge；分叉 → rebase（Step1 已保证无内容冲突）；领先/相等 → 不动
  # 即使工作区/暂存区"脏"（典型是子模块指针超前——那正是落后远端的症状，不是真实本地
  # 改动），也要尝试 ff；被挡时撤暂存后重试。用 git merge --ff-only 而非 git pull，
  # 绕开 pull.rebase=true 把 --ff-only 带偏成 rebase（rebase 要求全干净，被 jsonl 等挡死）。
  local r
  r=$(sha_remote "$dir")
  if [ -n "$r" ] && [ -n "$(git -C "$dir" rev-parse HEAD 2>/dev/null)" ]; then
    if [ "$(git -C "$dir" rev-parse HEAD)" != "$r" ] && is_anc "$dir" HEAD "$r"; then
      if [ "$DRY_RUN" = 0 ]; then
        if git -C "$dir" merge --ff-only "$r" 2>/dev/null; then
          act "Step2 ff-merge 成功，主记录追上 ${r:0:8}"
        else
          act "Step2 ff 被本地暂存/改动阻挡，撤暂存后重试"
          git -C "$dir" reset --mixed -q 2>/dev/null || true
          if git -C "$dir" merge --ff-only "$r" 2>/dev/null; then
            ok "Step2 撤暂存后 ff-merge 成功，主记录追上 ${r:0:8}"
          else
            warn "Step2 ff-merge 仍失败（真实本地改动或分叉），留给 Step4"
          fi
        fi
      else
        info "(dry-run) Step2 将 ff-merge 主仓库到 ${r:0:8}"
      fi
    elif ! is_anc "$dir" "$r" HEAD; then
      act "Step2 主仓库与远端分叉 → rebase"
      [ "$DRY_RUN" = 0 ] && { git -C "$dir" rebase "$r" 2>/dev/null || { warn "rebase 失败"; return 1; }; }
    fi
  fi

  # ── Step 3: 逐 submodule 四态分类 ──
  info "Step3 分类 submodule..."
  while IFS= read -r sub; do
    [ -z "$sub" ] && continue
    if [ -n "$fetch_failed" ] && printf '%s\n' "$fetch_failed" | grep -qxF "$sub"; then
      warn "  → 跳过 $sub（本轮 fetch 失败，下次再试）"
      continue
    fi
    local sd="$dir/$sub"
    if [ ! -d "$sd" ] || ! has_git "$sd"; then
      warn "submodule 未初始化: $sub"
      if [ "$DRY_RUN" = 0 ]; then
        read -rp "  是否初始化 $sub? (y/n): " ans </dev/tty
        if [ "$ans" = "y" ] || [ "$ans" = "Y" ]; then
          git -C "$dir" submodule update --init "$sub" 2>/dev/null && ok "已初始化 $sub" || { warn "初始化失败: $sub，跳过"; continue; }
        else
          warn "跳过 $sub"; continue
        fi
      else
        info "(dry-run) 未初始化: $sub（真实运行会询问是否 init）"
        continue
      fi
    fi
    local rec wd rem sub_up=""
    rec=$(sha_recorded "$dir" "$sub")    # Step2 后本地 HEAD==origin/main，rec 即远端 gitlink
    wd=$(sha_workdir "$sd")
    rem=$(sha_remote "$sd")
    sub_up=$(unpushed_first "$sd")
    printf "  • %-30s gitlink=%s wd=%s rem=%s\n" "$sub" "${rec:0:8}" "${wd:0:8}" "${rem:0:8}"

    # [本地改动]
    if is_dirty "$sd" || [ -n "$sub_up" ]; then
      act "  → [本地改动] 递归提交 $sub（提交+push 后回写 gitlink）"
      if [ "$DRY_RUN" = 0 ]; then
        sync_repo "$sd" false || { warn "submodule $sub 同步失败，跳过 bump"; continue; }
        git -C "$dir" add "$sub"
      fi
      continue
    fi

    # [分叉]：工作区与 gitlink 互非祖先
    if [ -n "$rec" ] && [ -n "$wd" ] \
       && ! is_anc "$sd" "$rec" "$wd" && ! is_anc "$sd" "$wd" "$rec"; then
      warn "  → [分叉] $sub 工作区与 gitlink 互非祖先，跳过（手动处理）"
      continue
    fi

    if [ "$rec" = "$rem" ]; then
      # Scenario X：远端已对齐
      if [ "$wd" = "$rec" ]; then
        ok "  → [对齐] $sub"
      else
        act "  → [待更新submodule] $sub → submodule update"
        if [ "$DRY_RUN" = 0 ]; then
          git -C "$dir" submodule update --init "$sub" 2>/dev/null || warn "update $sub 失败"
          local wd2; wd2=$(sha_workdir "$sd")
          [ "$wd2" = "$rec" ] && ok "  ③ 校准成功" || warn "  ③ 校准未达 $rec，请检查"
        fi
      fi
      continue
    fi

    # Scenario Y：远端漂移（gitlink != rem）→ bump 到 rem
    if [ -n "$wd" ] && [ "$wd" != "$rem" ] && is_anc "$sd" "$wd" "$rem"; then
      act "  → ff $sub 工作区到远端 ${rem:0:8}"
      if [ "$DRY_RUN" = 0 ]; then
        git -C "$sd" merge --ff-only "$rem" 2>/dev/null || warn "  ff 失败"
        wd=$(sha_workdir "$sd")
      else
        wd="$rem"
      fi
    fi
    if ! remote_reachable "$sd" "$wd"; then
      warn "  ① $sub 目标 ${wd:0:8} 不在远端，跳过 bump（先 push 该 submodule 后重跑）"
      continue
    fi
    act "  → [待更新主仓库gitlink] bump $sub → ${wd:0:8}"
    [ "$DRY_RUN" = 0 ] && git -C "$dir" add "$sub"
  done <<< "$(sub_paths "$dir")"

  # ── Step 4: 主仓库提交（gitlink bump + 本地变更）+ 整合远端 ──
  local up_top
  up_top=$(unpushed_first "$dir")

  # 三分类收集：已暂存(staged, 索引相对HEAD) / 未暂存(unstaged, 工作区相对索引) / 未跟踪(untracked)
  # git status --porcelain 行格式: "XY path"（重命名/复制为 "XY new\told"）
  # 一个文件可能同时出现在"已暂存"和"未暂存"（部分暂存），与 git status 行为一致
  local staged="" unstaged="" untracked=""
  local s_cnt=0 u_cnt=0 t_cnt=0 line x y rest path lbl
  while IFS= read -r line; do
    [ -z "$line" ] && continue
    x="${line:0:1}"; y="${line:1:1}"; rest="${line:3}"
    path="${rest%%$'\t'*}"   # 重命名/复制只取新路径
    if [ "$x" = "?" ] && [ "$y" = "?" ]; then
      untracked+="${path}"$'\n'; t_cnt=$((t_cnt+1)); continue
    fi
    # 已暂存：X 非 space（已 add 到索引）
    if [ "$x" != " " ]; then
      case "$x" in A) lbl="新增";; D) lbl="删除";; R) lbl="重命名";; C) lbl="复制";; *) lbl="修改";; esac
      staged+="${lbl}"$'\t'"${path}"$'\n'; s_cnt=$((s_cnt+1))
    fi
    # 未暂存：Y 非 space（工作区相对索引还有改动）
    if [ "$y" != " " ]; then
      case "$y" in D) lbl="删除";; A) lbl="新增";; R) lbl="重命名";; C) lbl="复制";; *) lbl="修改";; esac
      unstaged+="${lbl}"$'\t'"${path}"$'\n'; u_cnt=$((u_cnt+1))
    fi
  done < <(git -C "$dir" status --porcelain 2>/dev/null)
  local total=$((s_cnt + u_cnt + t_cnt))

  echo ""
  printf "${YELLOW}📂 %s${NC}\n" "$name"
  if [ "$total" -gt 0 ]; then
    if [ "$s_cnt" -gt 0 ]; then
      printf "${GREEN}  ▸ 已暂存待提交（%s）:${NC}\n" "$s_cnt"
      printf "%s" "$staged" | while IFS=$'\t' read -r lbl pth; do
        [ -z "${pth:-}" ] && continue
        printf "      ${GREEN}[%s]${NC} %s\n" "$lbl" "$pth"
      done
    fi
    if [ "$u_cnt" -gt 0 ]; then
      printf "${YELLOW}  ▸ 未暂存变更（%s）:${NC}\n" "$u_cnt"
      printf "%s" "$unstaged" | while IFS=$'\t' read -r lbl pth; do
        [ -z "${pth:-}" ] && continue
        c="$YELLOW"
        case "$lbl" in 删除) c="$RED";; 重命名|复制) c="$BLUE";; 新增) c="$GREEN";; esac
        printf "      ${c}[%s]${NC} %s\n" "$lbl" "$pth"
      done
    fi
    if [ "$t_cnt" -gt 0 ]; then
      printf "${CYAN}  ▸ 未跟踪（%s）:${NC}\n" "$t_cnt"
      printf "%s" "$untracked" | while IFS= read -r pth; do
        [ -z "${pth:-}" ] && continue
        printf "      ${CYAN}%s${NC}\n" "$pth"
      done
    fi
    if [ "$DRY_RUN" = 0 ]; then
      read -rp $'\n'"是否提交以上变更? (y/n): " answer </dev/tty
      case "$answer" in
        y|Y)
          git -C "$dir" add -A || die_or_skip "git add failed" "$dir"
          local msg
          read -rp "commit message: " msg </dev/tty
          [ -z "$msg" ] && { msg="chore: sync $name (bump submodule pointers)"; info "使用默认: $msg"; }
          git -C "$dir" commit -m "$msg" || die_or_skip "git commit failed" "$dir"
          ok "Committed: $msg"
          if has_upstream "$dir"; then
            info "pull --rebase 整合远端..."
            git -C "$dir" pull --rebase 2>/dev/null || warn "pull --rebase 失败，请手动处理"
            git -C "$dir" submodule update --init --recursive 2>/dev/null || true
          fi
          ;;
        *) info "跳过提交（gitlink 已暂存于 index，未提交）";;
      esac
    else
      info "(dry-run) 不提交"
    fi
  elif [ -n "$up_top" ]; then
    info "$name 无新改动，但有未推送提交 → $up_top"
  else
    ok "$name 无需提交"
  fi

  # ── Step 5 / 6: ② push 前独立复查 + push（仅有未推送时）──
  if [ "$DRY_RUN" = 0 ] && has_upstream "$dir"; then
    local to_push; to_push=$(unpushed_first "$dir")
    if [ -z "$to_push" ]; then
      ok "$name 无未推送提交，跳过 push"
    else
      info "Step5 push 前校验 gitlink 目标可达..."
      local bad=0
      while IFS= read -r sub; do
        [ -z "$sub" ] && continue
        local sd="$dir/$sub"; [ -d "$sd" ] || continue
        local new rec_remote rem_sha
        new=$(sha_workdir "$sd")
        # 用已解析的远端 SHA(sha_remote 已做 origin/HEAD→main→master 回退)，不直接用
        # origin/HEAD(常未设置，会 exit 128 触发 set -e 中止整个脚本)
        rem_sha=$(sha_remote "$dir")
        rec_remote=""
        if [ -n "$rem_sha" ]; then
          rec_remote=$(git -C "$dir" ls-tree "$rem_sha" -- "$sub" 2>/dev/null | awk '{print $3}' || true)
        fi
        [ -z "$rec_remote" ] && rec_remote=$(sha_recorded "$dir" "$sub")
        if [ "$new" != "$rec_remote" ]; then
          if ! remote_reachable "$sd" "$new"; then
            warn "② $sub 指针 ${new:0:8} 未在远端可达 → 拒绝 push"
            bad=1
          fi
        fi
      done <<< "$(sub_paths "$dir")"
      [ "$bad" = 1 ] && { warn "请先 push 对应 submodule 后重跑"; return 1; }
      ok "Step5 gitlink 目标均可达"
      info "$name 有未推送提交 → $to_push"
      read -rp "是否 push $name 到远程? (y/n): " p </dev/tty
      case "$p" in
        y|Y) git -C "$dir" push 2>/dev/null || die_or_skip "push failed" "$dir"; ok "pushed $name" ;;
        *) info "已跳过 push（记得稍后 push）";;
      esac
    fi
  fi

  $is_home && lock_settings
}

# ── 主循环 ───────────────────────────────────────────────────
for dir in "${DIRS[@]}"; do
  [ -d "$dir" ] || { die_or_skip "目录不存在" "$dir"; continue; }
  has_git "$dir" || { die_or_skip "不是 git 仓库" "$dir"; continue; }
  is_home=false; [ "$dir" = "$HOME" ] && is_home=true
  sync_repo "$dir" "$is_home"
  echo ""
done
ok "Done!"
