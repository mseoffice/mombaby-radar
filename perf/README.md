# 母婴雷达 AI — 性能压测（#18 perf-baseline）

## 前置条件

```bash
# 1) 系统参数（ES 需要）
sudo sysctl -w vm.max_map_count=262144

# 2) 启动基础设施
docker compose -f infra/docker-compose.yml up -d

# 3) 启动后端（dev profile，Mock LLM，无需真实 Key）
cd backend && mvn spring-boot:run
```

## 运行压测

```bash
# API 基线（50 VU，30s）
k6 run --vus 50 --duration 30s perf/k6/api-smoke.js

# 内容生成基线（3 VU，3min；断言 ≤3min）
k6 run --vus 3 --duration 180s perf/k6/content-generate.js

# 评论分析基线（5 VU，30s；断言 ≤5s）
k6 run --vus 5 --duration 30s perf/k6/comment-analyze.js
```

## 容量灌入（种子数据）

```bash
# 灌入测试种子数据（开发期可选；真实容量基线时跑）
# 待实现：backend 内 CommandLineRunner 或独立 SQL 脚本
```

## 指标采集

```bash
# Actuator 端点（后端已暴露）
curl http://localhost:8080/api/actuator/health
curl http://localhost:8080/api/actuator/prometheus
curl http://localhost:8080/api/actuator/metrics
```

## 真实 LLM 时延专项（条件步骤）

真实 DeepSeek Key 到位后：

```bash
SPRING_PROFILES_ACTIVE=prod DEEPSEEK_API_KEY=sk-xxx mvn spring-boot:run
# 然后重新运行 k6，断言内容生成≤3min（真实 API）与评论分析≤5s
```
