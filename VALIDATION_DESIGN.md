# DailyMate 全面防呆设计文档

## 设计理念
**预防 > 提示 > 自动修正 > 报错**

## 待办事项表单 - 7 层防护体系

### 第 1 层：HTML 原生限制
```vue
<!-- 标题：最大长度限制 -->
<el-input maxlength="100" show-word-limit />

<!-- 内容：最大长度限制 -->
<el-input maxlength="500" show-word-limit />
```
- 用户无法输入超过限制的字符
- 实时显示剩余字数

### 第 2 层：Element Plus 表单验证
```javascript
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1-100 个字符之间', trigger: 'blur' }
  ],
  content: [
    { max: 500, message: '内容长度不能超过 500 个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  date: [
    { required: true, message: '请选择日期', trigger: 'change' }
  ]
}
```
- 必填项验证
- 长度范围验证
- 实时触发验证

### 第 3 层：自定义业务验证
```javascript
// 标题验证
const validateTitle = () => {
  if (!formData.title || !formData.title.trim()) {
    ElMessage.error('标题不能为空')
    return false
  }
  if (formData.title.length > 100) {
    ElMessage.error('标题长度不能超过 100 个字符')
    return false
  }
  // 移除首尾空格和危险字符
  formData.title = formData.title.trim().replace(/[<>"\'&]/g, '')
  return true
}

// 内容验证
const validateContent = () => {
  if (formData.content && formData.content.length > 500) {
    ElMessage.error('内容长度不能超过 500 个字符')
    return false
  }
  // 移除危险字符
  if (formData.content) {
    formData.content = formData.content.trim().replace(/[<>"\'&]/g, '')
  }
  return true
}

// 优先级验证
const validatePriority = () => {
  if (!formData.priority || ![1, 2, 3].includes(formData.priority)) {
    ElMessage.error('请选择正确的优先级')
    formData.priority = 2 // 默认为中
    return false
  }
  return true
}

// 状态验证
const validateStatus = () => {
  if (!formData.status || ![0, 1].includes(formData.status)) {
    ElMessage.error('请选择正确的状态')
    formData.status = 0 // 默认为未完成
    return false
  }
  return true
}
```

### 第 4 层：日期时间逻辑验证
```javascript
// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 86400000
}

// 开始时间禁用逻辑
const disabledStartDate = (time) => {
  // 不能选择今天之前
  // 根据结束时间动态禁用小时和分钟
}

// 结束时间禁用逻辑
const disabledEndDate = (time) => {
  // 不能选择今天之前
  // 根据开始时间动态禁用小时和分钟
}
```

### 第 5 层：智能默认值
```javascript
const handleAdd = () => {
  const now = new Date()
  const today = now.toISOString().split('T')[0]
  const oneHourLater = new Date(now.getTime() + 3600000)
  
  formData = {
    title: '',
    content: '',
    priority: 2,        // 默认为中
    status: 0,          // 默认为未完成
    date: today,        // 默认为今天
    startTime: oneHourLater,     // 1 小时后
    endTime: new Date(oneHourLater.getTime() + 3600000)  // 2 小时后
  }
}
```

### 第 6 层：提交前全面验证
```javascript
const handleSubmit = async () => {
  // 第一层：Element Plus 表单验证
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('请检查表单填写是否正确')
      return
    }

    // 第二层：自定义业务验证
    if (!validateAllInputs()) {
      ElMessage.error('请修正输入内容后重新提交')
      return
    }

    // 第三层：防呆设计：验证日期时间逻辑
    validateDateTime()

    // 验证通过，提交数据
    ...
  })
}
```

### 第 7 层：XSS 防护
```javascript
// 移除危险字符，防止 XSS 攻击
formData.title = formData.title.trim().replace(/[<>"\'&]/g, '')
formData.content = formData.content.trim().replace(/[<>"\'&]/g, '')
```

## 输入字段详细验证规则

### 标题（title）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 必填 | 不能为空 | "标题不能为空" |
| 最小长度 | 1 字符 | "标题长度在 1-100 个字符之间" |
| 最大长度 | 100 字符 | "标题长度不能超过 100 个字符" |
| 特殊字符 | 过滤 `<>\"'&` | 自动过滤 |
| 空格 | 自动去除首尾空格 | 自动处理 |

### 内容（content）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 可选 | 可以为空 | - |
| 最大长度 | 500 字符 | "内容长度不能超过 500 个字符" |
| 特殊字符 | 过滤 `<>\"'&` | 自动过滤 |
| 空格 | 自动去除首尾空格 | 自动处理 |

