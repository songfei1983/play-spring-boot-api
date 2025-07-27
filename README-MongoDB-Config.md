# MongoDB配置说明

本文档说明了项目中MongoDB配置的改进和使用方法。

## 配置改进概述

### 1. 统一配置管理
- 扩展了 `DataSourceConfig` 以支持 `MONGODB` 数据源类型
- 创建了专用的 `MongoProperties` 配置类，统一管理MongoDB相关配置
- 支持通过配置文件灵活配置MongoDB连接参数

### 2. 连接池优化
- 配置了MongoDB连接池参数，提高连接复用效率
- 支持自定义最大/最小连接数、连接超时时间等参数
- 优化了连接生命周期管理

### 3. 索引自动创建
- 实现了 `MongoIndexConfig` 类，自动创建必要的数据库索引
- 针对不同业务场景创建了优化的索引策略
- 支持复合索引以提高复杂查询性能

### 4. 异常处理机制
- 创建了 `MongoExceptionHandler` 工具类，提供统一的异常处理
- 支持异常分类和重试机制
- 完善的日志记录和错误追踪

## 配置文件说明

### 主配置文件 (application.properties)
```properties
# 数据源类型配置
app.datasource.type=MONGODB

# MongoDB基础配置
mongo.enabled=true
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=openrtb
spring.data.mongodb.auto-index-creation=true

# MongoDB连接池配置
spring.data.mongodb.connection-pool.max-size=20
spring.data.mongodb.connection-pool.min-size=5
spring.data.mongodb.connection-pool.max-wait-time-seconds=2
spring.data.mongodb.connection-pool.max-connection-idle-time-seconds=600
spring.data.mongodb.connection-pool.max-connection-life-time-seconds=1800
```

### MongoDB专用配置 (application-mongodb.yml)
使用MongoDB环境时，可以激活 `mongodb` profile：
```bash
java -jar app.jar --spring.profiles.active=mongodb
```

## 核心组件说明

### 1. MongoProperties
- **位置**: `fei.song.play_spring_boot_api.config.MongoProperties`
- **功能**: 统一管理MongoDB配置参数
- **特性**: 支持配置验证、默认值设置、连接池参数配置

### 2. MongoConfig (优化后)
- **位置**: `fei.song.play_spring_boot_api.ads.infrastructure.config.MongoConfig`
- **改进**: 
  - 使用 `MongoProperties` 替代硬编码配置
  - 添加连接池配置
  - 支持条件化配置 (`@ConditionalOnProperty`)
  - 修复了 `@NonNull` 约束问题

### 3. MongoIndexConfig
- **位置**: `fei.song.play_spring_boot_api.ads.infrastructure.config.MongoIndexConfig`
- **功能**: 自动创建数据库索引
- **索引策略**:
  - 竞价请求: 时间戳、请求ID、应用ID、设备类型、地理位置
  - 竞价响应: 响应ID、竞价请求ID、时间戳、竞价价格
  - 广告位库存: 广告位ID、应用ID、广告位类型、状态
  - 用户画像: 用户ID、设备ID、人口统计学信息、兴趣标签

### 4. MongoExceptionHandler
- **位置**: `fei.song.play_spring_boot_api.ads.infrastructure.util.MongoExceptionHandler`
- **功能**: 统一异常处理和重试机制
- **特性**: 
  - 异常分类处理
  - 智能重试策略
  - 详细的日志记录
  - 函数式编程接口支持

## 使用示例

### 1. 在Repository中使用异常处理
```java
@Repository
public class BidRequestRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private MongoExceptionHandler exceptionHandler;
    
    public BidRequest save(BidRequest bidRequest) {
        return exceptionHandler.executeWithExceptionHandling(
            "保存竞价请求",
            () -> mongoTemplate.save(bidRequest)
        );
    }
}
```

### 2. 环境配置切换
```bash
# 开发环境 - 使用内存数据源
java -jar app.jar --app.datasource.type=MEMORY

# 测试环境 - 使用MongoDB
java -jar app.jar --spring.profiles.active=mongodb

# 生产环境 - 使用MongoDB集群
java -jar app.jar --spring.profiles.active=mongodb --spring.data.mongodb.uri=mongodb://cluster1:27017,cluster2:27017,cluster3:27017/openrtb?replicaSet=rs0
```

## 性能优化建议

### 1. 索引优化
- 根据实际查询模式调整索引策略
- 定期分析慢查询日志
- 使用复合索引优化多字段查询

### 2. 连接池调优
- 根据应用负载调整连接池大小
- 监控连接池使用情况
- 合理设置连接超时时间

### 3. 查询优化
- 使用聚合管道优化复杂查询
- 实现分页查询避免大结果集
- 使用投影减少网络传输

## 监控和运维

### 1. 健康检查
- 配置了MongoDB健康检查端点
- 支持Actuator监控集成
- 提供详细的连接状态信息

### 2. 日志配置
- MongoDB操作日志记录
- 异常详细追踪
- 性能指标监控

### 3. 指标监控
- 连接池使用率
- 查询响应时间
- 异常发生频率

## 安全配置

### 1. 连接安全
```properties
# 启用SSL连接
spring.data.mongodb.uri=mongodb://username:password@localhost:27017/openrtb?ssl=true

# 配置认证数据库
spring.data.mongodb.authentication-database=admin
```

### 2. 访问控制
- 配置MongoDB用户权限
- 限制网络访问
- 启用审计日志

## 故障排查

### 1. 常见问题
- **连接超时**: 检查网络连接和防火墙设置
- **认证失败**: 验证用户名密码和权限配置
- **索引创建失败**: 检查数据库权限和磁盘空间

### 2. 调试技巧
- 启用MongoDB调试日志
- 使用MongoDB Compass分析查询性能
- 监控系统资源使用情况

## 版本兼容性

- Spring Boot: 2.7+
- Spring Data MongoDB: 3.4+
- MongoDB Driver: 4.6+
- MongoDB Server: 4.4+

## 后续改进计划

1. **集群支持**: 添加MongoDB副本集和分片集群配置
2. **缓存集成**: 集成Redis缓存减少数据库访问
3. **读写分离**: 支持读写分离配置
4. **数据迁移**: 提供数据迁移工具和脚本
5. **性能监控**: 集成APM工具进行性能监控