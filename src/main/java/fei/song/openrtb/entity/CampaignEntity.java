package fei.song.openrtb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广告活动实体类
 * 映射到 MongoDB 的 campaigns 集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "campaigns")
@CompoundIndexes({
    @CompoundIndex(name = "idx_advertiser_status", def = "{'advertiserId': 1, 'status': 1}"),
    @CompoundIndex(name = "idx_status_start_end", def = "{'status': 1, 'schedule.startDate': 1, 'schedule.endDate': 1}"),
    @CompoundIndex(name = "idx_geo_device", def = "{'targeting.geo.countries': 1, 'targeting.device.deviceTypes': 1}"),
    @CompoundIndex(name = "idx_budget_bid", def = "{'budget.dailyBudget': 1, 'bidding.bidStrategy': 1}")
})
public class CampaignEntity {

    @Id
    private String id;

    /**
     * 广告活动ID
     */
    @Field("campaign_id")
    @Indexed(unique = true)
    private String campaignId;

    /**
     * 广告主ID
     */
    @Field("advertiser_id")
    @Indexed
    private String advertiserId;

    /**
     * 活动名称
     */
    @Field("name")
    private String name;

    /**
     * 活动状态：ACTIVE, PAUSED, COMPLETED, DRAFT
     */
    @Field("status")
    @Indexed
    private String status;

    /**
     * 预算设置
     */
    @Field("budget")
    private Budget budget;

    /**
     * 定向设置
     */
    @Field("targeting")
    private Targeting targeting;

    /**
     * 竞价设置
     */
    @Field("bidding")
    private Bidding bidding;

    /**
     * 创意信息
     */
    @Field("creatives")
    private List<Creative> creatives;

    /**
     * 频次控制
     */
    @Field("frequency_cap")
    private FrequencyCap frequencyCap;

    /**
     * 时间安排
     */
    @Field("schedule")
    private Schedule schedule;

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
     * 预算设置内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Budget {
        @Field("total_budget")
        private Long totalBudget; // 分
        
        @Field("daily_budget")
        private Long dailyBudget; // 分
        
        @Field("spent_budget")
        private Long spentBudget; // 分
        
        @Field("currency")
        private String currency;
    }

    /**
     * 定向设置内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Targeting {
        @Field("geo")
        private Geo geo;
        
        @Field("device")
        private Device device;
        
        @Field("audience")
        private Audience audience;
        
        @Field("time")
        private Time time;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geo {
        @Field("countries")
        private List<String> countries;
        
        @Field("regions")
        private List<String> regions;
        
        @Field("cities")
        private List<String> cities;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Device {
        @Field("device_types")
        private List<String> deviceTypes; // mobile, desktop, tablet
        
        @Field("operating_systems")
        private List<String> operatingSystems;
        
        @Field("browsers")
        private List<String> browsers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Audience {
        @Field("age_range")
        private AgeRange ageRange;
        
        @Field("genders")
        private List<String> genders;
        
        @Field("interests")
        private List<String> interests;
        
        @Field("behaviors")
        private List<String> behaviors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgeRange {
        @Field("min_age")
        private Integer minAge;
        
        @Field("max_age")
        private Integer maxAge;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Time {
        @Field("hours")
        private List<Integer> hours; // 0-23
        
        @Field("days_of_week")
        private List<Integer> daysOfWeek; // 1-7
        
        @Field("timezone")
        private String timezone;
    }

    /**
     * 竞价设置内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Bidding {
        @Field("bid_strategy")
        private String bidStrategy; // CPM, CPC, CPA
        
        @Field("max_bid")
        private Long maxBid; // 分
        
        @Field("target_cpm")
        private Long targetCpm; // 分
        
        @Field("bid_adjustment")
        private Double bidAdjustment;
    }

    /**
     * 创意信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Creative {
        @Field("creative_id")
        private String creativeId;
        
        @Field("name")
        private String name;
        
        @Field("type")
        private String type; // banner, video, native
        
        @Field("width")
        private Integer width;
        
        @Field("height")
        private Integer height;
        
        @Field("url")
        private String url;
        
        @Field("click_url")
        private String clickUrl;
        
        @Field("status")
        private String status;
    }

    /**
     * 频次控制内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrequencyCap {
        @Field("impressions")
        private Integer impressions;
        
        @Field("period")
        private String period; // hour, day, week, month
        
        @Field("enabled")
        private Boolean enabled;
    }

    /**
     * 时间安排内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Schedule {
        @Field("start_date")
        private LocalDateTime startDate;
        
        @Field("end_date")
        private LocalDateTime endDate;
        
        @Field("timezone")
        private String timezone;
    }
}