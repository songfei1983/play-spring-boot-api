# Bid Request 数量展示 - 快速实现示例

## 最小化实现方案

如果需要快速实现bid request数量展示功能，可以采用以下简化方案：

### 1. 简单计数器实现

#### 后端实现

```java
// BidRequestCounter.java - 简单的内存计数器
@Component
public class BidRequestCounter {
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong todayRequests = new AtomicLong(0);
    private final AtomicLong currentHourRequests = new AtomicLong(0);
    
    private String currentDate = LocalDate.now().toString();
    private String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
    
    public void increment() {
        String now = LocalDate.now().toString();
        String nowHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
        
        // 检查是否需要重置日计数器
        if (!currentDate.equals(now)) {
            todayRequests.set(0);
            currentDate = now;
        }
        
        // 检查是否需要重置小时计数器
        if (!currentHour.equals(nowHour)) {
            currentHourRequests.set(0);
            currentHour = nowHour;
        }
        
        totalRequests.incrementAndGet();
        todayRequests.incrementAndGet();
        currentHourRequests.incrementAndGet();
    }
    
    public BidRequestStats getStats() {
        return BidRequestStats.builder()
            .total(totalRequests.get())
            .today(todayRequests.get())
            .currentHour(currentHourRequests.get())
            .timestamp(LocalDateTime.now())
            .build();
    }
}

// BidRequestStats.java - 统计数据DTO
@Data
@Builder
public class BidRequestStats {
    private long total;
    private long today;
    private long currentHour;
    private LocalDateTime timestamp;
}
```

#### 控制器实现

```java
// 在现有的BidController中添加
@RestController
@RequestMapping("/api/v1/bid")
public class BidController {
    
    @Autowired
    private BidRequestCounter bidRequestCounter;
    
    @PostMapping("/request")
    public ResponseEntity<BidResponse> handleBidRequest(@RequestBody BidRequest bidRequest) {
        // 计数
        bidRequestCounter.increment();
        
        // 原有逻辑...
        BidResponse response = bidService.processBidRequest(bidRequest);
        return ResponseEntity.ok(response);
    }
    
    // 新增统计接口
    @GetMapping("/stats")
    public ResponseEntity<BidRequestStats> getBidRequestStats() {
        return ResponseEntity.ok(bidRequestCounter.getStats());
    }
}
```

### 2. 前端简单展示

#### React组件

```typescript
// BidRequestStatsWidget.tsx
import React, { useState, useEffect } from 'react';
import { Card, Statistic, Row, Col } from 'antd';

interface BidRequestStats {
  total: number;
  today: number;
  currentHour: number;
  timestamp: string;
}

const BidRequestStatsWidget: React.FC = () => {
  const [stats, setStats] = useState<BidRequestStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await fetch('/api/v1/bid/stats');
        const data = await response.json();
        setStats(data);
      } catch (error) {
        console.error('获取统计数据失败:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
    const interval = setInterval(fetchStats, 5000); // 每5秒更新

    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return <div>加载中...</div>;
  }

  return (
    <Card title="Bid Request 统计" style={{ marginBottom: 16 }}>
      <Row gutter={16}>
        <Col span={8}>
          <Statistic
            title="总请求数"
            value={stats?.total || 0}
            suffix="次"
          />
        </Col>
        <Col span={8}>
          <Statistic
            title="今日请求数"
            value={stats?.today || 0}
            suffix="次"
          />
        </Col>
        <Col span={8}>
          <Statistic
            title="当前小时"
            value={stats?.currentHour || 0}
            suffix="次"
          />
        </Col>
      </Row>
      <div style={{ marginTop: 16, fontSize: 12, color: '#666' }}>
        最后更新: {stats?.timestamp ? new Date(stats.timestamp).toLocaleString() : '-'}
      </div>
    </Card>
  );
};

export default BidRequestStatsWidget;
```

#### 集成到主页面

```typescript
// App.tsx 或相关页面组件
import BidRequestStatsWidget from './components/BidRequestStatsWidget';

function App() {
  return (
    <div className="App">
      {/* 其他组件 */}
      <BidRequestStatsWidget />
      {/* 其他组件 */}
    </div>
  );
}
```

