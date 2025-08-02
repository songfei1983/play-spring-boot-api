package fei.song.play_spring_boot_api.ads.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 广告活动数据传输对象
 * 用于前后端数据交换，提供扁平化的数据结构
 */
public class CampaignDTO {
    private String id;
    private String campaignId;
    private String advertiserId;
    private String name;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Budget fields
    private Double totalBudget;
    private Double dailyBudget;
    private Double spentTotal;
    private Double spentToday;
    private String currency;
    
    // Targeting fields
    private List<String> includedCountries;
    private List<String> excludedCountries;
    private List<String> includedRegions;
    private List<String> includedCities;
    private List<Integer> deviceTypes;
    private List<String> operatingSystems;
    private List<String> browsers;
    private Integer minAge;
    private Integer maxAge;
    private List<String> genders;
    private List<String> interests;
    private List<Integer> daysOfWeek;
    private List<Integer> hoursOfDay;
    
    // Bidding fields
    private String bidStrategy;
    private Double maxBid;
    private Double baseBid;
    
    // Creative fields
    private String creativeId;
    private String format;
    private Integer width;
    private Integer height;
    private String html;
    private String clickUrl;
    private List<String> impressionTrackers;
    private List<String> clickTrackers;
    
    // Frequency Cap fields
    private Integer impressionsPerUserPerDay;
    private Integer impressionsPerUserPerHour;
    
    // Schedule fields
    private String startDate;
    private String endDate;
    private String timezone;
    
    // Constructors
    public CampaignDTO() {}
    
    /**
     * 从CampaignEntity转换为CampaignDTO
     */
    public static CampaignDTO fromEntity(CampaignEntity entity) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(entity.getId());
        dto.setCampaignId(entity.getCampaignId());
        dto.setAdvertiserId(entity.getAdvertiserId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Budget
        if (entity.getBudget() != null) {
            dto.setTotalBudget(entity.getBudget().getTotalBudget() != null ? entity.getBudget().getTotalBudget().doubleValue() : null);
            dto.setDailyBudget(entity.getBudget().getDailyBudget() != null ? entity.getBudget().getDailyBudget().doubleValue() : null);
            dto.setSpentTotal(entity.getBudget().getSpentTotal() != null ? entity.getBudget().getSpentTotal().doubleValue() : null);
            dto.setSpentToday(entity.getBudget().getSpentToday() != null ? entity.getBudget().getSpentToday().doubleValue() : null);
            dto.setCurrency(entity.getBudget().getCurrency());
        }
        
        // Targeting
        if (entity.getTargeting() != null) {
            CampaignEntity.Targeting targeting = entity.getTargeting();
            if (targeting.getGeo() != null) {
                dto.setIncludedCountries(targeting.getGeo().getIncludedCountries());
                dto.setExcludedCountries(targeting.getGeo().getExcludedCountries());
                dto.setIncludedRegions(targeting.getGeo().getIncludedRegions());
                dto.setIncludedCities(targeting.getGeo().getIncludedCities());
            }
            if (targeting.getDevice() != null) {
                dto.setDeviceTypes(targeting.getDevice().getDeviceTypes());
                dto.setOperatingSystems(targeting.getDevice().getOperatingSystems());
                dto.setBrowsers(targeting.getDevice().getBrowsers());
            }
            if (targeting.getAudience() != null) {
                if (targeting.getAudience().getAgeRange() != null) {
                    dto.setMinAge(targeting.getAudience().getAgeRange().getMin());
                    dto.setMaxAge(targeting.getAudience().getAgeRange().getMax());
                }
                dto.setGenders(targeting.getAudience().getGenders());
                dto.setInterests(targeting.getAudience().getInterests());
            }
            if (targeting.getTime() != null) {
                dto.setDaysOfWeek(targeting.getTime().getDaysOfWeek());
                dto.setHoursOfDay(targeting.getTime().getHoursOfDay());
            }
        }
        
        // Bidding
        if (entity.getBidding() != null) {
            dto.setBidStrategy(entity.getBidding().getBidStrategy());
            dto.setMaxBid(entity.getBidding().getMaxBid() != null ? entity.getBidding().getMaxBid().doubleValue() : null);
            dto.setBaseBid(entity.getBidding().getBaseBid() != null ? entity.getBidding().getBaseBid().doubleValue() : null);
        }
        
        // Creative (取第一个)
        if (entity.getCreatives() != null && !entity.getCreatives().isEmpty()) {
            CampaignEntity.Creative creative = entity.getCreatives().get(0);
            dto.setCreativeId(creative.getCreativeId());
            dto.setFormat(creative.getFormat());
            dto.setWidth(creative.getWidth());
            dto.setHeight(creative.getHeight());
            dto.setHtml(creative.getHtml());
            dto.setClickUrl(creative.getClickUrl());
            dto.setImpressionTrackers(creative.getImpressionTrackers());
            dto.setClickTrackers(creative.getClickTrackers());
        }
        
        // Frequency Cap
        if (entity.getFrequencyCap() != null) {
            dto.setImpressionsPerUserPerDay(entity.getFrequencyCap().getImpressionsPerUserPerDay());
            dto.setImpressionsPerUserPerHour(entity.getFrequencyCap().getImpressionsPerUserPerHour());
        }
        
        // Schedule
        if (entity.getSchedule() != null) {
            dto.setStartDate(entity.getSchedule().getStartDate() != null ? entity.getSchedule().getStartDate().toString() : null);
            dto.setEndDate(entity.getSchedule().getEndDate() != null ? entity.getSchedule().getEndDate().toString() : null);
            dto.setTimezone(entity.getSchedule().getTimezone());
        }
        
        return dto;
    }
    
