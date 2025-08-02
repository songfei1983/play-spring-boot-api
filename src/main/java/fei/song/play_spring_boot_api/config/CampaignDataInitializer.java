package fei.song.play_spring_boot_api.config;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 广告活动数据初始化器
 * 在应用启动时初始化示例广告活动数据
 */
@Component
public class CampaignDataInitializer implements CommandLineRunner {

    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public void run(String... args) throws Exception {
        // 清空现有数据并重新初始化
        campaignRepository.deleteAll();
        initializeCampaignData();
    }

    private void initializeCampaignData() {
        // 创建示例广告活动数据
        CampaignEntity campaign1 = createCampaign(
            "campaign_001",
            "advertiser_001", 
            "夏季促销活动",
            "ACTIVE",
            new BigDecimal("10000.00"),
            new BigDecimal("500.00"),
            new BigDecimal("2.50")
        );

        CampaignEntity campaign2 = createCampaign(
            "campaign_002",
            "advertiser_002",
            "新品发布推广", 
            "PAUSED",
            new BigDecimal("15000.00"),
            new BigDecimal("750.00"),
            new BigDecimal("3.00")
        );

        CampaignEntity campaign3 = createCampaign(
            "campaign_003",
            "advertiser_001",
            "品牌宣传活动",
            "ACTIVE", 
            new BigDecimal("8000.00"),
            new BigDecimal("400.00"),
            new BigDecimal("2.00")
        );

        // 添加测试所需的活动
        CampaignEntity editTestCampaign = createCampaign(
            "EDIT001",
            "ADV003",
            "待编辑广告活动",
            "ACTIVE",
            new BigDecimal("8000.00"),
            new BigDecimal("800.00"),
            new BigDecimal("1.0")
        );

        // 添加包含ADV001的测试活动
        CampaignEntity adv001Campaign = createCampaign(
            "TEST001",
            "ADV001",
            "ADV001测试活动",
            "ACTIVE",
            new BigDecimal("5000.00"),
            new BigDecimal("500.00"),
            new BigDecimal("1.5")
        );

        // 保存到数据库
        campaignRepository.saveAll(Arrays.asList(campaign1, campaign2, campaign3, editTestCampaign, adv001Campaign));
        
        System.out.println("已初始化 " + campaignRepository.count() + " 个示例广告活动");
    }

    private CampaignEntity createCampaign(String campaignId, String advertiserId, String name, 
                                        String status, BigDecimal totalBudget, BigDecimal dailyBudget, 
                                        BigDecimal maxBid) {
        CampaignEntity campaign = new CampaignEntity();
        campaign.setCampaignId(campaignId);
        campaign.setAdvertiserId(advertiserId);
        campaign.setName(name);
        campaign.setStatus(status);
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());
        campaign.setCreatedBy("system");
        
        // 设置预算信息
        CampaignEntity.Budget budget = new CampaignEntity.Budget();
        budget.setTotalBudget(totalBudget);
        budget.setDailyBudget(dailyBudget);
        budget.setSpentTotal(BigDecimal.ZERO);
        budget.setSpentToday(BigDecimal.ZERO);
        campaign.setBudget(budget);
        
        // 设置竞价信息
        CampaignEntity.Bidding bidding = new CampaignEntity.Bidding();
        bidding.setBidStrategy("CPC");
        bidding.setMaxBid(maxBid);
        bidding.setBaseBid(new BigDecimal("2.00"));
        campaign.setBidding(bidding);
        
        // 设置定向信息
        CampaignEntity.Targeting targeting = new CampaignEntity.Targeting();
        
        // 地理定向
        CampaignEntity.GeoTargeting geoTargeting = new CampaignEntity.GeoTargeting();
        geoTargeting.setIncludedCountries(Arrays.asList("CN"));
        geoTargeting.setIncludedCities(Arrays.asList("北京", "上海", "广州", "深圳"));
        targeting.setGeo(geoTargeting);
        
        // 设备定向
        CampaignEntity.DeviceTargeting deviceTargeting = new CampaignEntity.DeviceTargeting();
        deviceTargeting.setDeviceTypes(Arrays.asList(1, 2)); // 1: 手机, 2: 平板
        deviceTargeting.setOperatingSystems(Arrays.asList("iOS", "Android"));
        targeting.setDevice(deviceTargeting);
        
        campaign.setTargeting(targeting);
        
        // 设置时间安排
        CampaignEntity.Schedule schedule = new CampaignEntity.Schedule();
        schedule.setStartDate(LocalDateTime.now().minusDays(1));
        schedule.setEndDate(LocalDateTime.now().plusDays(30));
        schedule.setTimezone("Asia/Shanghai");
        campaign.setSchedule(schedule);
        
        // 设置创意信息
        CampaignEntity.Creative creative = new CampaignEntity.Creative();
        creative.setCreativeId(campaignId + "_creative_001");
        creative.setFormat("banner");
        creative.setWidth(300);
        creative.setHeight(250);
        creative.setHtml("<div>" + name + "</div>");
        creative.setClickUrl("https://example.com/landing");
        campaign.setCreatives(Arrays.asList(creative));
        
        // 设置频次控制
        CampaignEntity.FrequencyCap frequencyCap = new CampaignEntity.FrequencyCap();
        frequencyCap.setImpressionsPerUserPerDay(5);
        frequencyCap.setImpressionsPerUserPerHour(2);
        campaign.setFrequencyCap(frequencyCap);
        
        return campaign;
    }
}