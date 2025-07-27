# Bid Request 数量展示功能设计文档

## 概述

本文档描述如何在OpenRTB系统中实现bid request数量的统计和展示功能，为用户提供实时的竞价请求监控能力。

## 功能需求

### 1. 核心功能
- 实时统计bid request数量
- 按时间维度展示统计数据（小时、天、周、月）
- 按广告位类型分类统计
- 按来源DSP分类统计
- 提供API接口供前端调用
- 支持数据导出功能

### 2. 性能要求
- 支持高并发bid request统计
- 统计数据延迟不超过1秒
- 历史数据查询响应时间不超过500ms

## 技术方案

### 1. 数据存储设计

#### MongoDB集合设计

```javascript
// bid_request_metrics 集合
{
  "_id": ObjectId,
  "timestamp": ISODate,           // 统计时间点
  "hour": String,                 // 小时维度 "2024-01-15T10"
  "date": String,                 // 日期维度 "2024-01-15"
  "adSlotType": String,           // 广告位类型
  "dspSource": String,            // DSP来源
  "requestCount": Number,         // 请求数量
  "successCount": Number,         // 成功响应数量
  "failureCount": Number,         // 失败数量
  "avgResponseTime": Number,      // 平均响应时间(ms)
  "createdAt": ISODate,
  "updatedAt": ISODate
}

// 索引设计
db.bid_request_metrics.createIndex({ "timestamp": 1, "adSlotType": 1, "dspSource": 1 })
db.bid_request_metrics.createIndex({ "date": 1 })
db.bid_request_metrics.createIndex({ "hour": 1 })
```

### 2. 后端实现

#### 2.1 实体类设计

```java
// BidRequestMetrics.java
@Document(collection = "bid_request_metrics")
public class BidRequestMetrics {
    @Id
    private String id;
    
    @Indexed
    private LocalDateTime timestamp;
    
    @Indexed
    private String hour;  // 格式: "2024-01-15T10"
    
    @Indexed
    private String date;  // 格式: "2024-01-15"
    
    private String adSlotType;
    private String dspSource;
    private Long requestCount;
    private Long successCount;
    private Long failureCount;
    private Double avgResponseTime;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // getters, setters, constructors
}
```

#### 2.2 Repository层

```java
// BidRequestMetricsRepository.java
@Repository
public interface BidRequestMetricsRepository extends MongoRepository<BidRequestMetrics, String> {
    
    // 按日期范围查询
    List<BidRequestMetrics> findByDateBetweenOrderByTimestampDesc(
        String startDate, String endDate);
    
    // 按小时范围查询
    List<BidRequestMetrics> findByHourBetweenOrderByTimestampDesc(
        String startHour, String endHour);
    
    // 按广告位类型统计
    @Aggregation(pipeline = {
        "{ '$match': { 'date': { '$gte': ?0, '$lte': ?1 } } }",
        "{ '$group': { '_id': '$adSlotType', 'totalRequests': { '$sum': '$requestCount' } } }"
    })
    List<AdSlotTypeStats> getStatsByAdSlotType(String startDate, String endDate);
    
    // 按DSP来源统计
    @Aggregation(pipeline = {
        "{ '$match': { 'date': { '$gte': ?0, '$lte': ?1 } } }",
        "{ '$group': { '_id': '$dspSource', 'totalRequests': { '$sum': '$requestCount' } } }"
    })
    List<DspSourceStats> getStatsByDspSource(String startDate, String endDate);
}
```

#### 2.3 服务层

