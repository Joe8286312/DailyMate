# 实体字段说明

## 1 User（用户）

| 字段      | 类型     | 说明                     |
| --------- | -------- | ------------------------ |
| id        | Long     | 用户ID                   |
| username  | String   | 用户名，唯一             |
| password  | String   | 密码（哈希存储）         |
| email     | String   | 邮箱，唯一可为空         |
| avatar    | String   | 头像URL                  |
| isDelete  | Integer  | 软删除标记（0未删1已删） |
| createdAt | DateTime | 注册时间（自动填充）     |
| updatedAt | DateTime | 信息更新时间（自动填充） |

---

## 2 Todo（待办清单）

| 字段       | 类型     | 说明                            |
| ---------- | -------- | ------------------------------- |
| id         | Long     | 待办ID                          |
| userId     | Long     | 所属用户ID                      |
| title      | String   | 标题                            |
| content    | String   | 内容                            |
| priority   | Integer  | 优先级（1高 2中 3低，自定约定） |
| status     | Integer  | 状态（0未完成 1已完成）         |
| date       | Date     | 归属日期（如今日/本周）         |
| startTime  | DateTime | 计划/实际开始时间（ISO8601）    |
| endTime    | DateTime | 计划/截止时间（ISO8601）        |
| finishTime | DateTime | 完成时间（已完成时赋值）        |
| isDelete   | Integer  | 软删除标记                      |
| createdAt  | DateTime | 创建时间（自动）                |
| updatedAt  | DateTime | 更新时间（自动）                |

---

## 3 Bill（账单）

| 字段      | 类型     | 说明                     |
| --------- | -------- | ------------------------ |
| id        | Long     | 账单ID                   |
| userId    | Long     | 所属用户ID               |
| type      | Integer  | 类型（1-收入，2-支出）   |
| category  | String   | 分类（如餐饮/购物/交通） |
| amount    | Double   | 金额                     |
| remark    | String   | 备注说明                 |
| date      | Date     | 账单发生日期             |
| isDelete  | Integer  | 软删除标记               |
| createdAt | DateTime | 创建时间（自动填充）     |
| updatedAt | DateTime | 更新时间（自动填充）     |

---

好的！以下为你**全面详细**地梳理出每一个后端接口的API文档，**每个接口均有：功能说明、访问路径、方法、请求参数（含所有字段，注明可为空）、示例输入、响应样例、注意事项**。  
覆盖所有与用户、Todo、Bill相关的常规及批量操作、查询筛选、统计接口。

---

# DailyMate 完整详细后端API文档

---

## 1. 用户（User）模块

---

### 1.1 用户注册

- **接口**：`POST /api/auth/register`
- **功能**：注册新账号
- **请求体**
    ```json
    {
      "username": "alice",                // 必填，唯一
      "password": "mypassword",           // 必填
      "email": "alice@example.com",       // 可为空，唯一
      "avatar": "https://xx.com/avatar.png" // 可为空
    }
    ```
- **响应**
    ```json
    {
      "success": true,
      "msg": "注册成功",
      "user": {
        "id": 1,
        "username": "alice",
        "email": "alice@example.com",
        "avatar": "https://xx.com/avatar.png",
        "isDelete": 0,
        "createdAt": "2025-12-23T13:30:00",
        "updatedAt": "2025-12-23T13:30:00"
      }
    }
    ```
- **注意**：email与avatar均可为空，username唯一不可重复。

---

### 1.2 用户登录

- **接口**：`POST /api/auth/login`
- **功能**：登录
- **请求体**
    ```json
    {
      "username": "alice",   // 必填
      "password": "mypassword" // 必填
    }
    ```
- **响应**
    ```json
    {
      "success": true,
      "token": "<jwt-token>",
      "user": { ...同1.1 }
    }
    ```
- **注意**：后续需在请求头加`Authorization: Bearer <jwt-token>`

---

### 1.3 查询当前登录用户

- **接口**：`GET /api/user/me`
- **功能**：查token绑定用户信息
- **响应**
    ```json
    {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "avatar": "https://xx.com/avatar.png",
      "isDelete": 0,
      "createdAt": "2025-12-23T13:30:00",
      "updatedAt": "2025-12-23T13:30:00"
    }
    ```

---

### 1.4 修改用户资料

- **接口**：`PUT /api/user/profile`
- **请求体**
    ```json
    {
      "email": "new@mail.com",                  // 可为空
      "avatar": "https://xx.com/avatar2.png",   // 可为空
      "username": "小明"                        // 可为空
    }
    ```
- **响应**
    ```json
    {
      "success": true,
      "msg": "信息修改成功",
      "user": { ... }
    }
    ```

---

### 1.5 修改密码

