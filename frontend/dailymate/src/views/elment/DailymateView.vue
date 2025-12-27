<template>
  <el-container class="full-height-container">
    <!-- 顶部区域 -->
    <el-header class="dailymate-header">
      <!-- 第一层：顶部标题+用户信息 -->
      <div class="header-row header-row-top">
        <div class="header-title">
          <img src="https://img.icons8.com/color/48/000000/rainbow.png" style="height: 36px;vertical-align: middle;margin-right: 12px;" />
          DailyMate
        </div>
        <div class="user-info">
          <el-avatar src="https://img.icons8.com/color/48/000000/user-male-circle--v2.png" />
          <span class="user-name">{{ username }}</span>
          <el-button class="logout-btn" @click="logout">退出登录</el-button>
        </div>
      </div>
      
      <!-- 第二层：日历+3卡片 -->
      <div class="header-row header-row-bottom">
        <!-- 日历区域 -->
        <div class="calendar-block">
          <div class="calendar-month-bar">
            <el-button class="calendar-nav-btn" @click="changeMonth(-1)" size="mini" icon="el-icon-arrow-left" circle></el-button>
            <span class="calendar-month" @click="showMonthPicker = true">{{ calendarYear }}年{{ calendarMonth + 1 }}月</span>
            <el-button class="calendar-nav-btn" @click="changeMonth(1)" size="mini" icon="el-icon-arrow-right" circle></el-button>
            <el-date-picker
              v-model="monthPickerValue"
              type="month"
              format="yyyy-MM"
              value-format="yyyy-MM"
              :visible.sync="showMonthPicker"
              style="display:none"
              @change="handleMonthPick"
            />
          </div>
          
          <!-- 自定义日历 -->
          <div class="mini-calendar-container">
            <div class="mini-calendar-weekdays">
              <div class="mini-calendar-weekday">一</div>
              <div class="mini-calendar-weekday">二</div>
              <div class="mini-calendar-weekday">三</div>
              <div class="mini-calendar-weekday">四</div>
              <div class="mini-calendar-weekday">五</div>
              <div class="mini-calendar-weekday">六</div>
              <div class="mini-calendar-weekday">日</div>
            </div>
            <div class="mini-calendar-grid">
              <!-- 上个月的日期 -->
              <div 
                v-for="day in prevMonthDays" 
                :key="'prev-' + day"
                class="mini-calendar-cell other-month"
              >
                {{ day }}
              </div>
              
              <!-- 当前月的日期 -->
              <div 
                v-for="day in currentMonthDays" 
                :key="day"
                class="mini-calendar-cell"
                :class="{
                  'today': isToday(day),
                  'selected': isSelected(day)
                }"
                @click="selectDate(day)"
              >
                {{ day }}
                <div v-if="hasEvents(day)" class="mini-dot-container">
                  <span class="mini-dot" style="background-color: #4299e1;"></span>
                  <span v-if="hasMultipleEvents(day)" class="mini-dot" style="background-color: #38b2ac;"></span>
                </div>
              </div>
              
              <!-- 下个月的日期 -->
              <div 
                v-for="day in nextMonthDays" 
                :key="'next-' + day"
                class="mini-calendar-cell other-month"
              >
                {{ day }}
              </div>
            </div>
          </div>
        </div>
        
        <!-- 数据卡片区域 -->
        <div class="data-card-block">
          <div class="data-card card-todo">
            <div class="card-title">本月代办事项管理</div>
            <div class="card-content">
              已完成 <span class="text-green">15</span> 件待办事项<br>
              未完成 <span class="text-red">4</span> 件待办事项
            </div>
          </div>
          <div class="data-card card-bill">
            <div class="card-title">本月账单管理</div>
            <div class="card-content">
              支出 <span class="text-green">2850</span> 元<br>
              收入 <span class="text-red">8900</span> 元
            </div>
          </div>
          <div class="data-card card-today">
            <div class="card-title">今日管理</div>
            <div class="card-content">
              今日完成 <span class="text-blue">3</span> 件事<br>
              今日收入 <span class="text-red">500</span> 元<br>
              今日支出 <span class="text-green">180</span> 元
            </div>
          </div>
        </div>
      </div>
    </el-header>
    
    <!-- 主内容区域 -->
    <el-container class="main-content-container">
      <el-aside width="200px" class="sidebar-menu">
        <el-menu 
          :default-openeds="['1']" 
          :default-active="activeMenu"
          @select="handleMenuSelect"
        >
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-menu"></i>
              <span>管理选项</span>
            </template>
            <el-menu-item-group>
              <el-menu-item index="1-1">
                <i class="el-icon-tickets"></i>
                <span>待办事项</span>
              </el-menu-item>
              <el-menu-item index="1-2">
                <i class="el-icon-money"></i>
                <span>记账管理</span>
              </el-menu-item>
            </el-menu-item-group>
          </el-submenu>
        </el-menu>
      </el-aside>
      
      <el-main class="main-content">
        <!-- 这里可以放置主要内容，如待办事项列表或记账表单 -->
        <div class="welcome-message">
          <h2>欢迎使用 DailyMate</h2>
          <p>请从左侧菜单选择一个功能开始使用</p>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  data() {
    const now = new Date();
    return {
      username: '张三',
      calendarDate: now,
      showMonthPicker: false,
      monthPickerValue: '',
      activeMenu: '1-1',
      selectedDate: now.getDate()
    }
  },
  computed: {
    calendarYear() {
      return this.calendarDate.getFullYear();
    },
    calendarMonth() {
      return this.calendarDate.getMonth();
    },
    
    // 计算日历相关数据
    calendarData() {
      const year = this.calendarYear;
      const month = this.calendarMonth;
      
      // 获取当前月的第一天
      const firstDay = new Date(year, month, 1);
      // 获取当前月的最后一天
      const lastDay = new Date(year, month + 1, 0);
      // 获取第一天是星期几 (0-6, 0是周日)
      const firstDayOfWeek = firstDay.getDay();
      // 获取上个月最后一天
      const prevMonthLastDay = new Date(year, month, 0).getDate();
      
      // 计算需要显示的日期
      const daysInMonth = lastDay.getDate();
      
      // 调整星期一为一周的开始 (如果firstDayOfWeek为0（周日），则变为6)
      const startDay = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1;
      
      // 上个月需要显示的天数
      const prevMonthDays = [];
      for (let i = startDay - 1; i >= 0; i--) {
        prevMonthDays.push(prevMonthLastDay - i);
      }
      
      // 当前月需要显示的天数
      const currentMonthDays = [];
      for (let i = 1; i <= daysInMonth; i++) {
        currentMonthDays.push(i);
      }
      
      // 计算下个月需要显示的天数
      const totalCells = 42; // 6行 * 7列
      const totalDays = prevMonthDays.length + currentMonthDays.length;
      const nextMonthDays = [];
      for (let i = 1; i <= totalCells - totalDays; i++) {
        nextMonthDays.push(i);
      }
      
      return {
        prevMonthDays,
        currentMonthDays,
        nextMonthDays
      };
    },
    
    prevMonthDays() {
      return this.calendarData.prevMonthDays;
    },
    
    currentMonthDays() {
      return this.calendarData.currentMonthDays;
    },
    
    nextMonthDays() {
      return this.calendarData.nextMonthDays;
    }
  },
  methods: {
    logout() {
      this.$message.success('已退出登录');
    },
    changeMonth(offset) {
      const d = new Date(this.calendarDate);
      d.setMonth(d.getMonth() + offset);
      this.calendarDate = d;
      this.selectedDate = null; // 切换月份时重置选中日期
    },
    handleMonthPick(val) {
      if (!val) return;
      const [year, month] = val.split('-');
      const d = new Date(this.calendarDate);
      d.setFullYear(Number(year));
      d.setMonth(Number(month) - 1);
      this.calendarDate = d;
      this.selectedDate = null; // 切换月份时重置选中日期
      this.showMonthPicker = false;
    },
    
    // 检查是否是今天
    isToday(day) {
      const today = new Date();
      return day === today.getDate() &&
             this.calendarMonth === today.getMonth() &&
             this.calendarYear === today.getFullYear();
    },
    
    // 检查是否被选中
    isSelected(day) {
      return this.selectedDate === day;
    },
    
    // 选择日期
    selectDate(day) {
      this.selectedDate = day;
      const selectedDate = new Date(this.calendarYear, this.calendarMonth, day);
      console.log('选择日期:', selectedDate);
    },
    
    // 检查是否有事件
    hasEvents(day) {
      const today = new Date();
      // 模拟今天和明天有事件
      return (day === today.getDate() && 
              this.calendarMonth === today.getMonth() && 
              this.calendarYear === today.getFullYear()) ||
             (day === today.getDate() + 1 && 
              this.calendarMonth === today.getMonth() && 
              this.calendarYear === today.getFullYear());
    },
    
    hasMultipleEvents(day) {
      const today = new Date();
      // 模拟今天有多个事件
      return day === today.getDate() && 
             this.calendarMonth === today.getMonth() && 
             this.calendarYear === today.getFullYear();
    },
    
    handleMenuSelect(index) {
      this.activeMenu = index;
      console.log('选择菜单:', index);
      // 这里可以根据选择显示不同的内容
    }
  }
}
</script>

