package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRTB原生广告对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Native {
    
    /**
     * 原生广告请求载荷
     */
    private String request;
    
    /**
     * 原生广告版本
     */
    @JsonProperty("ver")
    private String version;
    
    /**
     * 支持的API框架列表
     */
    private List<Integer> api;
    
    /**
     * 阻止的创意属性列表
     */
    @JsonProperty("battr")
    private List<Integer> blockedAttributes;
    
    /**
     * 扩展字段
     */
    private Object ext;
}