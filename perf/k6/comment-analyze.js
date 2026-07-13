// perf/k6/comment-analyze.js — #18 perf-baseline
// 基线目标：评论分析 ≤5s（dev Mock profile）。
// 用法：k6 run --vus 5 --duration 30s perf/k6/comment-analyze.js

import { check } from 'k6';
import http from 'k6/http';

export const options = {
  vus: 5,
  duration: '30s',
  thresholds: {
    'http_req_duration{endpoint:analyze}': ['p(95)<5000'],   // ≤5s
    'http_req_failed': ['rate<0.1'],
  },
};

const BASE = 'http://localhost:8080/api';

export default function () {
  let loginRes = http.post(`${BASE}/auth/login`, JSON.stringify({
    username: 'admin',
    password: 'admin123',
  }), { headers: { 'Content-Type': 'application/json' } });
  let token = '';
  try { token = loginRes.json().token; } catch (e) { /* */ }

  let headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;

  // 录入评论
  let ingestRes = http.post(`${BASE}/comments/ingest`, null, {
    headers,
    params: { platform: '小红书', rawText: '这个商品怎么样？适合1岁宝宝吗？' },
    tags: { endpoint: 'ingest' },
  });

  let commentId = '';
  try { commentId = ingestRes.json().id; } catch (e) { /* */ }
  if (!commentId) return;

  // 分析评论
  let analyzeRes = http.post(`${BASE}/comments/${commentId}/analyze`, '', {
    headers,
    tags: { endpoint: 'analyze' },
  });

  check(analyzeRes, { 'analyze 200': (r) => r.status === 200 });
}
