#!/usr/bin/env bash
# =============================================================================
# migrate-claude-context.sh
# -----------------------------------------------------------------------------
# 用途: 当 claude 项目目录(即执行 claude 命令时的 cwd)发生迁移时, 把该路径下
#       的 claude 上下文(session 轨迹)从旧路径适配到新路径, 使在 <新路径> 下执行
#       `claude -r` 时能枚举并恢复历史 session。
#
# 事实依据(本机实证):
#   - claude 把每个 cwd 的 session 存于 ~/.claude/projects/<cwd编码>/
#   - cwd 编码规则: 绝对路径中所有非字母数字字符(A-Za-z0-9)均替换为 '-'
#       例: /Users/yinshi.nc/workspaces/default/tmp -> -Users-yinshi-nc-workspaces-default-tmp
#       例: /home/admin/workspace/default/tmp      -> -home-admin-workspace-default-tmp
#       ( / . 空格 _ 等悉数变 '-', 非仅 '/'; 实测 /tmp/cc.test a_b-d -> -tmp-cc-test-a-b-d)
#   - session 文件形如 <uuid>.jsonl, 内部 "cwd" 字段记录该 session 的 cwd
#   - claude -r (无参) 按"当前 cwd 编码"枚举对应目录下的 session
#
# 本脚本只做两件事:
#   1. cp -a 复制 ~/.claude/projects/<旧编码> -> ~/.claude/projects/<新编码>
#      (复制而非移动, 旧上下文保留不删, 天然满足"不删除旧")
#   2. 仅精确替换新目录所有 .jsonl 中 "cwd":"<旧路径>" -> "cwd":"<新路径>"
#      只动结构化字段, 消息正文里的旧路径(历史对话内容)一律保留不篡改
#
# 不迁移的(与 cwd 无关, 按 sessionId/pid/路径hash 分桶, 迁移路径不影响):
#   todos/ session-env/ plans/ tasks/ sessions/ shell-snapshots/ file-history/
#   history.jsonl / telemetry / paste-cache/ ...
# =============================================================================
set -euo pipefail

CLAUDE_HOME="${CLAUDE_HOME:-$HOME/.claude}"
PROJECTS_DIR="$CLAUDE_HOME/projects"

usage() {
  cat <<EOF
用法: $(basename "$0") [-n] <旧项目目录> <新项目目录>
  -n          dry-run, 仅做校验, 不实际复制/替换(不写任何东西)
  -h, --help  帮助

参数:
  <旧项目目录>  迁移前你执行 claude 的 cwd(绝对/相对均可)
  <新项目目录>  迁移后你将执行 claude 的 cwd(绝对/相对均可)

行为:
  - 仅迁移 ~/.claude/projects/<cwd编码>/ 下的 session 轨迹
  - 复制方式迁移, 旧上下文保留不删
  - 仅精确替换 jsonl 内 "cwd" 字段值, 对话正文中的旧路径保留不动
  - 任何校验失败立即中断, 不执行写操作, 并打印错误现场
  - 新 projects 目录若已存在 -> 拒绝覆盖, 中断(绝不自动覆盖)
EOF
}

DRY_RUN=0
while [[ $# -gt 0 ]]; do
  case "$1" in
    -n) DRY_RUN=1; shift ;;
    -h|--help) usage; exit 0 ;;
    --) shift; break ;;
    -*) echo "未知选项: $1" >&2; usage; exit 2 ;;
    *) break ;;
  esac
done

