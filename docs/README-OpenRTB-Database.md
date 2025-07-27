# OpenRTB 数据持久化方案

本文档详细介绍了为 OpenRTB 竞价系统设计的 NoSQL 数据持久化方案，包括数据库选择、表结构设计、性能优化和部署指南。

## 📋 目录

- [架构概览](#架构概览)
- [数据库选择](#数据库选择)
- [数据模型设计](#数据模型设计)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [性能优化](#性能优化)
- [监控与运维](#监控与运维)
- [最佳实践](#最佳实践)

## 🏗️ 架构概览

### 技术栈

- **主数据库**: MongoDB 7.0 - 文档型 NoSQL 数据库
- **缓存层**: Redis 7.2 - 内存数据库
- **应用框架**: Spring Boot 3.x + Spring Data MongoDB
- **容器化**: Docker + Docker Compose

### 架构优势

1. **高性能**: MongoDB 的文档存储适合 OpenRTB 的 JSON 数据结构
2. **高可用**: 支持副本集和分片集群
3. **灵活扩展**: 水平扩展能力强
4. **开发友好**: 与 Java 对象映射简单

## 🎯 数据库选择

### 为什么选择 MongoDB？

| 特性 | MongoDB | 其他 NoSQL |
|------|---------|------------|
| JSON 原生支持 | ✅ 完美匹配 OpenRTB | ❌ 需要序列化 |
| 查询能力 | ✅ 丰富的查询语法 | ⚠️ 有限 |
| 索引支持 | ✅ 复合索引、地理索引 | ⚠️ 基础索引 |
| 事务支持 | ✅ ACID 事务 | ❌ 最终一致性 |
| 生态成熟度 | ✅ 企业级 | ⚠️ 相对较新 |
| 运维工具 | ✅ 丰富的工具链 | ⚠️ 工具较少 |

### 为什么选择 Redis 作为缓存？

- **超高性能**: 内存存储，微秒级响应
- **丰富数据结构**: 支持 String、Hash、List、Set 等
- **过期策略**: 自动清理过期数据
- **持久化**: 支持 RDB 和 AOF

## 📊 数据模型设计

### 核心集合 (Collections)

#### 1. bid_requests - 竞价请求
```javascript
{
  "_id": "req_20241201_001",
  "timestamp": ISODate("2024-12-01T10:00:00Z"),
  "sourceIp": "192.168.1.100",
  "exchangeId": "exchange_001",
  "bidRequest": { /* OpenRTB BidRequest 对象 */ },
  "status": "PROCESSED",
  "processingTime": 45,
  "expiresAt": ISODate("2024-12-08T10:00:00Z")
}
```

#### 2. bid_responses - 竞价响应
```javascript
{
  "_id": "resp_20241201_001",
  "requestId": "req_20241201_001",
  "responseId": "resp_001",
  "timestamp": ISODate("2024-12-01T10:00:00Z"),
  "bidResponse": { /* OpenRTB BidResponse 对象 */ },
  "bidResults": {
    "totalBids": 3,
    "winningBidId": "bid_001",
    "winningPrice": 2.50
  }
}
```

#### 3. campaigns - 广告活动
```javascript
{
  "_id": "camp_001",
  "advertiserId": "adv_001",
  "name": "春季促销活动",
  "status": "ACTIVE",
  "budget": {
    "daily": 10000.0,
    "total": 100000.0,
    "spent": 2500.0
  },
  "targeting": {
    "geo": ["CN", "US"],
    "devices": ["mobile", "desktop"],
    "audiences": ["tech_enthusiasts"]
  }
}
```

#### 4. user_profiles - 用户画像
```javascript
{
  "_id": "user_12345",
  "demographics": {
    "age": 28,
    "gender": "M",
    "country": "CN",
    "city": "Beijing"
  },
  "interests": ["technology", "gaming", "travel"],
  "behavior": {
    "pageViews": 150,
    "purchases": 5,
    "adClicks": 12
  }
}
```

#### 5. inventory - 广告位库存
```javascript
{
  "_id": "slot_001",
  "publisherId": "pub_001",
  "siteId": "site_001",
  "name": "首页横幅",
  "status": "ACTIVE",
  "specs": {
    "adTypes": ["banner", "video"],
    "sizes": [[728, 90], [320, 50]]
  },
  "pricing": {
    "floorPrice": 1.0,
    "currency": "USD",
    "pricingModel": "CPM"
  }
}
```

#### 6. bid_statistics - 竞价统计
```javascript
{
  "_id": "stat_20241201_10",
  "date": "2024-12-01",
  "hour": 10,
  "campaignId": "camp_001",
  "publisherId": "pub_001",
  "bidStats": {
    "requests": 1000,
    "bids": 800,
    "wins": 120,
    "fillRate": 0.8,
    "winRate": 0.15
  },
  "revenue": {
    "totalSpend": 300.0,
    "avgCpm": 2.5,
    "currency": "USD"
  }
}
```

### 索引设计

每个集合都设计了优化的复合索引：

```javascript
// bid_requests 索引
db.bid_requests.createIndex({ "timestamp": -1, "status": 1 })
db.bid_requests.createIndex({ "exchangeId": 1, "timestamp": -1 })
db.bid_requests.createIndex({ "expiresAt": 1 }, { expireAfterSeconds: 0 })

// campaigns 索引
db.campaigns.createIndex({ "status": 1, "targeting.geo": 1 })
db.campaigns.createIndex({ "advertiserId": 1, "status": 1 })

// user_profiles 索引
db.user_profiles.createIndex({ "demographics.country": 1, "demographics.age": 1 })
db.user_profiles.createIndex({ "interests": 1 })

// 地理位置索引
db.user_profiles.createIndex({ "location": "2dsphere" })
```

## 🚀 快速开始

### 1. 环境准备

```bash
# 克隆项目
git clone <repository-url>
cd play-spring-boot-api

# 启动数据库服务
docker-compose -f docker-compose-openrtb.yml up -d

# 等待服务启动完成
docker-compose -f docker-compose-openrtb.yml logs -f
```

### 2. 验证服务状态

```bash
# 检查 MongoDB
docker exec -it openrtb-mongodb mongosh --eval "db.adminCommand('ping')"

# 检查 Redis
docker exec -it openrtb-redis redis-cli ping
```

### 3. 启动应用

```bash
# 使用 OpenRTB 配置启动
./mvnw spring-boot:run -Dspring-boot.run.profiles=openrtb

# 或者
java -jar target/play-spring-boot-api.jar --spring.profiles.active=openrtb
```

### 4. 访问管理界面

- **MongoDB 管理**: http://localhost:8081 (admin/admin123)
- **Redis 管理**: http://localhost:8082 (admin/admin123)
- **应用健康检查**: http://localhost:8080/actuator/health
- **Swagger API**: http://localhost:8080/swagger-ui.html

## ⚙️ 配置说明

### MongoDB 连接配置

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:password123@localhost:27017/openrtb?authSource=admin
      database: openrtb
      auto-index-creation: true
```

### Redis 缓存配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: redis123
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
```

### OpenRTB 业务配置

```yaml
openrtb:
  bid:
    timeout: 100          # 竞价超时 100ms
  data-retention:
    bid-requests: 7d      # 数据保留策略
  cache:
    campaigns:
      ttl: 300s           # 缓存过期时间
```

## 🔧 性能优化

### 1. 数据库优化

#### MongoDB 优化
```javascript
// 启用分片
sh.enableSharding("openrtb")
sh.shardCollection("openrtb.bid_requests", { "timestamp": 1 })
sh.shardCollection("openrtb.bid_statistics", { "date": 1, "hour": 1 })

// 设置读偏好
db.getMongo().setReadPref("secondaryPreferred")
```

#### 索引优化
```javascript
// 部分索引 - 只为活跃活动创建索引
db.campaigns.createIndex(
  { "status": 1, "targeting.geo": 1 },
  { partialFilterExpression: { "status": "ACTIVE" } }
)

// 稀疏索引 - 跳过 null 值
db.user_profiles.createIndex(
  { "behavior.lastPurchase": -1 },
  { sparse: true }
)
```

### 2. 缓存策略

#### 多级缓存
```java
// L1: 应用内缓存 (Caffeine)
@Cacheable(value = "campaigns", key = "#advertiserId")
public List<Campaign> getActiveCampaigns(String advertiserId) {
    // L2: Redis 缓存
    // L3: MongoDB 查询
}
```

#### 缓存预热
```java
@EventListener(ApplicationReadyEvent.class)
public void warmupCache() {
    // 预加载热点数据
    campaignService.preloadActiveCampaigns();
    inventoryService.preloadHighTrafficSlots();
}
```

### 3. 连接池优化

```yaml
spring:
  data:
    mongodb:
      options:
        max-pool-size: 50
        min-pool-size: 10
        max-wait-time: 2000
        max-connection-idle-time: 30000
  redis:
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

## 📈 监控与运维

### 1. 健康检查

```bash
# 应用健康状态
curl http://localhost:8080/actuator/health

# MongoDB 状态
curl http://localhost:8080/actuator/health/mongo

# Redis 状态
curl http://localhost:8080/actuator/health/redis
```

### 2. 性能指标

```bash
# Prometheus 指标
curl http://localhost:8080/actuator/prometheus

# 自定义业务指标
curl http://localhost:8080/actuator/metrics/openrtb.bid.requests
curl http://localhost:8080/actuator/metrics/openrtb.bid.latency
```

### 3. 数据库监控

#### MongoDB 监控
```javascript
// 查看慢查询
db.setProfilingLevel(2, { slowms: 100 })
db.system.profile.find().sort({ ts: -1 }).limit(5)

// 查看索引使用情况
db.bid_requests.aggregate([
  { $indexStats: {} }
])
```

#### Redis 监控
```bash
# 内存使用情况
redis-cli info memory

# 慢查询日志
redis-cli slowlog get 10
```

### 4. 数据清理

```java
// 定时清理过期数据
@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
public void cleanupExpiredData() {
    bidRequestRepository.deleteExpiredRequests();
    bidResponseRepository.deleteExpiredResponses();
    userProfileRepository.deleteExpiredProfiles();
}
```

## 💡 最佳实践

### 1. 数据建模

- **嵌入 vs 引用**: 小文档嵌入，大文档引用
- **反范式化**: 适度冗余提高查询性能
- **版本控制**: 为数据结构变更预留版本字段

### 2. 查询优化

- **使用投影**: 只查询需要的字段
- **批量操作**: 使用 `bulkWrite` 提高写入性能
- **聚合管道**: 复杂统计使用聚合框架

### 3. 缓存策略

- **缓存穿透**: 缓存空结果防止穿透
- **缓存雪崩**: 设置随机过期时间
- **缓存更新**: 使用 Cache-Aside 模式

### 4. 安全考虑

- **访问控制**: 启用 MongoDB 认证
- **网络安全**: 使用 VPC 和防火墙
- **数据加密**: 敏感数据字段加密

### 5. 备份策略

```bash
# MongoDB 备份
mongodump --uri="mongodb://admin:password123@localhost:27017/openrtb" --out=/backup/

# Redis 备份
redis-cli --rdb /backup/dump.rdb
```

## 📚 相关文档

- [OpenRTB 2.5 规范](https://www.iab.com/wp-content/uploads/2016/03/OpenRTB-API-Specification-Version-2-5-FINAL.pdf)
- [MongoDB 官方文档](https://docs.mongodb.com/)
- [Redis 官方文档](https://redis.io/documentation)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 📄 许可证

MIT License