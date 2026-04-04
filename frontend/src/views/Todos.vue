<template>
  <div class="todos-container">
    <div class="todos-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-tabs v-model="filter" class="filter-tabs" @tab-change="handleFilterChange">
            <el-tab-pane :label="t('todoPage.filterAll')" name="all" />
            <el-tab-pane :label="t('todoPage.filterDone')" name="done" />
            <el-tab-pane :label="t('todoPage.filterUndone')" name="undone" />
          </el-tabs>

          <el-input
            v-model="searchKeyword"
            :placeholder="t('todo.searchPlaceholder')"
            class="search-input"
            clearable
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="toolbar-right">
          <el-button
            v-if="selectedIds.length > 0"
            type="danger"
            plain
            size="small"
            @click="handleBatchDelete"
          >
            {{ t('todoPage.batchDeleteWithCount', { count: selectedIds.length }) }}
          </el-button>
          <el-button
            v-if="selectedIds.length > 0"
            type="success"
            plain
            size="small"
            @click="handleBatchComplete"
          >
            {{ t('todoPage.batchComplete') }}
          </el-button>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            {{ t('todo.add') }}
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="filteredTodos"
        @selection-change="handleSelectionChange"
        class="todos-table"
      >
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column :label="t('todo.title')" prop="title" min-width="150">
          <template #default="{ row }">
            <span class="todo-title" :class="{ 'is-done': row.status === 1 }">
              {{ row.title }}
            </span>
          </template>
        </el-table-column>
        <el-table-column :label="t('todo.content')" prop="content" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="todo-content">{{ row.content || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('todo.priority')" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small" effect="plain">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('todo.status')" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small" effect="plain">
              {{ row.status === 1 ? t('todoPage.statusDone') : t('todoPage.statusUndone') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('todoPage.tableDueTime')" width="140" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isOverdue(row.endTime) && row.status === 0 }">
              {{ row.endTime ? formatDate(row.endTime) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column :label="t('todo.reminder')" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              v-if="row.reminderEnabled" 
              type="danger" 
              size="small" 
              effect="plain"
            >
              <el-icon><Bell /></el-icon>
              {{ getReminderText(row.reminderOffset) }}
            </el-tag>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('todoPage.tableActions')" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              link
              @click="handleComplete(row)"
            >
              {{ t('todoPage.complete') }}
            </el-button>
            <el-button
              v-else
              type="warning"
              size="small"
              link
              @click="handleUndo(row)"
            >
              {{ t('todoPage.undo') }}
            </el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button type="warning" size="small" link @click="handleSetReminder(row)">
              <el-icon><Bell /></el-icon>
              {{ row.reminderEnabled ? t('todoPage.modifyReminder') : t('todoPage.setReminder') }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && filteredTodos.length === 0" :description="t('todo.noTodos')" :image-size="80" />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? t('todoPage.dialogEdit') : t('todoPage.dialogAdd')"
      width="560px"
      @close="handleDialogClose"
      class="todo-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item :label="t('todo.title')" prop="title">
          <el-input 
            v-model="formData.title" 
            :placeholder="t('todoPage.titlePlaceholder')"
            maxlength="100"
            show-word-limit
            @blur="validateTitle"
          />
        </el-form-item>
        <el-form-item :label="t('todo.content')" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="3"
            :placeholder="t('todoPage.contentPlaceholder')"
            maxlength="500"
            show-word-limit
            @blur="validateContent"
          />
        </el-form-item>
        <el-form-item :label="t('todo.date')" prop="date">
          <el-date-picker
            v-model="formData.date"
            type="date"
            :placeholder="t('todoPage.datePlaceholder')"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('todo.priority')" prop="priority">
              <el-select 
                v-model="formData.priority" 
                :placeholder="t('todoPage.priorityPlaceholder')"
                style="width: 100%"
                @change="validatePriority"
              >
                <el-option :label="`🔴 ${t('todo.high')}`" :value="1" />
                <el-option :label="`🟡 ${t('todo.medium')}`" :value="2" />
                <el-option :label="`🟢 ${t('todo.low')}`" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('todo.status')" prop="status">
              <el-select 
                v-model="formData.status" 
                :placeholder="t('todoPage.statusPlaceholder')"
                style="width: 100%"
                @change="validateStatus"
              >
                <el-option :label="`🔵 ${t('todo.undone')}`" :value="0" />
                <el-option :label="`✅ ${t('todo.done')}`" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('todo.startTime')" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                :placeholder="t('todoPage.startTimePlaceholder')"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled-date="disabledStartDate"
                :disabled-time="disabledStartTime"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('todo.endTime')" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                :placeholder="t('todoPage.endTimePlaceholder')"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled-date="disabledEndDate"
                :disabled-time="disabledEndTime"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item :label="t('todoPage.reminderSetting')" prop="reminderEnabled">
              <el-switch
                v-model="formData.reminderEnabled"
                :active-text="t('todoPage.reminderOn')"
                :inactive-text="t('todoPage.reminderOff')"
                @change="handleReminderToggle"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="formData.reminderEnabled" :gutter="16">
          <el-col :span="24">
            <el-form-item :label="t('todo.reminderOffset')" prop="reminderOffset">
              <el-select
                v-model="formData.reminderOffset"
                :placeholder="t('todoPage.offsetPlaceholder')"
                style="width: 100%"
              >
                <el-option :label="t('todoPage.onTime')" :value="0" />
                <el-option :label="t('todoPage.offset5m')" :value="5" />
                <el-option :label="t('todoPage.offset10m')" :value="10" />
                <el-option :label="t('todoPage.offset15m')" :value="15" />
                <el-option :label="t('todoPage.offset30m')" :value="30" />
                <el-option :label="t('todoPage.reminderOffsetTextHour')" :value="60" />
                <el-option :label="t('todoPage.offset2h')" :value="120" />
                <el-option :label="t('todoPage.reminderOffsetTextDay')" :value="1440" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 提醒设置对话框 -->
    <el-dialog
      v-model="reminderDialogVisible"
      :title="currentTodo ? t('todoPage.reminderDialogTitle', { title: currentTodo.title }) : t('todoPage.reminderDialogTitleDefault')"
      width="450px"
      @close="handleReminderDialogClose"
      class="reminder-dialog"
    >
      <el-form
        ref="reminderFormRef"
        :model="reminderFormData"
        label-width="100px"
      >
        <el-form-item :label="t('todoPage.reminderSwitch')">
          <el-switch
            v-model="reminderFormData.enabled"
            :active-text="t('todo.reminderEnabled')"
            :inactive-text="t('todo.reminderDisabled')"
          />
        </el-form-item>
        <el-form-item 
          v-if="reminderFormData.enabled" 
          :label="t('todoPage.reminderOffset')"
        >
          <el-select
            v-model="reminderFormData.offset"
            :placeholder="t('todoPage.offsetPlaceholder')"
            style="width: 100%"
          >
            <el-option :label="t('todoPage.onTime')" :value="0" />
            <el-option :label="t('todoPage.offset5m')" :value="5" />
            <el-option :label="t('todoPage.offset10m')" :value="10" />
            <el-option :label="t('todoPage.offset15m')" :value="15" />
            <el-option :label="t('todoPage.offset30m')" :value="30" />
            <el-option :label="t('todoPage.reminderOffsetTextHour')" :value="60" />
            <el-option :label="t('todoPage.offset2h')" :value="120" />
            <el-option :label="t('todoPage.reminderOffsetTextDay')" :value="1440" />
          </el-select>
          <el-alert
            style="margin-top: 8px"
            type="info"
            :closable="false"
            show-icon
          >
            <template #title>
              {{ t('todoPage.reminderNotice', { offset: getReminderText(reminderFormData.offset) }) }}
            </template>
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reminderDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmitReminder" :loading="reminderSubmitLoading">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useTodoStore } from '@/stores/todo'
import { setReminder as apiSetReminder, cancelReminder } from '@/api/todo'

const authStore = useAuthStore()
const todoStore = useTodoStore()
const { t, locale } = useI18n()

const loading = ref(false)
const submitLoading = ref(false)
const filter = ref('all')
const searchKeyword = ref('')
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

// 提醒相关
const reminderDialogVisible = ref(false)
const reminderSubmitLoading = ref(false)
const reminderFormRef = ref(null)
const currentTodo = ref(null)
const sseEmitter = ref(null)

const formData = reactive({
  id: null,
  title: '',
  content: '',
  priority: 2,
  status: 0,
  date: '',
  startTime: '',
  endTime: '',
  reminderEnabled: false,
  reminderOffset: 0
})

const reminderFormData = reactive({
  enabled: false,
  offset: 0
})

const rules = {
  title: [
    { required: true, message: t('todoPage.titleRequired'), trigger: 'blur' },
    { min: 1, max: 100, message: t('todoPage.titleLength'), trigger: 'blur' }
  ],
  content: [
    { max: 500, message: t('todoPage.contentLength'), trigger: 'blur' }
  ],
  priority: [
    { required: true, message: t('todoPage.priorityRequired'), trigger: 'change' }
  ],
  status: [
    { required: true, message: t('todoPage.statusRequired'), trigger: 'change' }
  ],
  date: [
    { required: true, message: t('todoPage.dateRequired'), trigger: 'change' }
  ]
}

const filteredTodos = computed(() => {
  return todoStore.filteredTodos
})

const getPriorityType = (priority) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success' }
  return types[priority] || 'info'
}

const getPriorityText = (priority) => {
  const texts = { 1: t('todo.high'), 2: t('todo.medium'), 3: t('todo.low') }
  return texts[priority] || '-'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString(locale.value, {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const isOverdue = (dateStr) => {
  if (!dateStr) return false
  return new Date(dateStr) < new Date()
}

// ========== 日期防呆验证函数 ==========

// 禁用过去的日期（日期选择器）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000 // 不能选择昨天之前的日期
}

// 开始时间选择器 - 禁用日期
const disabledStartDate = (time) => {
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  // 不能选择今天之前的日期
  return time.getTime() < now.getTime()
}

// 结束时间选择器 - 禁用日期
const disabledEndDate = (time) => {
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  // 不能选择今天之前的日期
  if (time.getTime() < now.getTime()) {
    return true
  }
  // 如果选择了开始时间，结束时间不能早于开始时间
  if (formData.startTime) {
    const startTime = new Date(formData.startTime)
    startTime.setHours(0, 0, 0, 0)
    return time.getTime() < startTime.getTime()
  }
  return false
}

// 开始时间选择器 - 禁用时间
const disabledStartTime = (date) => {
  // 如果选择了结束时间，限制开始时间的小时和分钟
  if (formData.endTime) {
    const endDate = new Date(formData.endTime)
    const startDate = new Date(date)
    
    // 如果是同一天，开始时间的小时不能大于结束时间的小时
    if (startDate.toDateString() === endDate.toDateString()) {
      return {
        disabledHours: () => {
          const endHour = endDate.getHours()
          return Array.from({ length: 24 }, (_, i) => i).filter(h => h > endHour)
        },
        disabledMinutes: (hour) => {
          if (hour === endDate.getHours()) {
            const endMinute = endDate.getMinutes()
            return Array.from({ length: 60 }, (_, i) => i).filter(m => m > endMinute)
          }
          return []
        }
      }
    }
  }
  return {}
}

// 结束时间选择器 - 禁用时间
const disabledEndTime = (date) => {
  // 如果选择了开始时间，限制结束时间的小时和分钟
  if (formData.startTime) {
    const startDate = new Date(formData.startTime)
    const endDate = new Date(date)
    
    // 如果是同一天，结束时间的小时不能小于开始时间的小时
    if (startDate.toDateString() === endDate.toDateString()) {
      return {
        disabledHours: () => {
          const startHour = startDate.getHours()
          return Array.from({ length: 24 }, (_, i) => i).filter(h => h < startHour)
        },
        disabledMinutes: (hour) => {
          if (hour === startDate.getHours()) {
            const startMinute = startDate.getMinutes()
            return Array.from({ length: 60 }, (_, i) => i).filter(m => m < startMinute)
          }
          return []
        }
      }
    }
  }
  return {}
}

// 提交前验证时间逻辑
const validateDateTime = () => {
  const today = new Date().toISOString().split('T')[0]
  
  // 确保日期存在且不为空字符串
  if (!formData.date || formData.date === '') {
    formData.date = today
  }
  
  // 验证日期格式
  const datePattern = /^\d{4}-\d{2}-\d{2}$/
  if (!datePattern.test(formData.date)) {
    formData.date = today
  }
  
  // 验证开始时间和结束时间的逻辑关系
  if (formData.startTime && formData.endTime) {
    const start = new Date(formData.startTime)
    const end = new Date(formData.endTime)
    
    if (start > end) {
      // 自动调整：将开始时间设置为结束时间
      formData.startTime = formData.endTime
    }
  }
  
  // 确保开始时间不早于当前时间（只修正，不阻止）
  if (formData.startTime) {
    const start = new Date(formData.startTime)
    const now = new Date()
    if (start < now) {
      formData.startTime = now.toISOString().replace('T', ' ').substring(0, 19)
    }
  }
  
  // 确保截止时间不早于开始时间（只修正，不阻止）
  if (formData.startTime && formData.endTime) {
    const start = new Date(formData.startTime)
    const end = new Date(formData.endTime)
    if (end < start) {
      formData.endTime = formData.startTime
    }
  }
  
  // 最终检查：确保 date 字段是有效的日期字符串
  console.log('验证后的日期:', formData.date)
  
  return true
}

// ========== 全面输入验证 ==========

// 验证标题
const validateTitle = () => {
  if (!formData.title || !formData.title.trim()) {
    ElMessage.error(t('todoPage.titleEmpty'))
    return false
  }
  if (formData.title.length > 100) {
    ElMessage.error(t('todoPage.titleTooLong'))
    return false
  }
  // 移除首尾空格和特殊字符
  formData.title = formData.title.trim().replace(/[<>\"\'&]/g, '')
  return true
}

// 验证内容
const validateContent = () => {
  if (formData.content && formData.content.length > 500) {
    ElMessage.error(t('todoPage.contentTooLong'))
    return false
  }
  // 移除危险字符
  if (formData.content) {
    formData.content = formData.content.trim().replace(/[<>\"\'&]/g, '')
  }
  return true
}

// 验证优先级
const validatePriority = () => {
  // formData.priority 默认值为 2，检查是否为有效值
  if (![1, 2, 3].includes(formData.priority)) {
    ElMessage.error(t('todoPage.priorityInvalid'))
    formData.priority = 2 // 默认为中
    return false
  }
  return true
}

// 验证状态
const validateStatus = () => {
  // formData.status 默认值为 0，检查是否为有效值
  if (![0, 1].includes(formData.status)) {
    ElMessage.error(t('todoPage.statusInvalid'))
    formData.status = 0 // 默认为未完成
    return false
  }
  return true
}

// 全面验证所有输入
const validateAllInputs = () => {
  let isValid = true
  let hasWarning = false
  
  // 验证标题
  if (!validateTitle()) {
    isValid = false
  }
  
  // 验证内容
  if (!validateContent()) {
    isValid = false
  }
  
  // 验证优先级
  if (!validatePriority()) {
    isValid = false
  }
  
  // 验证状态
  if (!validateStatus()) {
    isValid = false
  }
  
  // 验证日期（只检查是否存在，不阻止提交）
  if (!formData.date) {
    formData.date = new Date().toISOString().split('T')[0]
    hasWarning = true
  }
  
  return isValid
}

// ================== 提醒功能相关函数 ===================

// 获取提醒文本描述
const getReminderText = (offset) => {
  if (!offset || offset === 0) return t('todoPage.onTime')
  if (offset < 60) return `${offset}m`
  if (offset === 60) return t('todoPage.reminderOffsetTextHour')
  if (offset < 1440) return `${offset / 60}h`
  return t('todoPage.reminderOffsetTextDay')
}

// 处理编辑对话框中的提醒开关切换
const handleReminderToggle = () => {
  if (!formData.reminderEnabled) {
    formData.reminderOffset = 0
  }
}

// 打开提醒设置对话框
const handleSetReminder = (row) => {
  currentTodo.value = row
  reminderFormData.enabled = row.reminderEnabled || false
  reminderFormData.offset = row.reminderOffset || 0
  reminderDialogVisible.value = true
}

// 关闭提醒设置对话框
const handleReminderDialogClose = () => {
  currentTodo.value = null
  reminderFormRef.value?.resetFields()
}

// 提交提醒设置
const handleSubmitReminder = async () => {
  if (!currentTodo.value) return

  reminderSubmitLoading.value = true
  try {
    if (reminderFormData.enabled) {
      await apiSetReminder(currentTodo.value.id, {
        enabled: true,
        offset: reminderFormData.offset
      })
      ElMessage.success(t('todoPage.reminderSaved'))
    } else {
      await cancelReminder(currentTodo.value.id)
      ElMessage.success(t('todoPage.reminderClosed'))
    }
    reminderDialogVisible.value = false
    await loadTodos()
  } catch (error) {
    console.error('设置提醒失败:', error)
    ElMessage.error(t('todoPage.reminderFailed', { message: error.response?.data?.message || error.message || t('todoPage.unknownError') }))
  } finally {
    reminderSubmitLoading.value = false
  }
}

// 连接 SSE 消息推送
const connectSSE = () => {
  const userId = authStore.userInfo?.id
  if (!userId) return

  // 关闭旧的连接
  if (sseEmitter.value) {
    sseEmitter.value.close()
  }

  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const url = `${baseURL}/api/todo/remind/stream/${userId}`
  
  sseEmitter.value = new EventSource(url)

  // 监听提醒消息
  sseEmitter.value.addEventListener('message', (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'reminder') {
        // 显示浏览器通知
        showBrowserNotification(data.title, data.message)
        // 显示应用内通知
        ElNotification({
          title: t('todo.reminder'),
          message: data.message,
          type: 'warning',
          duration: 8000,
          position: 'bottom-right'
        })
        // 重新加载列表
        loadTodos()
      }
    } catch (error) {
      console.error('解析 SSE 消息失败:', error)
    }
  })

  sseEmitter.value.onerror = (error) => {
    console.error('SSE 连接错误:', error)
    // 尝试重连
    setTimeout(() => {
      if (sseEmitter.value?.readyState === EventSource.CLOSED) {
        connectSSE()
      }
    }, 5000)
  }

  console.log('SSE 连接已建立')
}

// 显示浏览器通知
const showBrowserNotification = (title, message) => {
  if ('Notification' in window && Notification.permission === 'granted') {
    new Notification(t('todo.reminder'), {
      body: message,
      icon: '/favicon.ico',
      tag: 'todo-reminder',
      requireInteraction: true
    })
  }
}

// 请求浏览器通知权限
const requestNotificationPermission = () => {
  if (!('Notification' in window)) {
    console.warn(t('todoPage.browserNoNotification'))
    return
  }

  if (Notification.permission === 'granted') {
    return
  }

  if (Notification.permission !== 'denied') {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        ElMessage.success(t('todoPage.notificationEnabled'))
      }
    })
  }
}

// ================== 原有函数 ===================

const handleFilterChange = () => {
  todoStore.setFilter(filter.value)
}

const handleSearch = () => {
  todoStore.setSearchKeyword(searchKeyword.value)
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isEdit.value = false
  const now = new Date()
  const today = now.toISOString().split('T')[0]
  // 默认开始时间为当前时间，截止时间为 1 分钟后
  const oneMinuteLater = new Date(now.getTime() + 60000)
  
  Object.assign(formData, {
    id: null,
    title: '',
    content: '',
    priority: 2,
    status: 0,
    date: today,
    startTime: now.toISOString().replace('T', ' ').substring(0, 19),
    endTime: oneMinuteLater.toISOString().replace('T', ' ').substring(0, 19)
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, { ...row })
  dialogVisible.value = true
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  if (!formRef.value) return

  // 第一层：Element Plus 表单验证
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error(t('todoPage.submitInvalid'))
      return
    }

    // 第二层：自定义业务验证（只验证必填项）
    if (!validateAllInputs()) {
      ElMessage.error(t('todoPage.submitFixInput'))
      return
    }

    // 第三层：自动修正时间（不阻止提交）
    validateDateTime()

    submitLoading.value = true
    try {
      const data = {
        ...formData,
        userId: authStore.userInfo?.id
      }

      // 调试输出
      console.log('提交数据:', JSON.stringify(data, null, 2))

      if (isEdit.value) {
        await todoStore.updateTodo(data)
        ElMessage.success(t('todoPage.updateSuccess'))
      } else {
        await todoStore.addTodo(data)
        ElMessage.success(t('todoPage.addSuccess'))
      }

      dialogVisible.value = false
      await loadTodos()
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error(t('todoPage.operationFailed', { message: error.response?.data?.message || error.message || t('todoPage.unknownError') }))
    } finally {
      submitLoading.value = false
    }
  })
}

const handleComplete = async (row) => {
  try {
    await todoStore.updateTodo({ ...row, status: 1 })
    ElMessage.success(t('todoPage.markedDone'))
    await loadTodos()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleUndo = async (row) => {
  try {
    await todoStore.updateTodo({ ...row, status: 0 })
    ElMessage.success(t('todoPage.markedUndone'))
    await loadTodos()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(t('todoPage.deleteConfirm'), t('common.tip'), {
      type: 'warning'
    })
    await todoStore.deleteTodo(row.id)
    ElMessage.success(t('todoPage.deleteSuccess'))
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleBatchComplete = async () => {
  try {
    await todoStore.batchUpdateStatusAction(selectedIds.value, 1)
    ElMessage.success(t('todoPage.batchCompleteSuccess'))
    selectedIds.value = []
    await loadTodos()
  } catch (error) {
    console.error('批量操作失败:', error)
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(t('todoPage.batchDeleteConfirm', { count: selectedIds.value.length }), t('common.tip'), {
      type: 'warning'
    })
    await todoStore.batchDeleteAction(selectedIds.value)
    ElMessage.success(t('todoPage.batchDeleteSuccess'))
    selectedIds.value = []
    await loadTodos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

const loadTodos = async () => {
  loading.value = true
  try {
    await todoStore.fetchTodoList({ userId: authStore.userInfo?.id })
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 请求浏览器通知权限
  requestNotificationPermission()
  // 连接 SSE 消息推送
  connectSSE()
  // 加载待办列表
  loadTodos()
})

onUnmounted(() => {
  // 关闭 SSE 连接
  if (sseEmitter.value) {
    sseEmitter.value.close()
  }
})
</script>

<style lang="scss" scoped>
.todos-container {
  padding: 16px;
  height: calc(100vh - 56px);
  overflow: hidden;
  background: transparent;

  .todos-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(10px);
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    gap: 12px;
    flex-wrap: wrap;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 12px;
      flex: 1;

      .filter-tabs {
        :deep(.el-tabs__header) {
          margin-bottom: 0;
        }

        :deep(.el-tabs__item) {
          font-size: 13px;
          padding: 6px 12px;
          border-radius: 6px;
          margin-right: 4px;
          transition: all 0.3s;

          &:hover {
            background: #f1f5f9;
          }

          &.is-active {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
          }
        }
      }

      .search-input {
        width: 240px;

        :deep(.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          padding: 6px 12px;
        }
      }
    }

    .toolbar-right {
      display: flex;
      gap: 8px;
    }
  }

  .todos-table {
    flex: 1;
    overflow: auto;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 10px;
    padding: 6px;

    :deep(.el-table__header th) {
      background: rgba(248, 250, 252, 0.9);
      font-weight: 600;
      color: #475569;
      font-size: 12px;
      padding: 10px 8px;
    }

    :deep(.el-table__body td) {
      padding: 8px;
      font-size: 13px;
      background: rgba(255, 255, 255, 0.75);

      .todo-title {
        font-weight: 500;
        color: #1e293b;

        &.is-done {
          text-decoration: line-through;
          color: #94a3b8;
        }
      }

      .todo-content {
        color: #64748b;
      }
    }

    :deep(.el-table__row:hover) {
      background: #f8fafc;
    }
  }
}

.todo-dialog {
  :deep(.el-dialog__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #e2e8f0;
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 20px;
    border-top: 1px solid #e2e8f0;
  }
}

.text-danger {
  color: #ef4444;
  font-weight: 500;
}

.text-gray {
  color: #94a3b8;
}

.reminder-dialog {
  :deep(.el-dialog__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #e2e8f0;
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 20px;
    border-top: 1px solid #e2e8f0;
  }
}
</style>
