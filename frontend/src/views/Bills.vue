<template>
  <div class="bills-container">
    <div class="bills-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-tabs v-model="filter" class="filter-tabs" @tab-change="handleFilterChange">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="收入" name="income" />
            <el-tab-pane label="支出" name="expense" />
          </el-tabs>

          <el-input
            v-model="searchKeyword"
            placeholder="搜索账单..."
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
          <el-button type="primary" size="small" @click="showStatistics = true">
            <el-icon><DataAnalysis /></el-icon>
            统计分析
          </el-button>
          <el-button type="success" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加账单
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="filteredBills"
        @selection-change="handleSelectionChange"
        class="bills-table"
      >
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column label="类型" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small" effect="plain">
              {{ row.type === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="90" align="center" prop="category">
          <template #default="{ row }">
            <span class="category-text">{{ row.category }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="120" align="center" prop="amount">
          <template #default="{ row }">
            <span :class="row.type === 1 ? 'text-success' : 'text-danger'" class="amount-text">
              {{ row.type === 1 ? '+' : '-' }}¥{{ row.amount?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="日期" width="100" align="center">
          <template #default="{ row }">
            {{ row.date ? formatDate(row.date) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
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
      <el-empty v-if="!loading && filteredBills.length === 0" description="暂无账单记录" :image-size="80" />
    </div>

    <!-- 统计分析对话框 -->
    <el-dialog
      v-model="showStatistics"
      title="账单统计"
      width="800px"
      :close-on-click-modal="false"
      class="statistics-dialog"
    >
      <BillStatistics />
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑账单' : '添加账单'"
      width="520px"
      @close="handleDialogClose"
      class="bill-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="70px"
      >
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange" class="type-radio">
            <el-radio :label="1">
              <el-icon :size="16" color="#48bb78"><TrendCharts /></el-icon>
              收入
            </el-radio>
            <el-radio :label="2">
              <el-icon :size="16" color="#f56565"><Money /></el-icon>
              支出
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in currentCategories"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="formData.amount"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
            placeholder="请输入金额"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" placeholder="请输入备注（可选）" />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="formData.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
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
import { DataAnalysis, TrendCharts, Money } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useBillStore } from '@/stores/bill'
import BillStatistics from '@/components/BillStatistics.vue'

const authStore = useAuthStore()
const billStore = useBillStore()

const loading = ref(false)
const submitLoading = ref(false)
const filter = ref('all')
const searchKeyword = ref('')
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const showStatistics = ref(false)

const incomeCategories = ['工资收入', '奖金收入', '投资收益', '其他收入']
const expenseCategories = ['餐饮', '购物', '交通', '住宿', '娱乐', '医疗', '教育', '其他支出']

const currentCategories = computed(() => {
  return formData.type === 1 ? incomeCategories : expenseCategories
})

const formData = reactive({
  id: null,
  type: 2,
  category: '餐饮',
  amount: 0,
  remark: '',
  date: new Date().toISOString().split('T')[0]
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const filteredBills = computed(() => {
  return billStore.filteredBills
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit'
  })
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleFilterChange = () => {
  billStore.setFilter(filter.value)
}

const handleSearch = () => {
  billStore.setSearchKeyword(searchKeyword.value)
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleTypeChange = () => {
  formData.category = currentCategories.value[0]
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    type: 2,
    category: '餐饮',
    amount: 0,
    remark: '',
    date: new Date().toISOString().split('T')[0]
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
        await billStore.updateBill(data)
        ElMessage.success('更新成功')
      } else {
        await billStore.addBill(data)
        ElMessage.success('添加成功')
      }

      dialogVisible.value = false
      await loadBills()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该账单吗？', '提示', {
      type: 'warning'
    })
    await billStore.deleteBill(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 项账单吗？`, '提示', {
      type: 'warning'
    })
    await billStore.batchDeleteAction(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    await loadBills()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

const loadBills = async () => {
  loading.value = true
  try {
    await billStore.fetchBillList({ userId: authStore.userInfo?.id })
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBills()
})
</script>

<style lang="scss" scoped>
.bills-container {
  padding: 16px;
  height: calc(100vh - 56px);
  overflow: hidden;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);

  .bills-card {
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
            background: linear-gradient(135deg, #10b981, #059669);
            color: white;
          }
        }
      }

      .search-input {
        width: 200px;

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

  .bills-table {
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

      .category-text {
        color: #64748b;
      }

      .amount-text {
        font-weight: 600;
        font-size: 14px;
      }

      .remark-text {
        color: #94a3b8;
      }
    }

    :deep(.el-table__row:hover) {
      background: #f8fafc;
    }
  }
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.type-radio {
  :deep(.el-radio) {
    margin-right: 20px;
    font-weight: 500;

    .el-radio__label {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.statistics-dialog {
  :deep(.el-dialog__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #e2e8f0;
  }

  :deep(.el-dialog__body) {
    padding: 20px;
    max-height: 60vh;
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    padding: 12px 20px;
    border-top: 1px solid #e2e8f0;
  }
}

.bill-dialog {
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
