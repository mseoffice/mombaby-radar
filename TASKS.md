# 母婴雷达 AI — 研发任务映射（TASKS）

> 输入：`研发任务拆解.md`（批次 0–3）+ 9 条 wb-issues 事项
> GitHub 仓库：`mseoffice/mombaby-radar` | 里程碑：[M1](https://github.com/mseoffice/mombaby-radar/milestone/1) / [M2](https://github.com/mseoffice/mombaby-radar/milestone/2) / [M3](https://github.com/mseoffice/mombaby-radar/milestone/3) / [M4](https://github.com/mseoffice/mombaby-radar/milestone/4)

## M1（第 2 周）：脚手架 + 核心服务

| Issue # | 标题 | wb-issues |
|---|---|---|
| [#1](https://github.com/mseoffice/mombaby-radar/issues/1) | product-entity — 商品表 + CRUD 接口 | r6HUar |
| [#2](https://github.com/mseoffice/mombaby-radar/issues/2) | content-entity — 内容表 + 状态机 | rfF0o8 |
| [#3](https://github.com/mseoffice/mombaby-radar/issues/3) | agent-task-entity — Agent 任务表 | r6HUar |
| [#4](https://github.com/mseoffice/mombaby-radar/issues/4) | role-entity + RBAC 骨架 | r6HUar |
| [#5](https://github.com/mseoffice/mombaby-radar/issues/5) | content_review_log + operation_log 审计表 | r6HUar / resjs4 |
| [#6](https://github.com/mseoffice/mombaby-radar/issues/6) | jwt-auth — JWT 认证 + 刷新 | resjs4 |
| [#7](https://github.com/mseoffice/mombaby-radar/issues/7) | frontend-shell — 6 页面路由 + 布局 | r1RECp |
| [#8](https://github.com/mseoffice/mombaby-radar/issues/8) | docker-compose 本地基础设施 | r6HUar |

## M2（第 4 周）：内容工厂闭环

| Issue # | 标题 | wb-issues |
|---|---|---|
| [#9](https://github.com/mseoffice/mombaby-radar/issues/9) | content-generate — 5 平台内容生成 | rjvgmc |
| [#10](https://github.com/mseoffice/mombaby-radar/issues/10) | content-review — 审核流程 | rfF0o8 |
| [#11](https://github.com/mseoffice/mombaby-radar/issues/11) | content-publish — 发布 + 锁定 + AI 标识 | rfF0o8 / resjs4 |
| [#12](https://github.com/mseoffice/mombaby-radar/issues/12) | agent-orchestrator — 6 Agent 调度 + 编排 | rHcFjC |

## M3（第 7 周）：全模块 + 平台接入

| Issue # | 标题 | 门控 | wb-issues |
|---|---|---|---|
| [#13](https://github.com/mseoffice/mombaby-radar/issues/13) | platform-gateway — 多平台 API 接入（D2）🚫 | D2 方案 | rHcFjC |
| [#14](https://github.com/mseoffice/mombaby-radar/issues/14) | comment-ops — 评论分析 + 回复（D3）🚫 | **D3 采集方式** | rhnHiO |
| [#15](https://github.com/mseoffice/mombaby-radar/issues/15) | group-push — 群推送计划 + 效果记录 | D2 | rhnHiO |
| [#16](https://github.com/mseoffice/mombaby-radar/issues/16) | analytics — 数据中心 | T1.1/T1.2 | r6HUar |
| [#17](https://github.com/mseoffice/mombaby-radar/issues/17) | dashboard — 6 卡片 + 待办 + 趋势 + AI 建议 | T1.1/T1.2 | rP8vVZ |

## M4（第 8 周）：压测 + 合规 + 上线

| Issue # | 标题 | wb-issues |
|---|---|---|
| [#18](https://github.com/mseoffice/mombaby-radar/issues/18) | perf-baseline — 性能压测 | r6HUar |
| [#19](https://github.com/mseoffice/mombaby-radar/issues/19) | compliance — 合规落地 | resjs4 |
| [#20](https://github.com/mseoffice/mombaby-radar/issues/20) | e2e-smoke — 验收标准端到端冒烟 | rP8vVZ |
| [#21](https://github.com/mseoffice/mombaby-radar/issues/21) | release-v1.0 — 上线清单 + tag | rIwCRO |

## 硬门控（🚫 = blocked）

- **D3** 采集方式（rhnHiO）→ 解之前，`#14 comment-ops` 不得 start
- **D2** 多平台方案（rHcFjC）→ 解之前，`#13 platform-gateway` 不得 start
- **38.3** 排期/范围（rIwCRO）→ 决定 M3 是否裁 `#15 group-push` / 图片生成
