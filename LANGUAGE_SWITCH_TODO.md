# DailyMate 语言切换功能修复任务

**创建时间**: 2026-04-02
**任务优先级**: 高
**预计难度**: 中等

---

## 📋 任务描述

修复 DailyMate 项目的 Element Plus 多语言切换功能。当前问题是：
- 用户在设置页面切换语言后，页面刷新时语言设置丢失，总是恢复为中文
- Element Plus 组件的语言没有正确切换

---

## 🔍 当前状态

### 已完成的工作

1. **Element Plus 语言包导入** - 已在 `main.js` 中静态导入所有语言包
2. **localStorage 保存** - 语言设置保存到 `localStorage.dailyMate_language`
3. **页面刷新机制** - 切换语言后自动刷新页面
4. **Settings.vue 修改** - 语言切换时只更新 localStorage，不保存到后端

### 已修复的问题 (2026-04-02)

1. **添加 URL 参数作为备用方案** - 修改 `main.js` 和 `locales/index.js`，语言切换时通过 URL 参数传递语言代码，确保刷新后不会丢失
2. **修复 ES 模块问题** - `App.vue` 和 `settings.js` 中将 `require()` 改为 ES6 `import` 语句
3. **增强调试日志** - 在 `main.js` 中添加详细的启动日志

### 仍需验证

1. 语言切换后 Element Plus 组件是否正确显示英文/日文/韩文
2. 刷新页面后语言选择器是否保持正确的语言选项

---

## 📁 相关文件

### 已修改的文件

1. **`frontend/src/main.js`**
   - 静态导入所有 Element Plus 语言包
   - 根据 localStorage 配置 Element Plus 语言

2. **`frontend/src/App.vue`**
   - 移除了动态加载语言包的逻辑
   - 简化为纯 UI 组件

3. **`frontend/src/locales/index.js`**
   - `setLocale()` 函数先保存 localStorage，再刷新页面
   - 添加了详细的调试日志

4. **`frontend/src/views/Settings.vue`**
   - `handleLanguageChange()` 只更新 localStorage，不保存到后端
   - `loadSettings()` 从 localStorage 读取语言，不从后端

---

## 🐛 问题诊断

### 控制台日志（切换语言时）
```
[Settings.vue] 用户切换语言：en-US
[locales/index.js] 尝试切换语言：en-US
[locales/index.js] 已保存语言到 localStorage: en-US
[locales/index.js] 验证 localStorage: en-US
[locales/index.js] 语言切换成功，将刷新页面
```

### 问题现象
- 切换语言时提示显示正确（如"语言已切换为 English"）
- 但页面刷新后，语言又变回中文
- 控制台没有显示读取到正确的语言

---

## ✅ 建议的解决方案

### 方案 1：检查 main.js 执行时机

**问题假设**: `main.js` 在 localStorage 保存完成前就执行了

**修复方法**:
```javascript
// frontend/src/main.js
// 在应用启动前确保 localStorage 已就绪
const savedLanguage = localStorage.getItem('dailyMate_language') || 'zh-CN'
console.log('[main.js] 从 localStorage 读取语言:', savedLanguage)
```

### 方案 2：使用同步方式保存

**问题假设**: `localStorage.setItem` 是异步的，刷新时还没保存完成

**修复方法**:
```javascript
// frontend/src/locales/index.js
export function setLocale(locale) {
  if (supportedLanguages.some(lang => lang.code === locale)) {
    // 同步保存
    localStorage.setItem('dailyMate_language', locale)
    
    // 强制同步
    const check = localStorage.getItem('dailyMate_language')
    if (check !== locale) {
      console.error('localStorage 保存失败')
      return false
    }
    
    // 延迟刷新确保保存完成
    setTimeout(() => {
      window.location.reload()
    }, 1000)  // 增加延迟时间
    return true
  }
  return false
}
```

### 方案 3：使用 URL 参数传递语言

**问题假设**: localStorage 在刷新时不可靠

**修复方法**:
```javascript
// 切换语言时
setLocale(locale) {
  localStorage.setItem('dailyMate_language', locale)
  // 通过 URL 参数传递
  window.location.href = `?lang=${locale}`
}

// main.js 中读取
const urlParams = new URLSearchParams(window.location.search)
const savedLanguage = urlParams.get('lang') || localStorage.getItem('dailyMate_language') || 'zh-CN'
```

### 方案 4：完全移除 Element Plus 多语言

**问题假设**: Element Plus 语言切换机制复杂，容易出错

**修复方法**:
- 只使用 vue-i18n 切换应用文本
- Element Plus 组件保持默认语言
- 通过 CSS 和自定义翻译覆盖 Element Plus 文本

---

## 🧪 测试步骤

1. 打开浏览器 http://localhost:3000
2. 按 F12 打开开发者工具
3. 登录账号（test/test123）
4. 进入「设置」页面
5. 切换语言到 English
6. 查看控制台日志
7. 页面刷新后检查：
   - 语言选择器是否显示 English
   - Element Plus 组件是否显示英文
   - localStorage.dailyMate_language 是否为 en-US

---

## 📝 调试要点

1. **检查 localStorage 是否可写**
   ```javascript
   localStorage.setItem('test', '123')
   console.log(localStorage.getItem('test'))  // 应该输出 123
   ```

2. **检查 main.js 是否执行**
   ```javascript
   // 在 main.js 开头添加
   console.log('[main.js] 应用启动')
   ```

3. **检查语言包是否正确导入**
   ```javascript
   console.log('en locale:', en)  // 应该显示语言包对象
   ```

---

## 🎯 验收标准

- [ ] 切换语言后页面刷新，语言选择器显示正确的语言
- [ ] Element Plus 组件（按钮、表单、日期选择器等）显示正确的语言
- [ ] 控制台没有 `locale=undefined` 警告
- [ ] localStorage.dailyMate_language 保存正确的语言代码
- [ ] 刷新页面后语言设置保持

---

## 📚 参考资料

- Element Plus 多语言文档：https://element-plus.org/en-US/guide/i18n.html
- Vue I18n 文档：https://vue-i18n.intlify.dev/
- localStorage API：https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage

---

**最后更新**: 2026-04-02
**下一步**: 测试语言切换功能是否正常工作