```java
// BidRequestMetricsService.java
@Service
@Transactional
public class BidRequestMetricsService {
    
    @Autowired
    private BidRequestMetricsRepository metricsRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 实时统计计数器
    public void incrementBidRequestCount(String adSlotType, String dspSource) {
        String key = String.format("bid_request_count:%s:%s:%s", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH")),
            adSlotType, dspSource);
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofHours(25)); // 保留25小时
    }
    
    // 定时任务：每分钟将Redis数据同步到MongoDB
    @Scheduled(fixedRate = 60000) // 每分钟执行
    public void syncMetricsToMongoDB() {
        String pattern = "bid_request_count:*";
        Set<String> keys = redisTemplate.keys(pattern);
        
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length == 4) {
                String dateHour = parts[1] + ":" + parts[2];
                String adSlotType = parts[3];
                String dspSource = parts[4];
                Long count = (Long) redisTemplate.opsForValue().get(key);
                
                updateMetricsInMongoDB(dateHour, adSlotType, dspSource, count);
                redisTemplate.delete(key);
            }
        }
    }
    
    // 获取实时统计数据
    public BidRequestStatsDTO getRealTimeStats() {
        LocalDateTime now = LocalDateTime.now();
        String currentHour = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
        
        // 从Redis获取当前小时的实时数据
        String pattern = "bid_request_count:" + currentHour + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        
        long totalCount = 0;
        for (String key : keys) {
            Long count = (Long) redisTemplate.opsForValue().get(key);
            totalCount += count != null ? count : 0;
        }
        
        return BidRequestStatsDTO.builder()
            .currentHourRequests(totalCount)
            .timestamp(now)
            .build();
    }
    
    // 获取历史统计数据
    public List<BidRequestMetrics> getHistoricalStats(
            String startDate, String endDate, String granularity) {
        
        if ("hour".equals(granularity)) {
            return metricsRepository.findByHourBetweenOrderByTimestampDesc(
                startDate, endDate);
        } else {
            return metricsRepository.findByDateBetweenOrderByTimestampDesc(
                startDate, endDate);
        }
    }
}
```

#### 2.4 控制器层

```java
// BidRequestMetricsController.java
@RestController
@RequestMapping("/api/v1/metrics/bid-requests")
@CrossOrigin(origins = "*")
public class BidRequestMetricsController {
    
    @Autowired
    private BidRequestMetricsService metricsService;
    
    // 获取实时统计
    @GetMapping("/realtime")
    public ResponseEntity<BidRequestStatsDTO> getRealTimeStats() {
        BidRequestStatsDTO stats = metricsService.getRealTimeStats();
        return ResponseEntity.ok(stats);
    }
    
    // 获取历史统计
    @GetMapping("/historical")
    public ResponseEntity<List<BidRequestMetrics>> getHistoricalStats(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "day") String granularity) {
        
        List<BidRequestMetrics> stats = metricsService.getHistoricalStats(
            startDate, endDate, granularity);
        return ResponseEntity.ok(stats);
    }
    
    // 按广告位类型统计
    @GetMapping("/by-adslot")
    public ResponseEntity<List<AdSlotTypeStats>> getStatsByAdSlotType(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        List<AdSlotTypeStats> stats = metricsService.getStatsByAdSlotType(
            startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    // 按DSP来源统计
    @GetMapping("/by-dsp")
    public ResponseEntity<List<DspSourceStats>> getStatsByDspSource(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        List<DspSourceStats> stats = metricsService.getStatsByDspSource(
            startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    // 导出数据
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStats(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "csv") String format) {
        
        byte[] data = metricsService.exportStats(startDate, endDate, format);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", 
            "bid_request_stats." + format);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(data);
    }
}
```

### 3. 前端实现

#### 3.1 React组件设计

