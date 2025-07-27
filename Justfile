# Spring Boot API with React Frontend - Justfile
# 使用 just 命令简化常用操作

# 默认显示所有可用命令
default:
    @just --list

# 🐳 Docker 相关命令

# 构建并启动所有服务
up:
    docker-compose up --build -d

# 启动服务（不重新构建）
start:
    docker-compose up -d

# 停止所有服务
down:
    docker-compose down

# 重启所有服务
restart:
    docker-compose restart

# 查看服务状态
status:
    docker-compose ps

# 查看服务日志
logs service="":
    #!/usr/bin/env bash
    if [ -z "{{service}}" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f {{service}}
    fi

# 清理Docker资源
clean:
    docker-compose down -v --remove-orphans
    docker system prune -f

# 🏗️ 构建相关命令

# Maven 清理和编译
build:
    ./mvnw clean compile

# Maven 打包
package:
    ./mvnw clean package -DskipTests

# Maven 打包（包含测试）
package-with-tests:
    ./mvnw clean package

# 🧪 测试相关命令

# 运行所有测试
test:
    ./mvnw test

# 运行特定测试类
test-class class:
    ./mvnw test -Dtest={{class}}

# 运行集成测试
test-integration:
    ./mvnw verify -Dspring.profiles.active=test

# 运行前端测试
test-frontend:
    cd frontend && npm test -- --watchAll=false --verbose

# 运行E2E测试
test-e2e:
    #!/usr/bin/env bash
    echo "启动API服务..."
    just run > /dev/null 2>&1 &
    API_PID=$!
    echo "等待API服务启动..."
    sleep 15
    echo "运行E2E测试..."
    cd frontend && npm run test:e2e
    echo "停止API服务..."
    kill $API_PID 2>/dev/null

# 运行所有 OpenRTB 测试
test-openrtb:
    ./run-openrtb-tests.sh

# 运行 OpenRTB 集成测试
test-openrtb-integration:
    ./mvnw test -Dtest=OpenRTBIntegrationTest

# 运行 OpenRTB 端到端测试
test-openrtb-e2e:
    ./mvnw test -Dtest=OpenRTBEndToEndTest

# 🚀 开发相关命令

# 启动Spring Boot应用（默认配置）
run:
    ./mvnw spring-boot:run

# 启动Spring Boot应用（开发模式）
dev:
    ./mvnw spring-boot:run -Dspring.profiles.active=dev

# 启动Spring Boot应用（H2数据库）
dev-h2:
    ./mvnw spring-boot:run -Dspring.profiles.active=h2

# 启动前端开发服务器
dev-frontend:
    cd frontend && npm start

# 安装前端依赖
install-frontend:
    cd frontend && npm install

# 构建前端生产版本
build-frontend:
    cd frontend && npm run build

# 📊 代码质量相关命令

# 运行代码格式化
format:
    ./mvnw spotless:apply

# 检查代码格式
format-check:
    ./mvnw spotless:check

# 运行静态代码分析
lint:
    ./mvnw checkstyle:check

# 🔍 实用工具命令

# 查看应用健康状态
health:
    curl -s http://localhost:8080/actuator/health | jq .

# 打开Swagger UI
swagger:
    open http://localhost:3000/swagger-ui.html

# 打开H2控制台
h2-console:
    open http://localhost:3000/h2-console

# 打开前端应用
open-app:
    open http://localhost:3000

# 查看API文档
api-docs:
    curl -s http://localhost:8080/v3/api-docs | jq .

# 📝 数据库相关命令

# 重置H2数据库
reset-db:
    rm -f *.db
    ./mvnw spring-boot:run -Dspring.profiles.active=h2 &
    sleep 10
    pkill -f "spring-boot:run"

# 🔧 维护命令

# 更新Maven依赖
update-deps:
    ./mvnw versions:display-dependency-updates

# 更新前端依赖
update-frontend-deps:
    cd frontend && npm update

# 生成依赖报告
dependency-report:
    ./mvnw dependency:tree > dependency-tree.txt
    echo "依赖树已保存到 dependency-tree.txt"

# 🚀 快速启动命令组合

# 完整启动：构建并启动所有服务，然后打开浏览器
quick-start:
    just up
    sleep 15
    just open-app

# 开发环境启动：启动后端和前端开发服务器
dev-all:
    #!/usr/bin/env bash
    echo "启动后端服务..."
    ./mvnw spring-boot:run -Dspring.profiles.active=h2 &
    echo "等待后端启动..."
    sleep 10
    echo "启动前端开发服务器..."
    cd frontend && npm start

# 运行所有测试
test-all:
    just test
    just test-frontend

# 完整构建和测试
full-build:
    just build
    just test-all
    just package-with-tests

# 📋 信息显示

# 显示项目信息
info:
    @echo "🚀 Spring Boot API with React Frontend"
    @echo "📁 项目目录: $(pwd)"
    @echo "🐳 Docker状态:"
    @docker-compose ps 2>/dev/null || echo "  Docker Compose未运行"
    @echo "📦 Maven版本:"
    @./mvnw --version | head -1
    @echo "🌐 可用端点:"
    @echo "  - 前端应用: http://localhost:3000"
    @echo "  - API文档: http://localhost:3000/swagger-ui.html"
    @echo "  - H2控制台: http://localhost:3000/h2-console"
    @echo "  - 健康检查: http://localhost:8080/actuator/health"