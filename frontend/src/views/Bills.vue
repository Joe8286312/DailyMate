<template>
  <div class="bills-container">
    <div class="bills-card">
      <!-- 操作栏 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-tabs v-model="filter" class="filter-tabs" @tab-change="handleFilterChange">
            <el-tab-pane :label="t('billPage.filterAll')" name="all" />
            <el-tab-pane :label="t('billPage.filterIncome')" name="income" />
            <el-tab-pane :label="t('billPage.filterExpense')" name="expense" />
          </el-tabs>

          <el-input
            v-model="searchKeyword"
            :placeholder="t('bill.searchPlaceholder')"
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
            {{ t('billPage.batchDeleteWithCount', { count: selectedIds.length }) }}
          </el-button>
          <el-button type="primary" size="small" @click="showStatistics = true">
            <el-icon><DataAnalysis /></el-icon>
            {{ t('billPage.stats') }}
          </el-button>
          <el-button type="success" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            {{ t('bill.add') }}
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
        <el-table-column :label="t('billPage.tableType')" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small" effect="plain">
              {{ row.type === 1 ? t('billPage.typeIncome') : t('billPage.typeExpense') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('billPage.tableCategory')" width="90" align="center" prop="category">
          <template #default="{ row }">
            <span class="category-text">{{ row.category }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('billPage.tableAmount')" width="120" align="center" prop="amount">
          <template #default="{ row }">
            <span :class="row.type === 1 ? 'text-success' : 'text-danger'" class="amount-text">
              {{ row.type === 1 ? '+' : '-' }}¥{{ row.amount?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column :label="t('billPage.tableRemark')" prop="remark" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="t('billPage.tableDate')" width="100" align="center">
          <template #default="{ row }">
            {{ row.date ? formatDate(row.date) : '-' }}
          </template>
        </el-table-column>
        <el-table-column :label="t('billPage.tableActions')" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && filteredBills.length === 0" :description="t('bill.noBills')" :image-size="80" />
    </div>

    <!-- 统计分析对话框 -->
    <el-dialog
      v-model="showStatistics"
      :title="t('billPage.dialogStatsTitle')"
      width="800px"
      :close-on-click-modal="false"
      class="statistics-dialog"
    >
      <BillStatistics />
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? t('billPage.dialogEdit') : t('billPage.dialogAdd')"
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
        <el-form-item :label="t('bill.type')" prop="type">
          <el-radio-group v-model="formData.type" @change="handleTypeChange" class="type-radio">
            <el-radio :label="1">
              <el-icon :size="16" color="#48bb78"><TrendCharts /></el-icon>
              {{ t('billPage.typeIncome') }}
            </el-radio>
            <el-radio :label="2">
              <el-icon :size="16" color="#f56565"><Money /></el-icon>
              {{ t('billPage.typeExpense') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item :label="t('bill.category')" prop="category">
          <el-select v-model="formData.category" :placeholder="t('billPage.categoryPlaceholder')" style="width: 100%">
            <el-option
              v-for="item in currentCategories"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('bill.amount')" prop="amount">
          <el-input-number
            v-model="formData.amount"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
            :placeholder="t('billPage.amountPlaceholder')"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item :label="t('billPage.tableRemark')" prop="remark">
          <el-input v-model="formData.remark" :placeholder="t('billPage.remarkPlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('bill.date')" prop="date">
          <el-date-picker
            v-model="formData.date"
            type="date"
            :placeholder="t('billPage.datePlaceholder')"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, TrendCharts, Money } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useBillStore } from '@/stores/bill'
import BillStatistics from '@/components/BillStatistics.vue'

const authStore = useAuthStore()
const billStore = useBillStore()
const { t, locale } = useI18n()

const loading = ref(false)
const submitLoading = ref(false)
const filter = ref('all')
const searchKeyword = ref('')
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const showStatistics = ref(false)

const incomeCategories = computed(() => t('billPage.incomeCategories'))
const expenseCategories = computed(() => t('billPage.expenseCategories'))

const currentCategories = computed(() => {
  return formData.type === 1 ? incomeCategories.value : expenseCategories.value
})

const formData = reactive({
  id: null,
  type: 2,
  category: expenseCategories.value[0],
  amount: 0,
  remark: '',
  date: new Date().toISOString().split('T')[0]
})

const rules = {
  type: [{ required: true, message: t('billPage.typeRequired'), trigger: 'change' }],
  category: [{ required: true, message: t('billPage.categoryRequired'), trigger: 'change' }],
  amount: [{ required: true, message: t('billPage.amountRequired'), trigger: 'blur' }],
  date: [{ required: true, message: t('billPage.dateRequired'), trigger: 'change' }]
}

const filteredBills = computed(() => {
  return billStore.filteredBills
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString(locale.value, {
    month: '2-digit',
    day: '2-digit'
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
    category: expenseCategories.value[0],
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
        ElMessage.success(t('billPage.updateSuccess'))
      } else {
        await billStore.addBill(data)
        ElMessage.success(t('billPage.addSuccess'))
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
    await ElMessageBox.confirm(t('billPage.deleteConfirm'), t('common.tip'), {
      type: 'warning'
    })
    await billStore.deleteBill(row.id)
    ElMessage.success(t('billPage.deleteSuccess'))
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(t('billPage.batchDeleteConfirm', { count: selectedIds.value.length }), t('common.tip'), {
      type: 'warning'
    })
    await billStore.batchDeleteAction(selectedIds.value)
    ElMessage.success(t('billPage.batchDeleteSuccess'))
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
  background: transparent;

  .bills-card {
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
