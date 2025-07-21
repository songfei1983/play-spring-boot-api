package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 视频广告对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    
    /**
     * 支持的MIME类型
     */
    private List<String> mimes;
    
    /**
     * 最小持续时间(秒)
     */
    @JsonProperty("minduration")
    private Integer minDuration;
    
    /**
     * 最大持续时间(秒)
     */
    @JsonProperty("maxduration")
    private Integer maxDuration;
    
    /**
     * 支持的协议数组
     */
    private List<Integer> protocols;
    
    /**
     * 协议白名单
     */
    @JsonProperty("protocol")
    private Integer protocol;
    
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
     * 视频开始延迟 (0=前贴片, >0=中贴片延迟秒数, -1=通用中贴片, -2=后贴片)
     */
    @JsonProperty("startdelay")
    private Integer startDelay;
    
    /**
     * 线性度 (1=线性/流内, 2=非线性/覆盖)
     */
    private Integer linearity;
    
    /**
     * 视频序列 (1=初始, 2=中间, 3=结束)
     */
    private Integer sequence;
    
    /**
     * 阻止的创意属性
     */
    @JsonProperty("battr")
    private List<Integer> blockedAttributes;
    
    /**
     * 最大扩展宽度
     */
    @JsonProperty("maxextended")
    private Integer maxExtended;
    
    /**
     * 最小比特率
     */
    @JsonProperty("minbitrate")
    private Integer minBitrate;
    
    /**
     * 最大比特率
     */
    @JsonProperty("maxbitrate")
    private Integer maxBitrate;
    
    /**
     * 盒装模式 (0=不允许, 1=允许)
     */
    @JsonProperty("boxingallowed")
    private Integer boxingAllowed;
    
    /**
     * 播放方法
     */
    @JsonProperty("playbackmethod")
    private List<Integer> playbackMethods;
    
    /**
     * 播放结束方法
     */
    @JsonProperty("playbackend")
    private Integer playbackEnd;
    
    /**
     * 投放方法
     */
    private List<Integer> delivery;
    
    /**
     * 广告位位置
     */
    private Integer pos;
    
    /**
     * 伴随横幅
     */
    @JsonProperty("companionad")
    private Banner companionAd;
    
    /**
     * 支持的API框架
     */
    private List<Integer> api;
    
    /**
     * 伴随类型
     */
    @JsonProperty("companiontype")
    private List<Integer> companionTypes;
    
    /**
     * 最大宽度
     */
    @JsonProperty("maxwidth")
    private Integer maxwidth;
    
    /**
     * 最大高度
     */
    @JsonProperty("maxheight")
    private Integer maxheight;
    
    /**
     * 最小宽度
     */
    @JsonProperty("minwidth")
    private Integer minwidth;
    
    /**
     * 最小高度
     */
    @JsonProperty("minheight")
    private Integer minheight;
    
    /**
     * 扩展字段
     */
    private Object ext;
}