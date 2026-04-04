# 待办事项添加问题排查指南

## 问题现象
```json
{"code":500,"message":"服务器内部错误：could not execute statement 
[Column 'date' cannot be null]"}
```

## 已修复内容

### 前端修复
1. **validateDateTime 函数**: 确保 date 不为空字符串
2. **调试输出**: 添加 `console.log('验证后的日期:', formData.date)`
3. **默认值设置**: handleAdd 中设置默认日期为今天

### 后端修复
1. **Controller 日志**: 添加详细的调试输出
2. **双重检查**: 确保 date 字段有值

## 测试步骤

### 1. 重启后端服务
```bash
cd D:\html\web_work\todo\DailyMate\backend\dailymate
mvn spring-boot:run
```

### 2. 刷新浏览器
按 `Ctrl+Shift+R` 强制刷新缓存

### 3. 打开浏览器控制台
按 `F12` 打开开发者工具

### 4. 添加待办事项
1. 点击"添加待办"按钮
2. 填写标题（必填）
3. 其他字段保持默认值
4. 点击确定

### 5. 查看前端控制台输出
应该看到：
```javascript
提交数据：{
  "userId": 1,
  "title": "测试",
  "content": "",
  "priority": 2,
  "status": 0,
  "date": "2026-04-01",
  "startTime": "2026-04-01 21:30:00",
  "endTime": "2026-04-01 21:31:00"
}
验证后的日期：2026-04-01
```

### 6. 查看后端控制台输出
应该看到：
```
=== 接收到添加待办请求 ===
request.getDate(): Wed Apr 01 00:00:00 CST 2026
request.getTitle(): 测试
使用 request 的 date: Wed Apr 01 00:00:00 CST 2026
=== 准备保存 ===
todo.getDate(): Wed Apr 01 00:00:00 CST 2026
```

## 问题排查

### 如果前端 date 为空字符串
检查 `validateDateTime()` 函数是否执行：
```javascript
// 确保日期存在且不为空字符串
if (!formData.date || formData.date === '') {
  formData.date = today
}
```

### 如果后端收到 null
检查 `@JsonFormat` 注解：
```java
@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
private Date date;
```

### 如果日期格式不正确
检查前端日期格式：
```javascript
formData.date = today // "2026-04-01"
```

## 常见错误

### 错误 1: 日期格式为 "2026/04/01"
**原因**: 浏览器区域设置问题
**解决**: 使用 `toISOString().split('T')[0]` 生成标准格式

### 错误 2: 日期字段为空字符串
**原因**: 表单初始化时 date 为 `''`
**解决**: `validateDateTime()` 函数检查并设置默认值

### 错误 3: 后端解析失败
**原因**: 时区问题
**解决**: `@JsonFormat` 设置 `timezone = "Asia/Shanghai"`

## 验证成功标志

✅ 前端控制台显示有效的日期
✅ 后端控制台显示收到日期
✅ 数据库插入成功
✅ 显示"添加成功"消息
