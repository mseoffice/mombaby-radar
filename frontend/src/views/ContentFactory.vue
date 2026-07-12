<template>
  <div>
    <h1>🏭 内容工厂</h1>

    <!-- 生成表单 -->
    <div class="generate-bar">
      <input v-model="productName" placeholder="商品名称" />
      <button @click="handleGenerate" :disabled="generating">
        {{ generating ? '生成中...' : '一键生成' }}
      </button>
    </div>

    <!-- 内容列表（Mock 占位） -->
    <table v-if="contents.length" class="content-table">
      <thead>
        <tr><th>平台</th><th>标题</th><th>状态</th><th>操作</th></tr>
      </thead>
      <tbody>
        <tr v-for="c in contents" :key="c.id">
          <td>{{ c.platform }}</td>
          <td>{{ c.title }}</td>
          <td>
            <span :class="'status ' + c.status">{{ statusLabel(c.status) }}</span>
            <span v-if="c.aiGenerated" class="ai-badge">AI 生成</span>
          </td>
          <td>
            <button v-if="c.status === 'PENDING_REVIEW'" @click="approve(c)">通过</button>
            <button v-if="c.status === 'PENDING_REVIEW'" @click="reject(c)">驳回</button>
            <button
              v-if="c.status === 'APPROVED'"
              :disabled="c.status !== 'APPROVED'"
              @click="publish(c)">
              发布
            </button>
            <span v-if="c.status === 'PUBLISHED'" class="published-tag">已发布</span>
            <span v-if="c.status === 'PENDING_REVIEW'" class="lock-hint" title="待审核内容不可发布">🔒</span>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-else class="empty">暂无内容，请生成第一条。</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api'

const productName = ref('')
const generating = ref(false)
const contents = ref([])

const statusLabel = (s) =>
  ({ GENERATING: '生成中', PENDING_REVIEW: '待审核', APPROVED: '已通过', PUBLISHED: '已发布', REJECTED: '已驳回', ARCHIVED: '已归档' })[s] || s

async function handleGenerate() {
  generating.value = true
  try {
    const { data } = await api.post('/content/generate', {
      productName: productName.value,
      platforms: ['小红书', '抖音', '微博', '公众号', '微信群'],
    })
    // 从 response 构建本地列表
    const list = (data.contents || []).map((c, i) => ({
      id: Date.now() + i,
      platform: c.platform,
      title: c.title,
      status: 'PENDING_REVIEW',
      aiGenerated: true,
    }))
    contents.value = [...list, ...contents.value]
  } catch (e) {
    alert('生成失败')
  } finally {
    generating.value = false
  }
}

async function approve(c) {
  await api.post(`/content/${c.id}/approve`, null, { params: { reviewerId: 1 } })
  c.status = 'APPROVED'
}

async function reject(c) {
  await api.post(`/content/${c.id}/reject`, null, { params: { reviewerId: 1 } })
  c.status = 'REJECTED'
}

async function publish(c) {
  await api.post(`/content/${c.id}/publish`)
  c.status = 'PUBLISHED'
}
</script>

<style scoped>
.generate-bar { display: flex; gap: 12px; margin-bottom: 24px; }
.generate-bar input { flex: 1; padding: 10px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; }
.generate-bar button { padding: 10px 24px; background: #1f2937; color: #fff; border: none; border-radius: 6px; cursor: pointer; }
.generate-bar button:disabled { opacity: .6; }
.content-table { width: 100%; border-collapse: collapse; }
.content-table th, .content-table td { padding: 10px 12px; border-bottom: 1px solid #e5e7eb; text-align: left; font-size: 14px; }
.content-table button { margin-right: 8px; padding: 4px 12px; border-radius: 4px; border: 1px solid #d1d5db; background: #fff; cursor: pointer; font-size: 13px; }
.content-table button:disabled { opacity: .4; cursor: not-allowed; }
.status { padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.status.PENDING_REVIEW { background: #fef3c7; color: #92400e; }
.status.APPROVED { background: #d1fae5; color: #065f46; }
.status.PUBLISHED { background: #dbeafe; color: #1e40af; }
.ai-badge { margin-left: 8px; padding: 1px 6px; background: #ede9fe; color: #7c3aed; border-radius: 4px; font-size: 11px; }
.lock-hint { margin-left: 8px; font-size: 14px; }
.published-tag { color: #6b7280; font-size: 13px; }
.empty { color: #9ca3af; margin-top: 24px; }
</style>