<style>
/* 全局容器 */
.full-height-container {
  height: 100vh;
  min-height: 100vh;
  max-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  overflow: hidden;
}

/* 顶部区域 */
.dailymate-header {
  height: 50vh;
  min-height: 400px;
  width: 100%;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(66, 153, 225, 0.12);
  padding: 0;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.dailymate-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4299e1, #38b2ac, #9f7aea);
  z-index: 2;
}

.header-row {
  width: 100%;
  display: flex;
  box-sizing: border-box;
}

.header-row-top {
  height: 25%;
  min-height: 70px;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  border-bottom: 1px solid #edf2f7;
  background: #ffffff;
}

.header-title {
  font-size: 36px;
  font-weight: 800;
  color: #4299e1;
  letter-spacing: 1.5px;
  display: flex;
  align-items: center;
  text-shadow: 0 2px 4px rgba(66, 153, 225, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-name {
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.logout-btn {
  background: linear-gradient(135deg, #4299e1 0%, #38b2ac 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 22px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.2);
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 153, 225, 0.3);
}

/* 日历+卡片区域 */
.header-row-bottom {
  height: 75%;
  min-height: 320px;
  align-items: stretch;
  justify-content: space-between;
  padding: 20px 50px; /* 减少上下内边距 */
  gap: 40px;
}

/* 日历区域 */
.calendar-block {
  flex: 1.8;
  min-width: 350px;
  max-width: 480px;
  background: linear-gradient(145deg, #ffffff, #f0f7ff);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(67, 153, 225, 0.15);
  padding: 18px 15px 15px 15px; /* 减少内边距 */
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: stretch;
  border: 1px solid rgba(66, 153, 225, 0.1);
  overflow: hidden; /* 防止溢出 */
}

.calendar-month-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px; /* 减少间距 */
  gap: 15px;
}

.calendar-month {
  font-size: 20px; /* 减小字体 */
  font-weight: 700;
  color: #2d3748;
  cursor: pointer;
  padding: 6px 14px; /* 减小内边距 */
  border-radius: 12px;
  transition: all 0.3s;
  background: rgba(66, 153, 225, 0.08);
  min-width: 160px; /* 减小宽度 */
  text-align: center;
}

.calendar-month:hover {
  background: rgba(66, 153, 225, 0.15);
  transform: scale(1.05);
}

.calendar-nav-btn {
  background: #ffffff;
  border: 2px solid #e0e7ff;
  color: #4299e1;
  font-size: 14px; /* 减小图标大小 */
  width: 32px; /* 减小按钮大小 */
  height: 32px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.1);
}

.calendar-nav-btn:hover {
  background: #4299e1;
  color: #ffffff;
  border-color: #4299e1;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.25);
}

/* 自定义迷你日历样式 */
.mini-calendar-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 允许缩小 */
}

.mini-calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px; /* 减少间距 */
  font-weight: 700;
  color: #4a5568;
}

