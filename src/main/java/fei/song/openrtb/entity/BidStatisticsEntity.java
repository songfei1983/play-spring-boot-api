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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 竞价统计实体类
 * 映射到 MongoDB 的 bid_statistics 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bid_statistics")
@CompoundIndexes({
    @CompoundIndex(name = "idx_date_hour_campaign", def = "{'date': 1, 'hour': 1, 'campaignId': 1}"),
    @CompoundIndex(name = "idx_date_publisher_placement", def = "{'date': 1, 'publisherId': 1, 'placementId': 1}"),
    @CompoundIndex(name = "idx_campaign_date", def = "{'campaignId': 1, 'date': -1}"),
    @CompoundIndex(name = "idx_publisher_date", def = "{'publisherId': 1, 'date': -1}")
})
public class BidStatisticsEntity {

    @Id
    private String id;

    /**
     * 统计日期
     */
    @Field("date")
    @Indexed
    private LocalDate date;

    /**
     * 统计小时（0-23）
     */
    @Field("hour")
    @Indexed
    private Integer hour;

    /**
     * 广告活动ID
     */
    @Field("campaign_id")
    @Indexed
    private String campaignId;

    /**
     * 发布商ID
     */
    @Field("publisher_id")
    @Indexed
    private String publisherId;

    /**
     * 广告位ID
     */
    @Field("placement_id")
    @Indexed
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
    private Map<String, GeoStats> geoStats;

    /**
     * 设备统计
     */
    @Field("device_stats")
    private Map<String, DeviceStats> deviceStats;

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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BidStats {
        @Field("total_requests")
        private Long totalRequests;
        
        @Field("total_bids")
        private Long totalBids;
        
        @Field("winning_bids")
        private Long winningBids;
        
        @Field("bid_rate")
        private Double bidRate; // totalBids / totalRequests
        
        @Field("win_rate")
        private Double winRate; // winningBids / totalBids
        
        @Field("fill_rate")
        private Double fillRate; // winningBids / totalRequests
        
        @Field("average_bid_price")
        private Long averageBidPrice; // 分
        
        @Field("average_winning_price")
        private Long averageWinningPrice; // 分
    }

    /**
     * 收入统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueStats {
        @Field("total_revenue")
        private Long totalRevenue; // 分
        
        @Field("advertiser_spend")
        private Long advertiserSpend; // 分
        
        @Field("publisher_revenue")
        private Long publisherRevenue; // 分
        
        @Field("platform_fee")
        private Long platformFee; // 分
        
        @Field("currency")
        private String currency;
        
        @Field("average_cpm")
        private Long averageCpm; // 分
        
        @Field("average_cpc")
        private Long averageCpc; // 分
        
        @Field("average_cpa")
        private Long averageCpa; // 分
    }

    /**
     * 性能统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceStats {
        @Field("impressions")
        private Long impressions;
        
        @Field("clicks")
        private Long clicks;
        
        @Field("conversions")
        private Long conversions;
        
        @Field("click_through_rate")
        private Double clickThroughRate; // clicks / impressions
        
        @Field("conversion_rate")
        private Double conversionRate; // conversions / clicks
        
        @Field("viewability_rate")
        private Double viewabilityRate;
        
        @Field("completion_rate")
        private Double completionRate; // for video ads
        
        @Field("bounce_rate")
        private Double bounceRate;
        
        @Field("average_view_duration")
        private Long averageViewDuration; // 秒
    }

    /**
     * 地理统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeoStats {
        @Field("requests")
        private Long requests;
        
        @Field("bids")
        private Long bids;
        
        @Field("wins")
        private Long wins;
        
        @Field("revenue")
        private Long revenue; // 分
        
        @Field("impressions")
        private Long impressions;
        
        @Field("clicks")
        private Long clicks;
    }

    /**
     * 设备统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceStats {
        @Field("requests")
        private Long requests;
        
        @Field("bids")
        private Long bids;
        
        @Field("wins")
        private Long wins;
        
        @Field("revenue")
        private Long revenue; // 分
        
        @Field("impressions")
        private Long impressions;
        
        @Field("clicks")
        private Long clicks;
        
        @Field("average_cpm")
        private Long averageCpm; // 分
    }
}