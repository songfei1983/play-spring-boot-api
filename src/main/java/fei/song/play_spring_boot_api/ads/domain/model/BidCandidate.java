package fei.song.play_spring_boot_api.ads.domain.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 竞价候选对象 - 内部使用
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidCandidate {
    
    /**
     * 广告位ID
     */
    private String impressionId;
    
    /**
     * 广告ID
     */
    private String adId;
    
    /**
     * 创意ID
     */
    private String creativeId;
    
    /**
     * 广告活动ID
     */
    private String campaignId;
    
    /**
     * 竞价价格
     */
    private Double bidPrice;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 质量分数
     */
    private Double qualityScore;
    
    /**
     * 广告标记/创意内容
     */
    private String adMarkup;
    
    /**
     * 广告域名
     */
    private List<String> advertiserDomains;
    
    /**
     * 广告类别
     */
    private List<String> categories;
    
    /**
     * 广告宽度
     */
    private Integer width;
    
    /**
     * 广告高度
     */
    private Integer height;
    
    /**
     * 通知URL
     */
    private String notificationUrl;
    
    /**
     * 点击URL
     */
    private String clickUrl;
    
    /**
     * 展示URL
     */
    private String impressionUrl;
    
    /**
     * 交易ID
     */
    private String dealId;
    
    /**
     * 座位ID
     */
    private String seatId;
    
    /**
     * 用户价值分数
     */
    private Double userValueScore;
    
    /**
     * 上下文相关性分数
     */
    private Double contextRelevanceScore;
    
    /**
     * 竞争强度分数
     */
    private Double competitionScore;
    
    /**
     * 最终得分
     */
    private Double finalScore;
    
    /**
     * 是否通过反欺诈检测
     */
    private Boolean passedFraudCheck;
    
    /**
     * 预算检查结果
     */
    private Boolean budgetAvailable;
    
    /**
     * 定向匹配结果
     */
    private Boolean targetingMatched;
    
    /**
     * 扩展属性
     */
    private Object extensions;
}