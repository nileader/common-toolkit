#!/usr/bin/env bash
# cc-statusline-tdai-check.sh
# 一键自检 TDAI 记忆体系 —— 5 层。
#
# 使用：
#   cc-statusline-tdai-check.sh              # 默认：5 层完整输出（人类查看）
#   cc-statusline-tdai-check.sh -v            # 详细（含 e2e body 前 400B、每篇场景名）
#   cc-statusline-tdai-check.sh --oneline     # 单行输出，跳过 Layer 3 e2e；供 statusline 集成
#
# 退出码：任一 Layer FAIL → 1；仅 warn → 0。

set -u

VERBOSE=0
ONELINE=0
for arg in "$@"; do
  case "$arg" in
    -v)         VERBOSE=1 ;;
    --oneline)  ONELINE=1 ;;
    -h|--help)  sed -n '2,10p' "$0"; exit 0 ;;
    *)          echo "unknown arg: $arg (see --help)"; exit 1 ;;
  esac
done

REPO="$HOME/workspace/install/TencentDB-Agent-Memory"
HANDLER="$REPO/MemoryProxy/src/anthropicHandler.ts"
LAB="$HOME/.tdai-lab"
DATA="$LAB/data"

RED=$'\033[31m'; GREEN=$'\033[1;36m'; YELLOW=$'\033[1;33m'; RESET=$'\033[0m'
# 备注：GREEN 变量名沿用，实际取 \033[1;36m = 加粗青色（bright cyan），
# 与 statusline.sh 里 git 仓库名 (C_NAME) 同色，避免多绿冲撞。
# YELLOW 同步升级为加粗，与 git 分支名 (C_BRANCH) 一致。
PASS=0; FAIL=0; WARN=0

# ─── 状态收集（供 --oneline 与详细模式共用）────────
core_state="?"; proxy_state="?"; mock_state="?"       # ok/warn/bad
core_pid=""; proxy_pid=""; mock_pid=""
patch_ok=0; patch_total=3
patch_missing=""
e2e_state="skip"; e2e_code=""; e2e_dur=""            # ok/bad/warn/skip
L0_records=0; L0_conversations=0
L0_records_present=0; L0_conversations_present=0
L2_scenes=0; L3_personas=0

_hdr() { [ "$ONELINE" = 1 ] || echo "$@"; }
_nl()  { [ "$ONELINE" = 1 ] || echo ""; }
ok()   {
  PASS=$((PASS+1))
  [ "$ONELINE" = 1 ] || printf "  ${GREEN}[✓]${RESET} %s\n" "$*"
}
bad()  {
  FAIL=$((FAIL+1))
  [ "$ONELINE" = 1 ] || printf "  ${RED}[✗]${RESET} %s\n" "$*"
}
warn() {
  WARN=$((WARN+1))
  [ "$ONELINE" = 1 ] || printf "  ${YELLOW}[!]${RESET} %s\n" "$*"
}

# ─── Layer 0: CC 环境变量指向 ───────────────────
# 严格比对当前 shell 是否指向记忆代理。判据：两个 env 都精确匹配。
#   ANTHROPIC_BASE_URL   == http://127.0.0.1:8096/claude-code/default
#   ANTHROPIC_AUTH_TOKEN == ~/.tdai-lab/cc-authtoken.sk-mem 文件内容
# 注意：主 CC 常态即 off（走 ducky），只有 claude-mem 会话内才 on。
# 因此 off 不计 FAIL、不染 TDAI 标签、不改退出码，仅作独立指示灯。
EXPECT_URL="http://127.0.0.1:8096/claude-code/default"
EXPECT_TOKEN=""
[ -s "$LAB/cc-authtoken.sk-mem" ] && EXPECT_TOKEN=$(cat "$LAB/cc-authtoken.sk-mem")
env_state="off"
if [ -n "$EXPECT_TOKEN" ] \
   && [ "${ANTHROPIC_BASE_URL:-}" = "$EXPECT_URL" ] \
   && [ "${ANTHROPIC_AUTH_TOKEN:-}" = "$EXPECT_TOKEN" ]; then
  env_state="on"
fi
if [ "$ONELINE" = 0 ]; then
  _hdr "── Layer 0: CC 环境变量指向 ────────────────"
  if [ "$env_state" = "on" ]; then
    printf "  ${GREEN}[✓]${RESET} 当前 shell 指向记忆代理（ANTHROPIC_BASE_URL / AUTH_TOKEN 严格匹配）\n"
  else
    printf "  ${YELLOW}[!]${RESET} 当前 shell 未指向记忆代理（主 CC 常态即如此；claude-mem 会话内应为 on）\n"
  fi
  _nl
fi

