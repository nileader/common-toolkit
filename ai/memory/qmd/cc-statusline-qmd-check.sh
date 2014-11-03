#!/usr/bin/env bash
# cc-statusline-qmd-check.sh
# 单行输出 qmd 索引栈状态，供 Claude Code statusline 集成（紧跟 TDAI 那行之后）。
#
# 格式：qmd [守护✓] [索引 1104篇 5566向量] [更新 5h前] [孤儿 0]
#
# 性能策略：
#   - 优先读 ~/.cache/qmd/.statusline-cache（守护进程每轮 cleanup 后写入，<1ms）
#   - 缓存缺失/过期(>1h) → fallback 跑 `qmd status`（~0.3s，statusline 可接受）
#   - qmd 未装 / 无索引 / 守护挂 → 仍输出（红标），绝不空行（便于发现故障）
#
# 退出码：永远 0（statusline 不能因探针失败而中断）

set -u

QMD_DIR="$HOME/workspace/common-toolkit/ai/memory/qmd"
PID_FILE="$QMD_DIR/qmd-auto.pid"
LOG="$QMD_DIR/qmd-auto.log"
CACHE="$HOME/.cache/qmd/.statusline-cache"
STALE_SEC=3600   # 缓存超过 1h 视为陈旧

RED=$'\033[31m'; GREEN=$'\033[1;36m'; YELLOW=$'\033[1;33m'; RESET=$'\033[0m'

now=$(date +%s)

# ─── 守护进程存活 ─────────────────────────────
daemon_state="bad"; daemon_pid=""
if [ -s "$PID_FILE" ]; then
  daemon_pid=$(cat "$PID_FILE" 2>/dev/null)
  if [ -n "$daemon_pid" ] && kill -0 "$daemon_pid" 2>/dev/null; then
    daemon_state="ok"
  fi
fi
case "$daemon_state" in
  ok)  dseg="${GREEN}守护✓${RESET}" ;;
  *)   dseg="${RED}守护✗${RESET}" ;;
esac

# ─── 索引规模 / 孤儿数 / 最近更新 ─────────────
# 优先走缓存；缓存陈旧或缺失 → fallback qmd status
files=""; vectors=""; orphan=""; updated_ago=""

read_cache() {
  [ -s "$CACHE" ] || return 1
  local ts
  ts=$(stat -c %Y "$CACHE" 2>/dev/null || echo 0)
  [ $((now - ts)) -gt "$STALE_SEC" ] && return 1   # 过期
  # 格式：files|vectors|orphan|updated_ts
  local line; line=$(cat "$CACHE" 2>/dev/null)
  files=$(echo "$line" | cut -d'|' -f1)
  vectors=$(echo "$line" | cut -d'|' -f2)
  orphan=$(echo "$line" | cut -d'|' -f3)
  local upd_ts; upd_ts=$(echo "$line" | cut -d'|' -f4)
  [ -z "$files" ] && return 1
  updated_ago=$(human_ago "$upd_ts")
  return 0
}

human_ago() {
  local t=$1
  [ -z "$t" ] || [ "$t" = "0" ] && { echo "?"; return; }
  local d=$((now - t))
  [ "$d" -lt 0 ] && d=0
  if   [ "$d" -lt 60 ];   then echo "${d}s前"
  elif [ "$d" -lt 3600 ]; then echo "$((d/60))m前"
  else                        echo "$((d/3600))h前"
  fi
}

fallback_status() {
  # 跑 qmd status，解析输出；~0.3s
  local out
  out=$(timeout 2 qmd status 2>/dev/null) || return 1
  files=$(echo "$out"   | grep -iE 'Total:.*files' | grep -oE '[0-9]+ files' | grep -oE '[0-9]+' | head -1)
  vectors=$(echo "$out" | grep -iE 'Vectors:' | grep -oE '[0-9]+' | head -1)
  orphan=$(echo "$out"  | grep -iE 'Orphaned:' | grep -oE '[0-9]+' | head -1)
  [ -z "$orphan" ] && orphan="0"
  # 更新时间：从 qmd-auto.log 最后一轮 done 时间戳取；解析不了就显示 ?
  updated_ago=$(log_last_done_ago)
}

log_last_done_ago() {
  [ -s "$LOG" ] || { echo "?"; return; }
  # 取最后一行 "=== done ===" 的完整时间戳行（形如 [2026-09-22 18:30:34] === done ===）
  local last
  last=$(grep '=== done ===' "$LOG" 2>/dev/null | tail -1)
  [ -z "$last" ] && { echo "?"; return; }
  local ts; ts=$(echo "$last" | grep -oE '\[[0-9-]+ [0-9:]+\]' | tr -d '[]')
  [ -z "$ts" ] && { echo "?"; return; }
  local t; t=$(date -d "$ts" +%s 2>/dev/null || echo 0)
  [ "$t" = "0" ] && { echo "?"; return; }
  human_ago "$t"
}

if ! read_cache; then
  fallback_status
fi

# 降级：实在拿不到规模 → 显示 ?
[ -z "$files" ]   && files="?"
[ -z "$vectors" ] && vectors="?"
[ -z "$orphan" ]  && orphan="?"
[ -z "$updated_ago" ] && updated_ago="?"

# ─── 段着色 ──────────────────────────────────
# 索引段：规模为 0 或 ? → 黄
if [ "$files" = "?" ] || [ "$files" = "0" ]; then
  iseg="${YELLOW}索引 ${files}篇 ${vectors}向量${RESET}"
else
  iseg="${GREEN}索引 ${files}篇 ${vectors}向量${RESET}"
fi

# 更新段：超过 2h → 黄（守护可能卡住）
upd_warn=0
case "$updated_ago" in
  *h前)
    hrs=$(echo "$updated_ago" | grep -oE '[0-9]+')
    [ -n "$hrs" ] && [ "$hrs" -ge 2 ] && upd_warn=1
    ;;
esac
if [ "$updated_ago" = "?" ] || [ "$upd_warn" = 1 ]; then
  useg="${YELLOW}更新 ${updated_ago}${RESET}"
else
  useg="${GREEN}更新 ${updated_ago}${RESET}"
fi

# 孤儿段：>0 → 黄（应被 cleanup 清零，非零说明守护没在跑 cleanup）
if [ "$orphan" = "?" ] || [ "$orphan" = "0" ]; then
  oseg="${GREEN}孤儿 ${orphan}${RESET}"
else
  oseg="${YELLOW}孤儿 ${orphan}${RESET}"
fi

printf "qmd [%s] [%s] [%s] [%s]\n" "$dseg" "$iseg" "$useg" "$oseg"
exit 0
