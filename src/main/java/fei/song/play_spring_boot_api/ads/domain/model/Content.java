package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 内容信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    
    /**
     * 内容ID
     */
    private String id;
    
    /**
     * 内容集数
     */
    private Integer episode;
    
    /**
     * 内容标题
     */
    private String title;
    
    /**
     * 内容系列
     */
    private String series;
    
    /**
     * 内容季数
     */
    private String season;
    
    /**
     * 艺术家
     */
    private String artist;
    
    /**
     * 流派
     */
    private String genre;
    
    /**
     * 专辑
     */
    private String album;
    
    /**
     * 国际标准录音制品编码
     */
    private String isrc;
    
    /**
     * 制作者
     */
    private Producer producer;
    
    /**
     * 内容URL
     */
    private String url;
    
    /**
     * 内容类别数组
     */
    @JsonProperty("cat")
    private List<String> cat;
    
    /**
     * 制作质量
     */
    @JsonProperty("prodq")
    private Integer prodq;
    
    /**
     * 视频质量
     */
    @JsonProperty("videoquality")
    private Integer videoquality;
    
    /**
     * 内容上下文
     */
    private Integer context;
    
    /**
     * 内容评级
     */
    @JsonProperty("contentrating")
    private String contentrating;
    
    /**
     * 用户评级
     */
    @JsonProperty("userrating")
    private String userrating;
    
    /**
     * 媒体评级
     */
    @JsonProperty("qagmediarating")
    private Integer qagmediarating;
    
    /**
     * 关键词
     */
    private String keywords;
    
    /**
     * 直播流 (0=否, 1=是)
     */
    @JsonProperty("livestream")
    private Integer livestream;
    
    /**
     * 源关系 (0=间接, 1=直接)
     */
    @JsonProperty("sourcerelationship")
    private Integer sourcerelationship;
    
    /**
     * 内容长度(秒)
     */
    @JsonProperty("len")
    private Integer len;
    
    /**
     * 内容语言
     */
    private String language;
    
    /**
     * 是否可嵌入 (0=否, 1=是)
     */
    private Integer embeddable;
    
    /**
     * 数据段信息
     */
    private List<User.Data> data;
    
    /**
     * 扩展字段
     */
    private Object ext;
}