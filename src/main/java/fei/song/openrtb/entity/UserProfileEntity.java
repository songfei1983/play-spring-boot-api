package fei.song.openrtb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户画像实体类
 * 映射到 MongoDB 的 user_profiles 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_profiles")
@CompoundIndexes({
    @CompoundIndex(name = "idx_demographics", def = "{'demographics.age': 1, 'demographics.gender': 1, 'demographics.country': 1}"),
    @CompoundIndex(name = "idx_device_os", def = "{'deviceInfo.deviceType': 1, 'deviceInfo.os': 1}"),
    @CompoundIndex(name = "idx_interests_behavior", def = "{'interests.categories': 1, 'behaviorData.activityLevel': 1}"),
    @CompoundIndex(name = "idx_expires_at", def = "{'expiresAt': 1}")
})
public class UserProfileEntity {

    @Id
    private String id;

    /**
     * 用户ID（可以是设备ID、Cookie ID等）
     */
    @Field("user_id")
    @Indexed(unique = true)
    private String userId;

    /**
     * 人口统计信息
     */
    @Field("demographics")
    private Demographics demographics;

    /**
     * 兴趣标签
     */
    @Field("interests")
    private Interests interests;

    /**
     * 行为数据
     */
    @Field("behavior_data")
    private BehaviorData behaviorData;

    /**
     * 设备信息
     */
    @Field("device_info")
    private DeviceInfo deviceInfo;

    /**
     * 地理位置信息
     */
    @Field("location")
    private Location location;

    /**
     * 频次控制数据
     */
    @Field("frequency_data")
    private Map<String, FrequencyData> frequencyData;

    /**
     * 过期时间（TTL索引）
     */
    @Field("expires_at")
    @Indexed(expireAfterSeconds = 0)
    private LocalDateTime expiresAt;

    /**
     * 创建时间
     */
    @Field("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 人口统计信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Demographics {
        @Field("age")
        private Integer age;
        
        @Field("gender")
        private String gender; // M, F, U
        
        @Field("country")
        private String country;
        
        @Field("region")
        private String region;
        
        @Field("city")
        private String city;
        
        @Field("language")
        private String language;
        
        @Field("income_level")
        private String incomeLevel; // LOW, MEDIUM, HIGH
        
        @Field("education_level")
        private String educationLevel;
        
        @Field("marital_status")
        private String maritalStatus;
    }

    /**
     * 兴趣标签内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Interests {
        @Field("categories")
        private List<String> categories;
        
        @Field("brands")
        private List<String> brands;
        
        @Field("keywords")
        private List<String> keywords;
        
        @Field("topics")
        private List<String> topics;
        
        @Field("confidence_scores")
        private Map<String, Double> confidenceScores;
    }

    /**
     * 行为数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BehaviorData {
        @Field("page_views")
        private Integer pageViews;
        
        @Field("session_duration")
        private Long sessionDuration; // 秒
        
        @Field("bounce_rate")
        private Double bounceRate;
        
        @Field("activity_level")
        private String activityLevel; // LOW, MEDIUM, HIGH
        
        @Field("purchase_history")
        private List<Purchase> purchaseHistory;
        
        @Field("ad_interactions")
        private List<AdInteraction> adInteractions;
        
        @Field("last_activity")
        private LocalDateTime lastActivity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Purchase {
        @Field("product_category")
        private String productCategory;
        
        @Field("amount")
        private Long amount; // 分
        
        @Field("currency")
        private String currency;
        
        @Field("purchase_date")
        private LocalDateTime purchaseDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdInteraction {
        @Field("campaign_id")
        private String campaignId;
        
        @Field("creative_id")
        private String creativeId;
        
        @Field("interaction_type")
        private String interactionType; // VIEW, CLICK, CONVERSION
        
        @Field("timestamp")
        private LocalDateTime timestamp;
    }

    /**
     * 设备信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceInfo {
        @Field("device_type")
        private String deviceType; // mobile, desktop, tablet
        
        @Field("os")
        private String os;
        
        @Field("os_version")
        private String osVersion;
        
        @Field("browser")
        private String browser;
        
        @Field("browser_version")
        private String browserVersion;
        
        @Field("screen_resolution")
        private String screenResolution;
        
        @Field("user_agent")
        private String userAgent;
    }

    /**
     * 地理位置信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {
        @Field("country")
        private String country;
        
        @Field("region")
        private String region;
        
        @Field("city")
        private String city;
        
        @Field("postal_code")
        private String postalCode;
        
        @Field("coordinates")
        @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
        private double[] coordinates; // [longitude, latitude]
        
        @Field("timezone")
        private String timezone;
    }

    /**
     * 频次控制数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrequencyData {
        @Field("impressions_today")
        private Integer impressionsToday;
        
        @Field("impressions_this_week")
        private Integer impressionsThisWeek;
        
        @Field("impressions_this_month")
        private Integer impressionsThisMonth;
        
        @Field("last_impression")
        private LocalDateTime lastImpression;
        
        @Field("clicks_today")
        private Integer clicksToday;
        
        @Field("last_click")
        private LocalDateTime lastClick;
    }
}