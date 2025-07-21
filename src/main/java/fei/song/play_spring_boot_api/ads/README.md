# OpenRTB 广告竞价系统

这是一个基于 OpenRTB 2.5 规范实现的实时竞价广告系统，提供完整的竞价请求处理、反欺诈检测、预算管理和竞价算法功能。

## 功能特性

### 核心功能
- **OpenRTB 2.5 兼容**: 完全符合 OpenRTB 2.5 规范
- **实时竞价**: 高性能的实时竞价处理
- **反欺诈检测**: 多维度欺诈风险评估
- **预算管理**: 实时预算控制和预扣机制
- **智能竞价**: 基于多因子的竞价算法
- **广告位过滤**: 精准的广告位匹配和过滤

### 技术特性
- **高并发**: 支持大量并发竞价请求
- **低延迟**: 优化的处理流程，确保快速响应
- **可配置**: 丰富的配置选项，支持灵活定制
- **监控统计**: 完整的性能监控和统计信息
- **容错处理**: 完善的异常处理和降级机制

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   BidController │────│    BidServer    │────│  BiddingAlgorithm│
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                ┌───────────────┼───────────────┐
                │               │               │
        ┌───────▼──────┐ ┌──────▼──────┐ ┌─────▼──────┐
        │FraudDetection│ │AdSlotFilter │ │BudgetService│
        │   Service    │ │  Service    │ │            │
        └──────────────┘ └─────────────┘ └────────────┘
```

## 主要组件

### 1. 数据模型 (domain/model)
- **BidRequest**: 竞价请求对象
- **BidResponse**: 竞价响应对象
- **Impression**: 广告位信息
- **Banner/Video**: 广告格式信息
- **Device/User/Site**: 上下文信息
- **BidCandidate**: 内部竞价候选对象

### 2. 服务层 (service)
- **BidServer**: 核心竞价服务器
- **FraudDetectionService**: 反欺诈检测服务
- **AdSlotFilterService**: 广告位过滤服务
- **BiddingAlgorithm**: 竞价算法服务
- **BudgetService**: 预算管理服务

### 3. 控制器 (controller)
- **BidController**: REST API 控制器

### 4. 配置 (config)
- **AdsConfiguration**: 系统配置类

### 5. 定时任务 (scheduler)
- **BudgetCleanupScheduler**: 预算清理定时任务

## API 接口

### 竞价请求
```http
POST /api/v1/bid/request
Content-Type: application/json

{
  "id": "request-123",
  "imp": [
    {
      "id": "imp-1",
      "banner": {
        "w": 300,
        "h": 250
      },
      "bidfloor": 0.5
    }
  ],
  "site": {
    "id": "site-123",
    "domain": "example.com"
  },
  "device": {
    "ua": "Mozilla/5.0...",
    "ip": "192.168.1.1"
  }
}
```

### 获胜通知
```http
POST /api/v1/bid/win/{bidId}?winPrice=1.25
```

### 损失通知
```http
POST /api/v1/bid/loss/{bidId}?winPrice=1.50&lossReason=1
```

### 服务器状态
```http
GET /api/v1/bid/status
```

### 健康检查
```http
GET /api/v1/bid/health
```

## 配置说明

### 反欺诈配置
```yaml
ads:
  fraud-detection:
    enabled: true
    risk-threshold: 0.7
    max-clicks-per-hour: 100
    max-impressions-per-hour: 1000
```

### 竞价算法配置
```yaml
ads:
  bidding:
    default-currency: "USD"
    auction-type: 2
    min-bid-price: 0.01
    max-bid-price: 100.0
    weights:
      user-value: 0.3
      context-relevance: 0.25
      competition: 0.2
      quality: 0.25
```

### 预算管理配置
```yaml
ads:
  budget:
    enabled: true
    check-timeout-ms: 50
    reservation-ttl-seconds: 300
    default-daily-budget: 1000.0
```

## 使用示例

### 1. 启动应用
```bash
# 使用 ads 配置文件启动
java -jar app.jar --spring.profiles.active=ads
```

### 2. 发送竞价请求
```bash
curl -X POST http://localhost:8080/api/v1/bid/request \
  -H "Content-Type: application/json" \
  -d @bid-request.json
```

### 3. 查看服务器状态
```bash
curl http://localhost:8080/api/v1/bid/status
```

## 监控指标

系统提供以下监控指标：

- **请求统计**: 总请求数、成功竞价数
- **反欺诈统计**: 检查次数、拦截次数、风险分布
- **过滤统计**: 各类过滤器的执行情况
- **竞价统计**: 竞价生成、选择、价格分布
- **预算统计**: 预算检查、预扣、确认情况
- **性能统计**: 响应时间、并发数、错误率

## 扩展开发

### 添加新的过滤器
```java
@Component
public class CustomAdFilter {
    public boolean filter(BidRequest request, Impression impression) {
        // 自定义过滤逻辑
        return true;
    }
}
```

### 自定义竞价算法
```java
@Service
public class CustomBiddingAlgorithm {
    public double calculateBidPrice(BidRequest request, Impression impression) {
        // 自定义竞价逻辑
        return 1.0;
    }
}
```

### 添加新的反欺诈检测
```java
@Component
public class CustomFraudDetector {
    public double calculateRiskScore(BidRequest request) {
        // 自定义风险评估逻辑
        return 0.0;
    }
}
```

## 性能优化建议

1. **缓存优化**: 使用 Redis 缓存热点数据
2. **数据库优化**: 使用连接池和读写分离
3. **异步处理**: 对非关键路径使用异步处理
4. **资源池化**: 复用对象，减少 GC 压力
5. **监控告警**: 设置关键指标的监控告警

## 故障排查

### 常见问题

1. **竞价响应慢**
   - 检查数据库连接
   - 查看 GC 情况
   - 分析慢查询日志

2. **预算扣除异常**
   - 检查预算服务状态
   - 查看预算配置
   - 分析预算日志

3. **反欺诈误判**
   - 调整风险阈值
   - 检查黑白名单
   - 分析用户行为模式

### 日志分析
```bash
# 查看竞价请求日志
grep "收到竞价请求" application.log

# 查看反欺诈日志
grep "欺诈检测" application.log

# 查看预算相关日志
grep "预算" application.log
```

## 版本历史

- **v1.0.0**: 初始版本，实现基础 OpenRTB 功能
- 支持 OpenRTB 2.5 规范
- 实现反欺诈检测
- 实现预算管理
- 实现智能竞价算法