### 3. 使用Redis的改进版本

如果需要更好的性能和持久化，可以使用Redis：

```java
// RedisBasedBidRequestCounter.java
@Component
public class RedisBasedBidRequestCounter {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final String TOTAL_KEY = "bid_request:total";
    private static final String TODAY_KEY_PREFIX = "bid_request:day:";
    private static final String HOUR_KEY_PREFIX = "bid_request:hour:";
    
    public void increment() {
        String today = LocalDate.now().toString();
        String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
        
        // 总计数
        redisTemplate.opsForValue().increment(TOTAL_KEY);
        
        // 日计数（24小时过期）
        String todayKey = TODAY_KEY_PREFIX + today;
        redisTemplate.opsForValue().increment(todayKey);
        redisTemplate.expire(todayKey, Duration.ofHours(25));
        
        // 小时计数（2小时过期）
        String hourKey = HOUR_KEY_PREFIX + currentHour;
        redisTemplate.opsForValue().increment(hourKey);
        redisTemplate.expire(hourKey, Duration.ofHours(2));
    }
    
    public BidRequestStats getStats() {
        String today = LocalDate.now().toString();
        String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH"));
        
        String totalStr = redisTemplate.opsForValue().get(TOTAL_KEY);
        String todayStr = redisTemplate.opsForValue().get(TODAY_KEY_PREFIX + today);
        String hourStr = redisTemplate.opsForValue().get(HOUR_KEY_PREFIX + currentHour);
        
        return BidRequestStats.builder()
            .total(totalStr != null ? Long.parseLong(totalStr) : 0)
            .today(todayStr != null ? Long.parseLong(todayStr) : 0)
            .currentHour(hourStr != null ? Long.parseLong(hourStr) : 0)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### 4. 测试验证

#### 单元测试

```java
@SpringBootTest
class BidRequestCounterTest {
    
    @Autowired
    private BidRequestCounter bidRequestCounter;
    
    @Test
    void testIncrement() {
        BidRequestStats initialStats = bidRequestCounter.getStats();
        
        bidRequestCounter.increment();
        
        BidRequestStats newStats = bidRequestCounter.getStats();
        
        assertThat(newStats.getTotal()).isEqualTo(initialStats.getTotal() + 1);
        assertThat(newStats.getToday()).isEqualTo(initialStats.getToday() + 1);
        assertThat(newStats.getCurrentHour()).isEqualTo(initialStats.getCurrentHour() + 1);
    }
}
```

#### API测试

```bash
# 测试bid request
curl -X POST http://localhost:8080/api/v1/bid/request \
  -H "Content-Type: application/json" \
  -d '{"id":"test-bid-001","imp":[{"id":"1","banner":{"w":300,"h":250}}]}'

# 查看统计
curl http://localhost:8080/api/v1/bid/stats
```

### 5. 部署注意事项

#### application.yml配置

```yaml
# 如果使用Redis版本
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    
# 日志配置
logging:
  level:
    fei.song.play_spring_boot_api: DEBUG
```

#### Docker Compose配置

```yaml
# 如果需要Redis
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
      
volumes:
  redis_data:
```

## 快速上手步骤

1. **选择实现方式**：
   - 简单场景：使用内存计数器
   - 生产环境：使用Redis版本

2. **添加后端代码**：
   - 复制相应的Java类到项目中
   - 在BidController中添加计数逻辑

3. **添加前端组件**：
   - 创建BidRequestStatsWidget组件
   - 集成到主页面

4. **测试验证**：
   - 发送几个bid request
   - 查看统计数据是否正确更新

5. **部署上线**：
   - 配置Redis（如果使用）
   - 部署到生产环境

## 扩展建议

- **持久化**：定期将计数器数据保存到数据库
- **监控**：添加Prometheus指标
- **告警**：设置异常流量告警
- **可视化**：添加图表展示趋势
- **分析**：按来源、类型等维度统计

这个简化方案可以快速满足基本的bid request数量展示需求，后续可以根据业务发展逐步完善功能。