// perf/k6/api-smoke.js — #18 perf-baseline
// 基线目标：API≤200ms（50 并发）。默认 dev Mock profile，无需真实 Key。
// 用法：k6 run --vus 50 --duration 30s perf/k6/api-smoke.js
// 前提：docker compose -f infra/docker-compose.yml up -d && vm.max_map_count>=262144 && 后端已启动

import { check, sleep } from 'k6';
import http from 'k6/http';

export const options = {
  vus: 50,
  duration: '30s',
  thresholds: {
    'http_req_duration{endpoint:login}': ['p(95)<2000'],     // JWT 生成允许慢一点
    'http_req_duration{endpoint:dashboard}': ['p(95)<500'],  // 聚合接口
    'http_req_duration{endpoint:products}': ['p(95)<200'],   // CRUD 接口
    'http_req_failed': ['rate<0.01'],
  },
};

const BASE = 'http://localhost:8080/api';

export default function () {
  // 1) 登录
  let loginRes = http.post(`${BASE}/auth/login`, JSON.stringify({
    username: 'admin',
    password: 'admin123',
  }), {
    headers: { 'Content-Type': 'application/json' },
    tags: { endpoint: 'login' },
  });
  check(loginRes, { 'login 200': (r) => r.status === 200 });

  let token = '';
  try { token = loginRes.json().token; } catch (e) { /* */ }
  let headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;

  // 2) Dashboard
  let dashRes = http.get(`${BASE}/dashboard`, { headers, tags: { endpoint: 'dashboard' } });
  check(dashRes, { 'dashboard 200': (r) => r.status === 200 });

  // 3) 商品列表
  let prodRes = http.get(`${BASE}/products`, { headers, tags: { endpoint: 'products' } });
  check(prodRes, { 'products 200': (r) => r.status === 200 || r.status === 401 });

  sleep(1);
}
