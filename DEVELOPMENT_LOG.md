# DailyMate 项目开发记录

**最后更新时间**: 2026-04-02  
**项目位置**: `D:\html\web_work\todo\DailyMate`

---

## 📋 项目概述

DailyMate 是一款全栈个人管理应用，提供待办事项管理、账单记账、日历视图和数据统计等功能。

### 技术栈
- **前端**: Vue 3 + Vite + Element Plus + Pinia + vue-i18n
- **后端**: Spring Boot 3.2 + Spring Data JPA + MySQL 8.0 + JWT
- **部署**: Docker + Docker Compose

---

## ✅ 已完成的功能

### 1. 待办提醒功能

#### 后端修改
| 文件 | 修改内容 |
|------|----------|
| `entity/Todo.java` | 添加 4 个提醒字段：`reminderEnabled`, `reminderOffset`, `reminderTime`, `isReminded` |
| `dto/request/TodoRequest.java` | 添加提醒相关 DTO 字段 |
| `repository/TodoRepository.java` | 添加 `findByReminderEnabledTrueAndIsRemindedFalse()` 方法 |
| `service/ReminderService.java` | 新建提醒服务，包含 SSE 消息推送、定时任务检查 |
| `controller/TodoController.java` | 添加提醒 API：`/remind/stream/{userId}`, `/remind/set/{id}`, `/remind/cancel/{id}`, `/remind/ack/{id}` |
| `DailymateApplication.java` | 添加 `@EnableScheduling` 启用定时任务 |

#### 前端修改
| 文件 | 修改内容 |
|------|----------|
| `api/todo.js` | 添加提醒相关 API 函数 |
| `views/Todos.vue` | 添加提醒列显示、提醒设置按钮、提醒设置对话框、SSE 连接逻辑、浏览器通知 |

#### 数据库变更
- 执行 `docs/mysql/update_reminder.sql` 添加提醒字段

---

### 2. 用户设置功能

#### 后端新增
| 文件 | 说明 |
|------|------|
| `entity/UserSettings.java` | 用户设置实体类 |
| `repository/UserSettingsRepository.java` | 设置数据访问层 |
| `service/UserSettingsService.java` | 设置业务逻辑层 |
| `controller/SettingsController.java` | 设置 API 控制器 |
| `dto/request/UserSettingsRequest.java` | 设置请求 DTO |

#### 设置字段
| 字段 | 类型 | 说明 |
|------|------|------|
| `language` | String | 语言：zh-CN, en-US, ja-JP, ko-KR |
| `theme` | String | 主题：light, dark, blue, green, purple |
| `backgroundType` | String | 背景类型：theme, image |
| `backgroundUrl` | String | 背景图 URL |
| `backgroundOpacity` | Integer | 背景透明度 0-100 |
| `backgroundSize` | String | 背景尺寸：cover, contain, 100%, auto 100% |
| `backgroundPosition` | String | 背景位置：center, left top 等 |
| `notificationEnabled` | Boolean | 是否启用通知 |
| `soundEnabled` | Boolean | 是否启用声音提醒 |
| `animationEnabled` | Boolean | 是否启用动画效果 |
| `defaultPriority` | Integer | 默认优先级 1-3 |
| `defaultReminderOffset` | Integer | 默认提醒时间（分钟） |

#### 前端新增
| 文件 | 说明 |
|------|------|
| `stores/settings.js` | 设置状态管理，包含主题配置、预设背景图 |
| `api/settings.js` | 设置相关 API |
| `views/Settings.vue` | 设置页面组件 |
| `locales/*.js` | 多语言文件（zh-CN, en-US, ja-JP, ko-KR） |
| `locales/index.js` | i18n 配置 |

#### 全局样式
| 文件 | 修改内容 |
|------|----------|
| `App.vue` | 添加背景层实现，支持透明度调节 |
| `views/Layout.vue` | 移除背景样式，由 App.vue 统一管理 |

#### 数据库变更
- 执行 `docs/mysql/add_user_settings.sql` 创建用户设置表

---

### 3. 多语言国际化 (i18n)

#### 支持的语言
- 🇨🇳 简体中文 (zh-CN)
- 🇺🇸 English (en-US)
- 🇯🇵 日本語 (ja-JP)
- 🇰🇷 한국어 (ko-KR)

#### 翻译覆盖的模块
- 通用文本（确认、取消、保存等）
- 导航菜单
- 登录/注册页面
- 仪表盘
- 待办事项页面
- 账单管理页面
- 设置页面

---

### 4. 主题切换功能

