import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, userApi } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(data) {
    token.value = data.token
    localStorage.setItem('token', data.token)
    user.value = {
      id: data.userId,
      name: data.name,
      email: data.email,
    }
  }

  async function login(email, password) {
    const { data } = await authApi.login(email, password)
    setAuth(data)
    await fetchProfile()
    return data
  }

  async function register(name, email, password) {
    const { data } = await authApi.register(name, email, password)
    setAuth(data)
    await fetchProfile()
    return data
  }

  async function fetchProfile() {
    if (!token.value) return
    const { data } = await userApi.me()
    user.value = data
    return data
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isLoggedIn, login, register, fetchProfile, logout }
})
