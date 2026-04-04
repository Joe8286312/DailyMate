<template>
  <div class="dashboard-container">
    <!-- 欢迎语和日期 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h1 class="welcome-title">
          <span class="gradient-text">{{ greetingText }}</span>
        </h1>
        <p class="welcome-subtitle">{{ currentDateText }}</p>
      </div>
      <el-button type="primary" round @click="handleQuickAdd" class="quick-add-btn">
        <el-icon><Plus /></el-icon>
        {{ t('common.quickAdd') }}
      </el-button>
    </div>

    <!-- 数据卡片区域 -->
    <el-row :gutter="12" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card todo-stat">
          <div class="stat-header">
            <div class="stat-icon-wrapper">
              <el-icon :size="20"><List /></el-icon>
            </div>
            <el-tag type="primary" size="small" effect="plain">{{ t('nav.todos') }}</el-tag>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ todoDoneCount + todoUndoneCount }}</div>
            <div class="stat-label">{{ t('dashboardPage.totalTasks') }}</div>
          </div>
          <el-progress
            :percentage="todoCompletionRate"
            :stroke-width="4"
            :show-text="false"
            class="stat-progress"
          />
          <div class="stat-footer">
            <span class="footer-label">{{ t('dashboardPage.completed') }}</span>
            <span class="footer-value text-success">{{ todoDoneCount }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card income-stat">
          <div class="stat-header">
            <div class="stat-icon-wrapper">
              <el-icon :size="20"><TrendCharts /></el-icon>
            </div>
            <el-tag type="success" size="small" effect="plain">{{ t('dashboardPage.income') }}</el-tag>
          </div>
          <div class="stat-body">
            <div class="stat-value text-success">¥{{ incomeAmount }}</div>
            <div class="stat-label">{{ t('dashboardPage.monthIncome') }}</div>
          </div>
          <div class="stat-footer">
            <span class="footer-label">{{ t('common.today') }}</span>
            <span class="footer-value text-success">+¥{{ todayIncome }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card expense-stat">
          <div class="stat-header">
            <div class="stat-icon-wrapper">
              <el-icon :size="20"><Money /></el-icon>
            </div>
            <el-tag type="danger" size="small" effect="plain">{{ t('dashboardPage.expense') }}</el-tag>
          </div>
          <div class="stat-body">
            <div class="stat-value text-danger">¥{{ expenseAmount }}</div>
            <div class="stat-label">{{ t('dashboardPage.monthExpense') }}</div>
          </div>
          <div class="stat-footer">
            <span class="footer-label">{{ t('common.today') }}</span>
            <span class="footer-value text-danger">-¥{{ todayExpense }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card balance-stat">
          <div class="stat-header">
            <div class="stat-icon-wrapper">
              <el-icon :size="20"><Wallet /></el-icon>
            </div>
            <el-tag type="warning" size="small" effect="plain">{{ t('dashboardPage.balance') }}</el-tag>
          </div>
          <div class="stat-body">
            <div class="stat-value" :class="balance >= 0 ? 'text-success' : 'text-danger'">
              ¥{{ balance }}
            </div>
            <div class="stat-label">{{ t('dashboardPage.monthBalance') }}</div>
          </div>
          <div class="stat-footer">
            <span class="footer-label">{{ t('dashboardPage.savingsRate') }}</span>
            <span class="footer-value" :class="savingsRate >= 0 ? 'text-success' : 'text-danger'">
              {{ savingsRate }}%
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日历和快捷操作区域 -->
    <el-row :gutter="10" class="main-section">
      <!-- 日历 -->
      <el-col :span="14">
        <el-card class="calendar-card">
          <template #header>
            <div class="calendar-header">
              <span class="calendar-title">{{ currentMonthText }}</span>
              <div class="calendar-nav">
                <el-button circle size="small" @click="prevMonth">
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <el-button circle size="small" @click="today" class="today-btn">
                  {{ t('dashboardPage.calendarTodayButton') }}
                </el-button>
                <el-button circle size="small" @click="nextMonth">
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </template>

          <div class="calendar-body">
            <div class="calendar-weekdays">
              <div v-for="day in weekDays" :key="day" class="weekday">
                {{ day }}
              </div>
            </div>
            <div class="calendar-dates">
              <div
                v-for="date in calendarDates"
                :key="date.value"
                class="date-cell"
                :class="{
                  'other-month': date.isOtherMonth,
                  'today': date.isToday,
                  'selected': date.isSelected
                }"
                @click="selectDate(date)"
              >
                <span class="date-text">{{ date.day }}</span>
                <div v-if="date.hasEvents" class="event-dots">
                  <span
                    v-for="(dot, index) in date.eventTypes"
                    :key="index"
                    class="event-dot"
                    :class="dot === 1 ? 'todo-dot' : 'bill-dot'"
                  />
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 黄历和 Tips 合并卡片 - 紧凑版本 -->
        <el-card class="info-card">
          <div class="info-body">
            <div class="info-row">
              <div class="date-section">
                <span class="lunar-day">{{ lunarDate?.dayCn }}</span>
                <span class="lunar-month">{{ lunarDate?.monthCn }}</span>
                <span class="solar-date">{{ selectedDate.format('MM.DD') }}</span>
                <el-tag type="warning" size="small" effect="plain" class="year-tag">
                  {{ lunarDate?.gzYear }}{{ lunarDate?.animal }}
                </el-tag>
              </div>
              <div class="almanac-section">
                <span class="good-tag">{{ t('dashboardPage.goodTag') }}</span>
                <div class="almanac-list">
                  <span v-for="(act, idx) in almanac?.good?.slice(0, 3)" :key="idx" class="almanac-item">{{ act }}</span>
                </div>
              </div>
              <div class="almanac-section">
                <span class="bad-tag">{{ t('dashboardPage.badTag') }}</span>
                <div class="almanac-list">
                  <span v-for="(act, idx) in almanac?.bad?.slice(0, 3)" :key="idx" class="almanac-item">{{ act }}</span>
                </div>
              </div>
              <div class="tips-section">
                <el-icon class="tips-icon"><Lightbulb /></el-icon>
                <span class="tip-text">{{ currentTip }}</span>
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="refreshTip"
                  class="refresh-btn"
                >
                  <el-icon><Refresh /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :span="10">
        <!-- 快捷操作 -->
        <el-card class="quick-actions-card">
          <template #header>
            <span class="card-title">{{ t('dashboardPage.quickActions') }}</span>
          </template>
          <div class="action-buttons">
            <el-button type="primary" @click="goToTodos" class="action-btn">
              <el-icon><List /></el-icon>
              <span>{{ t('dashboardPage.addTodo') }}</span>
            </el-button>
            <el-button type="success" @click="goToBills" class="action-btn">
              <el-icon><Money /></el-icon>
              <span>{{ t('dashboardPage.addBill') }}</span>
            </el-button>
          </div>
        </el-card>

        <!-- 今日待办 -->
        <el-card class="today-todos-card">
          <template #header>
            <div class="card-header-between">
              <span class="card-title">{{ t('dashboardPage.todayTodos') }}</span>
              <el-button link type="primary" size="small" @click="goToTodos">
                {{ t('common.viewAll') }}
              </el-button>
            </div>
          </template>
          <div v-if="todayTodos.length > 0" class="today-todo-list">
            <div
              v-for="todo in todayTodos.slice(0, 5)"
              :key="todo.id"
              class="today-todo-item"
              :class="{ 'is-done': todo.status === 1 }"
            >
              <el-checkbox
                :model-value="todo.status === 1"
                @change="handleTodoStatusChange(todo)"
              />
              <span class="todo-title">{{ todo.title }}</span>
              <el-tag v-if="todo.priority === 1" type="danger" size="small" class="priority-tag">
                {{ t('todo.high') }}
              </el-tag>
            </div>
          </div>
          <el-empty v-else :description="t('dashboardPage.noTodayTodos')" :image-size="50" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { useTodoStore } from '@/stores/todo'
import { useBillStore } from '@/stores/bill'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const { t, locale } = useI18n()
const authStore = useAuthStore()
const todoStore = useTodoStore()
const billStore = useBillStore()

const weekDays = computed(() => t('dashboardPage.weekDays'))
const currentDate = ref(dayjs())
const selectedDate = ref(dayjs())

// 黄历数据
const lunarDate = ref(null)
const almanac = ref(null)

// 每日 Tips 数据
const currentTip = ref('')

const currentMonthText = computed(() => {
  const dt = currentDate.value.toDate()
  return new Intl.DateTimeFormat(locale.value, { year: 'numeric', month: 'long' }).format(dt)
})

const currentDateText = computed(() => {
  return new Intl.DateTimeFormat(locale.value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  }).format(new Date())
})

const greetingText = computed(() => {
  const hour = dayjs().hour()
  if (hour < 6) return t('dashboardPage.greetingNight')
  if (hour < 9) return t('dashboardPage.greetingMorning')
  if (hour < 12) return t('dashboardPage.greetingForenoon')
  if (hour < 18) return t('dashboardPage.greetingAfternoon')
  if (hour < 22) return t('dashboardPage.greetingEvening')
  return t('dashboardPage.greetingLateNight')
})

const calendarDates = computed(() => {
  const year = currentDate.value.year()
  const month = currentDate.value.month()
  const startOfMonth = dayjs(new Date(year, month, 1))
  const endOfMonth = startOfMonth.endOf('month')
  const startDay = startOfMonth.day() || 7
  const endDay = endOfMonth.date()

  const dates = []
  const today = dayjs()

  const prevMonth = startOfMonth.subtract(1, 'month')
  const prevMonthDays = prevMonth.daysInMonth()
  for (let i = startDay - 1; i >= 0; i--) {
    const day = prevMonthDays - i
    dates.push({
      value: prevMonth.date(day).format('YYYY-MM-DD'),
      day,
      isOtherMonth: true,
      isToday: false,
      isSelected: false,
      hasEvents: false,
      eventTypes: []
    })
  }

  for (let day = 1; day <= endDay; day++) {
    const dateStr = startOfMonth.date(day).format('YYYY-MM-DD')
    dates.push({
      value: dateStr,
      day,
      isOtherMonth: false,
      isToday: today.format('YYYY-MM-DD') === dateStr,
      isSelected: selectedDate.value.format('YYYY-MM-DD') === dateStr,
      hasEvents: false,
      eventTypes: []
    })
  }

  const remaining = 42 - dates.length
  for (let day = 1; day <= remaining; day++) {
    dates.push({
      value: endOfMonth.add(day, 'day').format('YYYY-MM-DD'),
      day,
      isOtherMonth: true,
      isToday: false,
      isSelected: false,
      hasEvents: false,
      eventTypes: []
    })
  }

  return dates
})

const todoDoneCount = ref(0)
const todoUndoneCount = ref(0)
const expenseAmount = ref(0)
const incomeAmount = ref(0)
const todayDoneCount = ref(0)
const todayIncome = ref(0)
const todayExpense = ref(0)
const todayTodos = ref([])

const todoCompletionRate = computed(() => {
  const total = todoDoneCount.value + todoUndoneCount.value
  if (total === 0) return 0
  return Math.round((todoDoneCount.value / total) * 100)
})

const balance = computed(() => {
  return (parseFloat(incomeAmount.value) - parseFloat(expenseAmount.value)).toFixed(2)
})

const savingsRate = computed(() => {
  const income = parseFloat(incomeAmount.value)
  if (income === 0) return 0
  return Math.round(((income - parseFloat(expenseAmount.value)) / income) * 100)
})

const prevMonth = () => {
  currentDate.value = currentDate.value.subtract(1, 'month')
}

const nextMonth = () => {
  currentDate.value = currentDate.value.add(1, 'month')
}

const today = () => {
  currentDate.value = dayjs()
  selectedDate.value = dayjs()
  loadAlmanac()
}

const selectDate = (date) => {
  selectedDate.value = dayjs(date.value)
  loadAlmanac()
}

// 加载黄历数据
const loadAlmanac = () => {
  const date = selectedDate.value
  const year = date.year()
  const month = date.month() + 1
  const day = date.date()
  
  // 天干地支计算
  const heavenlyStems = ['甲', '乙', '丙', '丁', '戊', '己', '庚', '辛', '壬', '癸']
  const earthlyBranches = ['子', '丑', '寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥']
  const animals = ['鼠', '牛', '虎', '兔', '龙', '蛇', '马', '羊', '猴', '鸡', '狗', '猪']
  
  const stemIndex = (year - 4) % 10
  const branchIndex = (year - 4) % 12
  const animalIndex = (year - 4) % 12
  
  // 农历月份和日期（简化版）
  const lunarMonths = ['正', '二', '三', '四', '五', '六', '七', '八', '九', '十', '冬', '腊']
  const lunarDays = [
    '初一', '初二', '初三', '初四', '初五', '初六', '初七', '初八', '初九', '初十',
    '十一', '十二', '十三', '十四', '十五', '十六', '十七', '十八', '十九', '二十',
    '廿一', '廿二', '廿三', '廿四', '廿五', '廿六', '廿七', '廿八', '廿九', '三十'
  ]
  
  // 简化计算农历（实际需要更精确的算法）
  lunarDate.value = {
    gzYear: `${heavenlyStems[stemIndex]}${earthlyBranches[branchIndex]}`,
    animal: animals[animalIndex],
    monthCn: `${lunarMonths[(month - 1) % 12]}月`,
    dayCn: lunarDays[day - 1] || '初一'
  }
  
  // 宜忌数据（根据日期生成，保持一致性）
  const goodActivities = ['祭祀', '祈福', '求嗣', '出行', '纳财', '开市', '交易', '立券', '纳畜', '牧养']
  const badActivities = ['嫁娶', '安葬', '破土', '动土', '修造', '入宅', '移徙', '开仓', '出货财', '乘船']
  
  // 根据日期选择宜忌
  const seed = year * 10000 + month * 100 + day
  const goodCount = 3 + (seed % 3)
  const badCount = 2 + (seed % 2)
  
  const good = []
  const bad = []
  for (let i = 0; i < goodCount; i++) {
    good.push(goodActivities[(seed + i * 7) % goodActivities.length])
  }
  for (let i = 0; i < badCount; i++) {
    bad.push(badActivities[(seed + i * 11) % badActivities.length])
  }
  
  const chongShaList = ['鼠', '牛', '虎', '兔', '龙', '蛇', '马', '羊', '猴', '鸡', '狗', '猪']
  const chongIndex = (seed % 12)
  const chongSha = `冲${chongShaList[chongIndex]} 煞${chongIndex % 2 === 0 ? '北' : '南'}`
  
  const wealthDirections = ['正东', '正南', '正西', '正北', '东南', '东北', '西南', '西北']
  const godOfWealth = wealthDirections[seed % wealthDirections.length]
  
  almanac.value = {
    good,
    bad,
    chongSha,
    godOfWealth
  }
}

// 刷新 Tips
const refreshTip = () => {
  currentTip.value = t('dashboardPage.defaultTip')
}

const handleQuickAdd = () => {
  ElMessage.info(t('dashboardPage.selectAddType'))
}

const handleTodoStatusChange = async (todo) => {
  const newStatus = todo.status === 1 ? 0 : 1
  try {
    await todoStore.updateTodo({ ...todo, status: newStatus })
    await loadDashboardData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const loadDashboardData = async () => {
  const userId = authStore.userInfo?.id
  if (!userId) return

  await todoStore.fetchTodoList({ userId })
  const todos = todoStore.todos
  todoDoneCount.value = todos.filter(t => t.status === 1).length
  todoUndoneCount.value = todos.filter(t => t.status === 0).length

  const todayStr = dayjs().format('YYYY-MM-DD')
  todayTodos.value = todos.filter(t => t.endTime?.startsWith(todayStr))

  await billStore.fetchBillList({ userId })
  const bills = billStore.bills

  const currentMonth = dayjs().format('YYYY-MM')
  const currentMonthBills = bills.filter(b => b.date?.startsWith(currentMonth))
  const todayBills = bills.filter(b => b.date === todayStr)

  expenseAmount.value = currentMonthBills
    .filter(b => b.type === 2)
    .reduce((sum, b) => sum + (b.amount || 0), 0)
    .toFixed(2)

  incomeAmount.value = currentMonthBills
    .filter(b => b.type === 1)
    .reduce((sum, b) => sum + (b.amount || 0), 0)
    .toFixed(2)

  todayIncome.value = todayBills
    .filter(b => b.type === 1)
    .reduce((sum, b) => sum + (b.amount || 0), 0)
    .toFixed(2)

  todayExpense.value = todayBills
    .filter(b => b.type === 2)
    .reduce((sum, b) => sum + (b.amount || 0), 0)
    .toFixed(2)

  todayDoneCount.value = todos.filter(
    t => t.status === 1 && t.endTime?.startsWith(todayStr)
  ).length
}

const goToTodos = () => {
  router.push('/todos')
}

const goToBills = () => {
  router.push('/bills')
}

onMounted(() => {
  loadDashboardData()
  loadAlmanac()
  refreshTip()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 12px;
  background: transparent;
  height: calc(100vh - 70px);
  overflow: hidden;
  position: relative;

  // 背景动画
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 40% 20%, rgba(16, 185, 129, 0.1) 0%, transparent 50%);
    animation: bgPulse 10s ease-in-out infinite;
    pointer-events: none;
    z-index: 0;
  }

  @keyframes bgPulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.8; transform: scale(1.05); }
  }

  * {
    position: relative;
    z-index: 1;
  }

  &::before {
    display: none;
  }

  .welcome-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    padding: 12px 18px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);
    border-radius: 12px;
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
    animation: slideDown 0.6s ease-out;

    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .welcome-text {
      .welcome-title {
        font-size: 18px;
        font-weight: 700;
        margin: 0 0 4px 0;

        .gradient-text {
          background: linear-gradient(135deg, #fff 0%, #e0e7ff 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          animation: textShimmer 3s ease-in-out infinite;

          @keyframes textShimmer {
            0%, 100% { filter: brightness(1); }
            50% { filter: brightness(1.2); }
          }
        }
      }

      .welcome-subtitle {
        font-size: 12px;
        opacity: 0.9;
        margin: 0;
        color: rgba(255, 255, 255, 0.9);
      }
    }

    .quick-add-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      padding: 8px 16px;
      font-size: 12px;
      backdrop-filter: blur(10px);
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(255, 255, 255, 0.2);
      }
    }
  }

  .stats-row {
    margin-bottom: 10px;
    animation: fadeInUp 0.8s ease-out 0.2s both;

    @keyframes fadeInUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .stat-card {
      border: none;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
        transform: translateX(-100%);
        transition: transform 0.6s;
      }

      &:hover {
        transform: translateY(-6px) scale(1.02);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);

        &::before {
          transform: translateX(100%);
          transition: transform 0.6s;
        }
      }

      :deep(.el-card__body) {
        padding: 10px;
      }

      .stat-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .stat-icon-wrapper {
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 8px;
          animation: iconPulse 2s ease-in-out infinite;

          @keyframes iconPulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
          }

          .todo-stat & {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
          }

          .income-stat & {
            background: linear-gradient(135deg, #10b981, #059669);
            color: white;
          }

          .expense-stat & {
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: white;
          }

          .balance-stat & {
            background: linear-gradient(135deg, #f59e0b, #d97706);
            color: white;
          }
        }
      }

      .stat-body {
        margin-bottom: 8px;

        .stat-value {
          font-size: 20px;
          font-weight: 700;
          margin-bottom: 2px;
          background: linear-gradient(135deg, #1e293b, #475569);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;

          &.text-success {
            background: linear-gradient(135deg, #10b981, #059669);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }

          &.text-danger {
            background: linear-gradient(135deg, #ef4444, #dc2626);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }
        }

        .stat-label {
          font-size: 11px;
          color: #64748b;
        }
      }

      .stat-progress {
        margin-bottom: 6px;
      }

      .stat-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 6px;
        border-top: 1px solid rgba(226, 232, 240, 0.6);

        .footer-label {
          font-size: 11px;
          color: #64748b;
        }

        .footer-value {
          font-size: 12px;
          font-weight: 600;

          &.text-success {
            color: #10b981;
          }

          &.text-danger {
            color: #ef4444;
          }
        }
      }
    }
  }

  .main-section {
    animation: fadeInUp 0.8s ease-out 0.4s both;

    .calendar-card {
      border: none;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
      height: 42vh;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      transition: all 0.4s;

      &:hover {
        box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
        transform: translateY(-2px);
      }

      :deep(.el-card__header) {
        padding: 12px 16px;
        background: linear-gradient(135deg, #667eea, #764ba2);
        border-bottom: none;
        border-radius: 12px 12px 0 0;
        flex-shrink: 0;
      }

      .calendar-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .calendar-title {
          font-size: 16px;
          font-weight: 700;
          color: white;
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
        }

        .calendar-nav {
          display: flex;
          gap: 6px;

          .el-button {
            background: rgba(255, 255, 255, 0.25);
            border: none;
            color: white;
            width: 28px;
            height: 28px;
            padding: 0;
            font-size: 12px;
            border-radius: 50%;
            transition: all 0.3s;

            &:hover {
              background: rgba(255, 255, 255, 0.4);
              transform: scale(1.15) rotate(5deg);
            }

            &.today-btn {
              padding: 0 10px;
              width: auto;
              font-size: 12px;
              border-radius: 6px;
            }
          }
        }
      }

      .calendar-body {
        padding: 0;
        display: flex;
        flex-direction: column;
        height: calc(100% - 52px);
      }

      .calendar-weekdays {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        gap: 0;
        flex-shrink: 0;
        border-bottom: 1px solid #e2e8f0;

        .weekday {
          text-align: center;
          font-size: 12px;
          font-weight: 600;
          color: #64748b;
          padding: 10px 0;
          background: linear-gradient(180deg, #f8fafc, #f1f5f9);
          border-right: 1px solid #e2e8f0;

          &:last-child {
            border-right: none;
          }
        }
      }

      .calendar-dates {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        grid-template-rows: repeat(6, 1fr);
        gap: 0;
        height: 100%;
        border-bottom: 1px solid #e2e8f0;
        border-right: 1px solid #e2e8f0;

        .date-cell {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background: #ffffff;
          border-left: 1px solid #e2e8f0;
          border-top: 1px solid #e2e8f0;
          cursor: pointer;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          min-height: 50px;

          &:hover {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
            z-index: 1;
            box-shadow: inset 0 0 0 2px rgba(102, 126, 234, 0.3);
            transform: scale(1.05);
          }

          &.other-month {
            opacity: 0.25;
            cursor: default;
            background: #fafafa;

            &:hover {
              background: #fafafa;
              box-shadow: none;
              transform: none;
            }
          }

          &.today {
            background: linear-gradient(135deg, #667eea, #764ba2);
            border-color: transparent;
            box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
            animation: todayGlow 2s ease-in-out infinite;

            @keyframes todayGlow {
              0%, 100% { box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4); }
              50% { box-shadow: 0 6px 24px rgba(102, 126, 234, 0.6); }
            }

            .date-text {
              color: white;
              font-weight: 700;
              font-size: 14px;
              animation: numberPulse 2s ease-in-out infinite;

              @keyframes numberPulse {
                0%, 100% { transform: scale(1); }
                50% { transform: scale(1.15); }
              }
            }

            &::after {
              content: '';
              position: absolute;
              bottom: 6px;
              width: 5px;
              height: 5px;
              background: rgba(255, 255, 255, 0.8);
              border-radius: 50%;
              animation: dotBounce 1s ease-in-out infinite;

              @keyframes dotBounce {
                0%, 100% { transform: translateY(0); opacity: 0.6; }
                50% { transform: translateY(-2px); opacity: 1; }
              }
            }
          }

          &.selected {
            box-shadow: inset 0 0 0 2px #667eea;
            z-index: 1;
          }

          .date-text {
            font-size: 14px;
            font-weight: 500;
            color: #1e293b;
            transition: all 0.3s;
          }

          .event-dots {
            display: flex;
            gap: 4px;
            margin-top: 4px;

            .event-dot {
              width: 5px;
              height: 5px;
              border-radius: 50%;

              &.todo-dot {
                background: #667eea;
                box-shadow: 0 0 6px rgba(102, 126, 234, 0.8);
                animation: dotGlow 2s ease-in-out infinite;
              }

              &.bill-dot {
                background: #10b981;
                box-shadow: 0 0 6px rgba(16, 185, 129, 0.8);
                animation: dotGlow 2s ease-in-out infinite 0.5s;
              }

              @keyframes dotGlow {
                0%, 100% { opacity: 0.6; transform: scale(1); }
                50% { opacity: 1; transform: scale(1.2); }
              }
            }
          }
        }
      }
    }

    // 黄历卡片样式
    .almanac-card {
      border: none;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 251, 235, 0.95) 0%, rgba(254, 243, 199, 0.95) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      margin-top: 8px !important;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 32px rgba(245, 158, 11, 0.2);
      }

      :deep(.el-card__header) {
        padding: 8px 12px;
        background: linear-gradient(135deg, #f59e0b, #d97706);
        border-bottom: none;
        border-radius: 12px 12px 0 0;
      }

      .almanac-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .almanac-title {
          font-size: 12px;
          font-weight: 600;
          color: white;
          display: flex;
          align-items: center;
          gap: 4px;
        }

        :deep(.el-tag) {
          transform: scale(0.85);
          transform-origin: right center;
        }
      }

      .almanac-body {
        padding: 8px;

        .almanac-date {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          padding-bottom: 6px;
          border-bottom: 1px dashed rgba(245, 158, 11, 0.4);

          .lunar-date {
            display: flex;
            gap: 6px;
            align-items: baseline;

            .lunar-day {
              font-size: 18px;
              font-weight: 700;
              color: #92400e;
              animation: numberSlide 1s ease-out;

              @keyframes numberSlide {
                from {
                  opacity: 0;
                  transform: translateX(-10px);
                }
                to {
                  opacity: 1;
                  transform: translateX(0);
                }
              }
            }

            .lunar-month {
              font-size: 11px;
              color: #b45309;
            }
          }

          .solar-date {
            font-size: 12px;
            font-weight: 600;
            color: #78350f;
          }
        }

        .almanac-grid {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 6px;

          .almanac-item {
            display: flex;
            align-items: center;
            gap: 6px;

            .item-label {
              font-size: 11px;
              font-weight: 600;
              color: #92400e;
              min-width: 32px;
            }

            .item-content {
              flex: 1;
              font-size: 10px;
              color: #78350f;

              &.good {
                display: flex;
                flex-wrap: wrap;
                gap: 3px;

                .almanac-tag {
                  background: rgba(34, 197, 94, 0.15);
                  color: #166534;
                  padding: 2px 5px;
                  border-radius: 4px;
                  font-size: 10px;
                  animation: tagFadeIn 0.5s ease-out both;

                  @keyframes tagFadeIn {
                    from {
                      opacity: 0;
                      transform: scale(0.8);
                    }
                    to {
                      opacity: 1;
                      transform: scale(1);
                    }
                  }
                }
              }

              &.bad {
                display: flex;
                flex-wrap: wrap;
                gap: 3px;

                .almanac-tag {
                  background: rgba(239, 68, 68, 0.15);
                  color: #991b1b;
                  padding: 2px 5px;
                  border-radius: 4px;
                  font-size: 10px;
                }
              }
            }
          }
        }
      }
    }

    // 黄历和 Tips 合并卡片 - 紧凑版本
    .info-card {
      border: none;
      border-radius: 8px;
      background: linear-gradient(135deg, rgba(255, 251, 235, 0.9) 0%, rgba(254, 243, 199, 0.9) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      margin-top: 8px !important;
      height: 56px;
      flex-shrink: 0;

      :deep(.el-card__body) {
        padding: 0 12px;
        height: 100%;
        display: flex;
        align-items: center;
      }

      .info-row {
        display: flex;
        align-items: center;
        gap: 12px;
        width: 100%;

        .date-section {
          display: flex;
          align-items: baseline;
          gap: 6px;
          flex-shrink: 0;

          .lunar-day {
            font-size: 16px;
            font-weight: 700;
            color: #92400e;
          }

          .lunar-month {
            font-size: 11px;
            color: #b45309;
          }

          .solar-date {
            font-size: 11px;
            color: #64748b;
            font-weight: 500;
          }

          .year-tag {
            transform: scale(0.85);
          }
        }

        .almanac-section {
          display: flex;
          align-items: center;
          gap: 4px;
          flex-shrink: 0;

          .good-tag, .bad-tag {
            font-size: 11px;
            font-weight: 600;
            padding: 2px 6px;
            border-radius: 4px;
            min-width: 20px;
            text-align: center;
          }

          .good-tag {
            color: #166534;
            background: rgba(34, 197, 94, 0.2);
          }

          .bad-tag {
            color: #991b1b;
            background: rgba(239, 68, 68, 0.2);
          }

          .almanac-list {
            display: flex;
            gap: 4px;

            .almanac-item {
              font-size: 10px;
              color: #78350f;
              background: rgba(255, 255, 255, 0.5);
              padding: 2px 6px;
              border-radius: 4px;
              white-space: nowrap;
            }
          }
        }

        .tips-section {
          display: flex;
          align-items: center;
          gap: 6px;
          flex: 1;
          margin-left: auto;
          padding-left: 12px;
          border-left: 1px solid rgba(245, 158, 11, 0.2);

          .tips-icon {
            color: #f59e0b;
            font-size: 14px;
            flex-shrink: 0;
          }

          .tip-text {
            font-size: 11px;
            color: #475569;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .refresh-btn {
            padding: 4px;
            flex-shrink: 0;
            color: #667eea;

            &:hover {
              transform: rotate(90deg);
            }
          }
        }
      }
    }

    .quick-actions-card {
      border: none;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
      }

      :deep(.el-card__header) {
        padding: 10px 14px;
        font-weight: 600;
        font-size: 13px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
        border-bottom: 1px solid rgba(102, 126, 234, 0.2);
      }

      .action-buttons {
        display: flex;
        flex-direction: column;
        gap: 8px;
        padding: 12px;

        .action-btn {
          height: 36px;
          font-size: 12px;
          border-radius: 8px;
          transition: all 0.3s;
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
          border: 1px solid rgba(102, 126, 234, 0.3);

          &:hover {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
            transform: translateX(4px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
          }
        }
      }
    }

    .today-todos-card {
      border: none;
      border-radius: 12px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.98) 100%);
      backdrop-filter: blur(10px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      margin-top: 8px !important;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
      }

      :deep(.el-card__header) {
        padding: 10px 14px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
        border-bottom: 1px solid rgba(102, 126, 234, 0.2);
      }

      .card-header-between {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .card-title {
          font-weight: 600;
          font-size: 13px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .el-button {
          font-size: 11px;
        }
      }

      .today-todo-list {
        padding: 8px;

        .today-todo-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 8px;
          margin-bottom: 4px;
          border-radius: 6px;
          transition: all 0.3s;

          &:hover {
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
            transform: translateX(4px);
          }

          &:last-child {
            margin-bottom: 0;
          }

          &.is-done {
            opacity: 0.6;

            .todo-title {
              text-decoration: line-through;
            }
          }

          .todo-title {
            flex: 1;
            font-size: 12px;
            color: #1e293b;
          }

          .priority-tag {
            flex-shrink: 0;
            transform: scale(0.85);
          }
        }
      }
    }
  }
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.text-primary {
  color: #4299e1;
}
</style>
