package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Bid Request 统计指标实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bid_request_metrics")
public class BidRequestMetrics {
    
    @Id
    private String id;
    
    /**
     * 统计时间点
     */
    @Indexed
    private LocalDateTime timestamp;
    
    /**
     * 小时维度 格式: "2024-01-15T10"
     */
    @Indexed
    private String hour;
    
    /**
     * 日期维度 格式: "2024-01-15"
     */
    @Indexed
    private String date;
    
    /**
     * 广告位类型
     */
    private String adSlotType;
    
    /**
     * DSP来源
     */
    private String dspSource;
    
    /**
     * 请求数量
     */
    private Long requestCount;
    
    /**
     * 成功响应数量
     */
    private Long successCount;
    
    /**
     * 失败数量
     */
    private Long failureCount;
    
    /**
     * 平均响应时间(ms)
     */
    private Double avgResponseTime;
    
    /**
     * 创建时间
     */
    @CreatedDate
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}