# ─── Layer 1: 进程 & 端口 ───────────────────────
_hdr "── Layer 1: 进程 & 端口 ────────────────────"
check_port() {
  local name=$1 port=$2 pidfile=$3
  local pid=""
  [ -s "$pidfile" ] && pid=$(cat "$pidfile") || pid=""
  local alive="no"
  if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then alive="yes"; fi
  local http
  http=$(curl -sS -o /dev/null -w "%{http_code}" --max-time 2 "http://127.0.0.1:$port/" 2>/dev/null || echo 000)
  local st
  if [ "$alive" = "yes" ] && [ "$http" != "000" ]; then
    ok "$name pid=$pid :$port http=$http"
    st="ok"
  elif [ "$alive" = "yes" ]; then
    warn "$name pid=$pid alive 但 :$port 不响应 (http=$http)"
    st="warn"
  else
    bad "$name 未运行（pidfile=$pidfile, pid=${pid:-N/A}）"
    st="bad"
  fi
  case "$name" in
    core)  core_state="$st";  core_pid="$pid"  ;;
    proxy) proxy_state="$st"; proxy_pid="$pid" ;;
    mock)  mock_state="$st";  mock_pid="$pid"  ;;
  esac
}
check_port core  8420 "$LAB/core.pid"
check_port proxy 8096 "$LAB/proxy.pid"
check_port mock  8199 "$LAB/mock.pid"

# ─── Layer 2: 补丁 marker ───────────────────────
_nl
_hdr "── Layer 2: 补丁 marker ────────────────────"
if [ ! -f "$HANDLER" ]; then
  bad "anthropicHandler.ts 不存在: $HANDLER"
  patch_missing="handler-not-found"
else
  if grep -q 'headers\["authorization"\] = `Bearer ${effectiveApiKey}`' "$HANDLER"; then
    ok "patch-6.1 Bearer auth"; patch_ok=$((patch_ok+1))
  else
    bad "patch-6.1 Bearer 缺失（跑 sync-from-fork.sh 或 apply-proxy-patches.sh）"
    patch_missing="${patch_missing:+$patch_missing,}6.1"
  fi
  if grep -q 'patch-6.2' "$HANDLER"; then
    ok "patch-6.2 strip output_config"; patch_ok=$((patch_ok+1))
  else
    bad "patch-6.2 缺失"
    patch_missing="${patch_missing:+$patch_missing,}6.2"
  fi
  if grep -q 'patch-6.3' "$HANDLER"; then
    ok "patch-6.3 no-zstd"; patch_ok=$((patch_ok+1))
  else
    bad "patch-6.3 缺失"
    patch_missing="${patch_missing:+$patch_missing,}6.3"
  fi
fi

# ─── Layer 3: 端到端 (proxy → core → ducky) ─────
# --oneline 模式跳过此层（e2e curl 常耗 1-2s，不适合 statusline 高频探针）
if [ "$ONELINE" = 0 ]; then
  _nl
  _hdr "── Layer 3: 端到端调用 ────────────────────"
  SK=""
  [ -s "$LAB/cc-authtoken.sk-mem" ] && SK=$(cat "$LAB/cc-authtoken.sk-mem")
  if [ -z "$SK" ]; then
    bad "cc-authtoken.sk-mem 为空，跳过 e2e"
    e2e_state="bad"
  else
    BODY=/tmp/tdai-selfcheck-body
    resp=$(curl -sS -o "$BODY" -w "%{http_code} %{time_total}" \
      --max-time 30 \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $SK" \
      -H "x-conversation-id: self-check-$(date +%s)" \
      -X POST "http://127.0.0.1:8096/claude-code/default/v1/messages" \
      -d '{
        "model": "claude-opus-4-6",
        "max_tokens": 16,
        "messages": [{"role":"user","content":"reply with the single word ok"}]
      }' 2>&1) || resp="000 timeout"
    e2e_code=$(echo "$resp" | awk '{print $1}')
    e2e_dur=$(echo "$resp"  | awk '{print $2}')
    case "$e2e_code" in
      200)     ok "e2e /v1/messages → 200 (${e2e_dur}s)";      e2e_state="ok"  ;;
      401|403) bad "e2e 鉴权失败 http=$e2e_code";               e2e_state="bad" ;;
      000)     bad "e2e 无响应：proxy 是否在跑？";              e2e_state="bad" ;;
      *)       warn "e2e http=$e2e_code (${e2e_dur}s)";        e2e_state="warn" ;;
    esac
    if [ "$VERBOSE" = 1 ] && [ -s "$BODY" ]; then
      echo "    --- body head (400B) ---"
      head -c 400 "$BODY"; echo
    fi
  fi
fi

# ─── Layer 4: L0 当日记录 ───────────────────────
_nl
_hdr "── Layer 4: L0 当日对话落盘 ────────────────"
today=$(date +%F)
for kind in records conversations; do
  f="$DATA/$kind/$today.jsonl"
  if [ -s "$f" ]; then
    lines=$(wc -l < "$f")
    ok "$kind/$today.jsonl 存在, $lines 行"
    case "$kind" in
      records)       L0_records="$lines";       L0_records_present=1 ;;
      conversations) L0_conversations="$lines"; L0_conversations_present=1 ;;
    esac
  else
    warn "$kind/$today.jsonl 不存在或空（今天可能还没通过 claude-mem 聊过）"
  fi
