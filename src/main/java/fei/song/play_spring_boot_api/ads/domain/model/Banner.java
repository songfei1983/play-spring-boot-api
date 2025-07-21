package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 横幅广告对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Banner {
    
    /**
     * 支持的格式列表
     */
    private List<Format> format;
    
    /**
     * 宽度(像素)
     */
    @JsonProperty("w")
    private Integer w;
    
    /**
     * 高度(像素)
     */
    @JsonProperty("h")
    private Integer h;
    
    /**
     * 相对位置 (0=未知, 1=可见, 2=可能可见, 3=不可见)
     */
    private Integer pos;
    
    /**
     * 支持的MIME类型
     */
    private List<String> mimes;
    
    /**
     * 顶部框架标识
     */
    @JsonProperty("topframe")
    private Integer topFrame;
    
    /**
     * 可展开方向
     */
    @JsonProperty("expdir")
    private List<Integer> expandableDirections;
    
    /**
     * 支持的API框架
     */
    private List<Integer> api;
    
    /**
     * 广告ID
     */
    private String id;
    
    /**
     * 阻止的创意属性
     */
    @JsonProperty("battr")
    private List<Integer> blockedAttributes;
    
    /**
     * 阻止的创意类型
     */
    @JsonProperty("btype")
    private List<Integer> blockedTypes;
    
    /**
     * 最大扩展宽度
     */
    @JsonProperty("wmax")
    private Integer maxWidth;
    
    /**
     * 最大扩展高度
     */
    @JsonProperty("hmax")
    private Integer maxHeight;
    
    /**
     * 最小宽度
     */
    @JsonProperty("wmin")
    private Integer minWidth;
    
    /**
     * 最小高度
     */
    @JsonProperty("hmin")
    private Integer minHeight;
    
    /**
     * 扩展字段
     */
    private Object ext;
    
    /**
     * 格式对象
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Format {
        /**
         * 宽度
         */
        @JsonProperty("w")
        private Integer w;
        
        /**
         * 高度
         */
        @JsonProperty("h")
        private Integer h;
        
        /**
         * 相对宽度比例
         */
        @JsonProperty("wratio")
        private Integer widthRatio;
        
        /**
         * 相对高度比例
         */
        @JsonProperty("hratio")
        private Integer heightRatio;
        
        /**
         * 最小宽度
         */
        @JsonProperty("wmin")
        private Integer minWidth;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
}