# Just Commands - 命令简化指南

本项目使用 [just](https://github.com/casey/just) 命令运行器来简化常用操作。

## 🚀 快速开始

### 安装 just

```bash
# macOS
brew install just

# 或使用 cargo
cargo install just

# 或下载二进制文件
curl --proto '=https' --tlsv1.2 -sSf https://just.systems/install.sh | bash -s -- --to ~/bin
```

### 查看所有可用命令

```bash
just
# 或
just --list
```

## 📋 常用命令

### 🐳 Docker 操作

```bash
# 构建并启动所有服务
just up

# 停止所有服务
just down

# 查看服务状态
just status

# 查看日志
just logs
just logs api      # 查看API服务日志
just logs frontend # 查看前端服务日志

# 重启服务
just restart

# 清理Docker资源
just clean
```

### 🏗️ 构建和测试

```bash
# 构建项目
just build

# 打包（跳过测试）
just package

# 打包（包含测试）
just package-with-tests

# 运行测试
just test
just test-frontend
just test-all

# 完整构建和测试
just full-build
```

### 🚀 开发模式

```bash
# 启动后端开发服务器
just dev
just dev-h2  # 使用H2数据库

# 启动前端开发服务器
just dev-frontend

# 同时启动前后端开发服务器
just dev-all

# 安装前端依赖
just install-frontend
```

### 🔍 实用工具

```bash
# 打开应用
just open-app
just swagger
just h2-console

# 检查健康状态
just health

# 查看API文档
just api-docs

# 显示项目信息
just info
```

### 📊 代码质量

```bash
# 格式化代码
just format

# 检查代码格式
just format-check

# 静态代码分析
just lint
```

### 🚀 快速启动组合

```bash
# 一键启动：构建服务并打开浏览器
just quick-start
```

## 🎯 使用场景

### 新开发者入门

```bash
# 1. 克隆项目后
just install-frontend

# 2. 快速启动
just quick-start
```

### 日常开发

```bash
# 启动开发环境
just dev-all

# 运行测试
just test-all

# 格式化代码
just format
```

### 生产部署

```bash
# 完整构建和测试
just full-build

# Docker部署
just up
```

### 调试问题

```bash
# 查看服务状态
just status

# 查看日志
just logs

# 检查健康状态
just health

# 重启服务
just restart
```

## 💡 提示

- 使用 `just` 命令查看所有可用操作
- 大部分命令支持在项目根目录的任何位置运行
- Docker相关命令会自动处理服务依赖关系
- 开发模式命令会自动重载代码更改

## 🔧 自定义

你可以编辑 `Justfile` 来添加项目特定的命令或修改现有命令的行为。

```bash
# 编辑Justfile
vim Justfile

# 验证语法
just --evaluate
```

---

更多信息请参考：
- [Just 官方文档](https://just.systems/man/en/)
- [项目 Docker 设置](./DOCKER_SETUP.md)
- [API 文档](./API_DOCUMENTATION.md)