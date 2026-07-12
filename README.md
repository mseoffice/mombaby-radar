# 母婴雷达 AI（mombaby-radar）V1.0

> 单团队内部工具（D1 已确认，无多租户隔离，A7 不引入 `tenant_id`）。
> 产品需求文档 PRD：项目资料库 `FgUiuVpgaFpI`（`母婴雷达AI_PRD_V1.0.html`）。
> 设计/研发输入文档见仓库 `docs/` 索引与本地 `/workspace`（设计评审准备包、研发技术方案大纲、设计视角_页面与状态清单、研发任务拆解）。

## 技术栈

| 层 | 选型 |
|---|---|
| 前端 | Vue 3 + Vite |
| 后端 | Spring Boot 3.x + MySQL 8 |
| AI | Spring AI（**DeepSeek 为主 LLM**；开发期 Mock / 测试期真实 API 环境分层）+ RabbitMQ 事件编排 |
| 检索 | Elasticsearch 8 |
| 存储 | MinIO（对象，图片/内容资产） |
| 认证 | JWT（Token 2h 可刷新，第 35.2 章） |

## 目录结构

```
mombaby-radar/
├── frontend/        # Vue3 SPA（Vite）
├── backend/         # Spring Boot 单体单模块（V1.0 单体；5 服务边界以 package 形式存在，后续按需拆分）
├── infra/          # docker-compose（MySQL / RabbitMQ / ES / MinIO）
├── docs/           # 设计文档索引
├── TASKS.md         # 9 条事项 → GitHub Issues / 里程碑 映射
└── README.md
```

## 服务拆分（第 7.3 章，均 P0）

> V1.0 落地为**单体单模块**（假设 A5，≤50 人），下列服务边界在单体中以 package 形式存在；后续按需拆分为独立服务。

| 服务 | 职责 | 技术 |
|---|---|---|
| 商品服务 | 商品/价格历史/分类 | Spring Boot + MySQL |
| 内容服务 | 生成/审核/管理/发布 | Spring Boot + MySQL + MinIO |
| Agent 服务 | 6 个 AI Agent 调度 | LangChain4j + Spring AI + RabbitMQ |
| 运营服务 | 评论/私信/微信群 | Spring Boot + MySQL |
| 数据服务 | 采集/统计/报表 | Spring Boot + Elasticsearch |

## 里程碑（与第 36 章对齐，需 38.3 排期缓冲）

- **M1（第 2 周）**：脚手架 + 核心服务可用 + 数据模型草稿
- **M2（第 4 周）**：内容工厂闭环（生成→审核→发布）
- **M3（第 7 周）**：全模块 + 平台接入（D2/D3 已闭环）
- **M4（第 8 周）**：压测 + 合规 + 上线

## 分支策略

`main`（稳定）/ `dev`（集成）/ `feature/*`（按任务开分支）。

## 本地启动

```bash
# 1) 基础设施
docker compose -f infra/docker-compose.yml up -d

# 2) 后端
cd backend && ./mvnw spring-boot:run      # 或 mvn spring-boot:run

# 3) 前端
cd frontend && npm install && npm run dev   # http://localhost:5173
```

## 硬门控（研发启动前须闭环）

- **D3** 评论/私信采集方式（产品+技术）→ 决定模块④范围
- **38.3** 排期 buffer / 范围减法 → 决定交付节奏

详见 `TASKS.md` 与 PRD 附录 D。