done

# ─── Layer 5: L2 profiles 场景 ──────────────────
_nl
_hdr "── Layer 5: L2 profiles 场景 ────────────────"
if [ ! -d "$DATA/profiles" ]; then
  bad "$DATA/profiles 不存在"
else
  L2_scenes=$(find "$DATA/profiles" -name '*.md' -path '*/scene_blocks/*' 2>/dev/null | wc -l)
  L3_personas=$(find "$DATA/profiles" -name persona.md 2>/dev/null | wc -l)
  if [ "$L2_scenes" -gt 0 ]; then
    ok "L2 场景 $L2_scenes 篇, L3 persona $L3_personas 份"
    if [ "$VERBOSE" = 1 ]; then
      find "$DATA/profiles" -name '*.md' -path '*/scene_blocks/*' | sed 's|.*/scene_blocks/||; s|^|      |'
    fi
  else
    warn "L2 场景为 0（还没沉淀过 claude-mem 会话）"
  fi
fi

# ─── 输出总结 ──────────────────────────────────
if [ "$ONELINE" = 1 ]; then
  # 单行格式（Q2=C 详细版）：
  # TDAI [环境参数 on✓] [core✓ proxy✓ mock✓] [补丁 3/3] [今日 L0 137行 L2 7篇 L3 2份]
  # 颜色策略：整段 TDAI 标签根据严重度着色（FAIL→红、WARN→黄、全好→绿）
  # 环境参数段独立着色（on→绿✓ / off→红✗），不参与 TDAI 标签严重度判定。
  if [ "$env_state" = "on" ]; then
    envseg="${GREEN}环境参数 on✓${RESET}"
  else
    envseg="${RED}环境参数 off✗${RESET}"
  fi
  case "$core_state"  in ok) c1="${GREEN}core✓${RESET}"  ;; warn) c1="${YELLOW}core!${RESET}"  ;; *) c1="${RED}core✗${RESET}"  ;; esac
  case "$proxy_state" in ok) c2="${GREEN}proxy✓${RESET}" ;; warn) c2="${YELLOW}proxy!${RESET}" ;; *) c2="${RED}proxy✗${RESET}" ;; esac
  case "$mock_state"  in ok) c3="${GREEN}mock✓${RESET}"  ;; warn) c3="${YELLOW}mock!${RESET}"  ;; *) c3="${RED}mock✗${RESET}"  ;; esac

  if [ "$patch_ok" = "$patch_total" ]; then
    pseg="${GREEN}补丁 ${patch_ok}/${patch_total}${RESET}"
  else
    pseg="${RED}补丁 ${patch_ok}/${patch_total}${patch_missing:+ 缺${patch_missing}}${RESET}"
  fi

  # L0 段：records 是原子存盘计数、conversations 是消息计数；短标签取 conversations（更贴近"今天聊了多少"）
  if [ "$L0_conversations_present" = 1 ] || [ "$L0_records_present" = 1 ]; then
    l0seg="${GREEN}L0 ${L0_conversations}行${RESET}"
  else
    l0seg="${YELLOW}L0 -${RESET}"
  fi
  if [ "$L2_scenes" -gt 0 ]; then
    l2seg="${GREEN}L2 ${L2_scenes}篇${RESET}"
  else
    l2seg="${YELLOW}L2 0${RESET}"
  fi
  if [ "$L3_personas" -gt 0 ]; then
    l3seg="${GREEN}L3 ${L3_personas}份${RESET}"
  else
    l3seg="${YELLOW}L3 0${RESET}"
  fi

  # TDAI 头标签颜色
  if [ "$FAIL" -gt 0 ]; then
    tag="${RED}TDAI${RESET}"
  elif [ "$WARN" -gt 0 ]; then
    tag="${YELLOW}TDAI${RESET}"
  else
    tag="${GREEN}TDAI${RESET}"
  fi

  printf "%s [%s] [%s %s %s] [%s] [今日 %s %s %s]\n" \
    "$tag" "$envseg" "$c1" "$c2" "$c3" "$pseg" "$l0seg" "$l2seg" "$l3seg"
  exit $(( FAIL > 0 ? 1 : 0 ))
fi

# 详细模式尾部
_nl
echo "══════════════════════════════════════════════"
printf "总计: %s%d passed%s / %s%d warned%s / %s%d failed%s\n" \
  "$GREEN" "$PASS" "$RESET" "$YELLOW" "$WARN" "$RESET" "$RED" "$FAIL" "$RESET"
exit $(( FAIL > 0 ? 1 : 0 ))
