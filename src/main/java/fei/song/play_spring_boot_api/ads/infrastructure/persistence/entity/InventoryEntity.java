package fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 广告位库存实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "inventory")
@CompoundIndexes({
    @CompoundIndex(name = "publisher_placement_idx", def = "{'publisher_id': 1, 'placement_id': 1}")
})
public class InventoryEntity {

    @Id
    private String id;

    /**
     * 广告位ID
     */
    @Indexed(unique = true)
    @Field("placement_id")
    private String placementId;

    /**
     * 发布商ID
     */
    @Indexed
    @Field("publisher_id")
    private String publisherId;

    /**
     * 站点ID
     */
    @Indexed
    @Field("site_id")
    private String siteId;

    /**
     * 广告位名称
     */
    private String name;

    /**
     * 广告位状态
     */
    @Indexed
    private String status; // active, inactive, pending

    /**
     * 广告位规格
     */
    private PlacementSpec spec;

    /**
     * 定价信息
     */
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
    private QualityScore qualityScore;

    /**
     * 广告位设置
     */
    private PlacementSettings settings;

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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlacementSpec {
        /**
         * 广告位类型
         */
        @Field("ad_type")
        private String adType; // banner, video, native

        /**
         * 支持的格式
         */
        @Field("supported_formats")
        private List<AdFormat> supportedFormats;

        /**
         * 位置信息
         */
        private Position position;

        /**
         * 可见性要求
         */
        private Viewability viewability;
    }

    /**
     * 广告格式内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdFormat {
        private Integer width;
        private Integer height;
        private String format; // banner, video, native
        @Field("mime_types")
        private List<String> mimeTypes;
    }

    /**
     * 位置信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Position {
        @Field("fold_position")
        private Integer foldPosition; // 1=above fold, 0=below fold
        @Field("page_position")
        private String pagePosition; // header, sidebar, footer, content
    }

    /**
     * 可见性内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Viewability {
        @Field("min_viewable_percentage")
        private Integer minViewablePercentage;
        @Field("min_viewable_duration")
        private Integer minViewableDuration;
    }

    /**
     * 定价信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pricing {
        /**
         * 底价
         */
        @Field("floor_price")
        private BigDecimal floorPrice;

        /**
         * 货币
         */
        private String currency;

        /**
         * 定价模式
         */
        @Field("pricing_model")
        private String pricingModel; // cpm, cpc, cpa

        /**
         * 私有交易设置
         */
        @Field("private_deals")
        private List<PrivateDeal> privateDeals;
    }

    /**
     * 私有交易内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrivateDeal {
        @Field("deal_id")
        private String dealId;
        @Field("buyer_id")
        private String buyerId;
        private BigDecimal price;
        @Field("start_date")
        private LocalDateTime startDate;
        @Field("end_date")
        private LocalDateTime endDate;
    }

    /**
     * 流量统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TrafficStats {
        /**
         * 日均请求量
         */
        @Field("daily_requests")
        private Long dailyRequests;

        /**
         * 日均展示量
         */
        @Field("daily_impressions")
        private Long dailyImpressions;

        /**
         * 填充率
         */
        @Field("fill_rate")
        private Double fillRate;

        /**
         * 平均CPM
         */
        @Field("avg_cpm")
        private BigDecimal avgCpm;

        /**
         * 点击率
         */
        private Double ctr;

        /**
         * 统计周期
         */
        @Field("stats_period")
        private String statsPeriod; // last_7_days, last_30_days
    }

    /**
     * 质量评分内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QualityScore {
        /**
         * 总体评分
         */
        @Field("overall_score")
        private Double overallScore;

        /**
         * 可见性评分
         */
        @Field("viewability_score")
        private Double viewabilityScore;

        /**
         * 品牌安全评分
         */
        @Field("brand_safety_score")
        private Double brandSafetyScore;

        /**
         * 流量质量评分
         */
        @Field("traffic_quality_score")
        private Double trafficQualityScore;

        /**
         * 评分更新时间
         */
        @Field("last_updated")
        private LocalDateTime lastUpdated;
    }

    /**
     * 广告位设置内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlacementSettings {
        /**
         * 允许的广告类别
         */
        @Field("allowed_categories")
        private List<String> allowedCategories;

        /**
         * 禁止的广告类别
         */
        @Field("blocked_categories")
        private List<String> blockedCategories;

        /**
         * 允许的广告主
         */
        @Field("allowed_advertisers")
        private List<String> allowedAdvertisers;

        /**
         * 禁止的广告主
         */
        @Field("blocked_advertisers")
        private List<String> blockedAdvertisers;

        /**
         * 频次控制
         */
        @Field("frequency_cap")
        private FrequencyCap frequencyCap;
    }

    /**
     * 频次控制内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FrequencyCap {
        @Field("max_impressions_per_user_per_day")
        private Integer maxImpressionsPerUserPerDay;
        @Field("max_impressions_per_user_per_hour")
        private Integer maxImpressionsPerUserPerHour;
    }
}