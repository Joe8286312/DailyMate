# DailyMate 前端开发日志（个人）

- 项目名称：DailyMate – 个人日常管理工具
- 开发角色：前端开发
- 学号/姓名：XXXX / **谢苏阳**
- 前端 Git 用户：**kongbai-shike**
- 后端负责人：**Joe8286312（王琦）** – 负责后端及数据库
- 前后端分工：
  - 后端（王琦）：Spring Boot 后端项目，负责用户、待办、账单等实体的 CRUD，完善登录注册、鉴权、统计分析等接口。
  - 前端（我：谢苏阳）：以 `frontend/html` 目录为主，独立完成 `index.html`、`login.html`、`register.html` 等页面的结构、样式和交互逻辑，实现从“本地假数据”到“真实接口接入”的前端演进。

> 本日志按时间顺序记录我在 DailyMate 项目前端部分的开发过程，重点体现：**先搭前端页面骨架 → 用本地仿真数据跑通功能 → 接入后端真实接口并优化前端体验**。

---

## 一、开发总体思路（三阶段）

1. **建立其骨架（先把页面搭出来）**  
   - 在 `frontend/html` 目录下，分别新建 `index.html`、`login.html`、`register.html`。  
   - 初次提交时，样式大多通过 `<style>` 写在页面内部（内联 CSS），先不抽公共 `style.css`，目标是尽快搭出完整的 UI。
   - `index.html` 上搭出：登录检查页、顶部导航区、日历区、统计卡片、侧边栏菜单、待办/账单表格、模态框等。

2. **用仿真血肉填充（使用假数据，完成功能）**  
   - 在 `login.html`、`register.html` 中实现 `AuthManager` 类，用 `localStorage` 仿真用户注册与登录。
   - 在 `index.html` 的初版中，也以本地数据结构（如 `dailyMate_todos`、`dailyMate_bills`）来模拟 Todo 和 Bill 的存储。
   - 通过这些假数据，先跑通登录跳转、待办/账单增删改查的前端交互流程。

3. **使用真实血肉取替换仿真血肉（接入后端接口）**  
   - 后期引入 `APIManager` 类，`BASE_URL = 'http://localhost:8080'`，统一封装后端的 REST 接口调用。
   - 登录/注册从 `AuthManager.loginUser/registerUser` 改为 `fetch /api/auth/login`、`/api/auth/register`。
   - `index.html` 从 localStorage 仿真数据，切换为通过 `/api/todo/*`、`/api/bill/*`、`/api/user/*` 获取真实数据并渲染，并增加批量操作、统计卡片、日历事件点等高级功能。

下面按照实际 Git 提交时间线，具体记录每个阶段的开发过程。

---

## 二、按时间的开发流水账

### 1. 2025-12-19 ～ 2025-12-22：了解后端、设计前端结构

> 参考后端同学 Joe 的提交记录：从 `初始化项目`、`feat:实现基础CRUD功能`、`feat: 用户登录与注册鉴权功能` 一直到 `feat: 增强待办清单功能`、`feat: 增强记账功能` 等。

**我的工作：**

- 学习后端提供的接口文档（`docs/api-documentation` PR 中的内容），了解以下关键接口：
  - 用户鉴权：`POST /api/auth/login`、`POST /api/auth/register`
  - 用户信息：`GET /api/user/me`、`PUT /api/user/profile`、`PUT /api/user/password`
  - 待办：`GET /api/todo/list`、`POST /api/todo/add`、`PUT /api/todo/update`、`DELETE /api/todo/delete/{id}`，以及后续的批量接口
  - 账单：`GET /api/bill/list`、`POST /api/bill/add`、`PUT /api/bill/update`、`DELETE /api/bill/delete/{id}`，以及批量接口
- 在纸上画出了 DailyMate 前端的整体信息架构：
  - 登录 / 注册单独页面；
  - 主页面 `index.html` 中包含：顶部个人信息区、日历区 + 当日/本月统计卡片、左侧菜单、右侧待办/账单双 Tab 等。

