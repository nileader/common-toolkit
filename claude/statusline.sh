#!/bin/bash
input=$(cat)

project_dir=$(echo "$input" | jq -r '.workspace.project_dir // empty')
cwd=$(echo "$input" | jq -r '.cwd')
model=$(echo "$input" | jq -r '.model.display_name // .model.id')

# 工作区根:优先 project_dir(多工程工作区),否则 cwd
workspace_root="${project_dir:-$cwd}"

# full_path 显示工作区根,~ 缩写(显式替换 $HOME 前缀 → ~)
if [ -n "$HOME" ] && [ "${workspace_root:0:${#HOME}}" = "$HOME" ]; then
  full_path="~${workspace_root:${#HOME}}"
else
  full_path="$workspace_root"
fi

# ANSI 颜色(Claude Code statusline 支持 ANSI 转义码,不占显示宽度,不影响 CLI 的 left margin/对齐)
C_RESET=$'\033[0m'
C_NAME=$'\033[1;36m'     # 工程名:加粗青
C_BRANCH=$'\033[1;33m'   # 分支:加粗黄

# 单个 git 仓库的变更摘要,多行块输出:
#   - 名称:分支
#       [暂N 未跟踪N 修改N 删除N]
#       file1
#       file2
# 标题行单独一行;变更摘要用方括号单独一行(4 空格缩进);
# 改动文件每行 3 个(逗号分隔,4 空格缩进),全部显示不省略,避免单行过宽被 CLI 按 ... 截断。
# 用方括号把标题/摘要/文件分层,多工程块边界更清晰。
# 无改动则不输出(隐藏),只显示有变更的工程
git_summary() {
  local repo="$1"
  local branch
  branch=$(git -C "$repo" --no-optional-locks branch --show-current 2>/dev/null)
  [ -z "$branch" ] && return
  local name
  name=$(basename "$repo")
  local st
  st=$(git -C "$repo" --no-optional-locks status --porcelain 2>/dev/null)
  [ -z "$st" ] && return  # 无改动不显示
  local staged untracked modified deleted changes=""
  staged=$(echo "$st" | grep -c '^[MADRC]')
  untracked=$(echo "$st" | grep -c '^??')
  modified=$(echo "$st" | grep -c '^ M')
  deleted=$(echo "$st" | grep -c '^.D')
  [ "$staged" -gt 0 ]    && changes="暂存$staged"
  [ "$untracked" -gt 0 ] && changes="$changes 未跟踪$untracked"
  [ "$modified" -gt 0 ]  && changes="$changes 修改$modified"
  [ "$deleted" -gt 0 ]   && changes="$changes 删除$deleted"
  changes="${changes# }"
  # 标题行(工程块起点,带 "- " 前缀);工程名/分支上色;变更摘要用方括号单独一行(2 空格前缀,CLI 再加 2 margin = 终端 4 空格)
  local out="- ${C_NAME}$name${C_RESET}:${C_BRANCH}$branch${C_RESET}
  [$changes]"
  # 改动文件每行 3 个(逗号分隔,4 空格缩进),全部显示不省略,避免单行过宽被 CLI 截断
  local f line="" cnt=0
  while IFS= read -r f; do
    [ -z "$f" ] && continue
    if [ -z "$line" ]; then
      line="  $f"
    else
      line="$line, $f"
    fi
    cnt=$((cnt+1))
    if [ "$cnt" -ge 3 ]; then
      out="$out
$line"
      line=""
      cnt=0
    fi
  done <<< "$(echo "$st" | cut -c4-)"
  [ -n "$line" ] && out="$out
$line"
  echo "$out"
}

# 收集有改动的工程(每工程一块,多行),遍历工作区所有 git 工程(一级 + 二级 + 三级 aurix-pod/*)
repo_lines=""
for sub in $(find "$workspace_root" -maxdepth 3 -name .git \( -type d -o -type f \) 2>/dev/null | sed 's|/.git$||' | sort -u); do
  s=$(git_summary "$sub")
  [ -n "$s" ] && repo_lines="${repo_lines:+$repo_lines
}$s"
done

# 分段组装:第一行 工作区 | 模型 | Host,空行后 Git 变更情况 + 列表
lines=""
if [ -n "$full_path" ]; then
  lines="工作区：$full_path"
fi
if [ -n "$model" ]; then
  if [ -n "$lines" ]; then
    lines="$lines | 模型：$model"
  else
    lines="模型：$model"
  fi
fi
host=$(cat /etc/hostname 2>/dev/null || hostname 2>/dev/null)
if [ -n "$host" ]; then
  if [ -n "$lines" ]; then
    lines="$lines | Host：$host"
  else
    lines="Host：$host"
  fi
fi
if [ -n "$repo_lines" ]; then
  lines="$lines

Git 变更情况：
$repo_lines"
fi

printf '%s\n' "$lines"
