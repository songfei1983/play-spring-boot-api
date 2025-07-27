package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRTB音频广告对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Audio {
    
    /**
     * 支持的MIME类型列表
     */
    private List<String> mimes;
    
    /**
     * 最小音频时长(秒)
     */
    @JsonProperty("minduration")
    private Integer minDuration;
    
    /**
     * 最大音频时长(秒)
     */
    @JsonProperty("maxduration")
    private Integer maxDuration;
    
    /**
     * 支持的音频协议列表
     */
    private List<Integer> protocols;
    
    /**
     * 音频开始延迟类型
     */
    @JsonProperty("startdelay")
    private Integer startDelay;
    
    /**
     * 音频序列号
     */
    private Integer sequence;
    
    /**
     * 阻止的创意属性列表
     */
    @JsonProperty("battr")
    private List<Integer> blockedAttributes;
    
    /**
     * 最大扩展时长(秒)
     */
    @JsonProperty("maxextended")
    private Integer maxExtended;
    
    /**
     * 最小比特率(Kbps)
     */
    @JsonProperty("minbitrate")
    private Integer minBitrate;
    
    /**
     * 最大比特率(Kbps)
     */
    @JsonProperty("maxbitrate")
    private Integer maxBitrate;
    
    /**
     * 支持的交付方法列表
     */
    private List<Integer> delivery;
    
    /**
     * 伴随横幅对象
     */
    @JsonProperty("companionad")
    private List<Banner> companionAd;
    
    /**
     * 支持的API框架列表
     */
    private List<Integer> api;
    
    /**
     * 支持的伴随广告类型列表
     */
    @JsonProperty("companiontype")
    private List<Integer> companionType;
    
    /**
     * 最大序列号
     */
    @JsonProperty("maxseq")
    private Integer maxSequence;
    
    /**
     * 音频feed类型
     */
    private Integer feed;
    
    /**
     * 音频是否静音
     */
    private Integer stitched;
    
    /**
     * 音频音量标准化
     */
    @JsonProperty("nvol")
    private Integer normalizedVolume;
    
    /**
     * 扩展字段
     */
    private Object ext;
}