- **接口**：`PUT /api/user/password`
- **请求体**
    ```json
    {
      "oldPassword": "mypassword",   // 必填
      "newPassword": "newsec"        // 必填
    }
    ```
- **响应**
    ```json
    {
      "success": true,
      "msg": "密码修改成功"
    }
    ```

---

### 1.6 注销账号

- **接口**：`DELETE /api/user/me`
- **响应**
    ```json
    {
      "success": true,
      "msg": "账号注销成功"
    }
    ```

---

## 2. 待办清单 Todo 模块

---

### 2.1 新增待办

- **接口**：`POST /api/todo/add`
- **请求体**
    ```json
    {
      "userId": 1,                               // 必填
      "title": "整理代码",                        // 必填
      "content": "注释和格式化",                  // 可为空
      "priority": 2,                             // 可为空，默认2
      "status": 0,                               // 可为空，默认0
      "date": "2025-12-25",                      // 必填
      "startTime": "2025-12-25T08:00:00",        // 可为空
      "endTime": "2025-12-25T17:00:00",          // 可为空
      "finishTime": null                         // 可为空，新增时一般不填
    }
    ```
- **响应**
    ```json
    {
      "id": 10,
      "userId": 1,
      "title": "整理代码",
      "content": "注释和格式化",
      "priority": 2,
      "status": 0,
      "date": "2025-12-25",
      "startTime": "2025-12-25T08:00:00",
      "endTime": "2025-12-25T17:00:00",
      "finishTime": null,
      "isDelete": 0,
      "createdAt": "2025-12-23T14:00:00",
      "updatedAt": "2025-12-23T14:00:00"
    }
    ```

---

### 2.2 修改待办

- **接口**：`PUT /api/todo/update`
- **请求体**
    ```json
    {
      "id": 10,                                 // 必填
      "title": "整理全部代码",                   // 可为空
      "status": 1,                              // 可为空，改为已完成
      "finishTime": null,                       // 可为空。传null且status=1时后端自动补
      "startTime": "2025-12-25T08:00:00",       // 可为空
      "endTime": "2025-12-25T17:00:00"          // 可为空
    }
    ```
- **响应**：同新增

---

### 2.3 查询某用户全部/某天待办

- **接口**：`GET /api/todo/list?userId=1[&date=2025-12-25]`
- **响应**
    ```json
    [
      { ...如新增返回 },
      { ...如新增返回 },
      ...
    ]
    ```

---

### 2.4 查询单个待办

- **接口**：`GET /api/todo/{id}`
- **响应**：如新增返回

---

### 2.5 软删除待办

- **接口**：`DELETE /api/todo/delete/{id}`
- **无返回体，仅200**

---

### 2.6 彻底物理删除

- **接口**：`DELETE /api/todo/hard-delete/{id}`
- **响应**
    ```json
    "OK"
    ```

---

### 2.7 恢复软删除待办

- **接口**：`PUT /api/todo/restore/{id}`
- **响应**
    ```json
    "OK"
    ```

---

### 2.8 批量软删除

- **接口**：`PUT /api/todo/batch/delete`
- **请求体**
    ```json
    {
      "ids": [10, 12, 15]
    }
    ```
- **响应**
    ```json
    "OK"
    ```

---

### 2.9 批量彻底物理删除

- **接口**：`PUT /api/todo/batch/hard-delete`
- **请求体**
    ```json
    {
      "ids": [10, 12, 15]
    }
    ```
- **响应**
    ```json
    "OK"
    ```

---

### 2.10 按状态查询（已完成/未完成）

- **接口**：`GET /api/todo/by-status?userId=1&status=0`
- **响应**
    ```json
    [
      { ...如新增返回 },
      { ...如新增返回 },
      ...
    ]
    ```

---

### 2.11 批量设置完成/未完成

- **接口**：`PUT /api/todo/batch/finish`
- **请求体**
    ```json
    {
      "ids": [7, 10],           // 选中7和10
      "status": 1               // 1为完成，0为未完成
    }
    ```
- **响应**
    ```json
    "OK"
    ```

---

### 2.12 模糊查询

- **接口**：`GET /api/todo/search?userId=1&keyword=代码`
- **响应**：同新增

---

### 2.13 日期区间查询

- **接口**：`GET /api/todo/range?userId=1&start=2025-12-01&end=2025-12-31`
- **响应**：同新增

---

### 2.14 修改优先级

- **接口**：`PUT /api/todo/priority/{id}`
- **请求体**
    ```json
    {
      "priority": 1
    }
    ```
- **响应**
    ```json
    "OK"
    ```

---

### 2.15 按优先级筛选

- **接口**：`GET /api/todo/by-priority?userId=1&priority=1`
- **响应**：同新增

---

