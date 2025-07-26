package fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity;

import fei.song.play_spring_boot_api.ads.domain.model.BidResponse;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 竞价响应实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bid_responses")
public class BidResponseEntity {

    @Id
    private String id;

    /**
     * 关联的竞价请求ID
     */
    @Indexed(unique = true)
    @Field("request_id")
    private String requestId;

    /**
     * 响应ID
     */
    @Field("response_id")
    private String responseId;

    /**
     * 响应时间戳
     */
    @Indexed
    private LocalDateTime timestamp;

    /**
     * OpenRTB 竞价响应数据
     */
    @Field("bid_response")
    private BidResponse bidResponse;

    /**
     * 竞价结果统计
     */
    @Field("bid_result")
    private BidResult bidResult;

    /**
     * 性能指标
     */
    private Metrics metrics;

    /**
     * 过期时间(TTL)
     */
    @Indexed(expireAfterSeconds = 0)
    @Field("expires_at")
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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BidResult {
        /**
         * 总竞价数
         */
        @Field("total_bids")
        private Integer totalBids;

        /**
         * 最高竞价
         */
        @Field("highest_bid")
        private Double highestBid;

        /**
         * 获胜竞价ID
         */
        @Indexed
        @Field("winning_bid_id")
        private String winningBidId;

        /**
         * 拍卖类型
         */
        @Field("auction_type")
        private Integer auctionType;
    }

    /**
     * 性能指标内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metrics {
        /**
         * 处理时间(毫秒)
         */
        @Field("processing_time_ms")
        private Long processingTimeMs;

        /**
         * 评估的候选数量
         */
        @Field("candidates_evaluated")
        private Integer candidatesEvaluated;

        /**
         * 应用的过滤器
         */
        @Field("filters_applied")
        private List<String> filtersApplied;
    }
}