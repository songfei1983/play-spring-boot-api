# 用户分段和档案信息设计文档

## 1. 概述

本文档描述了在OpenRTB系统中实现用户分段过滤器、用户分段和档案信息存储的设计方案。该方案将通过UserRepository将数据保存到MongoDB，并使用用户ID关联BidRequest与用户分段和档案信息。

## 2. 业务需求分析

### 2.1 核心功能
- **用户分段管理**：创建、更新、删除用户分段
- **用户档案管理**：存储和管理用户的详细信息
- **分段过滤器**：根据条件筛选符合特定分段的用户
- **BidRequest关联**：通过用户ID将竞价请求与用户信息关联

### 2.2 OpenRTB规范要求
- 支持用户ID映射（Cookie ID、Device ID、User ID等）
- 用户分段信息传递给DSP
- 隐私保护和数据合规

## 3. 数据模型设计

### 3.1 用户档案 (UserProfile)

```java
@Document(collection = "user_profiles")
public class UserProfile {
    @Id
    private String id;
    
    // 用户标识符
    private String userId;           // 主用户ID
    private String cookieId;         // Cookie ID
    private String deviceId;         // 设备ID
    private String advertisingId;    // 广告ID (IDFA/GAID)
    
    // 基本信息
    private String gender;           // 性别
    private Integer age;             // 年龄
    private String ageRange;         // 年龄段
    private String location;         // 地理位置
    private String language;         // 语言偏好
    
    // 兴趣和行为
    private List<String> interests;  // 兴趣标签
    private List<String> categories; // 内容类别偏好
    private Map<String, Object> behaviors; // 行为数据
    
    // 购买力和消费
    private String incomeLevel;      // 收入水平
    private List<String> purchaseHistory; // 购买历史
    private Double lifetimeValue;    // 生命周期价值
    
    // 技术信息
    private String deviceType;       // 设备类型
    private String os;               // 操作系统
    private String browser;          // 浏览器
    
    // 元数据
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastSeenAt;
    
    // 隐私设置
    private Boolean optOut;          // 是否选择退出
    private String consentString;    // 同意字符串
}
```

### 3.2 用户分段 (UserSegment)

```java
@Document(collection = "user_segments")
public class UserSegment {
    @Id
    private String id;
    
    // 分段基本信息
    private String name;             // 分段名称
    private String description;      // 分段描述
    private String category;         // 分段类别
    private SegmentType type;        // 分段类型（行为、人口统计、兴趣等）
    
    // 分段规则
    private List<SegmentRule> rules; // 分段规则列表
    private String ruleLogic;        // 规则逻辑（AND/OR）
    
    // 分段属性
    private Integer priority;        // 优先级
    private Boolean active;          // 是否激活
    private LocalDateTime expiresAt; // 过期时间
    
    // 统计信息
    private Long userCount;          // 用户数量
    private Double ctr;              // 点击率
    private Double conversionRate;   // 转化率
    
    // 元数据
    private String createdBy;        // 创建者
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

public enum SegmentType {
    DEMOGRAPHIC,    // 人口统计
    BEHAVIORAL,     // 行为
    INTEREST,       // 兴趣
    GEOGRAPHIC,     // 地理
    DEVICE,         // 设备
    CUSTOM          // 自定义
}
```

### 3.3 分段规则 (SegmentRule)

```java
public class SegmentRule {
    private String field;            // 字段名
    private RuleOperator operator;   // 操作符
    private Object value;            // 值
    private List<Object> values;     // 多值
    
    // 时间相关
    private Integer timeWindow;      // 时间窗口（天）
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime endDate;   // 结束日期
}

public enum RuleOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    CONTAINS,
    NOT_CONTAINS,
    IN,
    NOT_IN,
    REGEX,
    EXISTS,
    NOT_EXISTS
}
```

### 3.4 用户分段关联 (UserSegmentMapping)

```java
@Document(collection = "user_segment_mappings")
public class UserSegmentMapping {
    @Id
    private String id;
    
    private String userId;           // 用户ID
    private String segmentId;        // 分段ID
    private Double score;            // 匹配分数
    private LocalDateTime assignedAt; // 分配时间
    private LocalDateTime expiresAt;  // 过期时间
    
    // 分段上下文
    private Map<String, Object> context; // 分段上下文信息
    private String source;           // 数据来源
}
```

## 4. 服务层设计

### 4.1 UserProfileService

