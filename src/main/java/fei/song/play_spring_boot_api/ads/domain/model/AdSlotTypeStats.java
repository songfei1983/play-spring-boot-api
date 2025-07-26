package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告位类型统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdSlotTypeStats {
    
    /**
     * 广告位类型
     */
    private String adSlotType;
    
    /**
     * 总请求数
     */
    private Long totalRequests;
    
    /**
     * 成功数
     */
    private Long successCount;
    
    /**
     * 成功率
     */
    private Double successRate;
}