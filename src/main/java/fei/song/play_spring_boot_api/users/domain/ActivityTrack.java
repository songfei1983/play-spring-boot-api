package fei.song.play_spring_boot_api.users.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户行动轨迹实体")
public class ActivityTrack {
    @Schema(description = "轨迹ID", example = "1")
    private Long id;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "活动类型", example = "登录", allowableValues = {"登录", "浏览", "搜索", "购买", "收藏", "评价", "分享"})
    private String activityType;
    
    @Schema(description = "活动描述", example = "用户浏览了商品详情页")
    private String description;
    
    @Schema(description = "经度", example = "116.404")
    private BigDecimal longitude;
    
    @Schema(description = "纬度", example = "39.915")
    private BigDecimal latitude;
    
    @Schema(description = "位置描述", example = "北京市朝阳区")
    private String location;
    
    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;
    
    @Schema(description = "设备类型", example = "手机", allowableValues = {"手机", "电脑", "平板", "其他"})
    private String deviceType;
    
    @Schema(description = "操作系统", example = "iOS 15.0")
    private String operatingSystem;
    
    @Schema(description = "浏览器", example = "Safari")
    private String browser;
    
    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;
    
    @Schema(description = "会话ID", example = "session123")
    private String sessionId;
    
    @Schema(description = "页面URL", example = "/products/123")
    private String pageUrl;
    
    @Schema(description = "引用页面", example = "/home")
    private String referrer;
    
    @Schema(description = "停留时长（秒）", example = "120")
    private Integer duration;
    
    @Schema(description = "额外数据（JSON格式）", example = "{\"productId\": 123}")
    private String extraData;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    // 自定义构造函数，用于设置默认创建时间
    public ActivityTrack(Long userId, String activityType, String description, 
                        BigDecimal longitude, BigDecimal latitude, String location) {
        this.userId = userId;
        this.activityType = activityType;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.createdAt = LocalDateTime.now();
    }
}