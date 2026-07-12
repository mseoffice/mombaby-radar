import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 前端 Vite 配置：dev 5173，代理 /api → 后端 8080
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
