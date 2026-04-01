# 项目部署文档

## 环境要求

### 基础环境
- **Node.js**: >= 18.0.0
- **JDK**: 17 或更高版本
- **Maven**: 3.6+
- **MySQL**: 8.0+

### 可选环境
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

## 快速开始

### 1. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE dailymate DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'dailymate'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON dailymate.* TO 'dailymate'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 后端启动

```bash
cd backend/dailymate

# 方式 1: 使用 Maven
mvn clean install
mvn spring-boot:run

# 方式 2: 直接运行 JAR
mvn clean package
java -jar target/dailymate-0.0.1-SNAPSHOT.jar

# 方式 3: 使用 IDE 运行 DailymateApplication.java
```

#### 多环境配置

```bash
# 开发环境（默认）
mvn spring-boot:run

# 生产环境
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# 测试环境
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

#### 环境变量

生产环境建议使用环境变量配置敏感信息：

```bash
# Linux/Mac
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret

# Windows
set DB_PASSWORD=your_password
set JWT_SECRET=your_jwt_secret
```

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览生产构建
npm run preview
```

## 生产部署

### 1. 前端构建

```bash
cd frontend
npm run build
```

构建产物在 `dist/` 目录，可部署到 Nginx 或其他 Web 服务器。

### 2. Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/frontend/dist;
    index index.html;

    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 3. 后端部署

```bash
cd backend/dailymate

# 构建
mvn clean package -DskipTests

# 运行
java -jar target/dailymate-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. 使用 systemd 管理（Linux）

创建服务文件 `/etc/systemd/system/dailymate.service`:

```ini
[Unit]
Description=DailyMate Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/path/to/backend
ExecStart=/usr/bin/java -jar target/dailymate-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable dailymate
sudo systemctl start dailymate
sudo systemctl status dailymate
```

## Docker 部署

详见 [Docker 部署指南](./DEPLOY_DOCKER.md)

## 常见问题

### 1. 端口被占用

修改 `application.yml` 中的 `server.port` 配置。

### 2. 数据库连接失败

- 检查 MySQL 服务是否启动
- 检查数据库用户名密码是否正确
- 检查防火墙设置

### 3. 前端跨域问题

开发环境使用 Vite 代理，生产环境使用 Nginx 反向代理。

### 4. JWT 相关错误

确保前后端时钟同步，检查 JWT_SECRET 配置。

## 日志查看

```bash
# 查看应用日志
tail -f logs/dailymate.log
tail -f logs/dailymate-error.log

# systemd 日志
journalctl -u dailymate -f
```

## 性能优化建议

1. **数据库**: 为常用查询字段添加索引
2. **缓存**: 集成 Redis 缓存热点数据
3. **CDN**: 静态资源使用 CDN 加速
4. **Gzip**: 启用 Nginx Gzip 压缩
5. **数据库连接池**: 根据负载调整 HikariCP 配置

## 安全建议

1. 生产环境必须修改默认 JWT_SECRET
2. 数据库密码使用环境变量管理
3. 启用 HTTPS
4. 定期更新依赖版本
5. 配置防火墙规则
