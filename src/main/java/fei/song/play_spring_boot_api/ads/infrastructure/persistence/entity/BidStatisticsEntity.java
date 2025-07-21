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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 竞价统计实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bid_statistics")
@CompoundIndexes({
    @CompoundIndex(name = "date_campaign_idx", def = "{'date': 1, 'campaign_id': 1}"),
    @CompoundIndex(name = "date_publisher_idx", def = "{'date': 1, 'publisher_id': 1}")
})
public class BidStatisticsEntity {

    @Id
    private String id;

    /**
     * 统计日期
     */
    @Indexed
    private LocalDate date;

    /**
     * 统计小时(0-23)
     */
    private Integer hour;

    /**
     * 广告活动ID
     */
    @Indexed
    @Field("campaign_id")
    private String campaignId;

    /**
     * 发布商ID
     */
    @Indexed
    @Field("publisher_id")
    private String publisherId;

    /**
     * 广告位ID
     */
    @Field("placement_id")
    private String placementId;

    /**
     * 竞价统计
     */
    @Field("bid_stats")
    private BidStats bidStats;

    /**
     * 收入统计
     */
    @Field("revenue_stats")
    private RevenueStats revenueStats;

    /**
     * 性能统计
     */
    @Field("performance_stats")
    private PerformanceStats performanceStats;

    /**
     * 地理统计
     */
    @Field("geo_stats")
    private Map<String, GeoStats> geoStats; // country -> stats

    /**
     * 设备统计
     */
    @Field("device_stats")
    private Map<String, DeviceStats> deviceStats; // device_type -> stats

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
     * 竞价统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BidStats {
        /**
         * 竞价请求数
         */
        @Field("bid_requests")
        private Long bidRequests;

        /**
         * 竞价响应数
         */
        @Field("bid_responses")
        private Long bidResponses;

        /**
         * 获胜竞价数
         */
        @Field("won_bids")
        private Long wonBids;

        /**
         * 展示数
         */
        private Long impressions;

        /**
         * 点击数
         */
        private Long clicks;

        /**
         * 转化数
         */
        private Long conversions;

        /**
         * 竞价参与率
         */
        @Field("bid_rate")
        private Double bidRate;

        /**
         * 获胜率
         */
        @Field("win_rate")
        private Double winRate;

        /**
         * 填充率
         */
        @Field("fill_rate")
        private Double fillRate;
    }

    /**
     * 收入统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueStats {
        /**
         * 总收入
         */
        @Field("total_revenue")
        private BigDecimal totalRevenue;

        /**
         * 平均CPM
         */
        @Field("avg_cpm")
        private BigDecimal avgCpm;

        /**
         * 平均CPC
         */
        @Field("avg_cpc")
        private BigDecimal avgCpc;

        /**
         * 平均CPA
         */
        @Field("avg_cpa")
        private BigDecimal avgCpa;

        /**
         * 货币
         */
        private String currency;

        /**
         * 花费
         */
        private BigDecimal spend;

        /**
         * 利润
         */
        private BigDecimal profit;

        /**
         * 利润率
         */
        @Field("profit_margin")
        private Double profitMargin;
    }

    /**
     * 性能统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PerformanceStats {
        /**
         * 点击率
         */
        private Double ctr;

        /**
         * 转化率
         */
        @Field("conversion_rate")
        private Double conversionRate;

        /**
         * 可见性率
         */
        @Field("viewability_rate")
        private Double viewabilityRate;

        /**
         * 平均响应时间(毫秒)
         */
        @Field("avg_response_time")
        private Double avgResponseTime;

        /**
         * 错误率
         */
        @Field("error_rate")
        private Double errorRate;

        /**
         * 超时率
         */
        @Field("timeout_rate")
        private Double timeoutRate;
    }

    /**
     * 地理统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeoStats {
        private Long impressions;
        private Long clicks;
        private BigDecimal revenue;
        private Double ctr;
        @Field("avg_cpm")
        private BigDecimal avgCpm;
    }

    /**
     * 设备统计内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceStats {
        private Long impressions;
        private Long clicks;
        private BigDecimal revenue;
        private Double ctr;
        @Field("avg_cpm")
        private BigDecimal avgCpm;
        @Field("device_type")
        private String deviceType;
    }
}