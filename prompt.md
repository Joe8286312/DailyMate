# DailyMate 项目开发指令

> 项目位置: `D:\html\web_work\todo\DailyMate`  
> 用途: 让其他 AI 完整接手并继续开发此项目

---

## 项目交接模板

请先不要继续大范围搜索。

先读取当前目录下已有的文档和关键代码，然后用最短方式告诉我：
- 当前做到哪里了
- 还差什么
- 下一步你准备做什么

如果信息足够，直接继续做，不要重复读全项目。

---

## 项目概览

**DailyMate** - 个人日常管理工具（全栈项目）

### 技术栈（必须严格遵守）
```
前端: Vue 3.4 + Vite 5 + Element Plus 2.6 + Pinia 2.1 + vue-i18n 9.14
后端: Spring Boot 3.2 + Spring Security + JWT + Spring Data JPA + MySQL 8.0
部署: Docker + Docker Compose + Nginx
```

### 已完成功能
- ✅ 用户注册/登录/注销（JWT 认证）
- ✅ 待办事项 CRUD + 批量操作 + 智能提醒（SSE）
- ✅ 账单管理 CRUD + 批量操作 + 统计分析
- ✅ 消息通知系统
- ✅ 多语言支持（zh-CN, en-US, ja-JP, ko-KR）
- ✅ 主题切换（5 种主题）+ 自定义背景图
- ✅ 用户设置持久化
- ✅ 数据仪表盘（ECharts 图表）
- ✅ Docker 一键部署

---

## 项目结构

### 前端目录
```
frontend/src/
├── api/           # API 接口（auth.js, todo.js, bill.js, message.js, settings.js）
├── components/    # 公共组件（BillStatistics.vue, MessageDrawer.vue）
├── locales/       # 国际化（zh-CN.js, en-US.js, ja-JP.js, ko-KR.js）
├── router/        # 路由配置（index.js）
├── stores/        # Pinia 状态管理（auth.js, todo.js, bill.js, message.js, settings.js）
├── styles/        # 全局样式（variables.scss, index.scss）
├── utils/         # 工具函数（request.js, errorHandler.js, notification.js, imageCompressor.js）
├── views/         # 页面（Login.vue, Register.vue, Layout.vue, Dashboard.vue, Todos.vue, Bills.vue, Settings.vue）
├── App.vue        # 根组件
└── main.js        # 入口文件
```

### 后端目录
```
backend/dailymate/src/main/java/com/joe/dailymate/
├── common/        # 公共类（Result.java, GlobalExceptionHandler.java）
├── config/        # 配置类（CorsConfig, JwtConfig, ScheduleConfig, SecurityConfig）
├── controller/    # 控制器（Auth, User, Todo, Bill, Message, Settings, Avatar）
├── dto/request/   # 请求 DTO（LoginRequest, RegisterRequest, TodoRequest, BillRequest, UserSettingsRequest）
├── entity/        # 实体类（User, Todo, Bill, Message, UserSettings）
├── filter/        # 过滤器（JwtAuthenticationFilter）
├── repository/    # 数据访问层（JpaRepository）
├── service/       # 业务逻辑层（AuthService, UserService, TodoService, BillService, MessageService, UserSettingsService, ReminderService）
├── util/          # 工具类（JwtUtil）
└── DailymateApplication.java  # 启动类（@EnableScheduling）
```

---

## 数据库表结构

### 1. user 表
```sql
id, username, password(BCrypt加密), email, avatar, is_delete, created_at, updated_at
```

### 2. todo 表
```sql
id, user_id, title, content, priority(1高2中3低), status(0未完成1已完成), date, 
start_time, end_time, finish_time, is_delete, 
reminder_enabled, reminder_offset, reminder_time, is_reminded, 
created_at, updated_at
```

### 3. bill 表
```sql
id, user_id, type(1收入2支出), category, amount(DECIMAL(10,2)), remark, date, is_delete, created_at, updated_at
```

### 4. message 表
```sql
id, user_id, title, content, type(0系统1待办2账单), is_read, read_time, created_at, updated_at
```

### 5. user_settings 表
```sql
id, user_id(唯一), language, theme, background_type, background_url, background_opacity, 
background_size, background_position, notification_enabled, sound_enabled, animation_enabled, 
default_priority, default_reminder_offset, created_at, updated_at
```

---

## API 接口清单

### 认证模块
```
POST   /api/auth/register      用户注册
POST   /api/auth/login         用户登录
POST   /api/auth/logout        退出登录
GET    /api/user/me            获取当前用户
PUT    /api/user/profile       修改个人资料
PUT    /api/user/password      修改密码
DELETE /api/user/me            注销账号
```

