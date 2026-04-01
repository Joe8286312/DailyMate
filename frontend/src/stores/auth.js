import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getCurrentUser, logout as logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('dailyMate_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('dailyMate_user') || 'null'))

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const avatar = computed(() => userInfo.value?.avatar || '')

  // 方法
  /**
   * 用户登录
   */
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    const { token: newToken, user } = res.data || res
    
    token.value = newToken
    userInfo.value = user
    
    localStorage.setItem('dailyMate_token', newToken)
    localStorage.setItem('dailyMate_user', JSON.stringify(user))
    
    return { success: true }
  }

  /**
   * 用户注册
   */
  async function register(registerForm) {
    const res = await registerApi(registerForm)
    return res
  }

  /**
   * 获取当前用户信息
   */
  async function fetchCurrentUser() {
    try {
      const res = await getCurrentUser()
      userInfo.value = res.data || res
      localStorage.setItem('dailyMate_user', JSON.stringify(userInfo.value))
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  /**
   * 更新用户信息
   */
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('dailyMate_user', JSON.stringify(userInfo.value))
  }

  /**
   * 退出登录
   */
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('dailyMate_token')
      localStorage.removeItem('dailyMate_user')
    }
  }

  /**
   * 清除本地存储（用于异常处理）
   */
  function clearStorage() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('dailyMate_token')
    localStorage.removeItem('dailyMate_user')
  }

  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isAuthenticated,
    username,
    avatar,
    // 方法
    login,
    register,
    fetchCurrentUser,
    updateUserInfo,
    logout,
    clearStorage
  }
})