### 2.16 查询未完成任务数量

- **接口**：`GET /api/todo/unfinished-count?userId=1`
- **响应**：
    ```json
    4
    ```

---

## 3. 账单 Bill 模块

---

### 3.1 新增账单

- **接口**：`POST /api/bill/add`
- **请求体**
    ```json
    {
      "userId": 1,                        // 必填
      "type": 2,                          // 必填，1-收入 2-支出
      "category": "餐饮",                  // 必填
      "amount": 28.80,                    // 必填
      "remark": "午饭外卖",                // 可为空
      "date": "2025-12-25"                // 必填
    }
    ```
- **响应**
    ```json
    {
      "id": 21,
      "userId": 1,
      "type": 2,
      "category": "餐饮",
      "amount": 28.80,
      "remark": "午饭外卖",
      "date": "2025-12-25",
      "isDelete": 0,
      "createdAt": "2025-12-25T13:05:23",
      "updatedAt": "2025-12-25T13:05:23"
    }
    ```

---

### 3.2 更新账单

- **接口**：`PUT /api/bill/update`
- **请求体**
    ```json
    {
      "id": 21,           // 必填
      "amount": 31.20,    // 可为空
      "category": "餐饮", // 可为空
      "remark": "午饭+奶茶" // 可为空
    }
    ```

---

### 3.3 查询账单（单日/全部/区间/分类/金额区间/关键字）

- **接口一**（单用户全部/指定日）：
    - `GET /api/bill/list?userId=1`
    - `GET /api/bill/list?userId=1&date=2025-12-25`
- **接口二**（时间区间）：
    - `GET /api/bill/range?userId=1&start=2025-12-01&end=2025-12-31`
- **接口三**（分类）：
    - `GET /api/bill/by-category?userId=1&category=餐饮&start=2025-12-01&end=2025-12-31`
- **接口四**（金额区间）：
    - `GET /api/bill/by-amount-range?userId=1&min=10&max=50&start=2025-12-01&end=2025-12-31`
- **接口五**（关键字）：
    - `GET /api/bill/search?userId=1&keyword=外卖`
- **响应**（均为账单数组）:
    ```json
    [
      {
        "id": 21,
        "userId": 1,
        "type": 2,
        "category": "餐饮",
        "amount": 31.20,
        "remark": "午饭+奶茶",
        "date": "2025-12-25",
        "isDelete": 0,
        "createdAt": "2025-12-25T13:10:00",
        "updatedAt": "2025-12-25T13:12:00"
      }
    ]
    ```

---

### 3.4 月度/分类/趋势统计

- **月度统计接口**：
    - `GET /api/bill/stat/month?userId=1&year=2025&month=12`
    - **响应**：
      ```json
      { "income": 4200, "expense": 830, "total": 3370 }
      ```

- **分类统计接口**：
    - `GET /api/bill/stat/category?userId=1&type=2&start=2025-12-01&end=2025-12-31`
    - **响应**：
      ```json
      { "餐饮": 400, "购物": 430 }
      ```

- **趋势接口**：
    - `GET /api/bill/stat/trend?userId=1&days=7`
    - **响应**：
      ```json
      [
        {"date": "2025-12-19", "income": 0, "expense": 40, "total": -40},
        {"date": "2025-12-20", "income": 1200, "expense": 0, "total": 1200},
        ...
      ]
      ```

---

### 3.5 软删除账单

- **接口**：`DELETE /api/bill/delete/{id}`
- **无返回体，仅200**

---

### 3.6 批量软删除

- **接口**：`PUT /api/bill/batch/delete`
- **请求体**：
    ```json
    { "ids": [15, 16, 17] }
    ```
- **响应**：`"OK"`

---

### 3.7 批量物理删除

- **接口**：`PUT /api/bill/batch/hard-delete`
- **请求体**：
    ```json
    { "ids": [15, 16, 17] }
    ```
- **响应**：`"OK"`

---

### 3.8 物理删除账单

- **接口**：`DELETE /api/bill/hard-delete/{id}`
- **响应**：`"OK"`

---

### 3.9 恢复软删除账单

- **接口**：`PUT /api/bill/restore/{id}`
- **响应**：`"OK"`

---

## 4. 通用注意事项

- 所有时间用 ISO8601 标准（如："2025-12-25T08:00:00"）
- POST/PUT接口可省略可空字段
- isDelete=1 的对象不会出现在查询/统计结果中
- id主键和userId属于long型，amount为decimal（double型），priority/type/status等为整数
- 字段默认值、必填与可选已在每个接口参数详细标明
- 返回对象字段顺序无特殊约定，如需用请前端做容错

---

如需每个接口的错误响应样例、token携带方式、字段最大长度校验补充等，欢迎继续提问！