#!/usr/bin/env bash
# 停止 qmd 自动更新守护进程
set -u

QMD_DIR="$HOME/workspace/common-toolkit/ai/memory/qmd"
PID_FILE="$QMD_DIR/qmd-auto.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "not running (no pid file)"
  exit 0
fi

PID="$(cat "$PID_FILE")"
if kill -0 "$PID" 2>/dev/null; then
  kill -TERM "$PID"
  # 等最多 5 秒让它清理退出
  for _ in 1 2 3 4 5; do
    kill -0 "$PID" 2>/dev/null || break
    sleep 1
  done
  if kill -0 "$PID" 2>/dev/null; then
    echo "TERM 未生效，改用 KILL"
    kill -KILL "$PID"
  fi
  echo "stopped: pid=$PID"
else
  echo "stale pid $PID (already dead)"
fi

rm -f "$PID_FILE"
