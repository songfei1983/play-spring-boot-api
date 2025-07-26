package fei.song.play_spring_boot_api.ads.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

/**
 * MongoDB索引配置
 * 自动创建必要的索引以优化查询性能
 */
@Configuration
public class MongoIndexConfig {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 初始化索引
     */
    @Bean
    public CommandLineRunner initIndexes() {
        return args -> {
            createBidRequestIndexes();
            createBidResponseIndexes();
            createInventoryIndexes();
            createUserProfileIndexes();
        };
    }
    
    /**
     * 创建竞价请求相关索引
     */
    private void createBidRequestIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("bid_requests");
        
        // 创建时间戳索引（用于时间范围查询）
        indexOps.createIndex(new Index().on("timestamp", Sort.Direction.DESC));
        
        // 注意：MongoDB的_id字段本身就是唯一的且已有索引，不需要额外创建
        
        // 创建应用ID索引（用于按应用过滤）
        indexOps.createIndex(new Index().on("app.id", Sort.Direction.ASC));
        
        // 创建设备类型索引（用于设备定向）
        indexOps.createIndex(new Index().on("device.devicetype", Sort.Direction.ASC));
        
        // 创建地理位置索引（用于地理定向）
        indexOps.createIndex(new Index().on("device.geo.country", Sort.Direction.ASC));
        indexOps.createIndex(new Index().on("device.geo.city", Sort.Direction.ASC));
        
        // 创建复合索引（用于复杂查询）
        indexOps.createIndex(new Index()
                .on("app.id", Sort.Direction.ASC)
                .on("device.devicetype", Sort.Direction.ASC)
                .on("timestamp", Sort.Direction.DESC));
    }
    
    /**
     * 创建竞价响应相关索引
     */
    private void createBidResponseIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("bid_responses");
        
        // 注意：MongoDB的_id字段本身就是唯一的且已有索引，不需要额外创建
        
        // 创建竞价请求ID索引（用于关联查询）
        indexOps.createIndex(new Index().on("bidid", Sort.Direction.ASC));
        
        // 创建时间戳索引
        indexOps.createIndex(new Index().on("timestamp", Sort.Direction.DESC));
        
        // 创建竞价价格索引（用于价格分析）
        indexOps.createIndex(new Index().on("seatbid.bid.price", Sort.Direction.DESC));
    }
    
    /**
     * 创建广告位库存相关索引
     */
    private void createInventoryIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("inventory");
        
        // 注意：MongoDB的_id字段本身就是唯一的且已有索引，不需要额外创建
        
        // 创建应用ID索引
        indexOps.createIndex(new Index().on("appId", Sort.Direction.ASC));
        
        // 创建广告位类型索引
        indexOps.createIndex(new Index().on("adType", Sort.Direction.ASC));
        
        // 创建状态索引
        indexOps.createIndex(new Index().on("status", Sort.Direction.ASC));
        
        // 创建更新时间索引
        indexOps.createIndex(new Index().on("updatedAt", Sort.Direction.DESC));
        
        // 创建复合索引
        indexOps.createIndex(new Index()
                .on("appId", Sort.Direction.ASC)
                .on("adType", Sort.Direction.ASC)
                .on("status", Sort.Direction.ASC));
    }
    
    /**
     * 创建用户画像相关索引
     */
    private void createUserProfileIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps("user_profiles");
        
        // 创建用户ID索引
        indexOps.createIndex(new Index().on("userId", Sort.Direction.ASC).unique());
        
        // 创建设备ID索引
        indexOps.createIndex(new Index().on("deviceId", Sort.Direction.ASC));
        
        // 创建年龄段索引
        indexOps.createIndex(new Index().on("demographics.ageGroup", Sort.Direction.ASC));
        
        // 创建性别索引
        indexOps.createIndex(new Index().on("demographics.gender", Sort.Direction.ASC));
        
        // 创建兴趣标签索引
        indexOps.createIndex(new Index().on("interests", Sort.Direction.ASC));
        
        // 创建地理位置索引
        indexOps.createIndex(new Index().on("location.country", Sort.Direction.ASC));
        indexOps.createIndex(new Index().on("location.city", Sort.Direction.ASC));
        
        // 创建最后活跃时间索引
        indexOps.createIndex(new Index().on("lastActiveAt", Sort.Direction.DESC));
        
        // 创建复合索引（用于用户定向）
        indexOps.createIndex(new Index()
                .on("demographics.ageGroup", Sort.Direction.ASC)
                .on("demographics.gender", Sort.Direction.ASC)
                .on("location.country", Sort.Direction.ASC));
    }
}