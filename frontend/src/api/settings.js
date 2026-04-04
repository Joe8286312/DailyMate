import request from '@/utils/request'

/**
 * 获取用户设置
 */
export function getSettings (userId) {
  return request({
    url: '/settings',
    method: 'get',
    params: { userId }
  })
}

/**
 * 更新用户设置
 */
export function updateSettings (userId, data) {
  return request({
    url: '/settings',
    method: 'put',
    params: { userId },
    data
  })
}

/**
 * 重置用户设置
 */
export function resetSettings (userId) {
  return request({
    url: '/settings/reset',
    method: 'post',
    params: { userId }
  })
}
