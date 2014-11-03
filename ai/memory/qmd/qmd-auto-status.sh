#!/usr/bin/env bash
# 查看 qmd 自动更新守护进程状态 + 最近日志
set -u

QMD_DIR="$HOME/workspace/common-toolkit/ai/memory/qmd"
PID_FILE="$QMD_DIR/qmd-auto.pid"
LOG="$QMD_DIR/qmd-auto.log"

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  PID="$(cat "$PID_FILE")"
  echo "status: RUNNING (pid=$PID)"
  ps -o pid,etime,stat,cmd -p "$PID" 2>&1 | tail -n +1
else
  echo "status: STOPPED"
  [ -f "$PID_FILE" ] && echo "  (stale pid file exists: $PID_FILE)"
fi

echo ""
echo "--- last 20 log lines ---"
if [ -f "$LOG" ]; then
  tail -n 20 "$LOG"
else
  echo "(no log yet)"
fi