[[ $# -eq 2 ]] || { usage; exit 2; }

OLD_RAW="$1"
NEW_RAW="$2"

# ---- 辅助 ----
log() { printf '\033[1;37m[%s]\033[0m %s\n' "$(date '+%H:%M:%S')" "$*"; }
err() { printf '\033[1;31m[ERROR]\033[0m %s\n' "$*" >&2; }
ok()  { printf '\033[1;32m[OK]\033[0m %s\n' "$*"; }
pad()  { sed 's/^/    /'; }

# 归一化为绝对路径(目录不存在也能解析, 不做 realpath 真存在性校验)
# 注: 若你从 symlink 路径跑 claude, 而 claude 记录的是 resolve 后真路径, 编码可能不一致;
#     校验 E/步骤3 会在事后兜住。如遇此情况, 传参时改用真路径。
norm() {
  python3 -c 'import os,sys; print(os.path.abspath(sys.argv[1]))' "$1"
}

# cwd 编码(实测 claude 行为): 绝对路径中所有非字母数字字符(A-Za-z0-9)均替换为 '-'
#   实测: /tmp/cc.test a_b-d -> -tmp-cc-test-a-b-d  (. 空格 _ 全部变 -)
#   旧实现只 tr '/' '-' -> 含 . 等字符的路径(如用户名 yinshi.nc)编码错位, 找不到 projects 目录
#   用 python re.sub 按 unicode 字符级处理, 与 claude 的 JS 编码口径对齐(对 unicode 路径更稳)
encode() { python3 -c 'import re,sys;print(re.sub(r"[^A-Za-z0-9]","-",sys.argv[1]))' "$1"; }

OLD_PATH="$(norm "$OLD_RAW")"
NEW_PATH="$(norm "$NEW_RAW")"
OLD_ENC="$(encode "$OLD_PATH")"
NEW_ENC="$(encode "$NEW_PATH")"
OLD_PROJ_DIR="$PROJECTS_DIR/$OLD_ENC"
NEW_PROJ_DIR="$PROJECTS_DIR/$NEW_ENC"

# ---- 参数解析报告 ----
log "参数解析:"
printf '  旧项目目录   : %s\n' "$OLD_PATH"
printf '  新项目目录   : %s\n' "$NEW_PATH"
printf '  旧 cwd 编码  : %s\n' "$OLD_ENC"
printf '  新 cwd 编码  : %s\n' "$NEW_ENC"
printf '  旧 projects  : %s\n' "$OLD_PROJ_DIR"
printf '  新 projects  : %s\n' "$NEW_PROJ_DIR"
echo

# =============================================================================
# 校验阶段: 任一失败 -> 打印现场 + 中断, 不写任何东西
# =============================================================================

# 校验 A: 旧 != 新
log "校验[1/6] 旧路径 != 新路径"
if [[ "$OLD_PATH" == "$NEW_PATH" ]]; then
  err "旧路径与新路径相同: $OLD_PATH"
  exit 2
fi
ok "通过"

# 校验 B: 路径不含换行(后续 grep/python 行级处理安全)
log "校验[2/6] 路径字符安全性"
for p in "$OLD_PATH" "$NEW_PATH"; do
  if [[ "$p" == *$'\n'* ]]; then err "路径含换行: $p"; exit 2; fi
done
ok "通过"

# 校验 C: 旧 projects 目录存在且是目录
log "校验[3/6] 旧 projects 目录是否存在(存量上下文)"
if [[ ! -d "$OLD_PROJ_DIR" ]]; then
  err "旧 projects 目录不存在: $OLD_PROJ_DIR"
  err "  -> 该路径下没有存量 claude 上下文(未在此目录跑过 claude, 或已被清理)"
  err "  -> 编码推导: 旧路径 $OLD_PATH -> 编码 $OLD_ENC"
  err "  -> 现有 projects 目录如下(供比对):"
  ls -1 "$PROJECTS_DIR" 2>/dev/null | pad >&2 || true
  err "  -> 中断, 不执行迁移"
  exit 3
fi
ok "存在"

# 校验 D: 至少含 1 个 .jsonl (符合迁移规范)
log "校验[4/6] 旧目录是否含存量 session(.jsonl)"
shopt -s nullglob
jsonl_files=( "$OLD_PROJ_DIR"/*.jsonl )
shopt -u nullglob
if [[ ${#jsonl_files[@]} -eq 0 ]]; then
  err "旧 projects 目录下没有任何 .jsonl 文件: $OLD_PROJ_DIR"
  err "  -> 不符合迁移条件(无 session 轨迹), 目录内容:"
  ls -la "$OLD_PROJ_DIR" | pad >&2
  exit 4
fi
ok "发现 ${#jsonl_files[@]} 个 jsonl"

# 校验 E: 每个 jsonl 至少有 1 个 cwd 字段 == 旧路径 (佐证编码/路径匹配, 防误传参数)
log "校验[5/6] jsonl 内 cwd 字段是否匹配旧路径(防误迁移)"
# 路径可能含正则元字符(如 . ), 用 re.escape 转义后再喂给 grep -E,
# 与步骤2替换保持同一转义口径, 避免校验与实际替换行为不一致。
old_re=$(python3 -c 'import re,sys;print(re.escape(sys.argv[1]))' "$OLD_PATH")
declare -a bad_files=()
for f in "${jsonl_files[@]}"; do
  if ! grep -qE "\"cwd\"[[:space:]]*:[[:space:]]*\"$old_re\"" "$f"; then
    bad_files+=( "$(basename "$f"): 0 处匹配" )
  fi
done
if [[ ${#bad_files[@]} -gt 0 ]]; then
  err "以下 jsonl 没有任何 \"cwd\" 字段等于旧路径, 编码/路径可能不匹配:"
  for b in "${bad_files[@]}"; do err "  - $b"; done
  err "  -> 该目录可能并不属于你给的旧 cwd(参数误传), 中断以防误迁移"
  err "  -> 请核对: 你给的旧路径='$OLD_PATH' 编码='$OLD_ENC'"
  exit 5
fi
ok "全部 jsonl 的 cwd 字段与旧路径一致"

# 校验 F: 新 projects 目录必须不存在(绝不覆盖)
log "校验[6/6] 新 projects 目录是否已存在(冲突检查)"
if [[ -e "$NEW_PROJ_DIR" ]]; then
  err "新 projects 目录已存在: $NEW_PROJ_DIR"
  err "  -> 拒绝覆盖, 避免破坏已有上下文"
  err "  -> 目录内容:"
  ls -la "$NEW_PROJ_DIR" | pad >&2
  err "  -> 如需合并请手动处理后重跑; 脚本绝不自动覆盖"
  exit 6
fi
ok "不存在, 可安全创建"
echo

# =============================================================================
# dry-run 出口
# =============================================================================
if [[ "$DRY_RUN" -eq 1 ]]; then
  log "DRY-RUN: 以上全部校验通过, 未执行任何写操作。去掉 -n 即可实际迁移。"
  exit 0
fi

# =============================================================================
# 迁移阶段
# =============================================================================

log "步骤 1/3 复制: cp -a 旧目录 -> 新目录"
cp -a "$OLD_PROJ_DIR" "$NEW_PROJ_DIR"
ok "复制完成: $OLD_PROJ_DIR -> $NEW_PROJ_DIR"

log "步骤 2/3 替换新目录所有 .jsonl 的 cwd 字段值(仅 \"cwd\":\"<旧>\" -> \"cwd\":\"<新>\", 正文不动)"
python3 - "$NEW_PROJ_DIR" "$OLD_PATH" "$NEW_PATH" <<'PY'
import os, re, sys, tempfile
root, old, new = sys.argv[1], sys.argv[2], sys.argv[3]
# 只匹配结构化字段 "cwd":"<旧路径>" (允许冒号前后空白), 不动消息正文里裸出现的旧路径
pat = re.compile(r'"cwd"\s*:\s*"' + re.escape(old) + r'"')
repl = '"cwd":"' + new + '"'
total = 0
rows = []
for dirpath, dirs, files in os.walk(root):
    for fn in sorted(files):
        if not fn.endswith('.jsonl'):
            continue
        p = os.path.join(dirpath, fn)
        with open(p, 'r', encoding='utf-8') as f:
            data = f.read()
        data2, n = pat.subn(repl, data)
        if n:
            # 原子写入: 先写临时文件再 os.replace, 避免中断导致 jsonl 被截断损坏
            d = os.path.dirname(p)
            fd, tmp = tempfile.mkstemp(dir=d, suffix='.tmp')
            try:
                with os.fdopen(fd, 'w', encoding='utf-8') as f:
                    f.write(data2)
                os.replace(tmp, p)
            except Exception:
                try:
                    os.unlink(tmp)
                except OSError:
                    pass
                raise
        rows.append((os.path.relpath(p, root), n))
        total += n
for rel, n in sorted(rows):
    print(f"    {rel}: 替换 {n} 处")
print(f"  合计替换 {total} 处 cwd 字段")
PY

log "步骤 3/3 验证: 新目录 jsonl 的 cwd 字段是否全部等于新路径"
shopt -s nullglob
new_files=( "$NEW_PROJ_DIR"/*.jsonl )
shopt -u nullglob
new_re=$(python3 -c 'import re,sys;print(re.escape(sys.argv[1]))' "$NEW_PATH")
verify_fail=0
for f in "${new_files[@]}"; do
  if ! grep -qE "\"cwd\"[[:space:]]*:[[:space:]]*\"$new_re\"" "$f"; then
    err "验证失败: $(basename "$f") 未找到新路径的 cwd 字段"
    verify_fail=1
  fi
done
if [[ $verify_fail -ne 0 ]]; then
  err "验证未通过, 请人工检查: $NEW_PROJ_DIR"
  exit 7
fi
ok "验证通过: 新目录 jsonl 的 cwd 已全部指向新路径"

# =============================================================================
# 迁移报告
# =============================================================================
echo
log "====================== 迁移报告 ======================"
printf '  旧项目目录    : %s\n' "$OLD_PATH"
printf '  新项目目录    : %s\n' "$NEW_PATH"
printf '  旧 projects   : %s  (保留未删)\n' "$OLD_PROJ_DIR"
printf '  新 projects   : %s\n' "$NEW_PROJ_DIR"
printf '  迁移 jsonl 数 : %d\n' "${#new_files[@]}"
echo "  下一步:"
echo "    cd \"$NEW_PATH\" && claude -r"
echo "    (按当前 cwd 编码枚举, 应能看到迁移来的 session)"
echo "  说明:"
echo "    - 旧目录上下文已保留, 可随时回退或手动删除"
echo "    - jsonl 正文中的旧路径(历史对话内容)未改动, 不影响 -r 恢复"
echo "    - 其它目录(todos/sessions/...)按 sessionId/pid 分桶, 与 cwd 无关, 无需迁移"
echo "======================================================"
ok "迁移完成 (旧上下文保留于 $OLD_PROJ_DIR)"
