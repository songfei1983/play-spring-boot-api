package fei.song.openrtb.service;

import fei.song.openrtb.entity.*;
import fei.song.openrtb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * OpenRTB 数据服务类
 * 集成 MongoDB 持久化和 Redis 缓存的业务逻辑
 */
@Service
@Transactional
public class OpenRTBDataService {

    @Autowired
    private BidRequestRepository bidRequestRepository;

    @Autowired
    private BidResponseRepository bidResponseRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BidStatisticsRepository bidStatisticsRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== 竞价请求相关操作 ====================

    /**
     * 保存竞价请求
     */
    public BidRequestEntity saveBidRequest(BidRequestEntity bidRequest) {
        BidRequestEntity saved = bidRequestRepository.save(bidRequest);
        // 缓存热点数据
        String cacheKey = "bid_request:" + saved.getRequestId();
        redisTemplate.opsForValue().set(cacheKey, saved, 1, TimeUnit.HOURS);
        return saved;
    }

    /**
     * 根据请求ID获取竞价请求（带缓存）
     */
    @Cacheable(value = "bidRequests", key = "#requestId")
    public Optional<BidRequestEntity> getBidRequestById(String requestId) {
        return bidRequestRepository.findByRequestId(requestId);
    }

    /**
     * 获取指定时间范围内的竞价请求
     */
    public List<BidRequestEntity> getBidRequestsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return bidRequestRepository.findByTimestampBetween(startTime, endTime, Pageable.unpaged()).getContent();
    }

    /**
     * 获取处理超时的竞价请求
     */
    public List<BidRequestEntity> getTimeoutBidRequests(Long maxProcessingTime) {
        return bidRequestRepository.findByProcessingTimeGreaterThan(maxProcessingTime);
    }

    /**
     * 删除过期的竞价请求
     */
    @CacheEvict(value = "bidRequests", allEntries = true)
    public long deleteExpiredBidRequests(LocalDateTime expireTime) {
        bidRequestRepository.deleteByExpiresAtBefore(expireTime);
        return 0L; // 返回删除数量，实际应该从repository方法返回
    }

    // ==================== 竞价响应相关操作 ====================

    /**
     * 保存竞价响应
     */
    public BidResponseEntity saveBidResponse(BidResponseEntity bidResponse) {
        BidResponseEntity saved = bidResponseRepository.save(bidResponse);
        // 缓存获胜竞价
        if (saved.getBidResult() != null && saved.getBidResult().getWinningBidId() != null) {
            String cacheKey = "winning_bid:" + saved.getBidResult().getWinningBidId();
            redisTemplate.opsForValue().set(cacheKey, saved, 24, TimeUnit.HOURS);
        }
        return saved;
    }

    /**
     * 根据请求ID获取竞价响应
     */
    public List<BidResponseEntity> getBidResponsesByRequestId(String requestId) {
        return bidResponseRepository.findByRequestId(requestId);
    }

    /**
     * 获取获胜竞价响应
     */
    @Cacheable(value = "winningBids", key = "#winningBidId")
    public List<BidResponseEntity> getWinningBidResponses(String winningBidId) {
        return bidResponseRepository.findByWinningCampaignId(winningBidId);
    }

    /**
     * 统计指定时间范围内的竞价响应数量
     */
    public long countBidResponsesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return bidResponseRepository.countByTimestampBetween(startTime, endTime);
    }

    // ==================== 广告活动相关操作 ====================

    /**
     * 保存广告活动
     */
    @CachePut(value = "campaigns", key = "#campaign.campaignId")
    public CampaignEntity saveCampaign(CampaignEntity campaign) {
        return campaignRepository.save(campaign);
    }

    /**
     * 根据活动ID获取广告活动（带缓存）
     */
    @Cacheable(value = "campaigns", key = "#campaignId")
    public Optional<CampaignEntity> getCampaignById(String campaignId) {
        return campaignRepository.findByCampaignId(campaignId);
    }

    /**
     * 获取活跃的广告活动
     */
    @Cacheable(value = "activeCampaigns")
    public List<CampaignEntity> getActiveCampaigns() {
        return campaignRepository.findActiveCampaigns();
    }

    /**
     * 获取当前时间范围内活跃的活动
     */
    public List<CampaignEntity> getCurrentlyActiveCampaigns() {
        return campaignRepository.findCurrentlyActiveCampaigns(LocalDateTime.now());
    }

    /**
     * 获取预算即将耗尽的活动
     */
    public List<CampaignEntity> getCampaignsNearBudgetLimit() {
        return campaignRepository.findCampaignsNearBudgetLimit();
    }

    /**
     * 根据定向条件查找活动
     */
    public List<CampaignEntity> getCampaignsByTargeting(List<String> countries, List<String> deviceTypes, List<String> interests) {
        // 可以根据业务需求组合多个查询条件
        List<CampaignEntity> campaigns = campaignRepository.findByTargetingCountries(countries);
        // 进一步过滤
        return campaigns.stream()
                .filter(c -> c.getTargeting().getDevice().getDeviceTypes().stream().anyMatch(deviceTypes::contains))
                .filter(c -> c.getTargeting().getAudience().getInterests().stream().anyMatch(interests::contains))
                .toList();
    }

    // ==================== 用户画像相关操作 ====================

    /**
     * 保存用户画像
     */
    @CachePut(value = "userProfiles", key = "#userProfile.userId")
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfile) {
        return userProfileRepository.save(userProfile);
    }

    /**
     * 根据用户ID获取用户画像（带缓存）
     */
    @Cacheable(value = "userProfiles", key = "#userId")
    public Optional<UserProfileEntity> getUserProfileById(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    /**
     * 根据人口统计信息查找用户
     */
    public List<UserProfileEntity> getUsersByDemographics(Integer minAge, Integer maxAge, String gender, List<String> interests) {
        return userProfileRepository.findByDemographicsAndInterests(minAge, maxAge, gender, interests);
    }

    /**
     * 获取高价值用户
     */
    public List<UserProfileEntity> getHighValueUsers(Long minPurchaseAmount) {
        return userProfileRepository.findHighValueUsers(minPurchaseAmount);
    }

    /**
     * 获取活跃用户
     */
    public List<UserProfileEntity> getActiveUsers(LocalDateTime since) {
        return userProfileRepository.findActiveUsers(since);
    }

    /**
     * 删除过期的用户画像
     */
    @CacheEvict(value = "userProfiles", allEntries = true)
    public long deleteExpiredUserProfiles() {
        return userProfileRepository.deleteExpiredUsers(LocalDateTime.now());
    }

    // ==================== 广告位库存相关操作 ====================

    /**
     * 保存广告位库存
     */
    @CachePut(value = "inventory", key = "#inventory.slotId")
    public InventoryEntity saveInventory(InventoryEntity inventory) {
        return inventoryRepository.save(inventory);
    }

    /**
     * 根据广告位ID获取库存（带缓存）
     */
    @Cacheable(value = "inventory", key = "#slotId")
    public Optional<InventoryEntity> getInventoryById(String slotId) {
        return inventoryRepository.findBySlotId(slotId);
    }

    /**
     * 获取活跃的广告位
     */
    @Cacheable(value = "activeSlots")
    public List<InventoryEntity> getActiveSlots() {
        return inventoryRepository.findActiveSlots();
    }

    /**
     * 根据规格查找广告位
     */
    public List<InventoryEntity> getInventoryBySpecs(String adType, Integer width, Integer height, List<String> formats) {
        return inventoryRepository.findByDimensionsAndFormats(width, height, formats)
                .stream()
                .filter(inv -> inv.getSpecs().getAdType().equals(adType))
                .toList();
    }

    /**
     * 获取高质量广告位
     */
    public List<InventoryEntity> getHighQualitySlots(Double minQualityScore) {
        return inventoryRepository.findHighQualitySlots(minQualityScore);
    }

    /**
     * 获取高流量广告位
     */
    public List<InventoryEntity> getHighTrafficSlots(Long minDailyImpressions) {
        return inventoryRepository.findHighTrafficSlots(minDailyImpressions);
    }

    // ==================== 竞价统计相关操作 ====================

    /**
     * 保存竞价统计
     */
    public BidStatisticsEntity saveBidStatistics(BidStatisticsEntity statistics) {
        BidStatisticsEntity saved = bidStatisticsRepository.save(statistics);
        // 缓存当日统计数据
        if (saved.getDate().equals(LocalDate.now())) {
            String cacheKey = "daily_stats:" + saved.getDate() + ":" + saved.getHour();
            redisTemplate.opsForValue().set(cacheKey, saved, 2, TimeUnit.HOURS);
        }
        return saved;
    }

    /**
     * 获取指定日期的统计数据
     */
    @Cacheable(value = "dailyStats", key = "#date")
    public List<BidStatisticsEntity> getStatisticsByDate(LocalDate date) {
        return bidStatisticsRepository.findByDate(date);
    }

    /**
     * 获取指定日期范围的统计数据
     */
    public List<BidStatisticsEntity> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bidStatisticsRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * 获取广告活动统计数据
     */
    @Cacheable(value = "campaignStats", key = "#campaignId")
    public List<BidStatisticsEntity> getCampaignStatistics(String campaignId) {
        return bidStatisticsRepository.findByCampaignId(campaignId);
    }

    /**
     * 获取发布商统计数据
     */
    @Cacheable(value = "publisherStats", key = "#publisherId")
    public List<BidStatisticsEntity> getPublisherStatistics(String publisherId) {
        return bidStatisticsRepository.findByPublisherId(publisherId);
    }

    /**
     * 获取高收入统计数据
     */
    public List<BidStatisticsEntity> getHighRevenueStats(Long minRevenue) {
        return bidStatisticsRepository.findHighRevenueStats(minRevenue);
    }

    /**
     * 删除历史统计数据
     */
    @CacheEvict(value = {"dailyStats", "campaignStats", "publisherStats"}, allEntries = true)
    public long deleteOldStatistics(LocalDate beforeDate) {
        return bidStatisticsRepository.deleteByDateBefore(beforeDate);
    }

    // ==================== 聚合统计方法 ====================

    /**
     * 获取指定日期的总竞价请求数
     */
    @Cacheable(value = "totalBidRequests", key = "#date")
    public Long getTotalBidRequestsByDate(LocalDate date) {
        return bidStatisticsRepository.getTotalBidRequestsByDate(date).orElse(0L);
    }

    /**
     * 获取指定日期范围的总收入
     */
    public Long getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        return bidStatisticsRepository.getTotalRevenueByDateRange(startDate, endDate).orElse(0L);
    }

    /**
     * 获取广告活动的总展示数
     */
    @Cacheable(value = "campaignImpressions", key = "#campaignId")
    public Long getCampaignTotalImpressions(String campaignId) {
        return bidStatisticsRepository.getTotalImpressionsByCampaign(campaignId).orElse(0L);
    }

    /**
     * 获取发布商的总点击数
     */
    @Cacheable(value = "publisherClicks", key = "#publisherId")
    public Long getPublisherTotalClicks(String publisherId) {
        return bidStatisticsRepository.getTotalClicksByPublisher(publisherId).orElse(0L);
    }

    // ==================== 缓存管理方法 ====================

    /**
     * 清除所有缓存
     */
    @CacheEvict(value = {"bidRequests", "campaigns", "activeCampaigns", "userProfiles", 
                        "inventory", "activeSlots", "dailyStats", "campaignStats", 
                        "publisherStats", "winningBids", "totalBidRequests", 
                        "campaignImpressions", "publisherClicks"}, allEntries = true)
    public void clearAllCaches() {
        // 清除所有缓存
    }

    /**
     * 预热缓存
     */
    public void warmUpCaches() {
        // 预加载活跃广告活动
        getActiveCampaigns();
        
        // 预加载活跃广告位
        getActiveSlots();
        
        // 预加载当日统计数据
        getStatisticsByDate(LocalDate.now());
    }

    // ==================== 业务逻辑方法 ====================

    /**
     * 竞价匹配逻辑
     */
    public List<CampaignEntity> findMatchingCampaigns(String userId, String slotId, String country, String deviceType) {
        // 获取用户画像
        Optional<UserProfileEntity> userProfile = getUserProfileById(userId);
        
        // 获取广告位信息
        Optional<InventoryEntity> inventory = getInventoryById(slotId);
        
        if (userProfile.isEmpty() || inventory.isEmpty()) {
            return List.of();
        }
        
        // 获取活跃广告活动
        List<CampaignEntity> activeCampaigns = getActiveCampaigns();
        
        // 根据定向条件过滤
        return activeCampaigns.stream()
                .filter(campaign -> {
                    // 检查地理定向
                    boolean geoMatch = campaign.getTargeting().getGeo().getCountries().contains(country);
                    
                    // 检查设备定向
                    boolean deviceMatch = campaign.getTargeting().getDevice().getDeviceTypes().contains(deviceType);
                    
                    // 检查用户兴趣匹配
                    boolean interestMatch = userProfile.get().getInterests().getCategories().stream()
                            .anyMatch(interest -> campaign.getTargeting().getAudience().getInterests().contains(interest));
                    
                    // 检查预算是否充足
                    boolean budgetSufficient = campaign.getBudget().getSpentBudget() < campaign.getBudget().getTotalBudget();
                    
                    return geoMatch && deviceMatch && interestMatch && budgetSufficient;
                })
                .toList();
    }

    /**
     * 更新竞价统计
     */
    public void updateBidStatistics(String campaignId, String publisherId, String slotId, 
                                   boolean bidWon, Long bidPrice, String country, String deviceType) {
        LocalDate today = LocalDate.now();
        int currentHour = LocalDateTime.now().getHour();
        
        // 查找或创建统计记录
        Optional<BidStatisticsEntity> existingStats = bidStatisticsRepository.findByDateAndHour(today, currentHour);
        
        BidStatisticsEntity stats;
        if (existingStats.isPresent()) {
            stats = existingStats.get();
        } else {
            stats = new BidStatisticsEntity();
            stats.setDate(today);
            stats.setHour(currentHour);
            stats.setCampaignId(campaignId);
            stats.setPublisherId(publisherId);
            stats.setPlacementId(slotId);
            stats.setCreatedAt(LocalDateTime.now());
            
            // 初始化统计对象
            stats.setBidStats(new BidStatisticsEntity.BidStats());
            stats.setRevenueStats(new BidStatisticsEntity.RevenueStats());
            stats.setPerformanceStats(new BidStatisticsEntity.PerformanceStats());
            stats.setGeoStats(new HashMap<>());
            stats.setDeviceStats(new HashMap<>());
        }
        
        // 更新竞价统计
        BidStatisticsEntity.BidStats bidStats = stats.getBidStats();
        bidStats.setTotalBids((bidStats.getTotalBids() != null ? bidStats.getTotalBids() : 0L) + 1);
        if (bidWon) {
            bidStats.setWinningBids((bidStats.getWinningBids() != null ? bidStats.getWinningBids() : 0L) + 1);
            BidStatisticsEntity.RevenueStats revenueStats = stats.getRevenueStats();
            revenueStats.setTotalRevenue((revenueStats.getTotalRevenue() != null ? revenueStats.getTotalRevenue() : 0L) + bidPrice);
        }
        
        // 更新地理统计
        BidStatisticsEntity.GeoStats geoStats = stats.getGeoStats().computeIfAbsent(country, k -> new BidStatisticsEntity.GeoStats());
        geoStats.setRequests((geoStats.getRequests() != null ? geoStats.getRequests() : 0L) + 1);
        if (bidWon) {
            geoStats.setWins((geoStats.getWins() != null ? geoStats.getWins() : 0L) + 1);
            geoStats.setRevenue((geoStats.getRevenue() != null ? geoStats.getRevenue() : 0L) + bidPrice);
        }
        
        // 更新设备统计
        BidStatisticsEntity.DeviceStats deviceStats = stats.getDeviceStats().computeIfAbsent(deviceType, k -> new BidStatisticsEntity.DeviceStats());
        deviceStats.setRequests((deviceStats.getRequests() != null ? deviceStats.getRequests() : 0L) + 1);
        if (bidWon) {
            deviceStats.setWins((deviceStats.getWins() != null ? deviceStats.getWins() : 0L) + 1);
            deviceStats.setRevenue((deviceStats.getRevenue() != null ? deviceStats.getRevenue() : 0L) + bidPrice);
        }
        
        stats.setUpdatedAt(LocalDateTime.now());
        
        // 保存统计数据
        saveBidStatistics(stats);
    }
}