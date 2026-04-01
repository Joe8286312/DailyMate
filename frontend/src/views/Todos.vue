<template>
  <div class="todos-container">
    <div class="todos-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-tabs v-model="filter" class="filter-tabs" @tab-change="handleFilterChange">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="已完成" name="done" />
            <el-tab-pane label="未完成" name="undone" />
          </el-tabs>

          <el-input
            v-model="searchKeyword"
            placeholder="搜索待办事项..."
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
            批量删除 ({{ selectedIds.length }})
          </el-button>
          <el-button
            v-if="selectedIds.length > 0"
            type="success"
            plain
            size="small"
            @click="handleBatchComplete"
          >
            批量完成
          </el-button>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加待办
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
        <el-table-column label="标题" prop="title" min-width="150">
          <template #default="{ row }">
            <span class="todo-title" :class="{ 'is-done': row.status === 1 }">
              {{ row.title }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="内容" prop="content" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="todo-content">{{ row.content || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small" effect="plain">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small" effect="plain">
              {{ row.status === 1 ? '✓ 完成' : '○ 未完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="截止时间" width="140" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isOverdue(row.endTime) && row.status === 0 }">
              {{ row.endTime ? formatDate(row.endTime) : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              link
              @click="handleComplete(row)"
            >
              完成
            </el-button>
            <el-button
              v-else
              type="warning"
              size="small"
              link
              @click="handleUndo(row)"
            >
              撤销
            </el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && filteredTodos.length === 0" description="暂无待办事项" :image-size="80" />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑待办' : '添加待办'"
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="3"
            placeholder="请输入内容（可选）"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="formData.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="高" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="低" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="未完成" :value="0" />
                <el-option label="已完成" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择截止时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useTodoStore } from '@/stores/todo'

const authStore = useAuthStore()
const todoStore = useTodoStore()

const loading = ref(false)
const submitLoading = ref(false)
const filter = ref('all')
const searchKeyword = ref('')
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  title: '',
  content: '',
  priority: 2,
  status: 0,
  startTime: '',
  endTime: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

const filteredTodos = computed(() => {
  return todoStore.filteredTodos
})

const getPriorityType = (priority) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success' }
  return types[priority] || 'info'
}

const getPriorityText = (priority) => {
  const texts = { 1: '高', 2: '中', 3: '低' }
  return texts[priority] || '未知'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
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
  Object.assign(formData, {
    id: null,
    title: '',
    content: '',
    priority: 2,
    status: 0,
    startTime: '',
    endTime: ''
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

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const data = {
        ...formData,
        userId: authStore.userInfo?.id
      }

      if (isEdit.value) {
        await todoStore.updateTodo(data)
        ElMessage.success('更新成功')
      } else {
        await todoStore.addTodo(data)
        ElMessage.success('添加成功')
      }

      dialogVisible.value = false
      await loadTodos()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleComplete = async (row) => {
  try {
    await todoStore.updateTodo({ ...row, status: 1 })
    ElMessage.success('已标记为完成')
    await loadTodos()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleUndo = async (row) => {
  try {
    await todoStore.updateTodo({ ...row, status: 0 })
    ElMessage.success('已标记为未完成')
    await loadTodos()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该待办事项吗？', '提示', {
      type: 'warning'
    })
    await todoStore.deleteTodo(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleBatchComplete = async () => {
  try {
    await todoStore.batchUpdateStatusAction(selectedIds.value, 1)
    ElMessage.success('批量完成成功')
    selectedIds.value = []
    await loadTodos()
  } catch (error) {
    console.error('批量操作失败:', error)
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 项待办事项吗？`, '提示', {
      type: 'warning'
    })
    await todoStore.batchDeleteAction(selectedIds.value)
    ElMessage.success('批量删除成功')
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
  loadTodos()
})
</script>

<style lang="scss" scoped>
.todos-container {
  padding: 16px;
  height: calc(100vh - 56px);
  overflow: hidden;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);

  .todos-card {
    background: white;
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

    :deep(.el-table__header th) {
      background: #f8fafc;
      font-weight: 600;
      color: #475569;
      font-size: 12px;
      padding: 10px 8px;
    }

    :deep(.el-table__body td) {
      padding: 8px;
      font-size: 13px;

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
</style>
