import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister } from '@/api/auth'
import { getUserInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isMerchant = computed(() => userInfo.value?.role === 1)
  const isAdmin = computed(() => userInfo.value?.role === 2)

  async function login(username, password) {
    const res = await apiLogin({ username, password })
    token.value = res.data.token
    setToken(res.data.token)
    userInfo.value = res.data.user
    return res
  }

  async function register(data) {
    const res = await apiRegister(data)
    return res
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      return res.data
    } catch (e) {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    removeToken()
  }

  return { token, userInfo, isLoggedIn, isMerchant, isAdmin, login, register, fetchUserInfo, logout }
})
