package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DSP来源统计
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DspSourceStats {
    
    /**
     * DSP来源
     */
    private String dspSource;
    
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