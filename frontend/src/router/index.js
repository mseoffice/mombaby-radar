import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login',          name: 'Login',          component: () => import('../views/Login.vue') },
  { path: '/',               redirect: '/dashboard' },
  { path: '/dashboard',      name: 'Dashboard',      component: () => import('../views/Dashboard.vue'),      meta: { requiresAuth: true } },
  { path: '/products',       name: 'Products',       component: () => import('../views/Products.vue'),       meta: { requiresAuth: true } },
  { path: '/content-factory',name: 'ContentFactory', component: () => import('../views/ContentFactory.vue'), meta: { requiresAuth: true } },
  { path: '/ai-ops',         name: 'AiOps',          component: () => import('../views/AiOps.vue'),          meta: { requiresAuth: true } },
  { path: '/private-ops',    name: 'PrivateOps',     component: () => import('../views/PrivateOps.vue'),     meta: { requiresAuth: true } },
  { path: '/analytics',      name: 'Analytics',      component: () => import('../views/Analytics.vue'),      meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 导航守卫：未登录跳 /login
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return '/login'
  }
})

export default router
