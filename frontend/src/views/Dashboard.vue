<template>
  <div>
    <h1>📊 Dashboard</h1>

    <!-- 6 卡片 -->
    <div class="cards" v-if="cards.length">
      <div class="card" v-for="c in cards" :key="c.label">
        <div class="card-value">{{ c.value }}</div>
        <div class="card-label">{{ c.label }}</div>
      </div>
    </div>

    <div class="grid">
      <!-- 今日待办 -->
      <section class="panel">
        <h2>📋 今日待办</h2>
        <h3>待审核内容（{{ todos.pendingReviewContents.length }}）</h3>
        <ul>
          <li v-for="c in todos.pendingReviewContents" :key="'pc' + c.id">
            [{{ c.platform }}] {{ c.title || '(无标题)' }}
          </li>
        </ul>
        <p v-if="!todos.pendingReviewContents.length" class="muted">无</p>

        <h3>待分析评论（{{ todos.newComments.length }}）</h3>
        <ul>
          <li v-for="c in todos.newComments" :key="'nc' + c.id">
            [{{ c.platform }}] {{ c.rawText?.slice(0, 30) || '' }}…
          </li>
        </ul>
        <p v-if="!todos.newComments.length" class="muted">无</p>
      </section>

      <!-- 运营趋势 -->
      <section class="panel">
        <h2>📈 近 7 日内容趋势</h2>
        <div class="trend">
          <div class="bar-col" v-for="t in trend" :key="t.date">
            <div class="bar" :style="{ height: barHeight(t.count) + 'px' }"></div>
            <div class="bar-label">{{ t.date.slice(5) }}</div>
            <div class="bar-count">{{ t.count }}</div>
          </div>
        </div>
      </section>
    </div>

    <!-- 昨爆文 -->
    <section class="panel" v-if="yesterdayHot">
      <h2>🔥 昨爆文</h2>
      <p>[{{ yesterdayHot.platform }}] {{ yesterdayHot.title || '(无标题)' }}</p>
    </section>

    <!-- 可折叠 AI 建议 -->
    <section class="panel">
      <h2 @click="showAi = !showAi" style="cursor:pointer">
        💡 AI 运营建议（{{ showAi ? '收起' : '展开' }}）
      </h2>
      <ul v-if="showAi">
        <li v-for="(s, i) in aiSuggestions" :key="i">{{ s }}</li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import api from '../api'

const cards = ref([])
const todos = ref({ pendingReviewContents: [], newComments: [] })
const trend = ref([])
const yesterdayHot = ref(null)
const aiSuggestions = ref([])
const showAi = ref(false)

const maxCount = computed(() => Math.max(1, ...trend.value.map(t => t.count)))
function barHeight(count) {
  return Math.round((count / maxCount.value) * 80) + 4
}

async function load() {
  try {
    const { data } = await api.get('/dashboard')
    cards.value = data.cards || []
    todos.value = data.todos || { pendingReviewContents: [], newComments: [] }
    trend.value = data.trend || []
    yesterdayHot.value = data.yesterdayHot || null
    aiSuggestions.value = data.aiSuggestions || []
  } catch (e) { /* ignore */ }
}

onMounted(load)
</script>

<style scoped>
.cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 24px; }
.card { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 18px; }
.card-value { font-size: 28px; font-weight: 700; }
.card-label { color: #6b7280; margin-top: 4px; font-size: 13px; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.panel { background: #fff; border: 1px solid #e5e7eb; border-radius: 10px; padding: 18px; margin-bottom: 16px; }
.panel h2 { font-size: 16px; margin: 0 0 12px; }
.panel h3 { font-size: 14px; margin: 12px 0 6px; }
.panel ul { margin: 0; padding-left: 18px; }
.panel li { font-size: 13px; margin: 4px 0; }
.muted { color: #9ca3af; font-size: 13px; }
.trend { display: flex; align-items: flex-end; gap: 10px; height: 110px; }
.bar-col { display: flex; flex-direction: column; align-items: center; justify-content: flex-end; flex: 1; }
.bar { width: 24px; background: #6366f1; border-radius: 4px 4px 0 0; }
.bar-label { font-size: 11px; color: #6b7280; margin-top: 4px; }
.bar-count { font-size: 11px; font-weight: 600; }
</style>
