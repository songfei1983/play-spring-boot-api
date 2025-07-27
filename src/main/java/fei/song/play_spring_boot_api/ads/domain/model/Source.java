package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * OpenRTB来源信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Source {
    
    /**
     * 实体负责最终销售决策 (0=否, 1=是)
     */
    @JsonProperty("fd")
    private Integer finalDecision;
    
    /**
     * 交易ID，用于跟踪整个供应链
     */
    @JsonProperty("tid")
    private String transactionId;
    
    /**
     * 支付ID链，用于跟踪支付流
     */
    @JsonProperty("pchain")
    private String paymentIdChain;
    
    /**
     * 扩展字段
     */
    private Object ext;
}