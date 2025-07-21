package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * OpenRTB指标对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Metric {
    
    /**
     * 指标类型
     */
    private String type;
    
    /**
     * 指标值
     */
    private Double value;
    
    /**
     * 供应商特定的指标类型
     */
    private String vendor;
    
    /**
     * 扩展字段
     */
    private Object ext;
}