---

### 2. 2025-12-23：前端工程初始化与测试页面

相关提交（前端）：

- `test`
- `test2`

**工作内容：**

- 在仓库中创建 `frontend/html` 目录，确认 Git 跟踪前端资源。
- 写了简单的 `test` 页面和脚本，验证：
  - 静态页面能被正确打开；
  - 浏览器环境里 ES6 语法、`localStorage`、`fetch` 等 API 正常可用。
- 为后续 `index.html`、`login.html`、`register.html` 的引入做好结构准备。

---

### 3. 2025-12-27：初次前端页面提交（骨架 + 本地假数据）

相关提交：

- `前端页面完成，还未完成前后端联调`（多次）
- `前后端联调完成`（当日后期）

这一天是**第一次提交比较完整的前端页面**，路径：

- `frontend/html/index.html`
- `frontend/html/login.html`
- `frontend/html/register.html`

#### 3.1 登录页面 login.html – 骨架 + 本地 AuthManager

> 文件：`frontend/html/login.html`  
> 提交链接：[login.html @ 6934edc](https://github.com/Joe8286312/DailyMate/blob/6934edc879f6377da1695195222ea3270ae147eb/frontend/html/login.html)

当时的做法是：

- 在 `<head>` 内直接写 `<style>...</style>`，快速完成登录页 UI：
  - 左边 `auth-left`：展示 DailyMate 名称和功能特点；
  - 右边 `auth-right`：登录表单（用户名 + 密码）。
- 在 `<script>` 中定义一个本地的 `AuthManager`，用 localStorage 仿真用户系统：

```js
class AuthManager {
    constructor() {
        this.initStorage();
        this.loadData();
    }

    initStorage() {
        if (!localStorage.getItem('dailyMate_users')) {
            localStorage.setItem('dailyMate_users', JSON.stringify([]));
        }
        if (!localStorage.getItem('dailyMate_currentUser')) {
            localStorage.setItem('dailyMate_currentUser', JSON.stringify(null));
        }
        this.initTestUser();
    }

    // 初始化测试用户 test/demo，并生成测试 TODO、账单数据
    initTestUser() { ... }

    loginUser(username, password) {
        const user = this.users.find(u =>
            u.username === username &&
            u.password === password &&
            u.isDelete === 0
        );
        if (user) {
            this.currentUser = user;
            this.saveData();
            return { success: true, user };
        }
        return { success: false, message: '用户名或密码错误' };
    }
}
```

表单提交时，直接调用 `authManager.loginUser`，**不依赖后端**，只用 localStorage 判断：

```js
loginForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const result = authManager.loginUser(username, password);
    if (result.success) {
        authManager.showNotification('登录成功！正在跳转...');
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 1500);
    } else {
        authManager.showError('loginError', result.message);
    }
});
```

#### 3.2 注册页面 register.html – 骨架 + 本地注册 + 密码强度

> 文件：`frontend/html/register.html`  
> 提交链接：[register.html @ 6934edc](https://github.com/Joe8286312/DailyMate/blob/6934edc879f6377da1695195222ea3270ae147eb/frontend/html/register.html)

- 同样在页面头部用 `<style>` 完成 UI 样式布局；
- 使用 `AuthManager` 管理 localStorage 中的 `dailyMate_users`；
- 实现用户名、邮箱、密码的基础校验：
  - 用户名是否已存在；
  - 邮箱是否已存在；
  - 密码长度；
  - 密码与确认密码一致等。
- 增加密码强度条 `password-strength`，实时评估密码强度并改变进度条颜色与宽度：

```js
checkPasswordStrength(password) {
    let strength = 0;
    // 判断长度、是否包含数字、特殊字符、大小写...
    // 最终限制在 0~4
    return { strength, text };
}
```

这个阶段，**注册成功后，是将用户写入 localStorage，而非后端数据库**。

#### 3.3 主页面 index.html – 初版骨架与内联 CSS

> 文件：`frontend/html/index.html`  
> 提交链接：[index.html @ 6934edc](https://github.com/Joe8286312/DailyMate/blob/6934edc879f6377da1695195222ea3270ae147eb/frontend/html/index.html)

初次提交的 `index.html` 主要工作：

1. **登录检查页**：  
   - 顶部有一个 `login-check` 页面，如果没有登录则提示“请先登录以访问您的个人日常管理工具”，并提供“前往登录”按钮。

2. **主应用布局**：
   - 顶部 Header：项目 Logo + 名称、用户名展示、退出按钮；
   - 中间区域：左侧迷你日历、右侧三张数据统计卡片；
   - 下方区域：左侧侧边导航（待办管理 / 账单管理），右侧数据管理表格。

3. 当时的 CSS 全部写在 `<style>` 里（很长的一段），重点是把**视觉样式和布局搭出来**，还未抽取公共 `style.css`，也还未完全接上真实后端。

---

### 4. 2025-12-27 ～ 2025-12-29：从前端骨架到本地功能跑通，再到简单联调

相关提交（前端）：

- `前端页面完成，还未完成前后端联调`
- `前后端联调完成`
- `增加了前端待办事项的批量管理`
- `增加了修改个人账户信息的功能...`
- `用户管理前端更新`
- `修改了部分bug...`

**我在这几天的工作重点：**

1. **让首页 index.html 用假数据跑起来：**
   - 按照 login/register 中 `AuthManager` 的风格，在 index 侧也通过 localStorage 读写模拟 Todo/Bill；
   - 渲染待办事项表格、账单表格，支持增删改查；
   - 实现日历、统计卡片的前端计算逻辑。

2. **增加批量管理和用户信息修改功能：**
   - 待办表头加“全选”复选框；每行加 `.todo-checkbox`，实现批量完成、批量未完成、批量删除；
   - 用户信息区域 `#usernameDisplay` 可点击，弹出用户资料编辑模态框（前端先本地更新，后续才接接口）。

3. **开始尝试调用后端接口：**
   - 用简单的 `fetch` 测试 `/api/todo/list` 等接口；
   - 排查 CORS、路径错误等问题；
   - 这个阶段只是“简单联调”，还没写出统一的 `APIManager`。

---

### 5. 2025-12-29 ～ 2026-01-02：引入 APIManager，全面切换到真实接口

相关提交（前端）：

- `前端的修复`
- 后续多次“前端优化”“前端修改”等

**关键变化：**

1. **新增 APIManager 类，统一封装后端调用**

在后来你发我的版本中，`index.html` 最上方引入了：

```js
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080',
    TOKEN_KEY: 'dailyMate_token',
    USER_KEY: 'dailyMate_user'
};

class APIManager {
    constructor() {
        this.token = localStorage.getItem(API_CONFIG.TOKEN_KEY);
        this.baseURL = API_CONFIG.BASE_URL;
        this.currentUser = null;
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
        ...
        const response = await fetch(url, { ...options, headers });
        // 统一处理 401、解析 JSON 文本等
    }

    async getTodos(userId) {
        return await this.request(`/api/todo/list?userId=${userId}`);
    }
    async addTodo(todoData) { ... }
    async updateTodo(id, todoData) { ... }
    async deleteTodo(id) { ... }
    async getBills(userId) { ... }
    async addBill(billData) { ... }
    ...
}
```

2. **登录/注册从本地仿真切换到真实接口**

后期在 `login.html` / `register.html` 中的脚本改为：

- `login.html`：  
  - `POST /api/auth/login`，成功后保存 `dailyMate_token` 和 `dailyMate_user` 到 localStorage，再跳转 `index.html`。
- `register.html`：  
  - `POST /api/auth/register`，成功后清理本地登录状态，并跳转回登录页。

这一步，**完成了从本地 AuthManager 仿真 → 调用真实后端的过渡**。

3. **首页 index.html 改为基于 APIManager 渲染真实数据**

- `DailyMateApp` 类中，初始化时调用 `this.apiManager.getCurrentUser()` 获取当前登录用户；
- 使用 `getTodos`、`getBills` 获取后端数据，渲染表格、统计卡片和日历事件点；
- 批量操作调用后端批量 API（如 `/api/todo/batch/delete`、`/api/bill/batch/delete`）；
- 用户信息修改、密码修改分别调用 `/api/user/profile` 和 `/api/user/password`。

---

### 6. 2026-01-03：前端优化与提交整理

相关提交：

- `Add files via upload`
- `保留本地 html 文件更改`
- `前端优化`
- `前端修改`

**工作内容：**

- 将原来分散在多个 HTML 内联 `<style>` 中的样式整理成统一的 `style.css`，并在新版 `index.html/login.html/register.html` 中通过 `<link rel="stylesheet" href="style.css">` 方式引用。
- 对交互进行了优化：
  - 搜索高亮、无结果提示文案优化；
  - 批量操作时的提示文案、更直观的确认逻辑；
  - 通用确认模态框 `#confirmModal` 替代原生 `window.confirm`。
- 保留了早期内联版本的 HTML 文件（此次你发我的 `frontend/html/*.html` 初次提交版本），作为**“先搭骨架 + 本地仿真阶段”**的证据。

---

## 三、总结：谢苏阳在前端开发中的贡献

综合上述时间线，可以清晰地看到我（谢苏阳，kongbai-shike）在前端部分的工作：

1. **从零搭建前端骨架：**
   - 在 `frontend/html` 目录下，独立完成了 `index.html`、`login.html`、`register.html` 初版页面，包括布局和视觉样式。
   - 初次提交中，大量 CSS 以 `<style>` 形式内联在页面中，以快速验证布局和效果。

2. **本地仿真阶段（假数据）：**
   - 设计并实现了 `AuthManager` 类，利用 `localStorage` 模拟用户登录、注册功能。
   - 在本地生成了测试用户（test/demo）和示例 Todo/Bill 数据，让前端在没有后端的情况下可用。
   - 通过本地仿真数据测试出待办/账单增删改查、搜索、筛选等前端功能的可行性。

3. **接口接入阶段（真实数据）：**
   - 根据后端同学王琦提供的接口文档，引入 `APIManager`，统一封装 HTTP 请求和错误处理。
   - 登录、注册从 localStorage 仿真切换为调用 `/api/auth/login`、`/api/auth/register`，并处理 token 存储、401 过期逻辑等。
   - index 页的 Todo/Bill 列表、日历和统计卡片全部改为通过 `/api/todo`、`/api/bill`、`/api/user` 系列接口获取真实数据。
   - 增强前端的批量操作、用户信息修改、密码修改等功能，并与对应后端接口对齐。

4. **前端体验与代码结构优化：**
   - 将内联 CSS 抽出为独立的 `style.css`，实现前端样式的统一管理。
   - 优化通知组件（`notification`）、确认弹窗（`confirmModal`）、搜索高亮等交互细节。
   - 修复多处联调中发现的问题（如删除成功但提示错误、更新时缺少 `userId` 或 `date` 字段、时间格式不匹配等）。

**整体上，我的前端开发过程严格遵循了：**

> 先把页面骨架搭出来 → 用本地假数据给页面“上血肉”跑通功能 → 再用后端真实接口替换假数据，让系统真正上线可用。

这份日志中引用的 `frontend/html/index.html`、`frontend/html/login.html`、`frontend/html/register.html` 初次提交内容，就对应了我前端开发的**第一阶段成果**；而后续多次“前端修复/优化”的提交，则体现了我在联调和优化阶段持续完善前端的过程。