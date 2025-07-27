package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 竞价响应对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidResponse {
    
    /**
     * 竞价请求ID
     */
    private String id;
    
    /**
     * 座位竞价数组
     */
    @JsonProperty("seatbid")
    private List<SeatBid> seatbid;
    
    /**
     * 竞价ID
     */
    @JsonProperty("bidid")
    private String bidid;
    
    /**
     * 响应货币
     */
    @JsonProperty("cur")
    private String cur;
    
    /**
     * 自定义数据
     */
    @JsonProperty("customdata")
    private String customdata;
    
    /**
     * 无竞价原因
     */
    @JsonProperty("nbr")
    private Integer nbr;
    
    /**
     * 扩展字段
     */
    private Object ext;
    
    /**
     * 座位竞价对象
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatBid {
        /**
         * 竞价数组
         */
        private List<Bid> bid;
        
        /**
         * 座位ID
         */
        private String seat;
        
        /**
         * 组竞价标识 (0=否, 1=是)
         */
        private Integer group;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
    
    /**
     * 竞价对象
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bid {
        /**
         * 竞价ID
         */
        private String id;
        
        /**
         * 广告位ID
         */
        @JsonProperty("impid")
        private String impid;
        
        /**
         * 竞价价格
         */
        private Double price;
        
        /**
         * 广告ID
         */
        @JsonProperty("adid")
        private String adid;
        
        /**
         * 通知URL
         */
        @JsonProperty("nurl")
        private String nurl;
        
        /**
         * 损失通知URL
         */
        @JsonProperty("lurl")
        private String lurl;
        
        /**
         * 广告标记
         */
        @JsonProperty("adm")
        private String adm;
        
        /**
         * 广告域名数组
         */
        @JsonProperty("adomain")
        private List<String> adomain;
        
        /**
         * 包名
         */
        private String bundle;
        
        /**
         * 广告活动ID
         */
        @JsonProperty("cid")
        private String cid;
        
        /**
         * 创意ID
         */
        @JsonProperty("crid")
        private String crid;
        
        /**
         * 交易ID
         */
        @JsonProperty("dealid")
        private String dealid;
        
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
         * 创意属性数组
         */
        @JsonProperty("attr")
        private List<Integer> attr;
        
        /**
         * API框架
         */
        private Integer api;
        
        /**
         * 协议
         */
        private Integer protocol;
        
        /**
         * 质量评级
         */
        @JsonProperty("qagmediarating")
        private Integer qagmediarating;
        
        /**
         * 语言
         */
        private String language;
        
        /**
         * 广告类别数组
         */
        @JsonProperty("cat")
        private List<String> cat;
        
        /**
         * 到期时间(秒)
         */
        @JsonProperty("exp")
        private Integer exp;
        
        /**
         * 计费通知URL
         */
        @JsonProperty("burl")
        private String burl;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
}