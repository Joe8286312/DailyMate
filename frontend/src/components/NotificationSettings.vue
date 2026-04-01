<template>
  <el-dialog
    v-model="visible"
    title="通知设置"
    width="500px"
    :close-on-click-modal="false"
  >
    <div class="notification-settings">
      <!-- 系统通知开关 -->
      <el-alert
        title="系统通知"
        description="开启后，新消息将通过 Windows 系统通知提醒您"
        type="info"
        :closable="false"
        show-icon
        class="mb-4"
      />

      <div class="setting-item">
        <div class="setting-label">
          <el-icon><Bell /></el-icon>
          <span>桌面通知</span>
        </div>
        <el-switch
          v-model="enableNotification"
          @change="handleNotificationChange"
        />
      </div>

      <!-- 通知权限状态 -->
      <div class="permission-status">
        <el-tag :type="permissionTagType">
          <el-icon><InfoFilled /></el-icon>
          {{ permissionText }}
        </el-tag>
      </div>

      <!-- 通知类型设置 -->
      <el-divider>通知类型</el-divider>

      <div class="setting-item">
        <div class="setting-label">
          <el-icon><List /></el-icon>
          <span>待办事项提醒</span>
        </div>
        <el-switch v-model="enableTodoNotification" />
      </div>

      <div class="setting-item">
        <div class="setting-label">
          <el-icon><Bell /></el-icon>
          <span>DDL 截止提醒</span>
          <el-tooltip placement="top">
            <template #content>
              将在截止前 1 天、12 小时、6 小时、2 小时、1 小时、5 分钟提醒
            </template>
            <el-icon class="ml-1"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
        <el-switch v-model="enableDdlReminder" />
      </div>

      <div class="setting-item">
        <div class="setting-label">
          <el-icon><Money /></el-icon>
          <span>账单提醒</span>
        </div>
        <el-switch v-model="enableBillNotification" />
      </div>

      <div class="setting-item">
        <div class="setting-label">
          <el-icon><ChatDotRound /></el-icon>
          <span>系统消息</span>
        </div>
        <el-switch v-model="enableSystemNotification" />
      </div>

      <!-- 测试通知 -->
      <el-divider>测试</el-divider>

      <el-button type="primary" @click="handleTestNotification" class="w-100">
        <el-icon><Bell /></el-icon>
        发送测试通知
      </el-button>

      <el-alert
        v-if="!isSupported"
        title="您的浏览器不支持系统通知功能"
        type="warning"
        :closable="false"
        show-icon
        class="mt-4"
      />
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="handleSave">保存设置</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, List, Money, ChatDotRound, InfoFilled, QuestionFilled } from '@element-plus/icons-vue'
import {
  requestNotificationPermission,
  checkNotificationPermission,
  sendSystemNotification
} from '@/utils/notification'

const visible = ref(false)
const isSupported = ref(true)
const enableNotification = ref(true)
const enableTodoNotification = ref(true)
const enableDdlReminder = ref(true)
const enableBillNotification = ref(true)
const enableSystemNotification = ref(true)
const permission = ref('default')

const permissionTagType = computed(() => {
  const types = {
    granted: 'success',
    denied: 'danger',
    default: 'warning',
    unsupported: 'info'
  }
  return types[permission.value] || 'info'
})

const permissionText = computed(() => {
  const texts = {
    granted: '已允许通知',
    denied: '已拒绝通知',
    default: '未设置通知',
    unsupported: '浏览器不支持'
  }
  return texts[permission.value] || '未知状态'
})

// 打开设置
const open = () => {
  visible.value = true
  checkPermission()
}

// 关闭设置
const close = () => {
  visible.value = false
}

// 检查通知权限
const checkPermission = () => {
  if (!('Notification' in window)) {
    isSupported.value = false
    permission.value = 'unsupported'
    return
  }

  isSupported.value = true
  permission.value = checkNotificationPermission()
}

// 通知开关变化
const handleNotificationChange = async (value) => {
  if (value) {
    const granted = await requestNotificationPermission()
    if (!granted) {
      enableNotification.value = false
      ElMessage.warning('请允许通知权限')
    } else {
      ElMessage.success('已开启通知')
    }
  } else {
    ElMessage.info('已关闭通知')
  }
}

// 测试通知
const handleTestNotification = async () => {
  if (!isSupported.value) {
    ElMessage.warning('浏览器不支持通知')
    return
  }

  const granted = await requestNotificationPermission()
  if (!granted) {
    ElMessage.warning('请先允许通知权限')
    return
  }

  sendSystemNotification({
    title: '✅ 测试通知',
    body: 'DailyMate 通知功能正常工作！',
    onClick: () => {
      ElMessage.success('通知已点击')
    }
  })
}

// 保存设置
const handleSave = () => {
  // 保存设置到 localStorage
  localStorage.setItem('dailymate_notification_settings', JSON.stringify({
    enableNotification: enableNotification.value,
    enableTodoNotification: enableTodoNotification.value,
    enableDdlReminder: enableDdlReminder.value,
    enableBillNotification: enableBillNotification.value,
    enableSystemNotification: enableSystemNotification.value
  }))

  ElMessage.success('设置已保存')
  visible.value = false
}

// 加载设置
const loadSettings = () => {
  const settings = localStorage.getItem('dailymate_notification_settings')
  if (settings) {
    const parsed = JSON.parse(settings)
    enableNotification.value = parsed.enableNotification ?? true
    enableTodoNotification.value = parsed.enableTodoNotification ?? true
    enableDdlReminder.value = parsed.enableDdlReminder ?? true
    enableBillNotification.value = parsed.enableBillNotification ?? true
    enableSystemNotification.value = parsed.enableSystemNotification ?? true
  }
}

onMounted(() => {
  loadSettings()
  checkPermission()
})

defineExpose({
  open,
  close
})
</script>

<style lang="scss" scoped>
.notification-settings {
  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .setting-label {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      color: #303133;

      .el-icon {
        color: #606266;
      }
    }
  }

  .permission-status {
    margin: 16px 0;
    text-align: center;
  }

  .w-100 {
    width: 100%;
  }

  .mb-4 {
    margin-bottom: 16px;
  }

  .mt-4 {
    margin-top: 16px;
  }
}
</style>
