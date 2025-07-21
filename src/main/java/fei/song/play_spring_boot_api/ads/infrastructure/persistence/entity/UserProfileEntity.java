package fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户画像实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_profiles")
@CompoundIndexes({
    @CompoundIndex(name = "user_geo_idx", def = "{'user_id': 1, 'demographics.geo.country': 1}")
})
public class UserProfileEntity {

    @Id
    private String id;

    /**
     * 用户ID
     */
    @Indexed(unique = true)
    @Field("user_id")
    private String userId;

    /**
     * 人口统计信息
     */
    private Demographics demographics;

    /**
     * 兴趣标签
     */
    private List<Interest> interests;

    /**
     * 行为数据
     */
    private Behavior behavior;

    /**
     * 设备信息
     */
    @Field("device_info")
    private DeviceInfo deviceInfo;

    /**
     * 频次控制数据
     */
    @Field("frequency_data")
    private FrequencyData frequencyData;

    /**
     * 过期时间(TTL)
     */
    @Indexed(expireAfterSeconds = 0)
    @Field("expires_at")
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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Demographics {
        private Integer age;
        private String gender;
        private Geo geo;
        private String language;
    }

    /**
     * 地理信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Geo {
        private String country;
        private String region;
        private String city;
        private String zip;
        private Double lat;
        private Double lon;
    }

    /**
     * 兴趣标签内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Interest {
        private String category;
        private String subcategory;
        private Double score;
        @Field("last_updated")
        private LocalDateTime lastUpdated;
    }

    /**
     * 行为数据内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Behavior {
        @Field("page_views")
        private List<PageView> pageViews;

        @Field("ad_interactions")
        private List<AdInteraction> adInteractions;

        @Field("purchase_history")
        private List<Purchase> purchaseHistory;

        @Field("session_data")
        private SessionData sessionData;
    }

    /**
     * 页面浏览内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageView {
        private String url;
        private String category;
        private LocalDateTime timestamp;
        @Field("time_spent")
        private Long timeSpent;
    }

    /**
     * 广告交互内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdInteraction {
        @Field("ad_id")
        private String adId;
        @Field("campaign_id")
        private String campaignId;
        private String action; // impression, click, conversion
        private LocalDateTime timestamp;
        private String placement;
    }

    /**
     * 购买历史内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Purchase {
        @Field("product_id")
        private String productId;
        private String category;
        private Double amount;
        private String currency;
        private LocalDateTime timestamp;
    }

    /**
     * 会话数据内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionData {
        @Field("session_count")
        private Integer sessionCount;
        @Field("avg_session_duration")
        private Long avgSessionDuration;
        @Field("last_session")
        private LocalDateTime lastSession;
        @Field("first_session")
        private LocalDateTime firstSession;
    }

    /**
     * 设备信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceInfo {
        @Field("device_type")
        private Integer deviceType;
        @Field("operating_system")
        private String operatingSystem;
        private String browser;
        @Field("user_agent")
        private String userAgent;
        @Field("screen_resolution")
        private String screenResolution;
    }

    /**
     * 频次控制数据内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FrequencyData {
        @Field("daily_impressions")
        private Map<String, Integer> dailyImpressions; // date -> count
        @Field("hourly_impressions")
        private Map<String, Integer> hourlyImpressions; // hour -> count
        @Field("campaign_impressions")
        private Map<String, Integer> campaignImpressions; // campaign_id -> count
    }
}