#!/usr/bin/env bash
# 手动跑一次 qmd 增量更新（前台，看得见输出）
# 用于：
#   - 加了新笔记想立刻生效，不等下一轮 30 分钟
#   - 守护进程挂了兜底手动跑
#
# 与守护共用 flock，避免和 loop 撞车

set -u

QMD_DIR="$HOME/workspace/common-toolkit/ai/memory/qmd"
LOCK="$QMD_DIR/qmd-auto.lock"

export NVM_DIR="$HOME/.nvm"
# shellcheck disable=SC1091
. "$NVM_DIR/nvm.sh" >/dev/null 2>&1 || true
export HF_ENDPOINT="${HF_ENDPOINT:-https://hf-mirror.com}"

if ! command -v qmd >/dev/null 2>&1; then
  echo "qmd not found on PATH" >&2
  exit 1
fi

run_once() {
  # -w 30：最多等守护那轮跑完（防两边同时改索引）
  if ! flock -w 30 9; then
    echo "[qmd] 拿不到 lock（守护正在跑？超过 30s 未释放）" >&2
    return 1
  fi
  echo "[qmd] $(date '+%F %T') === qmd update ==="
  qmd update
  echo ""
  echo "[qmd] $(date '+%F %T') === qmd embed ==="
  qmd embed
  echo ""
  echo "[qmd] $(date '+%F %T') === done ==="
} 9>"$LOCK"

run_once
