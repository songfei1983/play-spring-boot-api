# OpenRTB 数据持久化模块

## 📋 模块概述

本模块实现了完整的 OpenRTB（Open Real-Time Bidding）数据持久化解决方案，集成了 MongoDB 数据存储和 Redis 缓存，为实时竞价广告系统提供高性能的数据访问层。

## 🏗️ 架构设计

### 核心组件

- **实体层 (Entity)**: 定义了 MongoDB 文档结构
- **仓储层 (Repository)**: 提供数据访问接口
- **服务层 (Service)**: 实现业务逻辑和缓存管理
- **控制器层 (Controller)**: 提供 REST API 接口
- **配置层 (Config)**: MongoDB 和 Redis 配置

## 📊 数据模型

### 核心实体

1. **BidRequestEntity** - 竞价请求
   - 存储 OpenRTB 竞价请求数据
   - 支持 TTL 自动过期
   - 包含处理状态和时间戳

2. **BidResponseEntity** - 竞价响应
   - 存储竞价响应和结果
   - 记录获胜竞价信息
   - 支持自动过期清理

3. **CampaignEntity** - 广告活动
   - 广告活动配置和定向信息
   - 预算和出价策略
   - 活动状态管理

4. **UserProfileEntity** - 用户画像
   - 用户行为和偏好数据
   - 人口统计信息
   - 购买历史和价值评估

5. **InventoryEntity** - 广告位库存
   - 广告位规格和质量
   - 可用性和定价信息
   - 发布商信息

6. **BidStatisticsEntity** - 竞价统计
   - 按日期和小时聚合的统计数据
   - 竞价、收入、性能指标
   - 地理和设备维度统计

## 🚀 主要功能

### 数据持久化
- MongoDB 文档存储
- 复合索引优化查询性能
- TTL 索引自动清理过期数据
- 事务支持保证数据一致性

### 缓存管理
- Redis 缓存热点数据
- 多级缓存策略
- 缓存预热和清理
- 缓存穿透保护

### 业务逻辑
- 竞价匹配算法
- 统计数据聚合
- 用户画像分析
- 广告位质量评估

## 📡 API 接口

### 竞价请求管理
```http
POST   /api/openrtb/bid-requests              # 保存竞价请求
GET    /api/openrtb/bid-requests/{requestId}  # 获取竞价请求
GET    /api/openrtb/bid-requests/time-range   # 按时间范围查询
GET    /api/openrtb/bid-requests/timeout      # 获取超时请求
```

### 竞价响应管理
```http
POST   /api/openrtb/bid-responses                    # 保存竞价响应
GET    /api/openrtb/bid-responses/request/{requestId} # 按请求ID查询
GET    /api/openrtb/bid-responses/winning/{bidId}     # 获取获胜竞价
```

### 广告活动管理
```http
POST   /api/openrtb/campaigns              # 保存广告活动
GET    /api/openrtb/campaigns/{campaignId} # 获取广告活动
GET    /api/openrtb/campaigns/active       # 获取活跃活动
GET    /api/openrtb/campaigns/currently-active # 获取当前活跃活动
```

### 用户画像管理
```http
POST   /api/openrtb/user-profiles           # 保存用户画像
GET    /api/openrtb/user-profiles/{userId}  # 获取用户画像
GET    /api/openrtb/user-profiles/high-value # 获取高价值用户
```

### 广告位库存管理
```http
POST   /api/openrtb/inventory         # 保存广告位库存
GET    /api/openrtb/inventory/{slotId} # 获取库存信息
GET    /api/openrtb/inventory/active   # 获取活跃广告位
```

### 统计数据管理
```http
POST   /api/openrtb/statistics                    # 保存统计数据
GET    /api/openrtb/statistics/date/{date}        # 按日期查询
GET    /api/openrtb/statistics/range              # 按日期范围查询
GET    /api/openrtb/statistics/campaign/{campaignId} # 活动统计
GET    /api/openrtb/statistics/publisher/{publisherId} # 发布商统计
```

### 业务逻辑接口
```http
GET    /api/openrtb/matching/campaigns        # 竞价匹配
POST   /api/openrtb/statistics/update         # 更新统计
POST   /api/openrtb/cache/clear               # 清除缓存
POST   /api/openrtb/cache/warmup              # 预热缓存
```

### 分析统计接口
```http
GET    /api/openrtb/analytics/total-requests/{date}     # 总请求数
GET    /api/openrtb/analytics/total-revenue             # 总收入
GET    /api/openrtb/analytics/campaign-impressions/{campaignId} # 活动展示数
GET    /api/openrtb/analytics/publisher-clicks/{publisherId}    # 发布商点击数
```

## ⚙️ 配置说明

### MongoDB 配置
- 数据库名称: `openrtb_db`
- 连接池配置优化
- 读写分离支持
- 事务配置

### Redis 配置
- 缓存键命名规范
- TTL 策略配置
- 序列化配置
- 连接池优化

## 🔍 索引策略

### 复合索引
- 请求ID + 时间戳
- 状态 + 时间戳
- 交易所ID + 时间戳
- 活动ID + 日期
- 用户ID + 最后活跃时间

### TTL 索引
- 竞价请求: 24小时自动过期
- 竞价响应: 24小时自动过期
- 用户画像: 90天自动过期

## 📈 性能优化

### 查询优化
- 复合索引覆盖常用查询
- 分页查询支持
- 聚合管道优化
- 投影字段减少数据传输

### 缓存策略
- 热点数据缓存
- 查询结果缓存
- 缓存预热机制
- 缓存更新策略

### 数据清理
- TTL 自动过期
- 批量删除过期数据
- 定期统计数据归档

## 🛠️ 使用示例

### 保存竞价请求
```java
BidRequestEntity request = BidRequestEntity.builder()
    .requestId("req_001")
    .timestamp(LocalDateTime.now())
    .status("PENDING")
    .bidRequest(bidRequestData)
    .build();
    
BidRequestEntity saved = openRTBDataService.saveBidRequest(request);
```

### 查询活跃广告活动
```java
List<CampaignEntity> activeCampaigns = openRTBDataService.getActiveCampaigns();
```

### 竞价匹配
```java
List<CampaignEntity> matchingCampaigns = openRTBDataService.findMatchingCampaigns(
    "user123", "slot456", "US", "mobile"
);
```

### 更新统计数据
```java
openRTBDataService.updateBidStatistics(
    "campaign123", "publisher456", "slot789", 
    true, 1000L, "US", "mobile"
);
```

## 📝 注意事项

1. **数据一致性**: 使用事务确保关键操作的原子性
2. **性能监控**: 定期监控查询性能和缓存命中率
3. **数据清理**: 及时清理过期数据避免存储膨胀
4. **索引维护**: 根据查询模式调整索引策略
5. **缓存管理**: 合理设置缓存TTL和更新策略

## 🔧 扩展建议

1. **分片策略**: 大数据量时考虑MongoDB分片
2. **读写分离**: 配置MongoDB副本集实现读写分离
3. **缓存集群**: Redis集群部署提高可用性
4. **监控告警**: 集成监控系统实时监控性能
5. **数据归档**: 实现历史数据归档策略

---

**版本**: 1.0.0  
**更新时间**: 2024-12-01  
**维护者**: OpenRTB Team