```typescript
// BidRequestMetrics.tsx
import React, { useState, useEffect } from 'react';
import { Line, Bar, Pie } from 'react-chartjs-2';
import { Card, DatePicker, Select, Button, Statistic, Row, Col } from 'antd';

interface BidRequestMetricsProps {}

const BidRequestMetrics: React.FC<BidRequestMetricsProps> = () => {
  const [realTimeStats, setRealTimeStats] = useState<any>(null);
  const [historicalData, setHistoricalData] = useState<any[]>([]);
  const [dateRange, setDateRange] = useState<[string, string]>(['', '']);
  const [granularity, setGranularity] = useState<string>('day');
  
  // 获取实时数据
  useEffect(() => {
    const fetchRealTimeStats = async () => {
      try {
        const response = await fetch('/api/v1/metrics/bid-requests/realtime');
        const data = await response.json();
        setRealTimeStats(data);
      } catch (error) {
        console.error('获取实时统计失败:', error);
      }
    };
    
    fetchRealTimeStats();
    const interval = setInterval(fetchRealTimeStats, 5000); // 每5秒更新
    
    return () => clearInterval(interval);
  }, []);
  
  // 获取历史数据
  const fetchHistoricalData = async () => {
    if (!dateRange[0] || !dateRange[1]) return;
    
    try {
      const response = await fetch(
        `/api/v1/metrics/bid-requests/historical?startDate=${dateRange[0]}&endDate=${dateRange[1]}&granularity=${granularity}`
      );
      const data = await response.json();
      setHistoricalData(data);
    } catch (error) {
      console.error('获取历史数据失败:', error);
    }
  };
  
  return (
    <div className="bid-request-metrics">
      <Row gutter={[16, 16]}>
        {/* 实时统计卡片 */}
        <Col span={6}>
          <Card>
            <Statistic
              title="当前小时请求数"
              value={realTimeStats?.currentHourRequests || 0}
              suffix="次"
            />
          </Card>
        </Col>
        
        {/* 控制面板 */}
        <Col span={24}>
          <Card title="历史数据查询">
            <Row gutter={[16, 16]}>
              <Col span={8}>
                <DatePicker.RangePicker
                  onChange={(dates, dateStrings) => setDateRange(dateStrings)}
                />
              </Col>
              <Col span={4}>
                <Select
                  value={granularity}
                  onChange={setGranularity}
                  style={{ width: '100%' }}
                >
                  <Select.Option value="hour">按小时</Select.Option>
                  <Select.Option value="day">按天</Select.Option>
                </Select>
              </Col>
              <Col span={4}>
                <Button type="primary" onClick={fetchHistoricalData}>
                  查询
                </Button>
              </Col>
            </Row>
          </Card>
        </Col>
        
        {/* 趋势图表 */}
        <Col span={24}>
          <Card title="Bid Request 趋势">
            <Line
              data={{
                labels: historicalData.map(item => item.timestamp),
                datasets: [{
                  label: 'Bid Requests',
                  data: historicalData.map(item => item.requestCount),
                  borderColor: 'rgb(75, 192, 192)',
                  tension: 0.1
                }]
              }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default BidRequestMetrics;
```

### 4. 集成到现有系统

#### 4.1 在BidController中添加统计

```java
// 在现有的BidController中添加统计逻辑
@RestController
@RequestMapping("/api/v1/bid")
public class BidController {
    
    @Autowired
    private BidRequestMetricsService metricsService;
    
    @PostMapping("/request")
    public ResponseEntity<BidResponse> handleBidRequest(
            @RequestBody BidRequest bidRequest) {
        
        // 统计bid request
        metricsService.incrementBidRequestCount(
            bidRequest.getImp().get(0).getBanner() != null ? "banner" : "video",
            bidRequest.getApp() != null ? bidRequest.getApp().getId() : "unknown"
        );
        
        // 原有的竞价逻辑...
        BidResponse response = bidService.processBidRequest(bidRequest);
        
        return ResponseEntity.ok(response);
    }
}
```

## 部署配置

### 1. Redis配置

```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

### 2. MongoDB索引创建

```javascript
// 在MongoDB中创建必要的索引
use openrtb_db;

db.bid_request_metrics.createIndex({ "timestamp": 1, "adSlotType": 1, "dspSource": 1 });
db.bid_request_metrics.createIndex({ "date": 1 });
db.bid_request_metrics.createIndex({ "hour": 1 });
db.bid_request_metrics.createIndex({ "createdAt": 1 }, { expireAfterSeconds: 2592000 }); // 30天过期
```

## 监控和告警

### 1. 性能监控
- 监控Redis内存使用情况
- 监控MongoDB查询性能
- 监控API响应时间

### 2. 业务告警
- Bid request数量异常波动告警
- 成功率低于阈值告警
- 响应时间超过阈值告警

## 扩展功能

### 1. 高级分析
- 地域分布统计
- 设备类型分析
- 时段热力图
- 转化率分析

### 2. 实时大屏
- WebSocket实时推送
- 大屏展示组件
- 多维度实时监控

## 总结

本方案通过Redis + MongoDB的组合，实现了高性能的bid request统计功能，支持实时监控和历史数据分析。前端提供了直观的图表展示，满足了用户对bid request数量监控的需求。

系统具有以下特点：
- **高性能**: Redis缓存实时数据，MongoDB存储历史数据
- **可扩展**: 支持多维度统计和分析
- **实时性**: 秒级数据更新
- **易用性**: 直观的前端界面和丰富的图表展示