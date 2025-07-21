package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * OpenRTB法规信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Regs {
    
    /**
     * COPPA合规标识 (0=否, 1=是)
     * COPPA: 儿童在线隐私保护法
     */
    private Integer coppa;
    
    /**
     * GDPR适用标识 (0=否, 1=是, 省略=未知)
     * GDPR: 通用数据保护条例
     */
    private Integer gdpr;
    
    /**
     * 美国隐私法适用标识
     */
    @JsonProperty("us_privacy")
    private String usPrivacy;
    
    /**
     * 扩展字段
     */
    private Object ext;
}