<template>
  <div class="bill-statistics">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="8">
        <el-card class="stat-card income">
          <div class="card-icon">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">总收入</div>
            <div class="card-value">¥{{ statistics.totalIncome?.toFixed(2) || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card expense">
          <div class="card-icon">
            <el-icon :size="24"><Money /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">总支出</div>
            <div class="card-value">¥{{ statistics.totalExpense?.toFixed(2) || '0.00' }}</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card balance">
          <div class="card-icon">
            <el-icon :size="24"><Wallet /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">结余</div>
            <div class="card-value" :class="statistics.balance >= 0 ? 'positive' : 'negative'">
              ¥{{ statistics.balance?.toFixed(2) || '0.00' }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 月度趋势图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">收支趋势（近 6 个月）</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 分类饼图 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">支出分类占比</span>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { TrendCharts, Money, Wallet } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const trendChartRef = ref(null)
const categoryChartRef = ref(null)

const statistics = reactive({
  totalIncome: 0,
  totalExpense: 0,
  balance: 0,
  categoryStats: {},
  monthlyTrend: []
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const userId = authStore.userInfo?.id
    if (!userId) return
    
    const res = await fetch(`/api/bill/statistics?userId=${userId}`)
    const data = await res.json()
    
    if (data.code === 200) {
      statistics.totalIncome = data.data?.totalIncome || 0
      statistics.totalExpense = data.data?.totalExpense || 0
      statistics.balance = data.data?.balance || 0
      statistics.categoryStats = data.data?.categoryStats || {}
      statistics.monthlyTrend = data.data?.monthlyTrend || []
      
      nextTick(() => {
        renderTrendChart()
        renderCategoryChart()
      })
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 渲染趋势图
const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  const chart = echarts.init(trendChartRef.value)
  
  const months = statistics.monthlyTrend.map(item => item.month)
  const incomeData = statistics.monthlyTrend.map(item => item.income)
  const expenseData = statistics.monthlyTrend.map(item => item.expense)
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['收入', '支出'],
      bottom: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'bar',
        data: incomeData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#48bb78' },
            { offset: 1, color: '#38a169' }
          ])
        }
      },
      {
        name: '支出',
        type: 'bar',
        data: expenseData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#f56565' },
            { offset: 1, color: '#e53e3e' }
          ])
        }
      }
    ]
  })
  
  window.addEventListener('resize', () => chart.resize())
}

// 渲染分类饼图
const renderCategoryChart = () => {
  if (!categoryChartRef.value) return
  
  const chart = echarts.init(categoryChartRef.value)
  
  const categories = Object.entries(statistics.categoryStats || {}).map(([name, value]) => ({
    name,
    value: parseFloat(value)
  }))
  
  const colors = ['#4299e1', '#48bb78', '#ed8936', '#f56565', '#9f7aea', '#38b2ac', '#ecc94b', '#f687b3']
  
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '支出分类',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        data: categories,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          formatter: '{b}\n¥{c}'
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
  
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  loadStatistics()
})
</script>

<style lang="scss" scoped>
.bill-statistics {
  padding: 20px;
  
  .overview-cards {
    margin-bottom: 20px;
    
    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      border: none;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      
      :deep(.el-card__body) {
        padding: 20px;
        display: flex;
        align-items: center;
        width: 100%;
      }
      
      .card-icon {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        
        .income & {
          background: linear-gradient(135deg, #48bb78, #38a169);
          color: white;
        }
        
        .expense & {
          background: linear-gradient(135deg, #f56565, #e53e3e);
          color: white;
        }
        
        .balance & {
          background: linear-gradient(135deg, #4299e1, #38b2ac);
          color: white;
        }
      }
      
      .card-content {
        flex: 1;
        
        .card-label {
          font-size: 14px;
          color: #718096;
          margin-bottom: 4px;
        }
        
        .card-value {
          font-size: 24px;
          font-weight: 700;
          
          &.positive {
            color: #48bb78;
          }
          
          &.negative {
            color: #f56565;
          }
        }
      }
    }
  }
  
  .charts-row {
    .chart-card {
      border: none;
      border-radius: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      
      :deep(.el-card__header) {
        padding: 16px 20px;
        font-weight: 600;
      }
      
      .chart-container {
        height: 300px;
        width: 100%;
      }
    }
  }
}
</style>
