# OpenRTB 数据持久化设计方案

## 1. 数据库选择

### 推荐数据库：MongoDB

**选择理由：**
- **灵活的文档模型**：OpenRTB 数据结构复杂且嵌套深，MongoDB 的文档模型天然适合存储 JSON 格式的 OpenRTB 数据
- **高性能读写**：支持高并发的竞价请求处理，读写性能优异
- **水平扩展**：支持分片，可以处理大规模数据和高并发访问
- **索引优化**：丰富的索引类型支持复杂查询场景
- **TTL 支持**：自动过期功能适合处理时效性数据
- **聚合框架**：强大的聚合功能支持实时统计和分析

### 辅助数据库：Redis

**用途：**
- 用户画像缓存
- 频次控制
- 实时预算管理
- 竞价结果缓存

## 2. 数据表设计

### 2.1 竞价请求集合 (bid_requests)

```javascript
// 集合：bid_requests
{
  _id: ObjectId,
  request_id: "80ce30c53c16e6ede735f123ef6e32361bfc7b22", // 竞价请求ID
  timestamp: ISODate("2024-01-15T10:30:00Z"), // 请求时间
  source_ip: "192.168.1.100", // 来源IP
  exchange_id: "adx_001", // 交易平台ID
  
  // OpenRTB 请求数据
  bid_request: {
    id: "80ce30c53c16e6ede735f123ef6e32361bfc7b22",
    imp: [{
      id: "1",
      banner: {
        w: 300,
        h: 250,
        pos: 1
      },
      bidfloor: 0.03,
      bidfloorcur: "USD",
      secure: 1
    }],
    site: {
      id: "site123",
      name: "example.com",
      domain: "example.com",
      cat: ["IAB1"],
      page: "http://example.com/page1",
      publisher: {
        id: "pub123",
        name: "Publisher Name"
      }
    },
    device: {
      ua: "Mozilla/5.0...",
      geo: {
        lat: 40.7128,
        lon: -74.0060,
        country: "USA",
        region: "NY",
        city: "New York"
      },
      ip: "192.168.1.100",
      devicetype: 1,
      make: "Apple",
      model: "iPhone",
      os: "iOS",
      osv: "14.0"
    },
    user: {
      id: "user123",
      buyeruid: "buyer456",
      yob: 1990,
      gender: "M",
      keywords: "sports,technology"
    },
    at: 2, // 拍卖类型
    tmax: 100, // 超时时间
    cur: ["USD"]
  },
  
  // 处理状态
  status: "processed", // pending, processed, failed
  processing_time_ms: 45, // 处理时间
  
  // TTL 索引，30天后自动删除
  expires_at: ISODate("2024-02-14T10:30:00Z")
}
```

**索引设计：**
```javascript
// 主要索引
db.bid_requests.createIndex({ "request_id": 1 }, { unique: true })
db.bid_requests.createIndex({ "timestamp": -1 })
db.bid_requests.createIndex({ "exchange_id": 1, "timestamp": -1 })
db.bid_requests.createIndex({ "bid_request.user.id": 1 })
db.bid_requests.createIndex({ "bid_request.device.geo.country": 1 })
db.bid_requests.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 })

// 复合索引
db.bid_requests.createIndex({ 
  "bid_request.site.domain": 1, 
  "timestamp": -1 
})
db.bid_requests.createIndex({ 
  "bid_request.device.devicetype": 1, 
  "bid_request.device.os": 1 
})
```

### 2.2 竞价响应集合 (bid_responses)

```javascript
// 集合：bid_responses
{
  _id: ObjectId,
  request_id: "80ce30c53c16e6ede735f123ef6e32361bfc7b22", // 关联的请求ID
  response_id: "resp_001", // 响应ID
  timestamp: ISODate("2024-01-15T10:30:00Z"),
  
  // OpenRTB 响应数据
  bid_response: {
    id: "80ce30c53c16e6ede735f123ef6e32361bfc7b22",
    seatbid: [{
      bid: [{
        id: "bid_001",
        impid: "1",
        price: 0.089,
        adid: "314",
        nurl: "http://adserver.com/winnotice?impid=102&price=${AUCTION_PRICE}",
        adm: "<a href=\"...\"><img src=\"...\" width=\"300\" height=\"250\"/></a>",
        adomain: ["advertiser.com"],
        cid: "campaign_123",
        crid: "creative_456",
        w: 300,
        h: 250
      }],
      seat: "seat_001"
    }],
    cur: "USD"
  },
  
  // 竞价结果
  bid_result: {
    total_bids: 1,
    highest_bid: 0.089,
    winning_bid_id: "bid_001",
    auction_type: 2
  },
  
  // 性能指标
  metrics: {
    processing_time_ms: 45,
    candidates_evaluated: 150,
    filters_applied: ["geo", "device", "budget"]
  },
  
  // TTL 索引
  expires_at: ISODate("2024-02-14T10:30:00Z")
}
```

