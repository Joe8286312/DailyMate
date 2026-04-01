<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <el-icon :size="28"><DataLine /></el-icon>
          </div>
          <span v-show="!isCollapse" class="logo-text">DailyMate</span>
        </div>

        <el-menu
          :default-active="activeMenu"
          background-color="transparent"
          text-color="#94a3b8"
          active-text-color="#6366f1"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="side-menu"
        >
          <el-menu-item index="/dashboard" class="menu-item">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <el-menu-item index="/todos" class="menu-item">
            <el-icon><List /></el-icon>
            <template #title>待办事项</template>
          </el-menu-item>

          <el-menu-item index="/bills" class="menu-item">
            <el-icon><Money /></el-icon>
            <template #title>账单管理</template>
          </el-menu-item>
        </el-menu>

        <div class="sidebar-footer">
          <el-button
            link
            class="collapse-btn"
            @click="toggleCollapse"
          >
            <el-icon :size="18">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
            <span v-show="!isCollapse">收起</span>
          </el-button>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <h1 class="page-title">{{ pageTitle }}</h1>
          </div>

          <div class="header-right">
            <!-- 消息通知 -->
            <div class="message-btn-wrapper" @click="openMessageDrawer">
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
                <el-button link class="message-btn">
                  <el-icon :size="20"><Bell /></el-icon>
                </el-button>
              </el-badge>
            </div>

            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32" :style="{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
                  <span style="font-size: 14px; font-weight: 600;">{{ username.charAt(0).toUpperCase() }}</span>
                </el-avatar>
                <span class="username">{{ username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="notification">
                    <el-icon><Bell /></el-icon>
                    通知设置
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>

    <!-- 消息通知抽屉 -->
    <MessageDrawer ref="messageDrawerRef" />

    <!-- 通知设置 -->
    <NotificationSettings ref="notificationSettingsRef" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useMessageStore } from '@/stores/message'
import MessageDrawer from '@/components/MessageDrawer.vue'
import NotificationSettings from '@/components/NotificationSettings.vue'
import { sendSystemNotification } from '@/utils/notification'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const messageStore = useMessageStore()

const isCollapse = ref(false)
const messageDrawerRef = ref(null)
const notificationSettingsRef = ref(null)

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || 'DailyMate')
const username = computed(() => authStore.username)
const unreadCount = computed(() => messageStore.unreadCount)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const openMessageDrawer = () => {
  messageDrawerRef.value?.open()
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await authStore.logout()
      router.push('/login')
    } catch {
      // 取消退出
    }
  } else if (command === 'profile') {
    ElMessage.info('功能开发中...')
  } else if (command === 'notification') {
    notificationSettingsRef.value?.open()
  }
}

// 定时获取未读消息数量
let pollTimer = null
let lastUnreadCount = 0

const startPolling = () => {
  // 先获取一次
  if (authStore.userInfo?.id) {
    messageStore.fetchUnreadCount(authStore.userInfo.id)
    lastUnreadCount = messageStore.unreadCount
  }

  pollTimer = setInterval(() => {
    if (authStore.userInfo?.id) {
      const prevCount = messageStore.unreadCount
      messageStore.fetchUnreadCount(authStore.userInfo.id)

      // 检查是否有新消息
      setTimeout(() => {
        const newCount = messageStore.unreadCount
        if (newCount > prevCount && newCount > 0) {
          // 有新消息，发送系统通知
          sendSystemNotification({
            title: '📬 新消息提醒',
            body: `您有 ${newCount - prevCount} 条新消息，请及时查看`,
            onClick: () => {
              messageDrawerRef.value?.open()
            }
          })
        }
        lastUnreadCount = newCount
      }, 500)
    }
  }, 30000) // 每 30 秒刷新一次
}

onMounted(() => {
  if (authStore.userInfo?.id) {
    messageStore.fetchUnreadCount(authStore.userInfo.id)
  }
  startPolling()
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
}

.el-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.1);

  .logo-wrapper {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .logo-icon {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 10px;
      color: white;
    }

    .logo-text {
      font-size: 18px;
      font-weight: 700;
      color: white;
      white-space: nowrap;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .side-menu {
    flex: 1;
    border-right: none;
    padding: 12px 8px;

    .menu-item {
      height: 48px;
      margin: 4px 0;
      border-radius: 8px;
      color: #94a3b8;
      transition: all 0.3s;

      &:hover {
        background: rgba(99, 102, 241, 0.1);
        color: #6366f1;
      }

      &.is-active {
        background: linear-gradient(90deg, rgba(99, 102, 241, 0.2), rgba(99, 102, 241, 0.05));
        color: #6366f1;
        font-weight: 600;
      }

      .el-icon {
        margin-right: 8px;
      }
    }
  }

  .sidebar-footer {
    padding: 12px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    .collapse-btn {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: #64748b;
      padding: 8px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        background: rgba(99, 102, 241, 0.1);
        color: #6366f1;
      }

      span {
        font-size: 12px;
      }
    }
  }
}

.header {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .header-left {
    .page-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .message-btn-wrapper {
      cursor: pointer;
      display: flex;
      align-items: center;

      .message-btn {
        color: #64748b;
        padding: 8px;
        border-radius: 50%;
        transition: all 0.3s;

        &:hover {
          background-color: #f1f5f9;
          color: #6366f1;
        }
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 6px 12px;
      border-radius: 10px;
      transition: all 0.3s;

      &:hover {
        background-color: rgba(99, 102, 241, 0.1);
      }

      .username {
        font-size: 13px;
        color: #475569;
        font-weight: 500;
      }
    }
  }
}

.main-content {
  padding: 0;
  overflow: hidden;
  background: transparent;
}

// 页面过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