### 优先级（priority）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 必填 | 必须选择 | "请选择正确的优先级" |
| 有效值 | 1, 2, 3 | 自动修正为 2（中） |
| 默认值 | 2（中） | - |

### 状态（status）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 必填 | 必须选择 | "请选择正确的状态" |
| 有效值 | 0, 1 | 自动修正为 0（未完成） |
| 默认值 | 0（未完成） | - |

### 日期（date）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 必填 | 必须选择 | "请选择日期" |
| 格式 | YYYY-MM-DD | 自动修正为今天 |
| 范围 | 不能早于昨天 | 选择器禁用 |
| 默认值 | 今天 | - |

### 开始时间（startTime）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 可选 | 可以为空 | - |
| 格式 | YYYY-MM-DD HH:mm:ss | 自动格式化 |
| 范围 | 不能早于当前时间 | 自动修正 + 警告 |
| 逻辑 | 不能晚于结束时间 | 自动修正 + 警告 |
| 默认值 | 1 小时后 | - |

### 截止时间（endTime）
| 验证项 | 规则 | 错误提示 |
|--------|------|----------|
| 可选 | 可以为空 | - |
| 格式 | YYYY-MM-DD HH:mm:ss | 自动格式化 |
| 范围 | 不能早于今天 | 选择器禁用 |
| 逻辑 | 不能早于开始时间 | 自动修正 + 警告 |
| 默认值 | 2 小时后 | - |

## 用户体验优化

### 1. 实时反馈
- 输入时显示字数统计
- 失焦时立即验证
- 选择时实时提示

### 2. 智能提示
```vue
<el-option label="🔴 高" :value="1" />
<el-option label="🟡 中" :value="2" />
<el-option label="🟢 低" :value="3" />
<el-option label="🔵 未完成" :value="0" />
<el-option label="✅ 已完成" :value="1" />
```
- 使用 emoji 图标增强识别
- 颜色编码（红黄绿蓝）

### 3. 自动修正
- 时间逻辑错误自动调整
- 显示警告告知用户
- 保留用户原始意图

### 4. 错误提示分级
- **Error**: 阻止提交（必填项、格式错误）
- **Warning**: 允许提交但提示（时间自动调整）
- **Info**: 纯提示信息

## 后端验证（3 层防护）

### 第 1 层：DTO 验证
```java
@NotBlank(message = "标题不能为空")
@Size(max = 100, message = "标题长度不能超过 100 个字符")
private String title;

@Size(max = 500, message = "内容长度不能超过 500 个字符")
private String content;

@NotNull(message = "优先级不能为空")
private Integer priority;

@NotNull(message = "状态不能为空")
private Integer status;
```

### 第 2 层：Controller 验证
```java
// 确保 date 字段不为 null
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
// 最后一道防线
if (todo.getDate() == null) {
    todo.setDate(new Date());
}
```

## 测试清单

### 必填项测试
- [ ] 标题为空 → 阻止提交
- [ ] 优先级未选 → 阻止提交
- [ ] 状态未选 → 阻止提交
- [ ] 日期未选 → 阻止提交

### 长度限制测试
- [ ] 标题 101 字符 → 阻止输入
- [ ] 内容 501 字符 → 阻止输入
- [ ] 标题 1 字符 → 允许
- [ ] 标题 100 字符 → 允许

### 特殊字符测试
- [ ] 标题包含 `<script>` → 自动过滤
- [ ] 内容包含 `"` → 自动过滤
- [ ] 标题包含空格 → 自动去除

### 时间逻辑测试
- [ ] 开始时间 > 结束时间 → 自动调整
- [ ] 开始时间 < 当前时间 → 自动调整
- [ ] 结束时间 < 开始时间 → 自动调整
- [ ] 选择过去日期 → 选择器禁用

### 默认值测试
- [ ] 打开表单 → 日期为今天
- [ ] 打开表单 → 优先级为中
- [ ] 打开表单 → 状态为未完成
- [ ] 打开表单 → 开始时间 1 小时后
- [ ] 打开表单 → 结束时间 2 小时后

## 总结

通过 **7 层前端防护 + 3 层后端防护**，确保：
1. ✅ 用户无法输入非法数据
2. ✅ 输入错误实时提示
3. ✅ 逻辑错误自动修正
4. ✅ 危险字符自动过滤
5. ✅ 默认值合理智能
6. ✅ 后端兜底验证
7. ✅ 调试信息完整

**核心理念**：让用户无法犯错，即使犯错也能自动修正！
