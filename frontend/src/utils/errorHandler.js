/**
 * 全局错误处理工具
 */

// 错误日志队列
const errorQueue = []
const MAX_ERROR_QUEUE_LENGTH = 50

// 错误类型枚举
export const ErrorType = {
  NETWORK: 'NETWORK_ERROR',
  API: 'API_ERROR',
  VALIDATION: 'VALIDATION_ERROR',
  AUTH: 'AUTH_ERROR',
  UNKNOWN: 'UNKNOWN_ERROR'
}

/**
 * 错误日志类
 */
export class AppError extends Error {
  constructor(message, type = ErrorType.UNKNOWN, originalError = null, extraInfo = {}) {
    super(message)
    this.name = 'AppError'
    this.type = type
    this.originalError = originalError
    this.extraInfo = extraInfo
    this.timestamp = new Date().toISOString()
    this.userAgent = navigator.userAgent
    this.url = window.location.href
  }

  toJSON() {
    return {
      name: this.name,
      message: this.message,
      type: this.type,
      timestamp: this.timestamp,
      userAgent: this.userAgent,
      url: this.url,
      stack: this.stack,
      extraInfo: this.extraInfo
    }
  }
}

/**
 * 记录错误日志
 */
export function logError(error, options = {}) {
  const appError = error instanceof AppError 
    ? error 
    : new AppError(error.message || '未知错误', ErrorType.UNKNOWN, error, options.extraInfo)

  const errorLog = appError.toJSON()

  // 添加到队列
  errorQueue.push(errorLog)
  if (errorQueue.length > MAX_ERROR_QUEUE_LENGTH) {
    errorQueue.shift()
  }

  // 输出到控制台
  console.error('[App Error]', errorLog)

  // 发送到错误收集服务（可选）
  // sendToErrorCollectionService(errorLog)

  return appError
}

/**
 * 网络错误处理
 */
export function handleNetworkError(error) {
  const appError = new AppError(
    '网络连接失败，请检查网络连接',
    ErrorType.NETWORK,
    error,
    {
      requestUrl: error.config?.url,
      method: error.config?.method
    }
  )
  logError(appError)
  return appError
}

/**
 * API 错误处理
 */
export function handleApiError(error, defaultMessage = '请求失败') {
  const appError = new AppError(
    error.response?.data?.message || error.message || defaultMessage,
    ErrorType.API,
    error,
    {
      statusCode: error.response?.status,
      requestUrl: error.config?.url
    }
  )
  logError(appError)
  return appError
}

/**
 * 认证错误处理
 */
export function handleAuthError(error) {
  const appError = new AppError(
    '认证失败，请重新登录',
    ErrorType.AUTH,
    error
  )
  logError(appError)
  return appError
}

/**
 * 获取错误队列
 */
export function getErrorQueue() {
  return [...errorQueue]
}

/**
 * 清空错误队列
 */
export function clearErrorQueue() {
  errorQueue.length = 0
}

/**
 * 导出错误日志
 */
export function exportErrorLogs() {
  const blob = new Blob([JSON.stringify(errorQueue, null, 2)], { 
    type: 'application/json' 
  })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `error-logs-${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)
}

/**
 * 全局错误边界（Vue 错误处理）
 */
export function setupGlobalErrorHandler(app = null) {
  // Vue 错误处理
  if (app) {
    app.config.errorHandler = (error, instance, info) => {
      const appError = new AppError(
        `Vue 错误：${error.message}`,
        ErrorType.UNKNOWN,
        error,
        { component: instance?.$options?.name, info }
      )
      logError(appError)
    }
  }

  // 全局未捕获错误
  window.addEventListener('error', (event) => {
    const appError = new AppError(
      event.message || '未捕获的错误',
      ErrorType.UNKNOWN,
      event.error,
      {
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno
      }
    )
    logError(appError)
  })

  // 未捕获的 Promise rejection
  window.addEventListener('unhandledrejection', (event) => {
    const appError = new AppError(
      '未处理的 Promise rejection',
      ErrorType.UNKNOWN,
      event.reason
    )
    logError(appError)
    event.preventDefault()
  })
}

/**
 * 重试函数
 */
export async function retry(fn, options = {}) {
  const {
    maxRetries = 3,
    delay = 1000,
    backoff = 2,
    onRetry = () => {}
  } = options

  let lastError
  let currentDelay = delay

  for (let i = 0; i < maxRetries; i++) {
    try {
      return await fn()
    } catch (error) {
      lastError = error
      if (i < maxRetries - 1) {
        onRetry(error, i + 1)
        await new Promise(resolve => setTimeout(resolve, currentDelay))
        currentDelay *= backoff
      }
    }
  }

  throw lastError
}
