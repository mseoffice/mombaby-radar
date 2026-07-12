import { createRouter, createWebHistory } from 'vue-router'

// 6 页面骨架（设计视角_页面与状态清单 P1–P6）
const routes = [
  { path: '/',            redirect: '/dashboard' },
  { path: '/dashboard',       name: 'Dashboard',      component: () => import('../views/Dashboard.vue') },
  { path: '/products',        name: 'Products',       component: () => import('../views/Products.vue') },
  { path: '/content-factory', name: 'ContentFactory', component: () => import('../views/ContentFactory.vue') },
  { path: '/ai-ops',          name: 'AiOps',          component: () => import('../views/AiOps.vue') },
  { path: '/private-ops',     name: 'PrivateOps',     component: () => import('../views/PrivateOps.vue') },
  { path: '/analytics',       name: 'Analytics',      component: () => import('../views/Analytics.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
