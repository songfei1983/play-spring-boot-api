[![CI/CD Pipeline](https://github.com/songfei1983/play-spring-boot-api/actions/workflows/ci.yml/badge.svg)](https://github.com/songfei1983/play-spring-boot-api/actions/workflows/ci.yml)

# Play Spring Boot API

一个基于 Spring Boot 的 RESTful API 项目，采用领域驱动设计 (DDD) 架构模式，提供用户管理、购买历史、活动跟踪和用户档案等功能。

## 🚀 项目特性

- **领域驱动设计 (DDD)**: 清晰的分层架构，易于维护和扩展
- **RESTful API**: 标准的 REST 接口设计
- **测试覆盖**: 完整的单元测试和集成测试
- **代码质量**: 使用 Lombok 减少样板代码，JaCoCo 测试覆盖率监控
- **API 文档**: 集成 Springdoc OpenAPI 自动生成文档
- **AOP 支持**: 面向切面编程，用于日志记录和监控

## 🛠️ 技术栈

- **Java 17+**
- **Spring Boot 3.x**
  - Spring Web
  - Spring AOP
  - Spring Test
- **Lombok** - 减少样板代码
- **JaCoCo** - 测试覆盖率
- **Springdoc OpenAPI** - API 文档生成
- **Maven** - 依赖管理和构建工具

## 📁 项目结构

```
src/main/java/fei/song/play_spring_boot_api/
├── users/
│   ├── application/         # 应用服务层
│   │   ├── ActivityTrackService.java
│   │   ├── PurchaseHistoryService.java
│   │   ├── UserProfileService.java
│   │   └── UserService.java
│   ├── domain/              # 领域模型层
│   │   ├── ActivityTrack.java
│   │   ├── PurchaseHistory.java
│   │   ├── User.java
│   │   └── UserProfile.java
│   ├── infrastructure/       # 基础设施层
│   │   ├── ActivityTrackRepository.java
│   │   ├── PurchaseHistoryRepository.java
│   │   ├── UserAccessLogAspect.java
│   │   ├── UserProfileRepository.java
│   │   └── UserRepository.java
│   └── interfaces/          # 接口层 (Controllers)
│       ├── ActivityTrackController.java
│       ├── PurchaseHistoryController.java
│       ├── UserController.java
│       └── UserProfileController.java
└── PlaySpringBootApiApplication.java
```

### 架构说明

- **Interface Layer (接口层)**: REST Controllers，处理 HTTP 请求和响应
- **Application Layer (应用层)**: 业务服务，协调领域对象完成业务逻辑
- **Domain Layer (领域层)**: 核心业务实体和领域逻辑
- **Infrastructure Layer (基础设施层)**: 数据访问、外部服务集成等

## 🚀 快速开始

### 环境要求

- Java 17 或更高版本
- Maven 3.6 或更高版本

### 运行项目

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd play-spring-boot-api
   ```

2. **编译项目**
   ```bash
   ./mvnw clean compile
   ```

3. **运行测试**
   ```bash
   ./mvnw test
   ```

4. **启动应用**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **访问应用**
   - 应用地址: http://localhost:8080
   - API 文档: http://localhost:8080/swagger-ui.html

## 📊 测试覆盖率

项目使用 JaCoCo 进行测试覆盖率监控。

### 生成覆盖率报告

```bash
# 运行测试并生成覆盖率数据
./mvnw clean test

# 生成 HTML 格式的覆盖率报告
./mvnw jacoco:report

# 查看报告 (macOS)
open target/site/jacoco/index.html
```

### 当前覆盖率状况

- **Domain 层**: 92% (得益于 Lombok 注解)
- **Application 层**: 部分覆盖 (UserService 已测试)
- **Interface 层**: 部分覆盖 (PurchaseHistoryController 已测试)
- **Infrastructure 层**: 36%

## 📚 API 文档

### 主要 API 端点

#### 用户管理 (`/users`)
- `GET /users` - 获取所有用户
- `GET /users/{id}` - 根据 ID 获取用户
- `POST /users` - 创建新用户
- `PUT /users/{id}` - 更新用户信息
- `DELETE /users/{id}` - 删除用户

#### 购买历史 (`/purchase-history`)
- `GET /purchase-history` - 获取所有购买记录
- `GET /purchase-history/{id}` - 根据 ID 获取购买记录
- `GET /purchase-history/user/{userId}` - 获取用户的购买记录
- `GET /purchase-history/order/{orderNumber}` - 根据订单号获取记录
- `POST /purchase-history` - 创建购买记录
- `PUT /purchase-history/{id}` - 更新购买记录
- `DELETE /purchase-history/{id}` - 删除购买记录

#### 用户档案 (`/user-profiles`)
- `GET /user-profiles` - 获取所有用户档案
- `GET /user-profiles/{id}` - 根据 ID 获取用户档案
- `POST /user-profiles` - 创建用户档案
- `PUT /user-profiles/{id}` - 更新用户档案
- `DELETE /user-profiles/{id}` - 删除用户档案

#### 活动跟踪 (`/activity-tracks`)
- `GET /activity-tracks` - 获取所有活动记录
- `GET /activity-tracks/{id}` - 根据 ID 获取活动记录
- `POST /activity-tracks` - 创建活动记录
- `PUT /activity-tracks/{id}` - 更新活动记录
- `DELETE /activity-tracks/{id}` - 删除活动记录

### 在线 API 文档

启动应用后，访问 http://localhost:8080/swagger-ui.html 查看完整的 API 文档。

## 🧪 测试

### 运行所有测试

```bash
./mvnw test
```

### 运行特定测试类

```bash
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=PurchaseHistoryControllerTest
```

### 测试结构

- **单元测试**: 测试单个类的功能
- **集成测试**: 测试 Controller 层的 HTTP 接口
- **Mock 测试**: 使用 Mockito 模拟依赖

## 🔧 开发指南

### 代码规范

- 使用 Lombok 注解减少样板代码
- 遵循 DDD 分层架构原则
- 编写完整的单元测试
- 使用有意义的变量和方法命名

### 添加新功能

1. 在 `domain` 层创建实体类
2. 在 `infrastructure` 层创建 Repository 接口
3. 在 `application` 层创建 Service 类
4. 在 `interfaces` 层创建 Controller 类
5. 编写对应的测试类

### Git 工作流

- 使用有意义的提交信息
- 创建功能分支进行开发
- 提交前运行测试确保代码质量

## 📝 变更记录

详细的变更记录请查看 [CHANGE.md](CHANGE.md) 文件。

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目维护者: 开发团队
- 项目地址: [GitHub Repository](https://github.com/your-username/play-spring-boot-api)

---

**项目状态**: 🚧 开发中

**最后更新**: 2025-07-05