package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 地理位置信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Geo {
    
    /**
     * 纬度 (-90.0 到 +90.0)
     */
    @JsonProperty("lat")
    private Double lat;
    
    /**
     * 经度 (-180.0 到 +180.0)
     */
    @JsonProperty("lon")
    private Double lon;
    
    /**
     * 位置类型 (1=GPS/定位服务, 2=IP地址, 3=用户提供)
     */
    private Integer type;
    
    /**
     * 精度估计(米)
     */
    private Integer accuracy;
    
    /**
     * 最后修复时间(UTC时间戳)
     */
    @JsonProperty("lastfix")
    private Integer lastfix;
    
    /**
     * IP服务或位置提供商
     */
    @JsonProperty("ipservice")
    private Integer ipservice;
    
    /**
     * 国家代码(ISO-3166-1-alpha-3)
     */
    private String country;
    
    /**
     * 地区代码(ISO-3166-2)
     */
    private String region;
    
    /**
     * 地区FIPS 10-4代码
     */
    @JsonProperty("regionfips104")
    private String regionfips104;
    
    /**
     * 谷歌metro代码
     */
    private String metro;
    
    /**
     * 城市名称
     */
    private String city;
    
    /**
     * 邮政编码
     */
    private String zip;
    
    /**
     * 本地时间UTC偏移量(分钟)
     */
    @JsonProperty("utcoffset")
    private Integer utcoffset;
    
    /**
     * 扩展字段
     */
    private Object ext;
}