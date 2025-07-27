package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenRTB私有市场交易对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pmp {
    
    /**
     * 私有拍卖标识 (0=否, 1=是)
     */
    @JsonProperty("private_auction")
    private Integer privateAuction;
    
    /**
     * 交易对象列表
     */
    private List<Deal> deals;
    
    /**
     * 扩展字段
     */
    private Object ext;
    
    /**
     * 交易对象
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Deal {
        
        /**
         * 交易ID
         */
        private String id;
        
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
         * 拍卖类型 (1=第一价格, 2=第二价格, 3=固定价格)
         */
        @JsonProperty("at")
        private Integer auctionType;
        
        /**
         * 白名单座位ID
         */
        @JsonProperty("wseat")
        private List<String> whitelistedSeats;
        
        /**
         * 白名单广告主域名
         */
        @JsonProperty("wadomain")
        private List<String> whitelistedAdvertisers;
        
        /**
         * 扩展字段
         */
        private Object ext;
    }
}