**索引设计：**
```javascript
db.bid_responses.createIndex({ "request_id": 1 }, { unique: true })
db.bid_responses.createIndex({ "timestamp": -1 })
db.bid_responses.createIndex({ "bid_response.seatbid.bid.cid": 1 })
db.bid_responses.createIndex({ "bid_result.winning_bid_id": 1 })
db.bid_responses.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 })
```

### 2.3 广告活动集合 (campaigns)

```javascript
// 集合：campaigns
{
  _id: ObjectId,
  campaign_id: "campaign_123",
  advertiser_id: "adv_001",
  name: "春季促销活动",
  status: "active", // active, paused, completed
  
  // 预算信息
  budget: {
    total_budget: 10000.00,
    daily_budget: 500.00,
    spent_total: 2500.00,
    spent_today: 150.00,
    currency: "USD"
  },
  
  // 定向设置
  targeting: {
    geo: {
      included_countries: ["US", "CA"],
      excluded_countries: [],
      included_regions: ["NY", "CA"],
      included_cities: ["New York", "Los Angeles"]
    },
    device: {
      device_types: [1, 4, 5], // 移动设备
      operating_systems: ["iOS", "Android"],
      browsers: ["Chrome", "Safari"]
    },
    audience: {
      age_range: { min: 18, max: 65 },
      genders: ["M", "F"],
      interests: ["sports", "technology", "travel"]
    },
    time: {
      days_of_week: [1, 2, 3, 4, 5], // 工作日
      hours_of_day: [9, 10, 11, 12, 13, 14, 15, 16, 17, 18]
    }
  },
  
  // 竞价设置
  bidding: {
    bid_strategy: "cpm", // cpm, cpc, cpa
    max_bid: 2.00,
    base_bid: 0.50,
    bid_adjustments: {
      mobile: 1.2,
      weekend: 0.8,
      premium_inventory: 1.5
    }
  },
  
  // 创意信息
  creatives: [{
    creative_id: "creative_456",
    format: "banner",
    width: 300,
    height: 250,
    html: "<a href=\"...\"><img src=\"...\" /></a>",
    click_url: "http://advertiser.com/landing",
    impression_trackers: ["http://tracker.com/imp"],
    click_trackers: ["http://tracker.com/click"]
  }],
  
  // 频次控制
  frequency_cap: {
    impressions_per_user_per_day: 3,
    impressions_per_user_per_hour: 1
  },
  
  // 时间设置
  schedule: {
    start_date: ISODate("2024-01-01T00:00:00Z"),
    end_date: ISODate("2024-03-31T23:59:59Z"),
    timezone: "America/New_York"
  },
  
  // 元数据
  created_at: ISODate("2024-01-01T00:00:00Z"),
  updated_at: ISODate("2024-01-15T10:30:00Z"),
  created_by: "user_001"
}
```

**索引设计：**
```javascript
db.campaigns.createIndex({ "campaign_id": 1 }, { unique: true })
db.campaigns.createIndex({ "advertiser_id": 1 })
db.campaigns.createIndex({ "status": 1 })
db.campaigns.createIndex({ "schedule.start_date": 1, "schedule.end_date": 1 })
db.campaigns.createIndex({ "targeting.geo.included_countries": 1 })
db.campaigns.createIndex({ "targeting.device.device_types": 1 })
```

### 2.4 用户画像集合 (user_profiles)

