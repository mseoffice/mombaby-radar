// perf/k6/content-generate.js — #18 perf-baseline
// 基线目标：内容生成 ≤3min（5 平台，dev Mock profile）。
// 用法：k6 run --vus 3 --duration 3m perf/k6/content-generate.js

import { check } from 'k6';
import http from 'k6/http';

export const options = {
  vus: 3,
  duration: '180s',
  thresholds: {
    'http_req_duration{endpoint:generate}': ['p(95)<180000'],   // ≤3min
    'http_req_failed': ['rate<0.1'],
  },
};

const BASE = 'http://localhost:8080/api';

export default function () {
  // 先登录
  let loginRes = http.post(`${BASE}/auth/login`, JSON.stringify({
    username: 'admin',
    password: 'admin123',
  }), { headers: { 'Content-Type': 'application/json' } });
  let token = '';
  try { token = loginRes.json().token; } catch (e) { /* */ }

  let headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;

  let res = http.post(`${BASE}/content/generate`, JSON.stringify({
    productName: `测试商品-${__VU}-${__ITER}`,
    platforms: ['小红书', '抖音', '微博', '公众号', '微信群'],
  }), { headers, tags: { endpoint: 'generate' } });

  check(res, { 'generate 200': (r) => r.status === 200 });
}