```java
@Service
public class UserProfileService {
    
    // 创建或更新用户档案
    public UserProfile createOrUpdateProfile(String userId, UserProfileRequest request);
    
    // 根据用户ID获取档案
    public Optional<UserProfile> getProfileByUserId(String userId);
    
    // 根据多种ID获取档案
    public Optional<UserProfile> getProfileByAnyId(String cookieId, String deviceId, String advertisingId);
    
    // 更新用户行为数据
    public void updateBehaviorData(String userId, Map<String, Object> behaviors);
    
    // 记录用户活动
    public void recordActivity(String userId, ActivityType type, Map<String, Object> data);
}
```

### 4.2 UserSegmentService

```java
@Service
public class UserSegmentService {
    
    // 创建分段
    public UserSegment createSegment(CreateSegmentRequest request);
    
    // 更新分段规则
    public UserSegment updateSegmentRules(String segmentId, List<SegmentRule> rules);
    
    // 计算用户分段
    public List<UserSegment> calculateUserSegments(String userId);
    
    // 批量计算分段
    public void recalculateAllSegments();
    
    // 获取分段用户列表
    public Page<String> getSegmentUsers(String segmentId, Pageable pageable);
}
```

### 4.3 SegmentFilterService

```java
@Service
public class SegmentFilterService {
    
    // 根据分段过滤用户
    public List<String> filterUsersBySegments(List<String> segmentIds, FilterCriteria criteria);
    
    // 实时分段匹配
    public List<UserSegment> matchSegmentsForBidRequest(BidRequest bidRequest);
    
    // 分段规则评估
    public boolean evaluateSegmentRules(UserProfile profile, List<SegmentRule> rules);
    
    // 分段性能分析
    public SegmentPerformanceReport analyzeSegmentPerformance(String segmentId, DateRange dateRange);
}
```

## 5. Repository层设计

### 5.1 扩展UserRepository

```java
@Repository
public class UserRepository {
    
    // 现有用户相关方法...
    
    // 用户档案相关
    public Optional<UserProfile> findProfileByUserId(String userId);
    public Optional<UserProfile> findProfileByAnyId(String cookieId, String deviceId, String advertisingId);
    public UserProfile saveProfile(UserProfile profile);
    public void updateProfileBehaviors(String userId, Map<String, Object> behaviors);
    
    // 用户分段相关
    public List<UserSegment> findActiveSegments();
    public List<UserSegmentMapping> findUserSegments(String userId);
    public void saveUserSegmentMapping(UserSegmentMapping mapping);
    public void removeExpiredSegmentMappings();
    
    // 分段查询
    public List<String> findUsersBySegment(String segmentId);
    public List<UserProfile> findProfilesBySegmentRules(List<SegmentRule> rules);
}
```

### 5.2 新增Repository

```java
@Repository
public class UserSegmentRepository {
    public UserSegment save(UserSegment segment);
    public Optional<UserSegment> findById(String id);
    public List<UserSegment> findByActive(boolean active);
    public List<UserSegment> findByType(SegmentType type);
    public void deleteById(String id);
}

@Repository
public class UserSegmentMappingRepository {
    public UserSegmentMapping save(UserSegmentMapping mapping);
    public List<UserSegmentMapping> findByUserId(String userId);
    public List<UserSegmentMapping> findBySegmentId(String segmentId);
    public void deleteExpiredMappings(LocalDateTime cutoffTime);
    public long countBySegmentId(String segmentId);
}
```

## 6. OpenRTB集成设计

### 6.1 BidRequest增强

```java
// 在现有BidRequest处理中添加用户分段信息
public class BidRequestProcessor {
    
    public BidResponse processBidRequest(BidRequest request) {
        // 1. 提取用户标识
        String userId = extractUserId(request);
        
        // 2. 获取用户档案和分段
        UserProfile profile = userProfileService.getProfileByUserId(userId).orElse(null);
        List<UserSegment> segments = segmentFilterService.matchSegmentsForBidRequest(request);
        
        // 3. 增强BidRequest
        EnhancedBidRequest enhancedRequest = enhanceBidRequest(request, profile, segments);
        
        // 4. 处理竞价逻辑
        return processBid(enhancedRequest);
    }
    
    private String extractUserId(BidRequest request) {
        // 从Cookie、Device ID、User ID等提取统一用户标识
        return userIdMappingService.resolveUserId(request);
    }
}
```

