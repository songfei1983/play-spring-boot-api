package fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity;

import fei.song.play_spring_boot_api.ads.domain.model.BidRequest;
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

import java.time.LocalDateTime;

/**
 * 竞价请求实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bid_requests")
@CompoundIndexes({
    @CompoundIndex(name = "exchange_timestamp_idx", def = "{'exchange_id': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "site_timestamp_idx", def = "{'bid_request.site.domain': 1, 'timestamp': -1}"),
    @CompoundIndex(name = "device_type_os_idx", def = "{'bid_request.device.devicetype': 1, 'bid_request.device.os': 1}")
})
public class BidRequestEntity {

    @Id
    private String id;

    /**
     * 竞价请求唯一标识
     */
    @Indexed(unique = true)
    @Field("request_id")
    private String requestId;

    /**
     * 请求时间戳
     */
    @Indexed
    private LocalDateTime timestamp;

    /**
     * 来源IP地址
     */
    @Field("source_ip")
    private String sourceIp;

    /**
     * 交易平台ID
     */
    @Indexed
    @Field("exchange_id")
    private String exchangeId;

    /**
     * OpenRTB 竞价请求数据
     */
    @Field("bid_request")
    private BidRequest bidRequest;

    /**
     * 处理状态
     */
    @Indexed
    private String status; // pending, processed, failed

    /**
     * 处理时间(毫秒)
     */
    @Field("processing_time_ms")
    private Long processingTimeMs;

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
     * 更新时间
     */
    @Field("updated_at")
    private LocalDateTime updatedAt;
}