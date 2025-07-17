# Docker 部署指南

本文档介绍如何使用 Docker 和 Docker Compose 部署 Play Spring Boot API 项目。

## 📋 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- Just (可选，用于命令管理)

### 安装 Just (推荐)

```bash
# macOS
brew install just

# Linux
curl --proto '=https' --tlsv1.2 -sSf https://just.systems/install.sh | bash -s -- --to ~/bin

# Windows (使用 Scoop)
scoop install just
```

## 🚀 快速开始

### 使用 Just 命令 (推荐)

```bash
# 查看所有可用命令
just

# 启动所有服务
just up

# 启动服务 (包含 Nginx 代理)
just up-with-proxy

# 停止服务
just down

# 重新构建并启动
just rebuild

# 查看日志
just logs
just logs api      # 仅查看 API 日志
just logs frontend # 仅查看前端日志
```

### 使用 Docker Compose 命令

```bash
# 启动所有服务
docker-compose up -d

# 启动服务 (包含 Nginx 代理)
docker-compose --profile with-proxy up -d

# 停止服务
docker-compose down

# 重新构建
docker-compose build --no-cache

# 查看日志
docker-compose logs -f
```

## 🏗️ 服务架构

### 标准模式

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │      API        │
│   (React)       │    │  (Spring Boot)  │
│   Port: 3000    │    │   Port: 8080    │
└─────────────────┘    └─────────────────┘
```

### 代理模式 (with-proxy)

```
┌─────────────────┐
│     Nginx       │
│   Port: 80      │
│   (Reverse      │
│    Proxy)       │
└─────────┬───────┘
          │
    ┌─────┴─────┐
    │           │
┌───▼───┐   ┌───▼───┐
│Frontend│   │  API  │
│ :3000  │   │ :8080 │
└────────┘   └───────┘
```

## 🌐 访问地址

### 标准模式

- **前端应用**: http://localhost:3000
- **API 服务**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 控制台**: http://localhost:8080/h2-console
- **健康检查**: http://localhost:8080/actuator/health

### 代理模式

- **应用入口**: http://localhost
- **API 接口**: http://localhost/api
- **Swagger UI**: http://localhost/swagger-ui.html
- **健康检查**: http://localhost/health

## 📁 文件说明

### Docker 相关文件

- `docker-compose.yml` - Docker Compose 配置文件
- `Dockerfile` - 后端 API 镜像构建文件
- `frontend/Dockerfile` - 前端应用镜像构建文件
- `nginx.conf` - Nginx 反向代理配置
- `frontend/nginx.conf` - 前端 Nginx 配置

### 配置文件

- `justfile` - Just 命令管理文件
- `.dockerignore` - Docker 构建忽略文件

## 🔧 环境变量

### API 服务环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `SPRING_PROFILES_ACTIVE` | `h2` | Spring 配置文件 |
| `SERVER_PORT` | `8080` | 服务端口 |

### 前端服务环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `REACT_APP_API_BASE_URL` | `http://localhost:8080` | API 基础地址 |
| `NODE_ENV` | `production` | Node.js 环境 |

## 🐛 故障排除

### 常见问题

1. **端口冲突**
   ```bash
   # 检查端口占用
   lsof -i :8080
   lsof -i :3000
   lsof -i :80
   ```

2. **服务启动失败**
   ```bash
   # 查看详细日志
   docker-compose logs api
   docker-compose logs frontend
   ```

3. **健康检查失败**
   ```bash
   # 检查服务状态
   docker-compose ps
   
   # 手动健康检查
   curl http://localhost:8080/actuator/health
   ```

4. **构建失败**
   ```bash
   # 清理并重新构建
   docker-compose down
   docker system prune -f
   docker-compose build --no-cache
   ```

### 日志查看

```bash
# 查看所有服务日志
just logs

# 查看特定服务日志
just logs api
just logs frontend
just logs nginx

# 实时跟踪日志
docker-compose logs -f --tail=100
```

### 性能监控

```bash
# 查看容器资源使用情况
docker stats

# 查看容器详细信息
docker-compose ps
docker inspect play-spring-boot-api
```

## 🔄 更新部署

```bash
# 使用 Just
just rebuild

# 使用 Docker Compose
docker-compose down
docker-compose pull
docker-compose build --no-cache
docker-compose up -d
```

## 📊 监控和日志

### 应用日志

- API 日志: `./logs/` 目录
- 前端日志: 通过 `docker-compose logs frontend` 查看
- Nginx 日志: 通过 `docker-compose logs nginx` 查看

### 健康检查

```bash
# 检查所有服务健康状态
just health

# 手动检查
curl http://localhost:8080/actuator/health
curl http://localhost:3000/health
```

## 🚀 生产环境部署建议

1. **使用环境变量文件**
   ```bash
   # 创建 .env 文件
   cp .env.example .env
   # 编辑配置
   vim .env
   ```

2. **启用 HTTPS**
   - 配置 SSL 证书
   - 更新 Nginx 配置

3. **数据持久化**
   - 配置数据库卷挂载
   - 备份策略

4. **监控和日志**
   - 集成 Prometheus/Grafana
   - 配置日志聚合

5. **安全配置**
   - 网络隔离
   - 访问控制
   - 密钥管理