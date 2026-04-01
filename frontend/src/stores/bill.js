import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getBillList as getBillListApi,
  addBill as addBillApi,
  updateBill as updateBillApi,
  deleteBill as deleteBillApi,
  batchDeleteBill as batchDeleteBillApi,
  statMonth as statMonthApi,
  statCategory as statCategoryApi,
  statTrend as statTrendApi
} from '@/api/bill'

export const useBillStore = defineStore('bill', () => {
  // 状态
  const bills = ref([])
  const loading = ref(false)
  const filter = ref('all') // all, income, expense
  const searchKeyword = ref('')
  const monthStats = ref({ income: 0, expense: 0 })

  // 计算属性
  const filteredBills = computed(() => {
    let result = [...bills.value]
    
    // 按筛选条件过滤
    if (filter.value === 'income') {
      result = result.filter(bill => bill.type === 1)
    } else if (filter.value === 'expense') {
      result = result.filter(bill => bill.type === 2)
    }
    
    // 按关键字搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      result = result.filter(bill =>
        bill.category?.toLowerCase().includes(keyword) ||
        bill.remark?.toLowerCase().includes(keyword) ||
        bill.amount?.toString().includes(keyword)
      )
    }
    
    return result
  })

  const totalCount = computed(() => bills.value.length)
  const incomeCount = computed(() => bills.value.filter(b => b.type === 1).length)
  const expenseCount = computed(() => bills.value.filter(b => b.type === 2).length)
  const totalIncome = computed(() => bills.value.filter(b => b.type === 1).reduce((sum, b) => sum + (b.amount || 0), 0))
  const totalExpense = computed(() => bills.value.filter(b => b.type === 2).reduce((sum, b) => sum + (b.amount || 0), 0))

  // 方法
  /**
   * 获取账单列表
   */
  async function fetchBillList(params) {
    loading.value = true
    try {
      const res = await getBillListApi(params)
      bills.value = res.data || res || []
    } catch (error) {
      console.error('获取账单列表失败:', error)
      bills.value = []
    } finally {
      loading.value = false
    }
  }

  /**
   * 添加账单
   */
  async function addBill(data) {
    const res = await addBillApi(data)
    const newBill = res.data || res
    if (newBill) {
      bills.value.unshift(newBill)
    }
    return newBill
  }

  /**
   * 更新账单
   */
  async function updateBill(data) {
    const res = await updateBillApi(data)
    const updatedBill = res.data || res
    if (updatedBill) {
      const index = bills.value.findIndex(b => b.id === updatedBill.id)
      if (index !== -1) {
        bills.value[index] = updatedBill
      }
    }
    return updatedBill
  }

  /**
   * 删除账单
   */
  async function deleteBill(id) {
    await deleteBillApi(id)
    bills.value = bills.value.filter(b => b.id !== id)
  }

  /**
   * 批量删除
   */
  async function batchDeleteAction(ids) {
    await batchDeleteBillApi({ ids })
    bills.value = bills.value.filter(b => !ids.includes(b.id))
  }

  /**
   * 获取月度统计
   */
  async function fetchMonthStats(userId, year, month) {
    try {
      const res = await statMonthApi({ userId, year, month })
      const data = res.data || res
      monthStats.value = {
        income: data.income || 0,
        expense: data.expense || 0
      }
    } catch (error) {
      console.error('获取月度统计失败:', error)
      monthStats.value = { income: 0, expense: 0 }
    }
  }

  /**
   * 获取分类统计
   */
  async function fetchCategoryStats(params) {
    try {
      const res = await statCategoryApi(params)
      return res.data || res || {}
    } catch (error) {
      console.error('获取分类统计失败:', error)
      return {}
    }
  }

  /**
   * 获取趋势统计
   */
  async function fetchTrendStats(params) {
    try {
      const res = await statTrendApi(params)
      return res.data || res || []
    } catch (error) {
      console.error('获取趋势统计失败:', error)
      return []
    }
  }

  /**
   * 设置筛选条件
   */
  function setFilter(value) {
    filter.value = value
  }

  /**
   * 设置搜索关键字
   */
  function setSearchKeyword(value) {
    searchKeyword.value = value
  }

  /**
   * 清空搜索
   */
  function clearSearch() {
    searchKeyword.value = ''
  }

  return {
    // 状态
    bills,
    loading,
    filter,
    searchKeyword,
    monthStats,
    // 计算属性
    filteredBills,
    totalCount,
    incomeCount,
    expenseCount,
    totalIncome,
    totalExpense,
    // 方法
    fetchBillList,
    addBill,
    updateBill,
    deleteBill,
    batchDeleteAction,
    fetchMonthStats,
    fetchCategoryStats,
    fetchTrendStats,
    setFilter,
    setSearchKeyword,
    clearSearch
  }
})
