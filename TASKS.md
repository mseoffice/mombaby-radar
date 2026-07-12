# 母婴雷达 AI — 研发任务映射（TASKS）

> 输入：`研发任务拆解.md`（批次 0–3）+ 9 条 wb-issues 事项
> GitHub Issues / 里程碑 建议对照表。

## 里程碑 M1（第 2 周）：脚手架 + 核心服务

| Issue | 内容 | wb-issues |
|---|---|---|
| feat: product-entity | `product` / `price_history` / `product_category` 实体 + 基础 CRUD 接口 | r6HUar |
| feat: content-entity | `content` / `content_platform` + content 状态机（34.2.6）| rfF0o8 |
| feat: agent-task-entity | `agent_task` + 执行日志骨架 | r6HUar |
| feat: role-entity | `role`（RBAC）+ `sys_user.role_id` 引用 | r6HUar |
| feat: review-log-entity | `content_review_log`（审核追溯）| r6HUar |
| feat: operation-log-entity | `operation_log`（操作审计，保留 6 月）| resjs4 |
| feat: jwt-auth | JWT 认证 + 刷新（35.2，2h 有效期）| resjs4 |
| feat: frontend-shell | Vue3 + Vite + 6 页面路由 + 侧边导航 | r1RECp |
| feat: docker-compose | MySQL + RabbitMQ + ES + MinIO 本地 infra | r6HUar |

## 里程碑 M2（第 4 周）：内容工厂闭环

| Issue | 内容 | wb-issues |
|---|---|---|
| feat: content-generate | 5 平台内容生成（含 12.3 规格）| rjvgmc |
| feat: content-review | 审核流程（pending_review→approved/rejected）| rfF0o8 |
| feat: content-publish | 发布 + 锁定 + "AI 生成"标识 | rfF0o8 / resjs4 |
| feat: agent-orchestrator | 6 Agent 调度 + 任务编排 + RabbitMQ 事件 | rHcFjC |

## 里程碑 M3（第 7 周）：全模块 + 平台接入

| Issue | 内容 | 门控 | wb-issues |
|---|---|---|---|
| feat: platform-gateway | 多平台 API 接入（D2）| D2 方案 | rHcFjC |
| feat: comment-ops | 评论分析 + 回复（D3）| **D3 采集方式** | rhnHiO |
| feat: group-push | 群推送计划 + 效果记录 | D2（平台同步）| rhnHiO |
| feat: analytics | 数据中心（日报/周报/三维分析）| T2.1/T2.2 | r6HUar |
| feat: dashboard | 6 卡片 + 待办 + 趋势 + AI 建议 | T1.1/T1.2 | rP8vVZ |

## 里程碑 M4（第 8 周）：压测 + 合规 + 上线

| Issue | 内容 | wb-issues |
|---|---|---|
| test: perf-baseline | 首屏≤2s / API≤200ms / 内容≤3min / 评论≤5s / 50 并发 | r6HUar |
| feat: compliance | AI 标识 + 个人信息脱敏 + 审计 6 月 | resjs4 |
| chore: e2e-smoke | 附录B 验收标准端到端冒烟 | rP8vVZ |
| chore: release-1.0 | 上线清单 + 版本 tag v1.0.0 | rIwCRO |

## 硬门控

- **D3** 采集方式（rhnHiO）→ 解之前，模块④ `comment-ops` Issue 不得 start
- **38.3** 排期/范围（rIwCRO）→ 决定 M3 是否裁 `group-push` / 图片生成
