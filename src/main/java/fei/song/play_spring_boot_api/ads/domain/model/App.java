package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRTB应用信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class App {
    
    /**
     * 应用ID
     */
    private String id;
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用商店URL
     */
    @JsonProperty("storeurl")
    private String storeUrl;
    
    /**
     * 应用域名
     */
    private String domain;
    
    /**
     * 应用类别列表 (IAB类别)
     */
    @JsonProperty("cat")
    private List<String> categories;
    
    /**
     * 应用子类别列表 (IAB类别)
     */
    @JsonProperty("sectioncat")
    private List<String> sectionCategories;
    
    /**
     * 页面类别列表 (IAB类别)
     */
    @JsonProperty("pagecat")
    private List<String> pageCategories;
    
    /**
     * 应用版本
     */
    @JsonProperty("ver")
    private String version;
    
    /**
     * 应用包名
     */
    @JsonProperty("bundle")
    private String bundle;
    
    /**
     * 是否付费应用 (0=免费, 1=付费)
     */
    private Integer paid;
    
    /**
     * 发布者信息
     */
    @JsonProperty("publisher")
    private Publisher publisher;
    
    /**
     * 内容信息
     */
    private Content content;
    
    /**
     * 应用关键词
     */
    private String keywords;
    
    /**
     * 隐私政策 (0=无, 1=有)
     */
    @JsonProperty("privacypolicy")
    private Integer privacyPolicy;
    
    /**
     * 扩展字段
     */
    private Object ext;
}