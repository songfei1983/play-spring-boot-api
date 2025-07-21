package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 设备信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    
    /**
     * 用户代理字符串
     */
    @JsonProperty("ua")
    private String ua;
    
    /**
     * 地理位置信息
     */
    private Geo geo;
    
    /**
     * 是否支持Do Not Track (0=不支持, 1=支持)
     */
    @JsonProperty("dnt")
    private Integer dnt;
    
    /**
     * 是否限制广告跟踪 (0=不限制, 1=限制)
     */
    @JsonProperty("lmt")
    private Integer lmt;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * IPv6地址
     */
    @JsonProperty("ipv6")
    private String ipv6;
    
    /**
     * 设备类型 (1=移动/平板, 2=PC, 3=联网电视, 4=手机, 5=平板, 6=联网设备, 7=机顶盒)
     */
    @JsonProperty("devicetype")
    private Integer devicetype;
    
    /**
     * 设备制造商
     */
    private String make;
    
    /**
     * 设备型号
     */
    private String model;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 操作系统版本
     */
    @JsonProperty("osv")
    private String osv;
    
    /**
     * 硬件版本
     */
    @JsonProperty("hwv")
    private String hwv;
    
    /**
     * 屏幕宽度(像素)
     */
    @JsonProperty("w")
    private Integer w;
    
    /**
     * 屏幕高度(像素)
     */
    @JsonProperty("h")
    private Integer h;
    
    /**
     * 每英寸像素数
     */
    @JsonProperty("ppi")
    private Integer ppi;
    
    /**
     * 像素比例
     */
    @JsonProperty("pxratio")
    private Double pxratio;
    
    /**
     * 是否支持JavaScript (0=不支持, 1=支持)
     */
    @JsonProperty("js")
    private Integer js;
    
    /**
     * 地理位置服务 (0=不支持, 1=支持)
     */
    @JsonProperty("geofetch")
    private Integer geofetch;
    
    /**
     * Flash版本
     */
    @JsonProperty("flashver")
    private String flashver;
    
    /**
     * 浏览器语言
     */
    private String language;
    
    /**
     * 运营商或ISP
     */
    private String carrier;
    
    /**
     * 移动运营商代码
     */
    @JsonProperty("mccmnc")
    private String mccmnc;
    
    /**
     * 网络连接类型
     */
    @JsonProperty("connectiontype")
    private Integer connectiontype;
    
    /**
     * 设备ID哈希(MD5)
     */
    @JsonProperty("ifa")
    private String ifa;
    
    /**
     * 设备ID哈希(SHA1)
     */
    @JsonProperty("didsha1")
    private String didsha1;
    
    /**
     * 设备ID哈希(MD5)
     */
    @JsonProperty("didmd5")
    private String didmd5;
    
    /**
     * 平台设备ID哈希(SHA1)
     */
    @JsonProperty("dpidsha1")
    private String dpidsha1;
    
    /**
     * 平台设备ID哈希(MD5)
     */
    @JsonProperty("dpidmd5")
    private String dpidmd5;
    
    /**
     * MAC地址哈希(SHA1)
     */
    @JsonProperty("macsha1")
    private String macsha1;
    
    /**
     * MAC地址哈希(MD5)
     */
    @JsonProperty("macmd5")
    private String macmd5;
    
    /**
     * 扩展字段
     */
    private Object ext;
}