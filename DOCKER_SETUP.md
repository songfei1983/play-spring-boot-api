# Docker Compose 部署指南

本项目提供了完整的 Docker Compose 配置，可以一键启动 Spring Boot API 和 React 前端应用。

## 快速开始

### 前置要求

- Docker 20.10+
- Docker Compose 2.0+

### 启动服务

1. 在项目根目录执行以下命令：

```bash
# 构建并启动所有服务
docker-compose up --build

# 或者在后台运行
docker-compose up --build -d
```

2. 等待服务启动完成，你将看到以下输出：
   - API 服务健康检查通过
   - 前端服务启动完成

### 访问应用

- **前端应用**: http://localhost:3000
- **API 文档**: http://localhost:8080/swagger-ui.html
- **API 端点**: http://localhost:8080/api/
- **H2 数据库控制台**: http://localhost:8080/h2-console
- **健康检查**: http://localhost:3000/actuator/health

### 停止服务

```bash
# 停止服务
docker-compose down

# 停止服务并删除卷
docker-compose down -v
```

## 服务架构

### API 服务 (api)

- **端口**: 8080 (内部)
- **镜像**: 基于 eclipse-temurin:17-jre
- **健康检查**: /actuator/health
- **配置文件**: application-docker.properties

### 前端服务 (frontend)

- **端口**: 3000 (映射到容器的80端口)
- **镜像**: 基于 nginx:alpine
- **功能**: 
  - 提供 React 应用
  - 代理 API 请求到后端
  - 支持 React Router

## 网络配置

- 所有服务运行在 `app-network` 网络中
- 前端通过 nginx 代理访问后端 API
- 前端服务依赖于 API 服务的健康检查

## 开发模式

如果需要在开发模式下运行：

```bash
# 只启动 API 服务
docker-compose up api

# 在另一个终端启动前端开发服务器
cd frontend
npm start
```

## 故障排除

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs api
docker-compose logs frontend

# 实时跟踪日志
docker-compose logs -f
```

### 重新构建

```bash
# 强制重新构建镜像
docker-compose build --no-cache

# 重新构建并启动
docker-compose up --build --force-recreate
```

### 清理资源

```bash
# 删除所有容器、网络和镜像
docker-compose down --rmi all

# 清理未使用的 Docker 资源
docker system prune -a
```

## 配置说明

### 环境变量

- `SPRING_PROFILES_ACTIVE=docker`: 使用 Docker 专用配置
- `SERVER_PORT=8080`: API 服务端口

### 数据持久化

当前配置使用内存数据库，重启后数据会丢失。如需持久化数据，可以：

1. 修改 `application-docker.properties` 中的数据库配置
2. 在 `docker-compose.yml` 中添加数据库服务
3. 配置相应的数据卷

## 生产部署注意事项

1. **安全性**: 修改默认密码和密钥
2. **资源限制**: 为容器设置内存和 CPU 限制
3. **日志管理**: 配置日志轮转和集中收集
4. **监控**: 添加监控和告警配置
5. **备份**: 配置数据备份策略