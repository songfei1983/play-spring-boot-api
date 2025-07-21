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
 * 竞价响应实体类
 * 映射到 MongoDB 的 bid_responses 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bid_responses")
@CompoundIndexes({
    @CompoundIndex(name = "idx_request_id_timestamp", def = "{'requestId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_response_id_timestamp", def = "{'responseId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_winning_bid_timestamp", def = "{'bidResult.winningBidId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_expires_at", def = "{'expiresAt': 1}")
})
public class BidResponseEntity {

    @Id
    private String id;

    /**
     * 关联的竞价请求ID
     */
    @Field("request_id")
    @Indexed
    private String requestId;

    /**
     * 竞价响应的唯一标识符
     */
    @Field("response_id")
    @Indexed(unique = true)
    private String responseId;

    /**
     * 响应时间戳
     */
    @Field("timestamp")
    @Indexed
    private LocalDateTime timestamp;

    /**
     * OpenRTB 竞价响应数据（JSON格式）
     */
    @Field("openrtb_data")
    private Map<String, Object> openrtbData;

    /**
     * 竞价结果统计
     */
    @Field("bid_result")
    private BidResult bidResult;

    /**
     * 性能指标
     */
    @Field("metrics")
    private Metrics metrics;

    /**
     * 过期时间（TTL索引）
     */
    @Field("expires_at")
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiresAt;

    /**
     * 创建时间
     */
    @Field("created_at")
    private LocalDateTime createdAt;

    /**
     * 竞价结果内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BidResult {
        /**
         * 总竞价数量
         */
        @Field("total_bids")
        private Integer totalBids;

        /**
         * 获胜竞价ID
         */
        @Field("winning_bid_id")
        private String winningBidId;

        /**
         * 获胜价格（分）
         */
        @Field("winning_price")
        private Long winningPrice;

        /**
         * 获胜广告活动ID
         */
        @Field("winning_campaign_id")
        private String winningCampaignId;

        /**
         * 获胜广告主ID
         */
        @Field("winning_advertiser_id")
        private String winningAdvertiserId;

        /**
         * 货币代码
         */
        @Field("currency")
        private String currency;

        /**
         * 是否有竞价
         */
        @Field("has_bid")
        private Boolean hasBid;
    }

    /**
     * 性能指标内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Metrics {
        /**
         * 响应构建时间（毫秒）
         */
        @Field("response_building_time_ms")
        private Long responseBuildingTimeMs;

        /**
         * 序列化时间（毫秒）
         */
        @Field("serialization_time_ms")
        private Long serializationTimeMs;

        /**
         * 总处理时间（毫秒）
         */
        @Field("total_processing_time_ms")
        private Long totalProcessingTimeMs;

        /**
         * 响应大小（字节）
         */
        @Field("response_size_bytes")
        private Long responseSizeBytes;
    }
}