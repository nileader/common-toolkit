#!/usr/bin/env bash
# 启动 qmd 索引自动增量更新守护进程
# 每 INTERVAL 秒跑一次 `qmd update && qmd embed`；nohup 后台化，写 pid 文件
# 与 TDAI 栈完全独立，不共享 pid/log/lock
#
# 用法：
#   ~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-start.sh
#   QMD_INTERVAL=3600 ~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-start.sh

set -u

QMD_DIR="$HOME/workspace/common-toolkit/ai/memory/qmd"
PID_FILE="$QMD_DIR/qmd-auto.pid"
LOG="$QMD_DIR/qmd-auto.log"
LOCK="$QMD_DIR/qmd-auto.lock"
INTERVAL="${QMD_INTERVAL:-1800}"     # 默认 30 分钟

# 已在跑 → 幂等退出
if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "already running: pid $(cat "$PID_FILE")"
  echo "  log: $LOG"
  exit 0
fi
rm -f "$PID_FILE"

# 后台 loop（用 setsid 脱离控制终端；子 shell 里 source nvm 再进循环）
setsid bash -c '
  set -u
  LOG="'"$LOG"'"
  LOCK="'"$LOCK"'"
  INTERVAL="'"$INTERVAL"'"

  export NVM_DIR="$HOME/.nvm"
  # shellcheck disable=SC1091
  . "$NVM_DIR/nvm.sh" >/dev/null 2>&1 || true
  export HF_ENDPOINT="${HF_ENDPOINT:-https://hf-mirror.com}"

  log() { echo "[$(date "+%Y-%m-%d %H:%M:%S")] $*"; }

  run_once() {
    if ! flock -n 9; then
      log "skip: previous run still holds lock"
      return 0
    fi
    log "=== qmd update ==="
    qmd update 2>&1 | tail -n 5
    log "=== qmd embed ==="
    qmd embed 2>&1 | tail -n 5
    log "=== done ==="
  } 9>"$LOCK"

  log "loop started (pid=$$, interval=${INTERVAL}s, qmd=$(command -v qmd || echo NOT_FOUND))"
  trap "log \"loop stopped by signal\"; exit 0" TERM INT

  while true; do
    run_once
    sleep "$INTERVAL"
  done
' >>"$LOG" 2>&1 &

BG_PID=$!
echo $BG_PID > "$PID_FILE"

sleep 0.5
if kill -0 "$BG_PID" 2>/dev/null; then
  echo "started: pid $BG_PID"
  echo "  interval: ${INTERVAL}s (env QMD_INTERVAL 可覆盖)"
  echo "  log: $LOG"
else
  echo "failed to start; check $LOG"
  rm -f "$PID_FILE"
  exit 1
fi
