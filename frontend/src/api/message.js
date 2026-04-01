import request from '@/utils/request'

/**
 * 消息通知 API
 */

/**
 * 获取消息列表
 * @param {Object} params - 请求参数
 * @param {number} params.userId - 用户 ID
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
export function getMessageList(params) {
  return request({
    url: '/message/list',
    method: 'get',
    params
  })
}

/**
 * 获取未读消息数量
 * @param {number} userId - 用户 ID
 */
export function getUnreadCount(userId) {
  return request({
    url: '/message/unread-count',
    method: 'get',
    params: { userId }
  })
}

/**
 * 标记消息为已读
 * @param {number} messageId - 消息 ID
 */
export function markAsRead(messageId) {
  return request({
    url: `/message/mark-read/${messageId}`,
    method: 'post'
  })
}

/**
 * 批量标记已读
 * @param {number} userId - 用户 ID
 * @param {Array} messageIds - 消息 ID 数组
 */
export function markBatchAsRead(userId, messageIds) {
  return request({
    url: '/message/mark-batch-read',
    method: 'post',
    params: { userId },
    data: messageIds
  })
}

/**
 * 删除消息
 * @param {number} messageId - 消息 ID
 * @param {number} userId - 用户 ID
 */
export function deleteMessage(messageId, userId) {
  return request({
    url: `/message/${messageId}`,
    method: 'delete',
    params: { userId }
  })
}

/**
 * 删除所有已读消息
 * @param {number} userId - 用户 ID
 */
export function deleteAllRead(userId) {
  return request({
    url: '/message/all-read',
    method: 'delete',
    params: { userId }
  })
}
