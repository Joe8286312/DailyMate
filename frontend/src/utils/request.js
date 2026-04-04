import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import router from '@/router'
import {
  logError,
  handleNetworkError,
  handleApiError,
  handleAuthError,
  retry
} from '@/utils/errorHandler'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求计数（用于控制 loading 显示）
let loadingCount = 0
let loadingInstance = null

/**
 * 显示 loading
 */
function showLoading () {
  loadingCount++
  if (loadingCount === 1) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
}

/**
 * 隐藏 loading
 */
function hideLoading () {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  }
}

// 需要显示 loading 的请求方法白名单
const loadingMethods = ['POST', 'PUT', 'DELETE']

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 直接获取 token，避免循环依赖
    const token = localStorage.getItem('dailyMate_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 对于修改数据的请求，显示 loading
    if (loadingMethods.includes(config.method?.toUpperCase())) {
      showLoading()
    }
    
    return config
  },
  error => {
    hideLoading()
    const appError = new (class extends Error {
      constructor (message, type, originalError) {
        super(message)
        this.name = 'AppError'
        this.type = type
        this.originalError = originalError
      }
    })('请求发送失败', 'REQUEST_ERROR', error)
    logError(appError)
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data

    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未授权，跳转到登录页
      if (res.code === 401) {
        localStorage.removeItem('dailyMate_token')
        localStorage.removeItem('dailyMate_user')
        router.push('/login')
      }

      const appError = handleApiError(new Error(res.message), res.message || '请求失败')
      return Promise.reject(appError)
    }

    return res
  },
  error => {
    hideLoading()
    // 网络错误
    if (!error.response) {
      const appError = handleNetworkError(error)
      ElMessage.error('网络错误，请检查网络连接')
      return Promise.reject(appError)
    }

    // HTTP 错误
    switch (error.response.status) {
    case 401:
      handleAuthError(error)
      ElMessage.error('未授权，请重新登录')
      localStorage.removeItem('dailyMate_token')
      localStorage.removeItem('dailyMate_user')
      router.push('/login')
      break
    case 403:
      handleApiError(error, '拒绝访问')
      ElMessage.error('拒绝访问')
      break
    case 404:
      handleApiError(error, '请求的资源不存在')
      ElMessage.error('请求的资源不存在')
      break
    case 500:
      handleApiError(error, '服务器内部错误')
      ElMessage.error('服务器内部错误')
      break
    default:
      handleApiError(error, error.response.data?.message || '请求失败')
      ElMessage.error(error.response.data?.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

/**
 * 带重试的请求方法
 * @param {Function} requestFn - 请求函数
 * @param {Object} options - 重试选项
 */
export function requestWithRetry (requestFn, options = {}) {
  return retry(requestFn, {
    maxRetries: options.maxRetries || 3,
    delay: options.delay || 1000,
    backoff: options.backoff || 2,
    onRetry: (error, retryCount) => {
      console.log(`请求重试 ${retryCount}/${options.maxRetries || 3}`)
    }
  })
}

export default request
