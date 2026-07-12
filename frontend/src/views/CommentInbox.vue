<template>
  <div>
    <h1>💬 评论收件箱（AI 运营中心）</h1>

    <!-- 手动粘贴录入（D3 主路径） -->
    <div class="ingest-bar">
      <select v-model="platform">
        <option v-for="p in platforms" :key="p" :value="p">{{ p }}</option>
      </select>
      <textarea v-model="rawText" placeholder="粘贴评论 / 私信原文（手动录入，V1.0 主路径）"></textarea>
      <button @click="ingest" :disabled="ingesting">{{ ingesting ? '录入中...' : '录入' }}</button>
    </div>

    <!-- 评论列表 -->
    <table v-if="comments.length" class="comment-table">
      <thead>
        <tr>
          <th>平台</th><th>原文</th><th>状态</th><th>分类/情绪</th><th>AI 回复建议</th><th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in comments" :key="c.id">
          <td>{{ c.platform }}</td>
          <td class="raw">{{ c.rawText }}</td>
          <td><span :class="'cstatus ' + c.status">{{ statusLabel(c.status) }}</span></td>
          <td>
            <div v-if="c.type">类型：{{ c.type }}</div>
            <div v-if="c.sentiment">情绪：{{ c.sentiment }}</div>
            <div v-if="c.intent" class="intent">意图：{{ c.intent }}</div>
          </td>
          <td>
            <ul v-if="replyOptions(c).length">
              <li v-for="(r, i) in replyOptions(c)" :key="i">{{ r }}</li>
            </ul>
            <span v-else class="muted">—</span>
          </td>
          <td>
            <button v-if="c.status === 'NEW'" @click="analyze(c)">分析</button>
            <button
              v-for="(r, i) in replyOptions(c)"
              v-if="c.status === 'ANALYZED'"
              :key="'r' + i"
              @click="reply(c, r)">采用回复{{ i + 1 }}</button>
            <span v-if="c.status === 'REPLIED'" class="replied">已回复</span>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-else class="empty">暂无评论，粘贴一条试试。</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const platforms = ['小红书', '抖音', '微博', '公众号', '微信群']
const platform = ref('小红书')
const rawText = ref('')
const ingesting = ref(false)
const comments = ref([])

const statusLabel = (s) => ({ NEW: '待分析', ANALYZED: '已分析', REPLIED: '已回复', PENDING: '待人工' }[s] || s)

function replyOptions(c) {
  if (!c.replyOptions) return []
  try { return JSON.parse(c.replyOptions) } catch { return [] }
}

async function load() {
  try {
    const { data } = await api.get('/comments', { params: { size: 50, sort: 'id,desc' } })
    comments.value = data.content || []
  } catch (e) { /* ignore */ }
}

async function ingest() {
  if (!rawText.value.trim()) return
  ingesting.value = true
  try {
    await api.post('/comments/ingest', null, { params: { platform: platform.value, rawText: rawText.value } })
    rawText.value = ''
    await load()
  } catch (e) { alert('录入失败') } finally { ingesting.value = false }
}

async function analyze(c) {
  try {
    await api.post(`/comments/${c.id}/analyze`)
    await load()
  } catch (e) { alert('分析失败') }
}

async function reply(c, text) {
  try {
    await api.post(`/comments/${c.id}/reply`, null, { params: { replyText: text } })
    await load()
  } catch (e) { alert('回复失败') }
}

onMounted(load)
</script>

<style scoped>
.ingest-bar { display: flex; gap: 12px; margin-bottom: 24px; align-items: flex-start; }
.ingest-bar select { padding: 10px; border: 1px solid #d1d5db; border-radius: 6px; }
.ingest-bar textarea { flex: 1; min-height: 60px; padding: 10px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; resize: vertical; }
.ingest-bar button { padding: 10px 24px; background: #1f2937; color: #fff; border: none; border-radius: 6px; cursor: pointer; }
.ingest-bar button:disabled { opacity: .6; }
.comment-table { width: 100%; border-collapse: collapse; }
.comment-table th, .comment-table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; font-size: 13px; vertical-align: top; }
.comment-table .raw { max-width: 260px; white-space: pre-wrap; }
.comment-table .intent { color: #6b7280; }
.comment-table button { margin: 2px 6px 2px 0; padding: 4px 10px; border-radius: 4px; border: 1px solid #d1d5db; background: #fff; cursor: pointer; font-size: 12px; }
.cstatus { padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.cstatus.NEW { background: #fef3c7; color: #92400e; }
.cstatus.ANALYZED { background: #dbeafe; color: #1e40af; }
.cstatus.REPLIED { background: #d1fae5; color: #065f46; }
.replied { color: #6b7280; font-size: 12px; }
.muted { color: #9ca3af; }
.empty { color: #9ca3af; margin-top: 24px; }
</style>
