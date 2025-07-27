package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网站信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Site {
    
    /**
     * 交换特定站点ID
     */
    private String id;
    
    /**
     * 站点名称
     */
    private String name;
    
    /**
     * 站点域名
     */
    private String domain;
    
    /**
     * 内容类别数组
     */
    @JsonProperty("cat")
    private List<String> cat;
    
    /**
     * 内容子类别数组
     */
    @JsonProperty("sectioncat")
    private List<String> sectioncat;
    
    /**
     * 页面子类别数组
     */
    @JsonProperty("pagecat")
    private List<String> pagecat;
    
    /**
     * 当前页面URL
     */
    private String page;
    
    /**
     * 引用页面URL
     */
    @JsonProperty("ref")
    private String ref;
    
    /**
     * 搜索字符串
     */
    private String search;
    
    /**
     * 移动优化 (0=否, 1=是)
     */
    private Integer mobile;
    
    /**
     * 是否私有拍卖 (0=否, 1=是)
     */
    @JsonProperty("privacypolicy")
    private Integer privacypolicy;
    
    /**
     * 发布者信息
     */
    private Publisher publisher;
    
    /**
     * 内容信息
     */
    private Content content;
    
    /**
     * 关键词
     */
    private String keywords;
    
    /**
     * 扩展字段
     */
    private Object ext;
}