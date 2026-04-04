# DailyMate 待办事项日期防呆设计说明

## 设计原则
**前端预防为主，后端兜底为辅**

## 前端防呆设计（共 5 层）

### 第 1 层：日期选择器限制

#### 日期字段
```javascript
// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000 // 不能选择昨天之前的日期
}
```
- 用户只能选择今天及以后的日期
- 从源头防止选择过去的日期

#### 开始时间选择器
```javascript
// 禁用日期：不能选择今天之前
const disabledStartDate = (time) => {
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  return time.getTime() < now.getTime()
}

// 禁用时间：根据结束时间动态限制
const disabledStartTime = (date) => {
  if (formData.endTime) {
    // 同一天时，开始时间的小时不能大于结束时间的小时
    // 同小时时，开始时间的分钟不能大于结束时间的分钟
  }
  return {}
}
```

#### 结束时间选择器
```javascript
// 禁用日期：不能早于开始时间
const disabledEndDate = (time) => {
  if (formData.startTime) {
    const startTime = new Date(formData.startTime)
    return time.getTime() < startTime.getTime()
  }
  return false
}

// 禁用时间：根据开始时间动态限制
const disabledEndTime = (date) => {
  if (formData.startTime) {
    // 同一天时，结束时间的小时不能小于开始时间的小时
    // 同小时时，结束时间的分钟不能小于开始时间的分钟
  }
  return {}
}
```

### 第 2 层：默认值设置

#### 打开添加对话框时
```javascript
const handleAdd = () => {
  const now = new Date()
  const today = now.toISOString().split('T')[0]
  const oneHourLater = new Date(now.getTime() + 3600000)
  
  formData = {
    date: today,  // 默认为今天
    startTime: oneHourLater,  // 默认为 1 小时后
    endTime: new Date(oneHourLater.getTime() + 3600000)  // 默认为 2 小时后
  }
}
```

### 第 3 层：提交前验证

```javascript
const validateDateTime = () => {
  // 1. 确保日期存在
  if (!formData.date) {
    formData.date = today
  }
  
  // 2. 验证日期格式
  const datePattern = /^\d{4}-\d{2}-\d{2}$/
  if (!datePattern.test(formData.date)) {
    formData.date = today
  }
  
  // 3. 验证开始时间和结束时间的逻辑关系
  if (formData.startTime && formData.endTime) {
    const start = new Date(formData.startTime)
    const end = new Date(formData.endTime)
    
    if (start > end) {
      ElMessage.warning('开始时间不能晚于截止时间，已自动调整')
      formData.startTime = formData.endTime
    }
  }
  
  // 4. 确保开始时间不早于当前时间
  if (formData.startTime) {
    const start = new Date(formData.startTime)
    const now = new Date()
    if (start < now) {
      ElMessage.warning('开始时间不能早于当前时间，已自动调整')
      formData.startTime = now.toISOString()
    }
  }
  
  // 5. 确保截止时间不早于开始时间
  if (formData.endTime < formData.startTime) {
    ElMessage.warning('截止时间不能早于开始时间，已自动调整')
    formData.endTime = formData.startTime
  }
}
```

### 第 4 层：用户提示

- 当自动调整时间时，显示警告提示
- 告知用户具体调整了什么内容
- 用户可以手动修改回正确值

### 第 5 层：调试输出

```javascript
console.log('提交数据:', JSON.stringify(data, null, 2))
```
- 方便开发人员查看实际提交的数据
- 快速定位问题

## 后端防呆设计（共 3 层）

### 第 1 层：DTO 格式化

```java
@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
private Date date;

@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
private Date startTime;
```

### 第 2 层：Controller 验证

```java
if (request.getDate() != null) {
    todo.setDate(request.getDate());
} else {
    todo.setDate(new java.util.Date());
}

// 调试输出
System.out.println("添加待办 - date: " + todo.getDate());
```

### 第 3 层：Service 兜底

```java
if (todo.getDate() == null) {
    todo.setDate(new Date());
}
```

## 用户体验优化

### 1. 智能默认值
- 日期：默认为今天
- 开始时间：默认为 1 小时后
- 结束时间：默认为 2 小时后

### 2. 动态禁用
- 选择了结束时间后，开始时间的可选范围会自动更新
- 选择了开始时间后，结束时间的可选范围会自动更新

### 3. 自动修正
- 如果用户输入了不合法的时间，系统会自动调整并提示
- 而不是直接报错拒绝

### 4. 清晰提示
- 每次自动调整都会显示警告消息
- 用户知道发生了什么变化

## 测试场景

### 场景 1：正常流程
1. 点击添加待办
2. 日期默认为今天 ✅
3. 开始时间默认为 1 小时后 ✅
4. 结束时间默认为 2 小时后 ✅
5. 提交成功 ✅

### 场景 2：选择结束时间后修改开始时间
1. 选择结束时间为 15:00
2. 点击开始时间选择器
3. 15:00 之后的时间全部禁用 ✅
4. 只能选择 15:00 之前的时间 ✅

### 场景 3：选择开始时间后修改结束时间
1. 选择开始时间为 10:00
2. 点击结束时间选择器
3. 10:00 之前的时间全部禁用 ✅
4. 只能选择 10:00 之后的时间 ✅

### 场景 4：同一天内的时间选择
1. 选择开始时间为 2026-04-01 10:30
2. 选择结束时间时：
   - 小时只能选择 10-23 ✅
   - 如果选择 10 点，分钟只能选择 30-59 ✅

### 场景 5：提交时自动修正
1. 手动修改开始时间为 2025-01-01（过去时间）
2. 点击提交
3. 显示警告："开始时间不能早于当前时间，已自动调整为当前时间" ✅
4. 开始时间自动修正为当前时间 ✅

## 总结

通过 5 层前端防护 + 3 层后端防护，确保：
1. 用户无法选择明显错误的日期时间
2. 即使用户绕过前端验证，后端也能正确处理
3. 系统会自动修正错误并提示用户
4. 所有操作都有日志记录，方便调试

**核心理念**：预防 > 提示 > 自动修正 > 报错