    /**
     * 转换为CampaignEntity
     */
    public CampaignEntity toEntity() {
        CampaignEntity entity = new CampaignEntity();
        entity.setId(this.id);
        entity.setCampaignId(this.campaignId);
        entity.setAdvertiserId(this.advertiserId);
        entity.setName(this.name);
        entity.setStatus(this.status);
        entity.setCreatedBy(this.createdBy);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        
        // Budget
        if (this.totalBudget != null || this.dailyBudget != null || this.currency != null) {
            CampaignEntity.Budget budget = new CampaignEntity.Budget();
            budget.setTotalBudget(this.totalBudget != null ? BigDecimal.valueOf(this.totalBudget) : null);
            budget.setDailyBudget(this.dailyBudget != null ? BigDecimal.valueOf(this.dailyBudget) : null);
            budget.setSpentTotal(this.spentTotal != null ? BigDecimal.valueOf(this.spentTotal) : null);
            budget.setSpentToday(this.spentToday != null ? BigDecimal.valueOf(this.spentToday) : null);
            budget.setCurrency(this.currency);
            entity.setBudget(budget);
        }
        
        // Targeting
        CampaignEntity.Targeting targeting = new CampaignEntity.Targeting();
        
        // Geo targeting
        if (this.includedCountries != null || this.excludedCountries != null || 
            this.includedRegions != null || this.includedCities != null) {
            CampaignEntity.GeoTargeting geo = new CampaignEntity.GeoTargeting();
            geo.setIncludedCountries(this.includedCountries);
            geo.setExcludedCountries(this.excludedCountries);
            geo.setIncludedRegions(this.includedRegions);
            geo.setIncludedCities(this.includedCities);
            targeting.setGeo(geo);
        }
        
        // Device targeting
        if (this.deviceTypes != null || this.operatingSystems != null || this.browsers != null) {
            CampaignEntity.DeviceTargeting device = new CampaignEntity.DeviceTargeting();
            device.setDeviceTypes(this.deviceTypes);
            device.setOperatingSystems(this.operatingSystems);
            device.setBrowsers(this.browsers);
            targeting.setDevice(device);
        }
        
        // Audience targeting
        if (this.minAge != null || this.maxAge != null || this.genders != null || this.interests != null) {
            CampaignEntity.AudienceTargeting audience = new CampaignEntity.AudienceTargeting();
            if (this.minAge != null || this.maxAge != null) {
                CampaignEntity.AgeRange ageRange = new CampaignEntity.AgeRange();
                ageRange.setMin(this.minAge);
                ageRange.setMax(this.maxAge);
                audience.setAgeRange(ageRange);
            }
            audience.setGenders(this.genders);
            audience.setInterests(this.interests);
            targeting.setAudience(audience);
        }
        
        // Time targeting
        if (this.daysOfWeek != null || this.hoursOfDay != null) {
            CampaignEntity.TimeTargeting time = new CampaignEntity.TimeTargeting();
            time.setDaysOfWeek(this.daysOfWeek);
            time.setHoursOfDay(this.hoursOfDay);
            targeting.setTime(time);
        }
        
        entity.setTargeting(targeting);
        
        // Bidding
        if (this.bidStrategy != null || this.maxBid != null || this.baseBid != null) {
            CampaignEntity.Bidding bidding = new CampaignEntity.Bidding();
            bidding.setBidStrategy(this.bidStrategy);
            bidding.setMaxBid(this.maxBid != null ? BigDecimal.valueOf(this.maxBid) : null);
            bidding.setBaseBid(this.baseBid != null ? BigDecimal.valueOf(this.baseBid) : null);
            entity.setBidding(bidding);
        }
        
        // Creative
        if (this.creativeId != null || this.format != null || this.html != null) {
            CampaignEntity.Creative creative = new CampaignEntity.Creative();
            creative.setCreativeId(this.creativeId);
            creative.setFormat(this.format);
            creative.setWidth(this.width);
            creative.setHeight(this.height);
            creative.setHtml(this.html);
            creative.setClickUrl(this.clickUrl);
            creative.setImpressionTrackers(this.impressionTrackers);
            creative.setClickTrackers(this.clickTrackers);
            entity.setCreatives(List.of(creative));
        }
        
        // Frequency Cap
        if (this.impressionsPerUserPerDay != null || this.impressionsPerUserPerHour != null) {
            CampaignEntity.FrequencyCap frequencyCap = new CampaignEntity.FrequencyCap();
            frequencyCap.setImpressionsPerUserPerDay(this.impressionsPerUserPerDay);
            frequencyCap.setImpressionsPerUserPerHour(this.impressionsPerUserPerHour);
            entity.setFrequencyCap(frequencyCap);
        }
        
        // Schedule
        if (this.startDate != null || this.endDate != null || this.timezone != null) {
            CampaignEntity.Schedule schedule = new CampaignEntity.Schedule();
            schedule.setStartDate(this.startDate != null ? LocalDateTime.parse(this.startDate) : null);
            schedule.setEndDate(this.endDate != null ? LocalDateTime.parse(this.endDate) : null);
            schedule.setTimezone(this.timezone);
            entity.setSchedule(schedule);
        }
        
        return entity;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCampaignId() { return campaignId; }
    public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
    
    public String getAdvertiserId() { return advertiserId; }
    public void setAdvertiserId(String advertiserId) { this.advertiserId = advertiserId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }
    
    public Double getDailyBudget() { return dailyBudget; }
    public void setDailyBudget(Double dailyBudget) { this.dailyBudget = dailyBudget; }
    
    public Double getSpentTotal() { return spentTotal; }
    public void setSpentTotal(Double spentTotal) { this.spentTotal = spentTotal; }
    
    public Double getSpentToday() { return spentToday; }
    public void setSpentToday(Double spentToday) { this.spentToday = spentToday; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public List<String> getIncludedCountries() { return includedCountries; }
    public void setIncludedCountries(List<String> includedCountries) { this.includedCountries = includedCountries; }
    
    public List<String> getExcludedCountries() { return excludedCountries; }
    public void setExcludedCountries(List<String> excludedCountries) { this.excludedCountries = excludedCountries; }
    
    public List<String> getIncludedRegions() { return includedRegions; }
    public void setIncludedRegions(List<String> includedRegions) { this.includedRegions = includedRegions; }
    
    public List<String> getIncludedCities() { return includedCities; }
    public void setIncludedCities(List<String> includedCities) { this.includedCities = includedCities; }
    
    public List<Integer> getDeviceTypes() { return deviceTypes; }
    public void setDeviceTypes(List<Integer> deviceTypes) { this.deviceTypes = deviceTypes; }
    
    public List<String> getOperatingSystems() { return operatingSystems; }
    public void setOperatingSystems(List<String> operatingSystems) { this.operatingSystems = operatingSystems; }
    
    public List<String> getBrowsers() { return browsers; }
    public void setBrowsers(List<String> browsers) { this.browsers = browsers; }
    
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    
    public List<String> getGenders() { return genders; }
    public void setGenders(List<String> genders) { this.genders = genders; }
    
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    
    public List<Integer> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(List<Integer> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    
    public List<Integer> getHoursOfDay() { return hoursOfDay; }
    public void setHoursOfDay(List<Integer> hoursOfDay) { this.hoursOfDay = hoursOfDay; }
    
    public String getBidStrategy() { return bidStrategy; }
    public void setBidStrategy(String bidStrategy) { this.bidStrategy = bidStrategy; }
    
    public Double getMaxBid() { return maxBid; }
    public void setMaxBid(Double maxBid) { this.maxBid = maxBid; }
    
    public Double getBaseBid() { return baseBid; }
    public void setBaseBid(Double baseBid) { this.baseBid = baseBid; }
    
    public String getCreativeId() { return creativeId; }
    public void setCreativeId(String creativeId) { this.creativeId = creativeId; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public String getHtml() { return html; }
    public void setHtml(String html) { this.html = html; }
    
    public String getClickUrl() { return clickUrl; }
    public void setClickUrl(String clickUrl) { this.clickUrl = clickUrl; }
    
    public List<String> getImpressionTrackers() { return impressionTrackers; }
    public void setImpressionTrackers(List<String> impressionTrackers) { this.impressionTrackers = impressionTrackers; }
    
    public List<String> getClickTrackers() { return clickTrackers; }
    public void setClickTrackers(List<String> clickTrackers) { this.clickTrackers = clickTrackers; }
    
    public Integer getImpressionsPerUserPerDay() { return impressionsPerUserPerDay; }
    public void setImpressionsPerUserPerDay(Integer impressionsPerUserPerDay) { this.impressionsPerUserPerDay = impressionsPerUserPerDay; }
    
    public Integer getImpressionsPerUserPerHour() { return impressionsPerUserPerHour; }
    public void setImpressionsPerUserPerHour(Integer impressionsPerUserPerHour) { this.impressionsPerUserPerHour = impressionsPerUserPerHour; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}