.mini-calendar-weekday {
  text-align: center;
  font-size: 12px; /* 减小字体大小 */
  padding: 6px 0; /* 减小内边距 */
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #4299e1;
}

.mini-calendar-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(6, 1fr); /* 确保6行 */
  gap: 4px; /* 减少间距 */
  min-height: 0; /* 允许网格缩小 */
}

.mini-calendar-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px; /* 减小圆角 */
  background-color: #f8fafc;
  font-size: 13px; /* 减小字体大小 */
  font-weight: 600;
  color: #2d3748;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  padding: 4px 0; /* 减小内边距 */
  min-height: 30px; /* 减小最小高度 */
}

.mini-calendar-cell:hover {
  background-color: #e0f2fe;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.15);
}

.mini-calendar-cell.today {
  background: linear-gradient(135deg, #4299e1, #38b2ac);
  color: white !important;
  font-weight: 800;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.3);
  font-size: 14px; /* 稍微增大今天标记 */
}

.mini-calendar-cell.selected {
  background: linear-gradient(135deg, #9f7aea, #4299e1);
  color: white !important;
  font-weight: 800;
  box-shadow: 0 2px 8px rgba(159, 122, 234, 0.3);
  font-size: 14px; /* 稍微增大选中标记 */
}

.mini-calendar-cell.other-month {
  color: #cbd5e0 !important;
  background-color: #f7fafc;
  cursor: default;
}

.mini-calendar-cell.other-month:hover {
  background-color: #f7fafc;
  transform: none;
  box-shadow: none;
}

/* 事件点样式 */
.mini-dot-container {
  display: flex;
  gap: 2px; /* 减小间距 */
  margin-top: 2px; /* 减小上边距 */
  justify-content: center;
}

.mini-dot {
  width: 4px; /* 减小点大小 */
  height: 4px;
  border-radius: 50%;
  display: inline-block;
}

/* 数据卡片区域 */
.data-card-block {
  flex: 2.2;
  display: flex;
  gap: 25px; /* 减小卡片间距 */
  align-items: stretch;
  justify-content: space-between;
}

.data-card {
  flex: 1;
  background: linear-gradient(145deg, #ffffff, #f8fafc);
  border-radius: 18px; /* 减小圆角 */
  padding: 24px 20px; /* 减小内边距 */
  color: #333;
  box-shadow: 0 8px 25px rgba(67, 153, 225, 0.12);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  min-width: 180px; /* 减小最小宽度 */
  min-height: 120px; /* 减小最小高度 */
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(66, 153, 225, 0.1);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.data-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 15px 35px rgba(66, 153, 225, 0.2);
}

.data-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--gradient-start), var(--gradient-end));
  z-index: 1;
}

