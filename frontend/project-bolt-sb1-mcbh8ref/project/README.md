# 用户管理系统

这是一个基于 Vue 3 + TypeScript + Vite 开发的用户管理前端系统。

## 功能特性

- 用户登录认证
- 用户列表展示
- 编辑用户信息
- 修改用户密码
- 删除用户账号
- 现代化响应式UI设计

## 技术栈

- Vue 3
- TypeScript
- Vue Router
- Axios
- Vite

## 项目结构

```
src/
├── api/              # API接口层
│   ├── request.ts    # Axios请求封装
│   └── user.ts       # 用户相关API
├── components/       # 组件
│   ├── EditUserModal.vue      # 编辑用户模态框
│   └── PasswordModal.vue      # 修改密码模态框
├── views/           # 页面视图
│   ├── Login.vue            # 登录页面
│   └── UserManagement.vue   # 用户管理页面
├── router/          # 路由配置
│   └── index.ts
├── App.vue
├── main.ts
└── style.css        # 全局样式
```

## 安装与运行

### 1. 安装依赖

```bash
npm install
```

### 2. 配置后端API地址

编辑 `.env` 文件，配置后端API地址：

```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 3. 启动开发服务器

```bash
npm run dev
```

### 4. 构建生产版本

```bash
npm run build
```

## 后端API要求

本项目需要后端提供以下API接口：

### 用户认证
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 用户管理
- `GET /api/user/me` - 获取当前用户信息
- `GET /api/user/list` - 获取用户列表（**注意：需要后端提供此接口**）
- `PUT /api/user/profile` - 更新用户资料
- `PUT /api/user/password` - 修改密码
- `DELETE /api/user/me` - 删除用户账号

**重要提示：** 根据您提供的API文档，可能缺少 `GET /api/user/list` 接口。如果后端没有此接口，需要添加一个返回所有用户列表的接口，或者修改前端代码使用模拟数据。

## 使用说明

1. 首先在登录页面使用用户名和密码登录
2. 登录成功后自动跳转到用户管理页面
3. 在用户管理页面可以：
   - 查看所有用户列表
   - 点击"编辑"按钮修改用户信息
   - 点击"改密码"按钮修改用户密码
   - 点击"删除"按钮删除用户账号
   - 点击"刷新"按钮重新加载用户列表
   - 点击"退出登录"退出系统

## 注意事项

1. 请确保后端API服务已启动并运行在正确的端口
2. 跨域问题：如果遇到跨域问题，需要在后端配置CORS
3. Token存储：登录后的token存储在localStorage中
4. 路由守卫：未登录用户访问管理页面会自动跳转到登录页

## License

MIT
