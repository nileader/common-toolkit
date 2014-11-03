# qmd 自动更新守护脚本

给 `~/workspace/work-context` 和 `~/workspace/work-agent/work-note` 两个 md 目录做**每 1 小时一次的增量索引更新**。每轮跑 `qmd update → qmd embed → qmd cleanup`：`update` 增量同步文件增删改，`embed` 给新文件生成向量，`cleanup` 回收"文件已删/改但向量残留"的孤儿 chunk（前两步覆盖不到，需 `cleanup` 兜底）。避免每次改笔记都手动跑。

**与 TDAI（TencentDB Agent Memory）栈完全独立** —— 不共享 pid / log / lock；qmd 挂了不影响 TDAI，反之亦然。

## 文件说明

| 脚本 | 作用 |
|---|---|
| `qmd-auto-start.sh` | 启动**持续**守护：后台每 `INTERVAL` 秒跑一次 `qmd update && qmd embed && qmd cleanup`，写 pid |
| `qmd-auto-start-1-time.sh` | **一次性**前台跑一轮增量（守护挂了兜底 / 加完新笔记想立刻生效） |
| `qmd-auto-stop.sh` | 停止守护（SIGTERM，5 秒不退则 SIGKILL） |
| `qmd-auto-status.sh` | 查状态 + 最近日志 |

生成物（不入 git，见 `.gitignore`）：

- `qmd-auto.log` —— 守护循环日志
- `qmd-auto.pid` —— 守护进程 pid
- `qmd-auto.lock` —— flock 锁文件，守护与一次性脚本共用，避免同时写索引

## 用法

```sh
# 起守护（幂等，已在跑则打印现有 pid）
~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-start.sh

# 手动跑一次（前台，看得见输出；守护挂了或想立刻生效时用）
~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-start-1-time.sh

# 看守护状态
~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-status.sh

# 停守护
~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-stop.sh
```

## 调整间隔

默认 3600 秒（1 小时）。启动前设环境变量覆盖：

```sh
QMD_INTERVAL=7200 ~/workspace/common-toolkit/ai/memory/qmd/qmd-auto-start.sh
```

## 设计要点

- **flock 防重入**：如果 qmd 命令跑得慢，上一轮没结束就轮到下一轮，`flock -n` 会让下一轮跳过并留日志
- **NVM 环境**：脚本内部 source `~/.nvm/nvm.sh`，保证 nohup 后台环境也能找到 qmd
- **HF 镜像**：`HF_ENDPOINT=https://hf-mirror.com`，防止意外触发国外下载
- **信号处理**：SIGTERM/SIGINT 会打印日志再退出，方便看是主动停还是被杀

## 关联

- 部署文档：`~/workspace/tool/claude-brain-docs/04-日常运维-操作手册.md` §1
- qmd 本身：`~/workspace/tool/claude-brain-docs/qmd搭建-操作手册.md`
- 相关 auto-memory：`~/.claude/projects/-home-admin/memory/qmd-auto-update-daemon.md`
