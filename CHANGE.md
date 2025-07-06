# 项目变更记录 (CHANGE.md)

本文档记录了 play-spring-boot-api 项目从创建到现在的所有重要修改和改进。

## 版本历史

### [当前版本] - 2025-07-05

#### 🧪 新增测试覆盖

**PurchaseHistoryController 单元测试**
- 创建 `PurchaseHistoryControllerTest.java` - 完整的 Controller 层测试
- **测试覆盖范围**:
  - 所有 CRUD 操作的 HTTP 端点测试
  - 查询方法测试 (按用户ID、订单号、产品ID、类别、品牌等)
  - 统计方法测试 (用户购买统计、总金额、购买次数等)
  - 异常情况处理测试 (404 Not Found, 400 Bad Request, 500 Internal Server Error)
  - Mock 服务层依赖和验证
- **测试方法数量**: 23个测试用例
- **HTTP 状态码验证**: 200, 201, 204, 400, 404, 409, 500
- **JSON 响应内容验证**: 完整的响应体断言

**UserControllerTest 修复**
- 修正 `UserControllerTest.java` 中的编译错误
- 调整 User 实体字段映射 (username → name)
- 修正 Optional 类型的 mock 返回值
- 更新 URL 路径映射 (/api/users → /users)
- 移除重复的测试方法

#### 🔧 开发环境优化

**Git 忽略文件增强**
- 大幅扩展 `.gitignore` 文件覆盖范围
- **新增支持**:
  - **macOS**: .DS_Store, .AppleDouble, .LSOverride, Spotlight, Time Machine 等系统文件
  - **VS Code**: .vscode/ 目录，选择性保留有用配置文件
  - **IntelliJ IDEA**: .idea/, out/ 目录和相关文件
  - **Maven**: 发布文件、备份文件、时间属性等
  - **Java**: 编译文件、日志文件、归档文件等
  - **Metals**: Scala 语言服务器缓存文件 (.metals/, .bloop/, .ammonite/)
  - **临时文件**: 各种临时文件和系统生成文件
- **跨平台支持**: Windows (Thumbs.db) 和 Linux 系统文件

#### 🔧 代码重构

**使用 Lombok 简化 Domain 层实体类**
- 重构 `ActivityTrack.java` - 移除手动编写的 getter/setter 方法，添加 Lombok 注解
- 重构 `PurchaseHistory.java` - 移除手动编写的 getter/setter 方法，添加 Lombok 注解
- 重构 `User.java` - 移除手动编写的 getter/setter 方法，添加 Lombok 注解
- 重构 `UserProfile.java` - 移除手动编写的 getter/setter 方法，添加 Lombok 注解
- **影响**: 总计移除约 610 行冗余代码，提高代码可维护性
- **添加的注解**:
  - `@Data` - 自动生成 getter/setter、toString、equals、hashCode
  - `@NoArgsConstructor` - 生成无参构造函数
  - `@AllArgsConstructor` - 生成全参构造函数
  - `@Builder` - 支持建造者模式

#### 🧪 测试覆盖率配置

**添加 JaCoCo 测试覆盖率支持**
- 在 `pom.xml` 中配置 JaCoCo Maven 插件 (版本 0.8.11)
- 配置自动生成测试覆盖率报告
- 创建 `UserServiceTest.java` 示例测试类
- **测试覆盖范围**:
  - UserService 所有主要方法的单元测试
  - Mock 对象配置和验证
  - 边界条件和异常情况测试
  - 输入验证测试

**当前测试覆盖率状况**:
- 总体指令覆盖率: 17%
- Domain 层: 92% (得益于 Lombok 注解)
- Application 层: 6% (UserService 已测试)
- Infrastructure 层: 36%
- Interface 层: 0% (待添加 Controller 测试)

#### 📁 项目结构优化

**现有项目架构**:
```
src/main/java/fei/song/play_spring_boot_api/
├── users/
│   ├── application/     # 应用服务层
│   │   ├── ActivityTrackService.java
│   │   ├── PurchaseHistoryService.java
│   │   ├── UserProfileService.java
│   │   └── UserService.java
│   ├── domain/          # 领域模型层
│   │   ├── ActivityTrack.java
│   │   ├── PurchaseHistory.java
│   │   ├── User.java
│   │   └── UserProfile.java
│   ├── infrastructure/  # 基础设施层
│   │   ├── ActivityTrackRepository.java
│   │   ├── PurchaseHistoryRepository.java
│   │   ├── UserAccessLogAspect.java
│   │   ├── UserProfileRepository.java
│   │   └── UserRepository.java
│   └── interfaces/      # 接口层
│       ├── ActivityTrackController.java
│       ├── PurchaseHistoryController.java
│       ├── UserController.java
│       └── UserProfileController.java
└── PlaySpringBootApiApplication.java
```

#### 🛠️ 技术栈更新

**依赖管理**:
- Spring Boot Starter Web
- Spring Boot Starter AOP
- Spring Boot Starter Test
- Springdoc OpenAPI (API 文档)
- **新增**: Lombok (代码生成)
- **新增**: JaCoCo (测试覆盖率)

#### 📊 质量改进

**代码质量提升**:
- 减少样板代码，提高可读性
- 统一使用建造者模式创建对象
- 保留自定义业务逻辑方法
- 添加完整的测试覆盖率监控

**构建验证**:
- 所有类通过 Maven 编译验证
- 测试套件成功运行
- 覆盖率报告正常生成

## 使用说明

### 运行测试覆盖率

```bash
# 运行测试并生成覆盖率数据
mvn clean test

# 生成 HTML 格式的覆盖率报告
mvn jacoco:report

# 查看报告
open target/site/jacoco/index.html
```

### 覆盖率报告位置

- HTML 报告: `target/site/jacoco/index.html`
- CSV 数据: `target/site/jacoco/jacoco.csv`
- XML 数据: `target/site/jacoco/jacoco.xml`

## 下一步计划

### 🎯 待改进项目

1. **提升测试覆盖率**
   - 为 Controller 层添加集成测试
   - 为其他 Service 类添加单元测试
   - 添加 Repository 层测试

2. **代码质量**
   - 添加代码静态分析工具 (如 SpotBugs, PMD)
   - 配置代码格式化规则
   - 添加 API 文档注解

3. **功能完善**
   - 添加数据库集成
   - 实现完整的 CRUD 操作
   - 添加异常处理机制
   - 配置日志系统

---

**维护者**: 项目开发团队  
**最后更新**: 2025-07-05  
**项目状态**: 开发中