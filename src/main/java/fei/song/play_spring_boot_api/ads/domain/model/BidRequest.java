package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRTB竞价请求对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidRequest {
    
    /**
     * 竞价请求唯一标识
     */
    private String id;
    
    /**
     * 广告位列表
     */
    @JsonProperty("imp")
    private List<Impression> imp;
    
    /**
     * 网站信息
     */
    private Site site;
    
    /**
     * 应用信息
     */
    private App app;
    
    /**
     * 设备信息
     */
    private Device device;
    
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * 测试模式标识
     */
    private Integer test;
    
    /**
     * 拍卖类型 (1=第一价格, 2=第二价格)
     */
    @JsonProperty("at")
    private Integer auctionType;
    
    /**
     * 最大超时时间(毫秒)
     */
    @JsonProperty("tmax")
    private Integer timeoutMs;
    
    /**
     * 白名单座位ID
     */
    @JsonProperty("wseat")
    private List<String> whitelistedSeats;
    
    /**
     * 黑名单座位ID
     */
    @JsonProperty("bseat")
    private List<String> blacklistedSeats;
    
    /**
     * 全部广告位是否安全
     */
    @JsonProperty("allimps")
    private Integer allImpressions;
    
    /**
     * 支持的货币列表
     */
    @JsonProperty("cur")
    private List<String> currencies;
    
    /**
     * 白名单语言
     */
    @JsonProperty("wlang")
    private List<String> whitelistedLanguages;
    
    /**
     * 黑名单广告类别
     */
    @JsonProperty("bcat")
    private List<String> blacklistedCategories;
    
    /**
     * 黑名单广告主域名
     */
    @JsonProperty("badv")
    private List<String> blacklistedAdvertisers;
    
    /**
     * 黑名单应用
     */
    @JsonProperty("bapp")
    private List<String> blacklistedApps;
    
    /**
     * 来源信息
     */
    private Source source;
    
    /**
     * 规则信息
     */
    private Regs regs;
    
    /**
     * 扩展字段
     */
    private Object ext;
}