### 6.2 用户ID映射服务

```java
@Service
public class UserIdMappingService {
    
    // 统一用户ID解析
    public String resolveUserId(BidRequest request);
    
    // ID映射和同步
    public void mapUserIds(String primaryId, Map<String, String> alternativeIds);
    
    // 隐私保护处理
    public String hashUserId(String userId);
}
```

## 7. 数据库索引设计

### 7.1 UserProfile索引

```javascript
// MongoDB索引
db.user_profiles.createIndex({ "userId": 1 }, { unique: true })
db.user_profiles.createIndex({ "cookieId": 1 })
db.user_profiles.createIndex({ "deviceId": 1 })
db.user_profiles.createIndex({ "advertisingId": 1 })
db.user_profiles.createIndex({ "lastSeenAt": 1 })
db.user_profiles.createIndex({ "interests": 1 })
db.user_profiles.createIndex({ "location": 1, "ageRange": 1 })
```

### 7.2 UserSegmentMapping索引

```javascript
db.user_segment_mappings.createIndex({ "userId": 1, "segmentId": 1 }, { unique: true })
db.user_segment_mappings.createIndex({ "segmentId": 1 })
db.user_segment_mappings.createIndex({ "expiresAt": 1 })
db.user_segment_mappings.createIndex({ "assignedAt": 1 })
```

## 8. 性能优化策略

### 8.1 缓存策略
- **用户档案缓存**：Redis缓存热点用户档案（TTL: 1小时）
- **分段结果缓存**：缓存用户分段计算结果（TTL: 30分钟）
- **分段规则缓存**：缓存活跃分段规则（TTL: 10分钟）

### 8.2 异步处理
- **分段计算**：使用消息队列异步计算用户分段
- **行为数据更新**：异步更新用户行为数据
- **过期数据清理**：定时任务清理过期的分段映射

### 8.3 批量操作
- **批量分段计算**：定期批量重新计算所有用户分段
- **批量数据导入**：支持批量导入用户档案数据

## 9. 隐私和合规

### 9.1 数据保护
- **数据加密**：敏感字段加密存储
- **访问控制**：基于角色的数据访问控制
- **审计日志**：记录所有数据访问和修改操作

### 9.2 用户同意管理
- **同意字符串**：存储和验证用户同意信息
- **选择退出**：支持用户选择退出数据收集
- **数据删除**：支持用户数据删除请求

## 10. 监控和分析

### 10.1 性能监控
- **分段计算性能**：监控分段计算耗时
- **查询性能**：监控数据库查询性能
- **缓存命中率**：监控缓存效果

### 10.2 业务分析
- **分段效果分析**：分析分段的CTR和转化率
- **用户行为分析**：分析用户行为模式
- **数据质量监控**：监控数据完整性和准确性

## 11. 实施计划

### 阶段1：基础数据模型（1-2周）
1. 创建UserProfile、UserSegment、UserSegmentMapping实体
2. 实现基础Repository层
3. 设置MongoDB索引

### 阶段2：核心服务实现（2-3周）
1. 实现UserProfileService
2. 实现UserSegmentService
3. 实现SegmentFilterService
4. 添加基础的单元测试

### 阶段3：OpenRTB集成（1-2周）
1. 集成用户分段到BidRequest处理
2. 实现用户ID映射服务
3. 添加集成测试

### 阶段4：性能优化（1-2周）
1. 实现缓存策略
2. 添加异步处理
3. 性能测试和调优

### 阶段5：监控和部署（1周）
1. 添加监控和日志
2. 部署到测试环境
3. 生产环境部署

## 12. 风险和挑战

### 12.1 技术风险
- **性能问题**：大量用户的分段计算可能影响性能
- **数据一致性**：分布式环境下的数据一致性挑战
- **存储成本**：用户档案和分段数据的存储成本

### 12.2 业务风险
- **隐私合规**：需要严格遵守数据保护法规
- **数据质量**：用户数据的准确性和完整性
- **分段效果**：分段策略的有效性需要持续优化

## 13. 总结

本设计方案提供了一个完整的用户分段和档案管理系统，支持：
- 灵活的用户分段规则定义
- 高性能的分段计算和过滤
- 与OpenRTB系统的无缝集成
- 完善的隐私保护和合规机制
- 可扩展的架构设计

该方案能够满足现代程序化广告对用户定向的需求，同时保证系统的性能、可靠性和合规性。