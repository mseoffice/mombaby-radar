<template>
  <div v-if="isLoginPage" class="app-shell">
    <router-view />
  </div>
  <div v-else class="app-shell">
    <nav class="side-nav">
      <div class="logo">📊 母婴雷达</div>
      <router-link to="/dashboard">Dashboard</router-link>
      <router-link to="/products">商品中心</router-link>
      <router-link to="/content-factory">内容工厂</router-link>
      <router-link to="/ai-ops">AI 运营中心</router-link>
      <router-link to="/private-ops">私域运营</router-link>
      <router-link to="/analytics">数据中心</router-link>
      <div class="user-bar">
        <span>{{ auth.username }}</span>
        <button @click="handleLogout">退出</button>
      </div>
    </nav>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const isLoginPage = computed(() => route.path === '/login')

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style>
.app-shell { display: flex; min-height: 100vh; }
.side-nav { width: 200px; background: #1f2937; color: #fff; padding: 16px; display: flex; flex-direction: column; }
.side-nav a { display: block; color: #cbd5e1; padding: 8px 0; text-decoration: none; }
.side-nav a.router-link-active { color: #fff; font-weight: 600; }
.content { flex: 1; padding: 24px; }
.logo { font-weight: 700; margin-bottom: 16px; }
.user-bar { margin-top: auto; padding-top: 16px; border-top: 1px solid #374151; font-size: 13px; }
.user-bar button { margin-top: 8px; background: none; color: #9ca3af; border: 1px solid #4b5563; border-radius: 4px; padding: 4px 8px; cursor: pointer; font-size: 12px; }
</style>