.card-todo {
  --gradient-start: #4299e1;
  --gradient-end: #38b2ac;
}

.card-bill {
  --gradient-start: #38b2ac;
  --gradient-end: #48bb78;
}

.card-today {
  --gradient-start: #9f7aea;
  --gradient-end: #4299e1;
}

.card-title {
  font-size: 18px; /* 减小标题字体 */
  font-weight: 700;
  margin-bottom: 15px; /* 减小下边距 */
  color: #2d3748;
  position: relative;
  padding-bottom: 10px; /* 减小内边距 */
  z-index: 1;
  width: 100%;
}

.card-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 35px; /* 减小下划线宽度 */
  height: 3px;
  background: linear-gradient(90deg, var(--gradient-start), var(--gradient-end));
  border-radius: 3px;
  transition: width 0.3s;
}

.data-card:hover .card-title::after {
  width: 65px; /* 减小悬停时下划线宽度 */
}

.card-content {
  font-size: 15px; /* 减小内容字体 */
  line-height: 2; /* 调整行高 */
  color: #4a5568;
  z-index: 1;
  width: 100%;
}

.text-green {
  color: #38b2ac;
  font-weight: 800;
  font-size: 18px; /* 减小数字字体 */
  margin: 0 4px;
  text-shadow: 0 0 8px rgba(56, 178, 172, 0.25);
}

.text-red {
  color: #e53e3e;
  font-weight: 800;
  font-size: 18px; /* 减小数字字体 */
  margin: 0 4px;
  text-shadow: 0 0 8px rgba(229, 62, 62, 0.25);
}

.text-blue {
  color: #4299e1;
  font-weight: 800;
  font-size: 18px; /* 减小数字字体 */
  margin: 0 4px;
  text-shadow: 0 0 8px rgba(66, 153, 225, 0.25);
}

/* 主内容区域 */
.main-content-container {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  background: #f8fafc;
}

.sidebar-menu {
  background: #ffffff;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.05);
  z-index: 1;
  padding-top: 20px;
}

.sidebar-menu .el-menu {
  border: none;
  background: transparent;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-submenu__title {
  height: 56px;
  line-height: 56px;
  font-size: 16px;
  color: #4a5568;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-submenu__title:hover {
  background-color: #e0f2fe;
  color: #4299e1;
  transform: translateX(4px);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(90deg, #e0f2fe, #ffffff);
  color: #4299e1;
  font-weight: 600;
  border-left: 4px solid #4299e1;
}

.sidebar-menu .el-menu-item-group__title {
  padding: 8px 20px;
  font-size: 12px;
  color: #a0aec0;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 600;
}

.sidebar-menu .el-icon {
  font-size: 18px;
  margin-right: 12px;
}

.main-content {
  padding: 30px;
  background: #f8fafc;
  overflow-y: auto;
}

.welcome-message {
  max-width: 600px;
  margin: 80px auto;
  text-align: center;
  padding: 40px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.welcome-message h2 {
  color: #2d3748;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #4299e1, #38b2ac);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-message p {
  color: #718096;
  font-size: 18px;
  line-height: 1.6;
}
</style>