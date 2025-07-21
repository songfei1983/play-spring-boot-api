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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广告活动实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "campaigns")
@CompoundIndexes({
    @CompoundIndex(name = "schedule_idx", def = "{'schedule.start_date': 1, 'schedule.end_date': 1}")
})
public class CampaignEntity {

    @Id
    private String id;

    /**
     * 广告活动ID
     */
    @Indexed(unique = true)
    @Field("campaign_id")
    private String campaignId;

    /**
     * 广告主ID
     */
    @Indexed
    @Field("advertiser_id")
    private String advertiserId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动状态
     */
    @Indexed
    private String status; // active, paused, completed

    /**
     * 预算信息
     */
    private Budget budget;

    /**
     * 定向设置
     */
    private Targeting targeting;

    /**
     * 竞价设置
     */
    private Bidding bidding;

    /**
     * 创意信息
     */
    private List<Creative> creatives;

    /**
     * 频次控制
     */
    @Field("frequency_cap")
    private FrequencyCap frequencyCap;

    /**
     * 时间安排
     */
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
     * 创建者
     */
    @Field("created_by")
    private String createdBy;

    /**
     * 预算信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Budget {
        /**
         * 总预算
         */
        @Field("total_budget")
        private BigDecimal totalBudget;

        /**
         * 日预算
         */
        @Field("daily_budget")
        private BigDecimal dailyBudget;

        /**
         * 已花费总额
         */
        @Field("spent_total")
        private BigDecimal spentTotal;

        /**
         * 今日已花费
         */
        @Field("spent_today")
        private BigDecimal spentToday;

        /**
         * 货币
         */
        private String currency;
    }

    /**
     * 定向设置内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Targeting {
        /**
         * 地理定向
         */
        private GeoTargeting geo;

        /**
         * 设备定向
         */
        private DeviceTargeting device;

        /**
         * 受众定向
         */
        private AudienceTargeting audience;

        /**
         * 时间定向
         */
        private TimeTargeting time;
    }

    /**
     * 地理定向内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GeoTargeting {
        @Field("included_countries")
        private List<String> includedCountries;

        @Field("excluded_countries")
        private List<String> excludedCountries;

        @Field("included_regions")
        private List<String> includedRegions;

        @Field("included_cities")
        private List<String> includedCities;
    }

    /**
     * 设备定向内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceTargeting {
        @Field("device_types")
        private List<Integer> deviceTypes;

        @Field("operating_systems")
        private List<String> operatingSystems;

        private List<String> browsers;
    }

    /**
     * 受众定向内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AudienceTargeting {
        @Field("age_range")
        private AgeRange ageRange;

        private List<String> genders;

        private List<String> interests;
    }

    /**
     * 年龄范围内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AgeRange {
        private Integer min;
        private Integer max;
    }

    /**
     * 时间定向内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeTargeting {
        @Field("days_of_week")
        private List<Integer> daysOfWeek;

        @Field("hours_of_day")
        private List<Integer> hoursOfDay;
    }

    /**
     * 竞价设置内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bidding {
        @Field("bid_strategy")
        private String bidStrategy; // cpm, cpc, cpa

        @Field("max_bid")
        private BigDecimal maxBid;

        @Field("base_bid")
        private BigDecimal baseBid;

        @Field("bid_adjustments")
        private Map<String, Double> bidAdjustments;
    }

    /**
     * 创意信息内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Creative {
        @Field("creative_id")
        private String creativeId;

        private String format;

        private Integer width;

        private Integer height;

        private String html;

        @Field("click_url")
        private String clickUrl;

        @Field("impression_trackers")
        private List<String> impressionTrackers;

        @Field("click_trackers")
        private List<String> clickTrackers;
    }

    /**
     * 频次控制内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FrequencyCap {
        @Field("impressions_per_user_per_day")
        private Integer impressionsPerUserPerDay;

        @Field("impressions_per_user_per_hour")
        private Integer impressionsPerUserPerHour;
    }

    /**
     * 时间安排内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Schedule {
        @Field("start_date")
        private LocalDateTime startDate;

        @Field("end_date")
        private LocalDateTime endDate;

        private String timezone;
    }
}