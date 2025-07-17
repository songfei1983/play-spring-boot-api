# justfile - 项目命令管理
# 使用 `just --list` 查看所有可用命令

# 默认命令 - 显示帮助信息
default:
    @just --list

# 开发环境命令
# ================

# 启动开发环境 (API + 前端)
dev:
    @echo "🚀 启动开发环境..."
    @echo "📦 启动后端 API (端口 8080)..."
    ./mvnw spring-boot:run &
    @echo "⏳ 等待后端启动..."
    sleep 10
    @echo "🎨 启动前端 (端口 3000)..."
    cd frontend && npm start

# 仅启动后端 API
dev-api:
    @echo "📦 启动后端 API (端口 8080)..."
    ./mvnw spring-boot:run

# 仅启动前端
dev-frontend:
    @echo "🎨 启动前端 (端口 3000)..."
    cd frontend && npm start

# 构建命令
# ================

# 清理并构建整个项目
build:
    @echo "🔨 构建整个项目..."
    @just clean
    @just build-api
    @just build-frontend

# 构建后端 API
build-api:
    @echo "📦 构建后端 API..."
    ./mvnw clean package -DskipTests

# 构建前端
build-frontend:
    @echo "🎨 构建前端..."
    cd frontend && npm ci && npm run build

# 清理构建产物
clean:
    @echo "🧹 清理构建产物..."
    ./mvnw clean
    cd frontend && rm -rf build node_modules

# 测试命令
# ================

# 运行所有测试
test:
    @echo "🧪 运行所有测试..."
    @just test-api
    @just test-frontend

# 运行所有测试 (包含E2E测试)
test-all:
    @echo "🧪 运行所有测试 (包含E2E)..."
    @just test-api
    @just test-frontend
    @just test-e2e-with-api

# 运行后端测试
test-api:
    @echo "📦 运行后端测试..."
    ./mvnw test

# 运行前端测试
test-frontend:
    @echo "🎨 运行前端测试..."
    cd frontend && npm test -- --coverage --watchAll=false

# 运行端到端测试
test-e2e:
    @echo "🎭 运行端到端测试..."
    cd frontend && npx playwright test

# 启动API服务并运行UI测试
test-e2e-with-api:
    @echo "🎭 启动API服务并运行UI测试..."
    @echo "📦 启动后端 API (端口 8080)..."
    ./mvnw spring-boot:run > /dev/null 2>&1 &
    @echo "⏳ 等待API服务启动..."
    @sleep 15
    @echo "🏥 检查API服务健康状态..."
    @until curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; do \
        echo "⏳ 等待API服务就绪..."; \
        sleep 2; \
    done
    @echo "✅ API服务已就绪，开始运行UI测试..."
    cd frontend && npx playwright test
    @echo "🛑 停止API服务..."
    @pkill -f "spring-boot:run" || true

# 生成测试覆盖率报告
coverage:
    @echo "📊 生成测试覆盖率报告..."
    ./mvnw jacoco:report
    @echo "📊 后端覆盖率报告: target/site/jacoco/index.html"
    cd frontend && npm test -- --coverage --watchAll=false
    @echo "📊 前端覆盖率报告: frontend/coverage/lcov-report/index.html"

# Docker 命令
# ================

# 使用 Docker Compose 启动所有服务
up:
    @echo "🐳 启动 Docker 服务..."
    docker-compose up -d
    @echo "✅ 服务已启动:"
    @echo "   - API: http://localhost:8080"
    @echo "   - 前端: http://localhost:3000"
    @echo "   - Swagger: http://localhost:8080/swagger-ui.html"

# 使用 Docker Compose 启动所有服务 (包含 Nginx 代理)
up-with-proxy:
    @echo "🐳 启动 Docker 服务 (包含 Nginx 代理)..."
    docker-compose --profile with-proxy up -d
    @echo "✅ 服务已启动:"
    @echo "   - 应用: http://localhost (通过 Nginx 代理)"
    @echo "   - API: http://localhost/api"
    @echo "   - Swagger: http://localhost/swagger-ui.html"

# 停止 Docker 服务
down:
    @echo "🛑 停止 Docker 服务..."
    docker-compose down

# 重新构建并启动 Docker 服务
rebuild:
    @echo "🔄 重新构建并启动 Docker 服务..."
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d

# 查看 Docker 服务日志
logs service="":
    @if [ "{{service}}" = "" ]; then \
        echo "📋 查看所有服务日志..."; \
        docker-compose logs -f; \
    else \
        echo "📋 查看 {{service}} 服务日志..."; \
        docker-compose logs -f {{service}}; \
    fi

# 代码质量命令
# ================

# 代码格式化
format:
    @echo "✨ 格式化代码..."
    ./mvnw spotless:apply
    cd frontend && npm run format

# 代码检查
lint:
    @echo "🔍 检查代码质量..."
    ./mvnw spotless:check
    cd frontend && npm run lint

# 安全扫描
security-scan:
    @echo "🔒 运行安全扫描..."
    ./mvnw org.owasp:dependency-check-maven:check

# 数据库命令
# ================

# 启动 H2 数据库控制台
h2-console:
    @echo "💾 启动 H2 数据库控制台..."
    @echo "🌐 访问: http://localhost:8080/h2-console"
    @echo "📝 JDBC URL: jdbc:h2:mem:testdb"
    @echo "👤 用户名: sa"
    @echo "🔑 密码: password"

# 实用工具命令
# ================

# 安装依赖
install:
    @echo "📦 安装项目依赖..."
    ./mvnw dependency:resolve
    cd frontend && npm install

# 更新依赖
update:
    @echo "🔄 更新项目依赖..."
    ./mvnw versions:display-dependency-updates
    cd frontend && npm update

# 检查项目健康状态
health:
    @echo "🏥 检查项目健康状态..."
    @echo "📦 检查后端健康状态..."
    curl -f http://localhost:8080/actuator/health || echo "❌ 后端服务未运行"
    @echo "🎨 检查前端健康状态..."
    curl -f http://localhost:3000 || echo "❌ 前端服务未运行"

# 打开相关 URL
open-urls:
    @echo "🌐 打开相关 URL..."
    open http://localhost:3000
    open http://localhost:8080/swagger-ui.html
    open http://localhost:8080/h2-console

# 项目信息
info:
    @echo "ℹ️  项目信息:"
    @echo "   名称: Play Spring Boot API"
    @echo "   版本: $(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)"
    @echo "   Java 版本: $(java -version 2>&1 | head -n 1)"
    @echo "   Node 版本: $(node --version)"
    @echo "   Docker 版本: $(docker --version)"