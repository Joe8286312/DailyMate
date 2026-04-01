# Docker 部署文档

## 快速开始

### 1. 环境准备

确保已安装 Docker 和 Docker Compose:

```bash
# 检查 Docker 版本
docker --version
docker-compose --version
```

### 2. 配置环境变量

复制环境变量文件并修改配置:

```bash
cp .env.example .env
```

编辑 `.env` 文件:

```env
# 数据库密码
DB_PASSWORD=your_secure_password

# JWT 密钥（生产环境必须修改）
JWT_SECRET=your_secure_jwt_secret_key_2024
```

### 3. 启动服务

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f mysql
```

### 4. 访问应用

- 前端：http://localhost
- 后端 API: http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

### 5. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（谨慎使用）
docker-compose down -v
```

## 服务说明

### MySQL

- 端口：3306
- 数据库：dailymate
- 用户：dailymate
- 密码：由 `.env` 文件配置

### 后端

- 端口：8080
- 环境：prod
- 日志：./logs 目录

### 前端

- 端口：80
- Nginx 反向代理

## 常用命令

```bash
# 重启服务
docker-compose restart

# 重启特定服务
docker-compose restart backend

# 进入容器
docker-compose exec backend sh
docker-compose exec mysql mysql -u root -p

# 查看容器状态
docker-compose ps

# 重新构建
docker-compose build --no-cache

# 更新服务
docker-compose pull
docker-compose up -d
```

## 数据备份

```bash
# 备份数据库
docker-compose exec mysql mysqldump -u root -p${DB_PASSWORD} dailymate > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -u root -p${DB_PASSWORD} dailymate < backup.sql
```

## 故障排查

### 1. 后端启动失败

```bash
# 查看后端日志
docker-compose logs backend

# 进入容器检查
docker-compose exec backend sh
```

### 2. 数据库连接失败

```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql
```

### 3. 端口冲突

修改 `docker-compose.yml` 中的端口映射:

```yaml
ports:
  - "8081:8080"  # 将 8080 改为 8081
```

## 生产环境建议

1. **修改默认密码**: 必须修改 `.env` 中的默认密码
2. **启用 HTTPS**: 配置 Nginx SSL 证书
3. **限制网络访问**: 使用防火墙限制外部访问
4. **日志轮转**: 配置日志文件大小限制
5. **监控告警**: 集成 Prometheus + Grafana 监控
