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
 * 竞价请求实体类
 * 映射到 MongoDB 的 bid_requests 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bid_requests")
@CompoundIndexes({
    @CompoundIndex(name = "idx_request_id_timestamp", def = "{'requestId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_status_timestamp", def = "{'status': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_exchange_timestamp", def = "{'exchangeId': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_source_ip_timestamp", def = "{'sourceIp': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "idx_expires_at", def = "{'expiresAt': 1}")
})
public class BidRequestEntity {

    @Id
    private String id;

    /**
     * 竞价请求的唯一标识符
     */
    @Field("request_id")
    @Indexed(unique = true)
    private String requestId;

    /**
     * 请求时间戳
     */
    @Field("timestamp")
    @Indexed
    private LocalDateTime timestamp;

    /**
     * 来源IP地址
     */
    @Field("source_ip")
    @Indexed
    private String sourceIp;

    /**
     * 交易平台ID
     */
    @Field("exchange_id")
    @Indexed
    private String exchangeId;

    /**
     * 请求状态：RECEIVED, PROCESSING, COMPLETED, FAILED, TIMEOUT
     */
    @Field("status")
    @Indexed
    private String status;

    /**
     * OpenRTB 竞价请求数据（JSON格式）
     */
    @Field("openrtb_data")
    private Map<String, Object> openrtbData;

    /**
     * 处理结果统计
     */
    @Field("processing_result")
    private ProcessingResult processingResult;

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
     * 处理结果内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProcessingResult {
        /**
         * 匹配的广告活动数量
         */
        @Field("matched_campaigns")
        private Integer matchedCampaigns;

        /**
         * 生成的竞价数量
         */
        @Field("generated_bids")
        private Integer generatedBids;

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
         * 错误信息
         */
        @Field("error_message")
        private String errorMessage;
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
         * 处理时间（毫秒）
         */
        @Field("processing_time_ms")
        private Long processingTimeMs;

        /**
         * 用户画像查询时间（毫秒）
         */
        @Field("user_profile_query_time_ms")
        private Long userProfileQueryTimeMs;

        /**
         * 广告活动匹配时间（毫秒）
         */
        @Field("campaign_matching_time_ms")
        private Long campaignMatchingTimeMs;

        /**
         * 竞价生成时间（毫秒）
         */
        @Field("bid_generation_time_ms")
        private Long bidGenerationTimeMs;

        /**
         * 响应构建时间（毫秒）
         */
        @Field("response_building_time_ms")
        private Long responseBuildingTimeMs;
    }
}