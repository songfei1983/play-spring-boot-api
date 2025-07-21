package fei.song.openrtb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广告位库存实体类
 * 映射到 MongoDB 的 inventory 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "inventory")
@CompoundIndexes({
    @CompoundIndex(name = "idx_publisher_site", def = "{'publisherId': 1, 'siteId': 1}"),
    @CompoundIndex(name = "idx_status_type_size", def = "{'status': 1, 'specs.adType': 1, 'specs.width': 1, 'specs.height': 1}"),
    @CompoundIndex(name = "idx_pricing_quality", def = "{'pricing.floorPrice': 1, 'qualityScore': 1}"),
    @CompoundIndex(name = "idx_traffic_fill", def = "{'trafficStats.dailyImpressions': 1, 'trafficStats.fillRate': 1}")
})
public class InventoryEntity {

    @Id
    private String id;

    /**
     * 广告位ID
     */
    @Field("placement_id")
    @Indexed(unique = true)
    private String placementId;

    /**
     * 发布商ID
     */
    @Field("publisher_id")
    @Indexed
    private String publisherId;

    /**
     * 站点ID
     */
    @Field("site_id")
    @Indexed
    private String siteId;

    /**
     * 广告位名称
     */
    @Field("name")
    private String name;

    /**
     * 广告位状态：ACTIVE, INACTIVE, PENDING
     */
    @Field("status")
    @Indexed
    private String status;

    /**
     * 广告位规格
     */
    @Field("specs")
    private Specs specs;

    /**
     * 定价信息
     */
    @Field("pricing")
    private Pricing pricing;

    /**
     * 流量统计
     */
    @Field("traffic_stats")
    private TrafficStats trafficStats;

    /**
     * 质量评分
     */
    @Field("quality_score")
    @Indexed
    private Double qualityScore;

    /**
     * 广告位设置
     */
    @Field("settings")
    private Settings settings;

    /**
     * 创建时间
     */
    @Field("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 广告位规格内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Specs {
        @Field("ad_type")
        private String adType; // banner, video, native, interstitial
        
        @Field("width")
        private Integer width;
        
        @Field("height")
        private Integer height;
        
        @Field("supported_formats")
        private List<String> supportedFormats; // jpg, png, gif, mp4, etc.
        
        @Field("max_file_size")
        private Long maxFileSize; // bytes
        
        @Field("position")
        private String position; // above_fold, below_fold, sidebar
        
        @Field("visibility_score")
        private Double visibilityScore;
    }

    /**
     * 定价信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pricing {
        @Field("floor_price")
        private Long floorPrice; // 分
        
        @Field("currency")
        private String currency;
        
        @Field("pricing_model")
        private String pricingModel; // CPM, CPC, CPA
        
        @Field("private_deals")
        private List<PrivateDeal> privateDeals;
        
        @Field("average_cpm")
        private Long averageCpm; // 分
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrivateDeal {
        @Field("deal_id")
        private String dealId;
        
        @Field("advertiser_id")
        private String advertiserId;
        
        @Field("fixed_price")
        private Long fixedPrice; // 分
        
        @Field("start_date")
        private LocalDateTime startDate;
        
        @Field("end_date")
        private LocalDateTime endDate;
    }

    /**
     * 流量统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrafficStats {
        @Field("daily_impressions")
        private Long dailyImpressions;
        
        @Field("daily_requests")
        private Long dailyRequests;
        
        @Field("fill_rate")
        private Double fillRate;
        
        @Field("average_cpm")
        private Long averageCpm; // 分
        
        @Field("click_through_rate")
        private Double clickThroughRate;
        
        @Field("viewability_rate")
        private Double viewabilityRate;
        
        @Field("last_updated")
        private LocalDateTime lastUpdated;
    }

    /**
     * 广告位设置内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Settings {
        @Field("allowed_categories")
        private List<String> allowedCategories;
        
        @Field("blocked_categories")
        private List<String> blockedCategories;
        
        @Field("allowed_advertisers")
        private List<String> allowedAdvertisers;
        
        @Field("blocked_advertisers")
        private List<String> blockedAdvertisers;
        
        @Field("require_ssl")
        private Boolean requireSsl;
        
        @Field("allow_popups")
        private Boolean allowPopups;
        
        @Field("max_redirect_count")
        private Integer maxRedirectCount;
        
        @Field("content_rating")
        private String contentRating; // G, PG, PG13, R
    }
}