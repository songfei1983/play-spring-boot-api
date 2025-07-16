# 数据源配置说明

本项目支持通过配置选择不同的数据源实现，包括内存存储和H2数据库。

## 配置选项

### 基本配置

在 `application.properties` 中配置以下参数：

```properties
# 数据源类型 (可选值: MEMORY, H2, MYSQL, POSTGRESQL)
app.datasource.type=MEMORY

# 是否启用JPA (true=使用数据库, false=使用内存存储)
app.datasource.enable-jpa=false
```

### 配置说明

- `app.datasource.type`: 指定数据源类型
  - `MEMORY`: 内存存储（默认）
  - `H2`: H2内存数据库
  - `MYSQL`: MySQL数据库（需要额外配置）
  - `POSTGRESQL`: PostgreSQL数据库（需要额外配置）

- `app.datasource.enable-jpa`: 控制是否使用JPA
  - `false`: 使用内存存储（默认）
  - `true`: 使用数据库存储

## 使用方式

### 1. 内存存储模式（默认）

```properties
app.datasource.type=MEMORY
app.datasource.enable-jpa=false
```

特点：
- 数据存储在内存中
- 应用重启后数据丢失
- 启动速度快
- 适合开发和测试

### 2. H2数据库模式

#### 方式一：修改 application.properties

```properties
app.datasource.type=H2
app.datasource.enable-jpa=true
```

#### 方式二：使用配置文件

```bash
# 使用 application-h2.properties 配置
java -jar app.jar --spring.profiles.active=h2
```

#### 方式三：环境变量

```bash
export SPRING_PROFILES_ACTIVE=h2
java -jar app.jar
```

特点：
- 数据存储在H2内存数据库中
- 支持SQL查询
- 可通过H2控制台查看数据：http://localhost:8080/h2-console
- 应用重启后数据丢失（内存模式）

## API端点

### 查看当前数据源信息

```http
GET /users/datasource-info
```

响应示例：
```json
{
  "dataSourceType": "Memory Storage",
  "timestamp": "2024-01-01T12:00:00"
}
```

或

```json
{
  "dataSourceType": "JPA Database (H2)",
  "timestamp": "2024-01-01T12:00:00"
}
```

## 架构说明

### 组件结构

1. **UserRepositoryService**: 统一的Repository服务层
   - 根据配置自动选择使用内存存储还是JPA数据库
   - 提供统一的数据访问接口

2. **UserRepository**: 内存存储实现
   - 使用 `ConcurrentHashMap` 存储数据
   - 条件注解：`@ConditionalOnProperty(name = "app.datasource.enable-jpa", havingValue = "false", matchIfMissing = true)`

3. **UserJpaRepository**: JPA数据库实现
   - 继承 `JpaRepository`
   - 条件注解：`@ConditionalOnProperty(name = "app.datasource.enable-jpa", havingValue = "true")`

4. **DataSourceConfig**: 数据源配置类
   - 定义数据源类型枚举
   - 提供配置属性绑定

### 自动配置机制

- 当 `app.datasource.enable-jpa=false` 时，只有 `UserRepository` 被创建
- 当 `app.datasource.enable-jpa=true` 时，只有 `UserJpaRepository` 被创建
- `UserRepositoryService` 根据配置和可用的Repository自动选择使用哪个实现

## 扩展支持

### 添加新的数据库支持

1. 在 `DataSourceConfig.DataSourceType` 枚举中添加新类型
2. 在 `pom.xml` 中添加相应的数据库驱动依赖
3. 创建对应的配置文件（如 `application-mysql.properties`）
4. 配置相应的数据源连接信息

### MySQL示例配置

```properties
app.datasource.type=MYSQL
app.datasource.enable-jpa=true

spring.datasource.url=jdbc:mysql://localhost:3306/testdb
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

## 注意事项

1. **数据持久化**: 内存模式和H2内存模式都不会持久化数据
2. **性能**: 内存模式性能最佳，数据库模式提供更多功能
3. **测试**: 建议在测试环境使用内存模式，生产环境使用数据库模式
4. **迁移**: 从内存模式切换到数据库模式时，需要重新初始化数据

## 故障排除

### 常见问题

1. **启动失败**: 检查配置文件中的 `app.datasource.enable-jpa` 设置
2. **数据访问异常**: 确认对应的Repository实现已正确创建
3. **H2控制台无法访问**: 确认 `spring.h2.console.enabled=true` 且使用H2配置

### 调试方法

1. 查看启动日志，确认哪个Repository被创建
2. 访问 `/users/datasource-info` 端点查看当前数据源状态
3. 启用DEBUG日志查看详细信息：`logging.level.fei.song.play_spring_boot_api=DEBUG`