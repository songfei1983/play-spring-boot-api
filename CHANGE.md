# 项目变更记录 (CHANGE.md)

本文档记录了 play-spring-boot-api 项目从创建到现在的所有重要修改和改进。

## 版本历史

### [当前版本] - 2025-07-17

#### 🧪 基础设施层测试完善和覆盖率大幅提升

**测试修复和优化**
- 修复 `UserProfileRepositoryTest.java` 中的所有编译错误
  - 修正 `UserProfile` 构造函数调用，调整参数顺序和类型
  - 替换不存在的方法调用（`existsByPhoneNumber`、`countByGender`）为实际存在的方法
  - 修复属性访问错误（`getRealName()` → `getUserId()`，`getBirthDate()` → `getBirthday()`）
  - 删除重复的 `testCount` 方法定义
- 修复 `PurchaseHistoryRepositoryTest.java` 中的所有编译错误
  - 修正 `PurchaseHistory` 构造函数调用，提供完整的参数列表
  - 替换不存在的方法调用（`findByProductName`、`findByPriceRange`）为实际存在的方法
  - 修复属性访问错误（`getPrice()` → `getUnitPrice()`）
  - 修正 `testInitializeData` 中的断言，使其与实际初始化数据匹配

**测试覆盖率显著提升**
- **基础设施层覆盖率**: 从 49% 提升到 **90%**，远超 80% 的目标要求
- **整体项目覆盖率**: 达到 **89%**
- **测试执行结果**: 493个测试全部通过，无失败和错误

**测试内容完善**
- `UserProfileRepository` 完整测试覆盖：CRUD操作、查询方法、统计方法
- `PurchaseHistoryRepository` 完整测试覆盖：复杂查询、时间范围查询、用户统计
- `UserRepositoryService` 服务层测试：业务逻辑验证、异常处理

**代码质量保障**
- 所有基础设施层组件现在都有全面的单元测试
- 测试用例覆盖正常流程、边界条件和异常情况
- 确保代码重构和功能扩展的安全性

### [2025-07-05]

#### 🎨 前端UI优化和用户体验提升

**全屏布局重构**
- 优化所有管理页面布局，使数据表格填满整个窗口高度
- 修改 `App.css` 中的 `.main-content`、`.management-container`、`.table-container` 样式
- 移除不必要的边距、边框和阴影，适配全屏显示
- 添加 `flex: 1` 和 `overflow: auto` 实现响应式滚动

**组件结构统一**
- 重构 `UserManagement.tsx` - 添加 `table-container` 包装器和统一的操作按钮布局
- 重构 `UserProfileManagement.tsx` - 统一表格结构和按钮样式
- 重构 `ActivityTrackManagement.tsx` - 应用一致的布局模式
- 重构 `PurchaseHistoryManagement.tsx` - 作为布局标准模板

**操作按钮样式统一**
- 所有页面使用统一的 `actions-cell` 和 `action-buttons` 类
- 编辑按钮统一使用 `btn-primary` 样式
- 删除按钮保持 `btn-danger` 样式
- 添加 `flex-shrink: 0` 确保固定高度组件不被压缩

**页面状态持久化**
- 修改 `App.tsx` 添加 localStorage 支持
- 实现页面刷新后保持当前选中标签页状态
- 使用 React useEffect Hook 自动保存和恢复页面状态
- 提升用户体验，避免每次刷新都返回默认页面

**技术改进**
- 导入 `useEffect` Hook 实现状态管理
- 使用函数式状态初始化从 localStorage 读取保存的状态
- 添加状态变化监听器自动保存到浏览器本地存储
- 兼容所有现代浏览器的 localStorage API

**影响范围**
- 前端文件: 5个组件文件 + 1个样式文件
- 新增功能: 页面状态持久化
- 用户体验: 全屏显示 + 状态保持
- 代码一致性: 统一的组件结构和样式规范

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

#### 🚀 CI/CD 自动化流水线

**GitHub Actions 工作流**
- 创建 `.github/workflows/ci.yml` - 完整的 CI/CD 流水线
- **多任务并行执行**:
  - 测试和构建 (支持 Java 17 和 21 多版本矩阵)
  - 代码质量分析 (SonarCloud 集成)
  - 安全扫描 (OWASP 依赖漏洞检查)
  - Docker 构建 (多平台镜像构建和推送)
- **自动化功能**:
  - 单元测试和集成测试执行
  - JaCoCo 测试覆盖率报告生成和 Codecov 上传
  - Maven 依赖缓存优化
  - 构建产物自动上传
  - 仅在 main 分支推送时构建 Docker 镜像
- **触发条件**: Push 和 Pull Request 到 main/develop 分支

**Docker 容器化支持**
- 创建 `Dockerfile` - 多阶段构建优化
- **特性**:
  - 使用 Eclipse Temurin JRE 17 Alpine 基础镜像
  - 非 root 用户运行提高安全性
  - 健康检查配置
  - 构建缓存优化
- 创建 `.dockerignore` - 优化构建上下文
- 支持多平台镜像 (linux/amd64, linux/arm64)

**安全和质量保障**
- 创建 `.github/dependency-check-suppressions.xml` - OWASP 扫描抑制规则
- 集成 SonarCloud 代码质量分析
- OWASP 依赖漏洞扫描
- 安全最佳实践 (非 root 用户、最小权限)

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