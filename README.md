# DailyMate - 个人日常管理工具

> 一个现代化的个人日常管理工具，帮助您轻松管理待办事项和账单收支。

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Vue 3](https://img.shields.io/badge/vue-3.4-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/docker-ready-blue.svg)](https://www.docker.com/)

## 📋 项目简介

DailyMate 是一款全栈个人管理应用，提供待办事项管理、账单记账、日历视图和数据统计等功能。项目采用前后端分离架构，前端使用 Vue 3 + Element Plus，后端使用 Spring Boot + MySQL。

## ✨ 主要功能

### 待办事项管理
- ✅ 添加、编辑、删除待办事项
- 📊 按状态筛选（全部/已完成/未完成）
- 🔍 关键字搜索
- 🎯 优先级管理（高/中/低）
- ⏰ 开始时间和截止时间设置
- ✔️ 批量完成/批量删除

### 账单管理
- 💰 收入/支出记录
- 📁 分类管理（餐饮、购物、交通等）
- 📈 月度统计和趋势分析
- 🔍 关键字搜索
- 📊 分类统计图表
- ✔️ 批量删除

### 用户系统
- 🔐 JWT 认证
- 👤 用户注册/登录
- 🛡️ 密码加密存储
- 📧 邮箱绑定
- 🖼️ 头像设置

### 仪表盘
- 📅 日历视图
- 📊 数据统计卡片
- 📈 待办完成情况
- 💰 月度收支概览
- 📆 今日任务概览

## 🛠️ 技术栈

### 前端
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **日期处理**: Day.js
- **样式**: SCSS

### 后端
- **框架**: Spring Boot 3.2
- **ORM**: Spring Data JPA
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **文档**: SpringDoc OpenAPI
- **工具**: Lombok

### 部署
- **容器化**: Docker + Docker Compose
- **Web 服务器**: Nginx
- **日志**: Logback

## 📦 项目结构

```
DailyMate/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API 接口
│   │   ├── components/         # 公共组件
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia 状态管理
│   │   ├── styles/             # 全局样式
│   │   ├── utils/              # 工具函数
│   │   ├── views/              # 页面组件
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── .editorconfig           # 编辑器配置
│   ├── .env.*                  # 环境变量配置
│   ├── .eslintrc.cjs           # ESLint 配置
│   ├── Dockerfile              # Docker 构建文件
│   ├── nginx.conf              # Nginx 配置
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── backend/
│   └── dailymate/              # 后端项目
│       └── src/main/java/com/joe/dailymate/
│           ├── common/         # 公共类（Result、异常处理）
│           ├── config/         # 配置类（CORS、JWT、Security）
│           ├── controller/     # 控制器
│           ├── dto/            # 数据传输对象
│           ├── entity/         # 实体类
│           ├── filter/         # 过滤器
│           ├── repository/     # 数据访问层
│           ├── service/        # 业务逻辑层
│           └── util/           # 工具类
│       ├── Dockerfile
│       └── pom.xml
│
├── docs/
│   └── mysql/
│       └── init.sql            # 数据库初始化脚本
│
├── docker-compose.yml          # Docker Compose 配置
├── .env.example                # 环境变量示例
├── DEPLOY.md                   # 部署文档
├── DEPLOY_DOCKER.md            # Docker 部署文档
└── README.md
```

## 🚀 快速开始

### 方式一：Docker 部署（推荐）

```bash
# 1. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，修改密码和 JWT 密钥

# 2. 一键启动
docker-compose up -d

# 3. 访问应用
# 前端：http://localhost
# 后端：http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

详细文档：[Docker 部署指南](./DEPLOY_DOCKER.md)

### 方式二：本地开发

#### 环境要求
- Node.js >= 18
- JDK 17
- MySQL 8.0
- Maven 3.6+

#### 1. 数据库配置

```sql
CREATE DATABASE dailymate DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行初始化脚本：
```bash
mysql -u root -p dailymate < docs/mysql/init.sql
```

#### 2. 后端启动

```bash
cd backend/dailymate

# 配置数据库密码（编辑 application.yml 或使用环境变量）
# export DB_PASSWORD=your_password

# 运行
mvn spring-boot:run
```

#### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev
```

访问 http://localhost:3000

## 📖 API 文档

启动后端后访问：http://localhost:8080/swagger-ui.html

### 主要接口

#### 认证
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 退出登录

#### 待办事项
- `GET /api/todo/list` - 获取列表
- `POST /api/todo/add` - 添加待办
- `PUT /api/todo/update` - 更新待办
- `DELETE /api/todo/delete/{id}` - 删除待办
- `PUT /api/todo/batch/finish` - 批量完成
- `PUT /api/todo/batch/delete` - 批量删除

#### 账单
- `GET /api/bill/list` - 获取列表
- `POST /api/bill/add` - 添加账单
- `PUT /api/bill/update` - 更新账单
- `DELETE /api/bill/delete/{id}` - 删除账单
- `GET /api/bill/stat/month` - 月度统计
- `GET /api/bill/stat/category` - 分类统计

## 🔧 开发命令

### 前端
```bash
npm run dev      # 开发模式
npm run build    # 生产构建
npm run preview  # 预览构建
npm run lint     # 代码检查
```

### 后端
```bash
mvn clean install           # 构建
mvn spring-boot:run         # 运行
mvn spring-boot:run -Dspring-boot.run.profiles=prod  # 生产环境
mvn test                    # 测试
```

## 📝 部署文档

- [传统部署指南](./DEPLOY.md)
- [Docker 部署指南](./DEPLOY_DOCKER.md)

## 🔒 安全说明

- 密码使用 BCrypt 加密存储
- JWT Token 有效期为 24 小时
- 所有 API 接口需要认证（除登录注册）
- 生产环境必须修改 `.env` 中的默认密码和 JWT_SECRET
- 建议在生产环境启用 HTTPS

## 🎨 开发规范

### 前端开发规范
- 使用 Composition API
- 组件采用 `<script setup>` 语法
- 状态管理使用 Pinia
- API 调用统一封装在 `src/api` 目录
- 代码风格遵循 ESLint 配置

### 后端开发规范
- Controller 层只处理请求和响应
- 业务逻辑全部在 Service 层
- 统一使用 `Result<T>` 返回格式
- 参数校验使用 `@Validated`
- 日志使用 SLF4J

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

MIT License

## 👨‍💻 作者

DailyMate Team

## 📮 联系方式

如有问题或建议，欢迎通过 Issue 联系我们。

---

**Happy Coding! 🎉**
