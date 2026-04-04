<template>
  <el-drawer
    v-model="visible"
    title="消息通知"
    size="400px"
    @opened="onOpened"
    @closed="onClosed"
  >
    <div class="message-drawer">
      <!-- 头部操作 -->
      <div class="drawer-header">
        <span class="unread-count" v-if="unreadCount > 0">
          未读消息 ({{ unreadCount }})
        </span>
        <el-button
          v-if="unreadCount > 0"
          type="primary"
          link
          size="small"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
        <el-button
          v-if="hasReadMessages"
          type="danger"
          link
          size="small"
          @click="handleDeleteAllRead"
        >
          清空已读
        </el-button>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="{ 'is-unread': message.isRead === 0 }"
          @click="handleMessageClick(message)"
        >
          <div class="message-icon">
            <el-icon v-if="message.type === 1" color="#409EFF"><Bell /></el-icon>
            <el-icon v-else-if="message.type === 2" color="#67C23A"><List /></el-icon>
            <el-icon v-else-if="message.type === 3" color="#F56C6C"><Money /></el-icon>
            <el-icon v-else color="#909399"><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-title">{{ message.title }}</div>
            <div class="message-text">{{ message.content }}</div>
            <div class="message-meta">
              <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              <el-tag v-if="message.isRead === 0" size="small" type="primary">未读</el-tag>
            </div>
          </div>
          <div class="message-actions">
            <el-button
              v-if="message.isRead === 0"
              type="primary"
              link
              size="small"
              @click.stop="handleMarkRead(message)"
            >
              已读
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click.stop="handleDelete(message)"
            >
              删除
            </el-button>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty
          v-if="!loading && messages.length === 0"
          description="暂无消息"
          :image-size="80"
        />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-more">
        <el-skeleton :rows="3" animated />
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, List, Money, ChatDotRound } from '@element-plus/icons-vue'
import { useMessageStore } from '@/stores/message'
import { useAuthStore } from '@/stores/auth'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import {
  requestNotificationPermission,
  checkNotificationPermission,
  sendMessageNotification
} from '@/utils/notification'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const messageStore = useMessageStore()
const authStore = useAuthStore()

const visible = ref(false)
const loading = ref(false)
const lastMessageId = ref(null)

const messages = computed(() => messageStore.messages)
const unreadCount = computed(() => messageStore.unreadCount)
const hasReadMessages = computed(() => messages.value.some(m => m.isRead === 1))

// 监听未读消息数量变化，有新消息时发送系统通知
watch(unreadCount, (newCount, oldCount) => {
  if (newCount > oldCount && newCount > 0) {
    // 有新消息
    handleNewMessage()
  }
})

// 请求通知权限
const initNotification = async () => {
  const permission = checkNotificationPermission()
  if (permission === 'default') {
    await requestNotificationPermission()
  }
}

// 处理新消息
const handleNewMessage = async () => {
  // 初始化通知权限
  await initNotification()

  // 获取最新消息
  const newMessages = messages.value.filter(m => m.id !== lastMessageId.value)
  if (newMessages.length > 0) {
    const latestMessage = newMessages[0]
    lastMessageId.value = latestMessage.id

    // 发送系统通知
    sendMessageNotification(latestMessage)
  }
}

// 打开抽屉
const open = () => {
  visible.value = true
}

// 关闭抽屉
const close = () => {
  visible.value = false
}

// 抽屉打开后
const onOpened = async () => {
  loading.value = true
  await messageStore.fetchMessages(authStore.userInfo?.id, 0, 50)
  loading.value = false
  
  // 请求通知权限
  await initNotification()
}

// 抽屉关闭后
const onClosed = () => {
  // 可以在这里做一些清理工作
}

// 消息点击
const handleMessageClick = (message) => {
  if (message.isRead === 0) {
    handleMarkRead(message)
  }
}

// 标记已读
const handleMarkRead = async (message) => {
  try {
    await messageStore.markMessageAsRead(message.id)
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记失败:', error)
  }
}

// 全部已读
const handleMarkAllRead = async () => {
  try {
    const unreadIds = messages.value.filter(m => m.isRead === 0).map(m => m.id)
    await messageStore.markMessagesBatchAsRead(authStore.userInfo?.id, unreadIds)
    ElMessage.success('全部标记为已读')
  } catch (error) {
    console.error('批量标记失败:', error)
  }
}

// 删除消息
const handleDelete = async (message) => {
  try {
    await ElMessageBox.confirm('确定要删除该消息吗？', '提示', {
      type: 'warning'
    })
    await messageStore.removeMessage(message.id, authStore.userInfo?.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 删除所有已读
const handleDeleteAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有已读消息吗？', '提示', {
      type: 'warning'
    })
    await messageStore.removeAllRead(authStore.userInfo?.id)
    ElMessage.success('清空成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const time = dayjs(timeStr)
  const now = dayjs()

  if (now.diff(time, 'day') === 0) {
    return time.format('HH:mm')
  } else if (now.diff(time, 'day') < 7) {
    return time.fromNow()
  } else {
    return time.format('MM-DD HH:mm')
  }
}

// 暴露方法
defineExpose({
  open,
  close
})
</script>

<style lang="scss" scoped>
.message-drawer {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;

  .unread-count {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.message-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid #f7f7f7;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #f8f9fa;
  }

  &.is-unread {
    background: #f0f9ff;

    &:hover {
      background: #e6f7ff;
    }

    .message-title {
      font-weight: 600;
    }
  }

  .message-icon {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    border-radius: 50%;
  }

  .message-content {
    flex: 1;
    min-width: 0;

    .message-title {
      font-size: 14px;
      color: #303133;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .message-text {
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
      display: -webkit-box;
      line-clamp: 2;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .message-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .message-time {
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .message-actions {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
}

.loading-more {
  padding: 16px;
}
</style>
