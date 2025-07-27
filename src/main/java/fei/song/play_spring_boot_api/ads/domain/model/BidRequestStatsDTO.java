package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Bid Request 统计数据传输对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidRequestStatsDTO {
    
    /**
     * 总请求数
     */
    private Long totalRequests;
    
    /**
     * 今日请求数
     */
    private Long todayRequests;
    
    /**
     * 当前小时请求数
     */
    private Long currentHourRequests;
    
    /**
     * 成功率
     */
    private Double successRate;
    
    /**
     * 平均响应时间
     */
    private Double avgResponseTime;
    
    /**
     * 统计时间戳
     */
    private LocalDateTime timestamp;
}