```javascript
// 集合：user_profiles
{
  _id: ObjectId,
  user_id: "user_123",
  
  // 基本信息
  demographics: {
    age: 30,
    gender: "M",
    income_level: "middle",
    education: "college",
    marital_status: "single"
  },
  
  // 兴趣标签
  interests: [
    { category: "sports", score: 0.8, last_updated: ISODate() },
    { category: "technology", score: 0.9, last_updated: ISODate() },
    { category: "travel", score: 0.6, last_updated: ISODate() }
  ],
  
  // 行为数据
  behavior: {
    page_views: [
      { domain: "example.com", category: "sports", timestamp: ISODate() },
      { domain: "tech.com", category: "technology", timestamp: ISODate() }
    ],
    purchase_history: [
      { category: "electronics", amount: 299.99, timestamp: ISODate() }
    ],
    app_usage: [
      { app_id: "com.example.sports", usage_time: 3600, timestamp: ISODate() }
    ]
  },
  
  // 设备信息
  devices: [{
    device_id: "device_001",
    device_type: "mobile",
    os: "iOS",
    last_seen: ISODate()
  }],
  
  // 地理位置
  locations: [{
    country: "US",
    region: "NY",
    city: "New York",
    lat: 40.7128,
    lon: -74.0060,
    timestamp: ISODate()
  }],
  
  // 频次控制
  frequency_data: {
    daily_impressions: {
      "2024-01-15": 5,
      "2024-01-14": 3
    },
    campaign_impressions: {
      "campaign_123": 2,
      "campaign_456": 1
    }
  },
  
  // 元数据
  created_at: ISODate("2024-01-01T00:00:00Z"),
  updated_at: ISODate("2024-01-15T10:30:00Z"),
  
  // TTL 索引，90天后删除不活跃用户
  expires_at: ISODate("2024-04-15T10:30:00Z")
}
```

**索引设计：**
```javascript
db.user_profiles.createIndex({ "user_id": 1 }, { unique: true })
db.user_profiles.createIndex({ "demographics.age": 1, "demographics.gender": 1 })
db.user_profiles.createIndex({ "interests.category": 1, "interests.score": -1 })
db.user_profiles.createIndex({ "devices.device_id": 1 })
db.user_profiles.createIndex({ "locations.country": 1, "locations.city": 1 })
db.user_profiles.createIndex({ "expires_at": 1 }, { expireAfterSeconds: 0 })
```

### 2.5 广告位库存集合 (inventory)

```javascript
// 集合：inventory
{
  _id: ObjectId,
  placement_id: "placement_001",
  publisher_id: "pub_123",
  site_id: "site_456",
  
  // 广告位信息
  placement_info: {
    name: "首页横幅",
    description: "网站首页顶部横幅广告位",
    ad_formats: ["banner", "video"],
    sizes: [
      { width: 728, height: 90 },
      { width: 300, height: 250 }
    ],
    position: "above_fold", // above_fold, below_fold
    visibility: "high" // high, medium, low
  },
  
  // 定价信息
  pricing: {
    floor_price: 0.50,
    currency: "USD",
    pricing_model: "cpm", // cpm, cpc, cpa
    private_deals: [{
      deal_id: "deal_001",
      buyer_id: "buyer_123",
      price: 1.20
    }]
  },
  
  // 流量信息
  traffic: {
    daily_impressions: 100000,
    peak_hours: [9, 10, 11, 14, 15, 16, 20, 21],
    audience_demographics: {
      age_distribution: { "18-24": 0.2, "25-34": 0.3, "35-44": 0.25 },
      gender_distribution: { "M": 0.6, "F": 0.4 },
      geo_distribution: { "US": 0.7, "CA": 0.2, "UK": 0.1 }
    }
  },
  
  // 内容分类
  content: {
    categories: ["IAB1", "IAB12"], // IAB 分类
    keywords: ["sports", "news", "entertainment"],
    language: "en",
    content_rating: "general" // general, mature
  },
  
  // 技术要求
  technical: {
    supported_apis: ["MRAID", "VPAID"],
    ssl_required: true,
    javascript_enabled: true,
    cookie_support: true
  },
  
  // 状态信息
  status: "active", // active, inactive, pending
  created_at: ISODate("2024-01-01T00:00:00Z"),
  updated_at: ISODate("2024-01-15T10:30:00Z")
}
```

**索引设计：**
```javascript
db.inventory.createIndex({ "placement_id": 1 }, { unique: true })
db.inventory.createIndex({ "publisher_id": 1 })
db.inventory.createIndex({ "site_id": 1 })
db.inventory.createIndex({ "placement_info.ad_formats": 1 })
db.inventory.createIndex({ "content.categories": 1 })
db.inventory.createIndex({ "pricing.floor_price": 1 })
db.inventory.createIndex({ "status": 1 })
```

