<template>
  <div class="login-page">
    <form @submit.prevent="handleLogin" class="login-form">
      <h1>📊 母婴雷达 AI</h1>
      <p class="subtitle">V1.0 单团队内部工具</p>
      <input v-model="username" placeholder="用户名" required />
      <input v-model="password" type="password" placeholder="密码" required />
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await auth.login(username.value, password.value)
    router.push('/dashboard')
  } catch {
    error.value = '用户名或密码错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex; justify-content: center; align-items: center; min-height: 100vh;
  background: #f3f4f6;
}
.login-form {
  background: #fff; padding: 32px; border-radius: 8px; width: 360px;
  box-shadow: 0 2px 8px rgba(0,0,0,.1);
}
.login-form h1 { margin: 0 0 4px; font-size: 22px; }
.subtitle { color: #9ca3af; margin-bottom: 24px; font-size: 13px; }
.login-form input {
  width: 100%; padding: 10px 12px; margin-bottom: 12px; border: 1px solid #d1d5db;
  border-radius: 6px; font-size: 14px; box-sizing: border-box;
}
.login-form button {
  width: 100%; padding: 10px; background: #1f2937; color: #fff; border: none;
  border-radius: 6px; font-size: 14px; cursor: pointer;
}
.login-form button:disabled { opacity: .6; cursor: not-allowed; }
.error { color: #ef4444; font-size: 13px; margin-top: 8px; }
</style>