### 待办模块
```
GET    /api/todo/list                获取待办列表 (?userId=1&date=2026-04-03)
POST   /api/todo/add                 添加待办
PUT    /api/todo/update              更新待办
DELETE /api/todo/delete/{id}         软删除
DELETE /api/todo/hard-delete/{id}    硬删除
PUT    /api/todo/restore/{id}        恢复软删除
PUT    /api/todo/batch/finish        批量完成 {"ids":[1,2], "status":1}
PUT    /api/todo/batch/delete        批量软删除 {"ids":[1,2]}
PUT    /api/todo/batch/hard-delete   批量硬删除 {"ids":[1,2]}
GET    /api/todo/search              关键字搜索 (?userId=1&keyword=xxx)
GET    /api/todo/range               日期区间 (?userId=1&start=xxx&end=xxx)
GET    /api/todo/by-status           按状态筛选 (?userId=1&status=0)
GET    /api/todo/by-priority         按优先级筛选 (?userId=1&priority=1)
GET    /api/todo/unfinished-count    未完成数量 (?userId=1)
PUT    /api/todo/priority/{id}       修改优先级 {"priority":1}
GET    /api/todo/remind/stream/{userId}  SSE 提醒流
POST   /api/todo/remind/set/{id}     设置提醒
POST   /api/todo/remind/cancel/{id}  取消提醒
POST   /api/todo/remind/ack/{id}     确认收到提醒
```

### 账单模块
```
GET    /api/bill/list                      获取账单列表 (?userId=1&date=2026-04-03)
POST   /api/bill/add                       添加账单
PUT    /api/bill/update                    更新账单
DELETE /api/bill/delete/{id}               软删除
DELETE /api/bill/hard-delete/{id}          硬删除
PUT    /api/bill/restore/{id}              恢复软删除
PUT    /api/bill/batch/delete              批量软删除 {"ids":[1,2]}
PUT    /api/bill/batch/hard-delete         批量硬删除 {"ids":[1,2]}
GET    /api/bill/range                     时间区间 (?userId=1&start=xxx&end=xxx)
GET    /api/bill/by-category               按分类 (?userId=1&category=餐饮)
GET    /api/bill/by-amount-range           按金额区间 (?userId=1&min=10&max=50)
GET    /api/bill/search                    关键字搜索 (?userId=1&keyword=xxx)
GET    /api/bill/stat/month                月度统计 (?userId=1&year=2026&month=4)
GET    /api/bill/stat/category             分类统计 (?userId=1&type=2&start=xxx&end=xxx)
GET    /api/bill/stat/trend                趋势统计 (?userId=1&days=7)
```

### 消息模块
```
GET    /api/message/list                   获取消息列表 (?userId=1)
GET    /api/message/unread-count           未读数量 (?userId=1)
PUT    /api/message/mark-read/{id}         标记已读
PUT    /api/message/batch-mark-read        批量标记已读 {"ids":[1,2]}
DELETE /api/message/delete/{id}            删除消息
PUT    /api/message/delete-all-read        删除所有已读
```

### 设置模块
```
GET    /api/settings                       获取用户设置 (?userId=1)
PUT    /api/settings                       更新设置
POST   /api/settings/reset                 重置为默认设置
```

---

## 统一响应格式

```json
{
  "success": true,
  "msg": "操作成功",
  "data": { ... }
}
```

错误响应：
```json
{
  "success": false,
  "msg": "错误信息",
  "data": null
}
```

---

## 核心功能实现要求

### 1. JWT 认证流程
- 密码 BCrypt 加密
- JWT Token 有效期 24 小时
- 所有 API（除登录注册）必须携带 Token
- 前端路由守卫拦截未登录用户
- Token 存储在 localStorage（key: `dailyMate_token`）

### 2. 待办提醒功能
**后端逻辑**:
```java
// 定时任务每分钟扫描 reminderTime <= now 且 isReminded=false 的待办
@Scheduled(fixedRate = 60000)
public void checkReminders() {
    // 通过 SSE 发送提醒消息
    // 创建消息记录
    // 标记 isReminded=true
}
```

**前端逻辑**:
```javascript
// 建立 SSE 连接
const eventSource = new EventSource(`/api/todo/remind/stream/${userId}`)
eventSource.onmessage = (event) => {
  // 显示浏览器通知
  // 播放提示音
  // 刷新待办列表
}
```

### 3. 多语言国际化
- 支持 4 种语言：zh-CN, en-US, ja-JP, ko-KR
- Element Plus 组件语言必须同步切换
- 语言切换后保存到 localStorage 和后端 user_settings 表

### 4. 主题切换
- 5 种预设主题：light, dark, blue, green, purple
- 通过 CSS 变量动态更新
- 在 `<head>` 中注入动态样式

### 5. 数据统计
- 月度统计：{income, expense, total}
- 分类统计：{分类名: 金额, ...}
- 趋势统计：[{date, income, expense, total}, ...]
- 使用 ECharts 展示图表

---

## 开发规范

### 前端规范
```vue
<script setup>
// 1. 导入顺序：Vue API → 第三方库 → 本地模块 → 样式
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useTodoStore } from '@/stores/todo'

// 2. Props 定义
const props = defineProps({
  todoId: { type: Number, required: true }
})

// 3. Emits 定义
const emit = defineEmits(['update', 'delete'])

// 4. 响应式状态
const loading = ref(false)

// 5. 计算属性
const filteredTodos = computed(() => todos.value.filter(t => t.status === 0))

// 6. 方法
const fetchTodos = async () => {
  loading.value = true
  try {
    todos.value = await todoStore.fetchTodos()
  } finally {
    loading.value = false
  }
}

// 7. 生命周期
onMounted(() => {
  fetchTodos()
})
</script>
```

