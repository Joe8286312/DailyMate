import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getMessageList,
  getUnreadCount,
  markAsRead,
  markBatchAsRead,
  deleteMessage,
  deleteAllRead
} from '@/api/message'

export const useMessageStore = defineStore('message', () => {
  // 状态
  const messages = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const total = ref(0)

  // 计算属性
  const hasUnread = computed(() => unreadCount.value > 0)

  // 方法
  /**
   * 获取消息列表
   */
  async function fetchMessages(userId, page = 0, size = 20) {
    loading.value = true
    try {
      const res = await getMessageList({ userId, page, size })
      messages.value = res.data?.list || res.data || []
      total.value = res.data?.total || 0
    } catch (error) {
      console.error('获取消息失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取未读消息数量
   */
  async function fetchUnreadCount(userId) {
    try {
      const res = await getUnreadCount(userId)
      unreadCount.value = res.data?.count || 0
    } catch (error) {
      console.error('获取未读数量失败:', error)
    }
  }

  /**
   * 标记消息为已读
   */
  async function markMessageAsRead(messageId) {
    try {
      await markAsRead(messageId)
      // 更新本地状态
      const message = messages.value.find(m => m.id === messageId)
      if (message) {
        message.isRead = 1
      }
      // 重新获取未读数量
      await fetchUnreadCount(message.userId)
    } catch (error) {
      console.error('标记已读失败:', error)
      throw error
    }
  }

  /**
   * 批量标记已读
   */
  async function markMessagesBatchAsRead(userId, messageIds) {
    try {
      await markBatchAsRead(userId, messageIds)
      // 更新本地状态
      messages.value.forEach(m => {
        if (messageIds.includes(m.id)) {
          m.isRead = 1
        }
      })
      // 重新获取未读数量
      await fetchUnreadCount(userId)
    } catch (error) {
      console.error('批量标记失败:', error)
      throw error
    }
  }

  /**
   * 删除消息
   */
  async function removeMessage(messageId, userId) {
    try {
      await deleteMessage(messageId, userId)
      // 更新本地状态
      messages.value = messages.value.filter(m => m.id !== messageId)
      // 重新获取未读数量
      await fetchUnreadCount(userId)
    } catch (error) {
      console.error('删除消息失败:', error)
      throw error
    }
  }

  /**
   * 删除所有已读消息
   */
  async function removeAllRead(userId) {
    try {
      await deleteAllRead(userId)
      // 清空已读消息
      messages.value = messages.value.filter(m => m.isRead === 0)
      // 未读数量清零
      unreadCount.value = 0
    } catch (error) {
      console.error('删除已读消息失败:', error)
      throw error
    }
  }

  /**
   * 重置状态
   */
  function reset() {
    messages.value = []
    unreadCount.value = 0
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    messages,
    unreadCount,
    loading,
    total,
    // 计算属性
    hasUnread,
    // 方法
    fetchMessages,
    fetchUnreadCount,
    markMessageAsRead,
    markMessagesBatchAsRead,
    removeMessage,
    removeAllRead,
    reset
  }
})
