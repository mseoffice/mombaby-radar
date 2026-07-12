import { defineStore } from 'pinia'
import api from '../api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.user?.username || '',
  },
  actions: {
    async login(username, password) {
      const { data } = await api.post('/auth/login', { username, password })
      this.token = data.token
      this.user = { id: data.userId, username: data.username }
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return data
    },
    async refresh() {
      const { data } = await api.post('/auth/refresh', { token: this.token })
      this.token = data.token
      localStorage.setItem('token', data.token)
      return data
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})