### 后端规范
```java
// Controller 层只处理请求和响应
@RestController
@RequestMapping("/api/todo")
@Validated
public class TodoController {
    @Autowired
    private TodoService todoService;

    @PostMapping("/add")
    public Result<Todo> addTodo(@Validated @RequestBody TodoRequest request) {
        return Result.success(todoService.addTodo(request));
    }
}

// Service 层处理 业务逻辑
@Service
@Transactional
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public Todo addTodo(TodoRequest request) {
        Todo todo = convertToEntity(request);
        // 计算提醒时间等业务逻辑
        return todoRepository.save(todo);
    }
}
```

---

## 配置要求

### 前端环境变量
```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api

# .env.production
VITE_API_BASE_URL=/api
VITE_DROP_CONSOLE=true
```

### 后端配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dailymate?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: dailymate-dev-secret-key-2024
  expiration: 86400000  # 24 小时
```

---

## 部署步骤

### Docker 一键部署
```bash
# 1. 配置环境变量
cp .env.example .env
# 编辑 .env 修改 DB_PASSWORD 和 JWT_SECRET

# 2. 启动
docker-compose up -d

# 3. 访问
# 前端: http://localhost
# 后端: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### 本地开发
```bash
# 1. 启动 MySQL 并创建数据库
mysql -u root -p
CREATE DATABASE dailymate DEFAULT CHARACTER SET utf8mb4;
source docs/mysql/init.sql

# 2. 启动后端
cd backend/dailymate && mvn spring-boot:run

# 3. 启动前端
cd frontend && npm install && npm run dev

# 4. 访问
# 前端: http://localhost:3000
# 后端: http://localhost:8080
```

---

## 开发顺序建议

### 第一阶段：基础（1-2 天）
1. 数据库表创建
2. 后端基础结构
3. 前端基础结构
4. 用户认证模块

### 第二阶段：核心功能（3-4 天）
1. 待办事项 CRUD
2. 账单管理 CRUD
3. 筛选和搜索
4. 批量操作

### 第三阶段：高级功能（3-4 天）
1. 提醒功能（SSE + 定时任务）
2. 消息通知系统
3. 数据统计（ECharts）
4. 仪表盘

### 第四阶段：个性化（2-3 天）
1. 多语言国际化
2. 主题切换
3. 背景图功能
4. 用户设置

### 第五阶段：优化部署（1-2 天）
1. 性能优化
2. 错误处理
3. Docker 配置
4. 文档完善

---

## 关键实现细节

### JWT 认证
```java
// 生成 Token
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId().toString())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
}
```

```javascript
// 前端请求拦截器自动注入 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('dailyMate_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

### SSE 提醒
```java
// 后端 SSE 连接
@GetMapping("/remind/stream/{userId}")
public SseEmitter streamReminders(@PathVariable Long userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.put(userId, emitter);
    emitter.onCompletion(() -> emitters.remove(userId));
    emitter.onTimeout(() -> emitters.remove(userId));
    return emitter;
}
```

```javascript
// 前端 SSE 接收
const eventSource = new EventSource(`/api/todo/remind/stream/${userId}`)
eventSource.addEventListener('reminder', (event) => {
  const reminder = JSON.parse(event.data)
  new Notification('待办提醒', { body: reminder.title })
})
```

---

## 交付标准

### 必须完成
- ✅ 用户注册/登录/注销
- ✅ 待办事项完整 CRUD + 批量操作 + 提醒
- ✅ 账单管理完整 CRUD + 批量操作 + 统计
- ✅ 多语言切换（4 种语言）
- ✅ 主题切换（5 种主题）+ 背景图
- ✅ 用户设置持久化
- ✅ 消息通知系统
- ✅ 数据仪表盘
- ✅ Docker 可部署

### 代码质量
- 无编译错误
- 代码规范一致
- 关键逻辑有注释
- API 有 Swagger 文档
- 无性能问题

---

## 重要提示

1. **严格遵循技术栈版本**，不要随意升级
2. **保持代码风格一致**，参考现有代码
3. **所有功能必须可运行**，不要留 TODO
4. **禁止硬编码敏感信息**，使用环境变量
5. **先读最少必要文件**，不要扫描整个项目
6. **直接完成修改**，不要反复给计划
7. **如果有多种方案，只给最推荐的一种**

---

## 相关文档

- `项目设计方案.md` - 完整设计方案（详细设计）
- `README.md` - 项目说明
- `DEPLOY_DOCKER.md` - Docker 部署指南
- `docs/API文档.md` - API 详细文档
- `docs/mysql/init.sql` - 数据库初始化脚本

---

**开始工作前，请先读取：**
1. `README.md`
2. `docs/mysql/init.sql`
3. `frontend/src/main.js`
4. `backend/dailymate/src/main/java/com/joe/dailymate/DailymateApplication.java`

**确认项目现状后再继续开发，祝你顺利！🚀**
