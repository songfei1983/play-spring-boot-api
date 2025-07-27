package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 广告位对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Impression {
    
    /**
     * 广告位唯一标识
     */
    private String id;
    
    /**
     * 指标对象(用于计费)
     */
    private Metric metric;
    
    /**
     * 横幅广告对象
     */
    private Banner banner;
    
    /**
     * 视频广告对象
     */
    private Video video;
    
    /**
     * 音频广告对象
     */
    private Audio audio;
    
    /**
     * 原生广告对象
     */
    @JsonProperty("native")
    private Native nativeAd;
    
    /**
     * 私有市场交易对象
     */
    private Pmp pmp;
    
    /**
     * 显示管理器名称
     */
    @JsonProperty("displaymanager")
    private String displayManager;
    
    /**
     * 显示管理器版本
     */
    @JsonProperty("displaymanagerver")
    private String displayManagerVersion;
    
    /**
     * 广告位是否支持JavaScript
     */
    @JsonProperty("instl")
    private Integer interstitial;
    
    /**
     * 广告位标签名称
     */
    @JsonProperty("tagid")
    private String tagId;
    
    /**
     * 最低竞价价格
     */
    @JsonProperty("bidfloor")
    private Double bidfloor;
    
    /**
     * 底价货币，默认USD
     */
    @JsonProperty("bidfloorcur")
    private String bidfloorCurrency;
    
    /**
     * 点击计费标识
     */
    @JsonProperty("clickbrowser")
    private Integer clickBrowser;
    
    /**
     * 安全标识 (0=非安全, 1=安全)
     */
    private Integer secure;
    
    /**
     * iframe友好标识
     */
    @JsonProperty("iframebuster")
    private List<String> iframeBuster;
    
    /**
     * 预期曝光量
     */
    @JsonProperty("exp")
    private Integer expectedExposure;
    
    /**
     * 扩展字段
     */
    private Object ext;
}