#### 预设主题
| 主题名 | 说明 | 背景渐变 |
|--------|------|----------|
| light | 明亮主题 | #f5f7fa → #e4edf5 |
| dark | 黑暗主题 | #1a1a2e → #16213e |
| blue | 蓝色主题 | #1e3a8a → #3b82f6 → #60a5fa |
| green | 绿色主题 | #064e3b → #10b981 → #34d399 |
| purple | 紫色主题 | #581c87 → #8b5cf6 → #a78bfa |

#### 主题应用方式
- 通过 CSS 变量动态更新
- 在 `<head>` 中注入动态样式
- 支持平滑过渡动画

---

### 5. 背景图功能

#### 预设背景
- 默认渐变
- 自然风光
- 海洋
- 山川
- 日落
- 星空
- 简约
- 森林

#### 自定义背景
- 支持本地图片上传（Base64 存储）
- 支持图片 URL 输入
- 图片预览功能
- 支持清除自定义

#### 背景调节
- **透明度**: 0-100% 滑块调节
- **尺寸**: Cover/Contain/100%/Auto 100%
- **位置**: 居中/左上/右上/左下/右下

---

## 📁 新增文件清单

### 后端
```
backend/dailymate/src/main/java/com/joe/dailymate/
├── entity/
│   ├── UserSettings.java          # 用户设置实体
│   └── Todo.java                  # (已修改)
├── repository/
│   └── UserSettingsRepository.java # 设置 Repository
├── service/
│   ├── UserSettingsService.java   # 设置服务
│   └── ReminderService.java       # 提醒服务
├── controller/
│   └── SettingsController.java    # 设置控制器
└── dto/request/
    └── UserSettingsRequest.java   # 设置请求 DTO
```

### 前端
```
frontend/src/
├── locales/
│   ├── index.js                   # i18n 配置
│   ├── zh-CN.js                   # 中文语言包
│   ├── en-US.js                   # 英文语言包
│   ├── ja-JP.js                   # 日文语言包
│   └── ko-KR.js                   # 韩文语言包
├── stores/
│   └── settings.js                # 设置状态管理
├── api/
│   └── settings.js                # 设置 API
└── views/
    └── Settings.vue               # 设置页面
```

### 数据库脚本
```
docs/mysql/
├── add_user_settings.sql          # 创建用户设置表
├── update_reminder.sql            # 添加提醒字段
└── add_reminder_fields.sql        # (备用) 提醒字段脚本
```

---

## 🔧 配置修改

### package.json
- 添加 `vue-i18n@^9.14.5` 依赖

### vite.config.js
- 添加 `vue-i18n` 到依赖预构建

### 主要组件修改
| 文件 | 修改内容 |
|------|----------|
| `frontend/src/main.js` | 添加 i18n 配置 |
| `frontend/src/App.vue` | 添加背景层实现 |
| `frontend/src/views/Layout.vue` | 添加设置菜单项和路由 |
| `frontend/src/router/index.js` | 添加 `/settings` 路由 |

---

## 🚀 服务启动命令

### 后端
```bash
cd D:\html\web_work\todo\DailyMate\backend\dailymate
mvn spring-boot:run
```
- 端口：8080
- 地址：http://localhost:8080

### 前端
```bash
cd D:\html\web_work\todo\DailyMate\frontend
npm run dev
```
- 端口：3000
- 地址：http://localhost:3000

---

## 📝 待办事项清单

### 高优先级
- [ ] 执行数据库脚本创建设置表和提醒字段
- [ ] 测试语言切换后 Element Plus 组件语言同步
- [ ] 优化背景图片上传（压缩、裁剪）

### 中优先级
- [ ] 添加待办重复规则功能
- [ ] 实现账单预算功能
- [ ] 添加数据导出功能（Excel/CSV）

### 低优先级
- [ ] 实现番茄钟功能
- [ ] 添加习惯打卡功能
- [ ] 实现四象限视图

---

## 🐛 已知问题

1. **语言切换**: Element Plus 组件语言未同步切换（需配置 Element Plus 的 locale）
2. **背景图片**: Base64 格式存储大图片可能导致 localStorage 超限
3. **提醒功能**: 需要数据库脚本执行后才能正常工作

---

## 📚 相关文档

- [README.md](./README.md) - 项目说明
- [DEPLOY.md](./DEPLOY.md) - 部署指南
- [DEPLOY_DOCKER.md](./DEPLOY_DOCKER.md) - Docker 部署指南

---

## 🔄 下次继续任务

1. 执行数据库脚本：
   ```bash
   mysql -u root -p dailymate < docs/mysql/add_user_settings.sql
   mysql -u root -p dailymate < docs/mysql/update_reminder.sql
   ```

2. 测试所有设置功能是否正常工作

3. 优化 Element Plus 多语言支持

---

**记录人**: AI Assistant  
**记录时间**: 2026-04-02