### 2.6 竞价统计集合 (bid_statistics)

```javascript
// 集合：bid_statistics (按小时聚合)
{
  _id: ObjectId,
  date: ISODate("2024-01-15T10:00:00Z"), // 小时级别
  
  // 维度信息
  dimensions: {
    campaign_id: "campaign_123",
    placement_id: "placement_001",
    country: "US",
    device_type: "mobile",
    os: "iOS"
  },
  
  // 统计指标
  metrics: {
    bid_requests: 1000,
    bid_responses: 800,
    impressions: 150,
    clicks: 12,
    conversions: 2,
    
    spend: 75.50,
    revenue: 90.00,
    
    avg_bid: 0.52,
    max_bid: 1.20,
    win_rate: 0.15, // 竞价胜率
    
    cpm: 0.50,
    cpc: 6.29,
    cpa: 37.75,
    ctr: 0.08, // 点击率
    cvr: 0.167 // 转化率
  },
  
  created_at: ISODate("2024-01-15T11:00:00Z")
}
```

**索引设计：**
```javascript
db.bid_statistics.createIndex({ "date": -1 })
db.bid_statistics.createIndex({ "dimensions.campaign_id": 1, "date": -1 })
db.bid_statistics.createIndex({ "dimensions.country": 1, "date": -1 })
db.bid_statistics.createIndex({ "dimensions.device_type": 1, "date": -1 })
```

## 3. Redis 缓存设计

### 3.1 用户画像缓存
```
# Key 格式：user_profile:{user_id}
# TTL：1小时
user_profile:user_123 -> JSON 格式的用户画像数据
```

### 3.2 频次控制
```
# Key 格式：freq_cap:{user_id}:{campaign_id}:{date}
# TTL：24小时
freq_cap:user_123:campaign_456:2024-01-15 -> 当日展示次数
```

### 3.3 预算控制
```
# Key 格式：budget:{campaign_id}:{date}
# TTL：24小时
budget:campaign_123:2024-01-15 -> 当日已花费金额
```

### 3.4 竞价结果缓存
```
# Key 格式：bid_result:{request_id}
# TTL：5分钟
bid_result:req_123 -> 竞价结果数据
```

## 4. 分片策略

### 4.1 bid_requests 分片
```javascript
// 按时间分片
sh.shardCollection("openrtb.bid_requests", { "timestamp": 1 })
```

### 4.2 user_profiles 分片
```javascript
// 按用户ID哈希分片
sh.shardCollection("openrtb.user_profiles", { "user_id": "hashed" })
```

### 4.3 campaigns 分片
```javascript
// 按广告主ID分片
sh.shardCollection("openrtb.campaigns", { "advertiser_id": 1 })
```

## 5. 数据生命周期管理

### 5.1 TTL 设置
- **bid_requests**: 30天自动删除
- **bid_responses**: 30天自动删除
- **user_profiles**: 90天自动删除不活跃用户
- **bid_statistics**: 按业务需求保留1-2年

### 5.2 归档策略
- 历史竞价数据定期归档到冷存储
- 统计数据按月/年聚合后归档
- 用户画像数据定期清理和更新

## 6. 性能优化建议

### 6.1 读写分离
- 使用 MongoDB 副本集实现读写分离
- 竞价请求写入主节点
- 统计查询从从节点读取

### 6.2 连接池优化
- 合理配置连接池大小
- 使用连接复用减少连接开销

### 6.3 批量操作
- 批量插入竞价数据
- 批量更新统计信息

### 6.4 缓存策略
- 热点数据缓存到 Redis
- 使用本地缓存减少网络开销

## 7. 监控指标

### 7.1 数据库性能
- QPS (每秒查询数)
- 响应时间
- 连接数
- 内存使用率

### 7.2 业务指标
- 竞价请求量
- 竞价成功率
- 数据写入延迟
- 缓存命中率

## 8. 备份与恢复

### 8.1 备份策略
- 每日全量备份
- 实时增量备份
- 跨地域备份

### 8.2 恢复测试
- 定期进行恢复测试
- 制定灾难恢复预案