package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    /**
     * 交换特定用户ID
     */
    private String id;
    
    /**
     * 买方特定用户ID
     */
    @JsonProperty("buyeruid")
    private String buyeruid;
    
    /**
     * 出生年份
     */
    @JsonProperty("yob")
    private Integer yob;
    
    /**
     * 性别 (M=男性, F=女性, O=其他)
     */
    private String gender;
    
    /**
     * 关键词，兴趣或意图
     */
    private String keywords;
    
    /**
     * 自定义数据
     */
    @JsonProperty("customdata")
    private String customdata;
    
    /**
     * 地理位置信息
     */
    private Geo geo;
    
    /**
     * 数据段信息
     */
    private List<Data> data;
    
    /**
     * 扩展字段
     */
    private Object ext;
    
    /**
     * 数据段对象
     */
    @lombok.Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        /**
         * 数据提供商ID
         */
        private String id;
        
        /**
         * 数据提供商名称
         */
        private String name;
        
        /**
         * 数据段数组
         */
        private List<Segment> segment;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
    
    /**
     * 数据段对象
     */
    @lombok.Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Segment {
        /**
         * 段ID
         */
        private String id;
        
        /**
         * 段名称
         */
        private String name;
        
        /**
         * 段值
         */
        private String value;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
}