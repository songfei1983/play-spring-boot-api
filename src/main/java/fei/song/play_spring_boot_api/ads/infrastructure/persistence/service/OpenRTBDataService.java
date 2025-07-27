package fei.song.play_spring_boot_api.ads.infrastructure.persistence.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.*;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * OpenRTB 数据服务
 * 提供高级的数据操作和缓存管理
 */
@Slf4j
@Service
public class OpenRTBDataService {

    private final BidRequestRepository bidRequestRepository;
    private final BidResponseRepository bidResponseRepository;
    private final CampaignRepository campaignRepository;
    private final UserProfileRepository userProfileRepository;
    private final InventoryRepository inventoryRepository;
    private final BidStatisticsRepository bidStatisticsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public OpenRTBDataService(
            BidRequestRepository bidRequestRepository,
            BidResponseRepository bidResponseRepository,
            CampaignRepository campaignRepository,
            UserProfileRepository userProfileRepository,
            InventoryRepository inventoryRepository,
            BidStatisticsRepository bidStatisticsRepository,
            @Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.bidRequestRepository = bidRequestRepository;
        this.bidResponseRepository = bidResponseRepository;
        this.campaignRepository = campaignRepository;
        this.userProfileRepository = userProfileRepository;
        this.inventoryRepository = inventoryRepository;
        this.bidStatisticsRepository = bidStatisticsRepository;
        this.redisTemplate = redisTemplate;
    }

    // 缓存键前缀
    private static final String CACHE_PREFIX_CAMPAIGN = "campaign:";
    private static final String CACHE_PREFIX_USER_PROFILE = "user_profile:";
    private static final String CACHE_PREFIX_INVENTORY = "inventory:";
    private static final String CACHE_PREFIX_BID_STATS = "bid_stats:";

    /**
     * 保存竞价请求
     */
    @Transactional
    public BidRequestEntity saveBidRequest(BidRequestEntity bidRequest) {
        log.debug("Saving bid request: {}", bidRequest.getRequestId());
        return bidRequestRepository.save(bidRequest);
    }

    /**
     * 保存竞价响应
     */
    @Transactional
    public BidResponseEntity saveBidResponse(BidResponseEntity bidResponse) {
        log.debug("Saving bid response: {}", bidResponse.getResponseId());
        return bidResponseRepository.save(bidResponse);
    }

    /**
     * 获取活跃的广告活动（带缓存）
     */
    public List<CampaignEntity> getActiveCampaigns() {
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX_CAMPAIGN + "active";
            
            @SuppressWarnings("unchecked")
            List<CampaignEntity> cachedCampaigns = (List<CampaignEntity>) redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedCampaigns != null) {
                log.debug("Retrieved {} active campaigns from cache", cachedCampaigns.size());
                return cachedCampaigns;
            }
        }

        List<CampaignEntity> campaigns = campaignRepository.findActiveCampaigns(LocalDateTime.now());
        
        if (redisTemplate != null) {
            // 缓存5分钟
            String cacheKey = CACHE_PREFIX_CAMPAIGN + "active";
            redisTemplate.opsForValue().set(cacheKey, campaigns, 5, TimeUnit.MINUTES);
            log.debug("Cached {} active campaigns", campaigns.size());
        }
        
        return campaigns;
    }

    /**
     * 根据定向条件获取匹配的广告活动
     */
    public List<CampaignEntity> getMatchingCampaigns(String country, Integer deviceType, BigDecimal minBid) {
        List<CampaignEntity> activeCampaigns = getActiveCampaigns();
        
        return activeCampaigns.stream()
            .filter(campaign -> isTargetingMatch(campaign, country, deviceType))
            .filter(campaign -> campaign.getBidding().getMaxBid().compareTo(minBid) >= 0)
            .toList();
    }

    /**
     * 获取用户画像（带缓存）
     */
    public Optional<UserProfileEntity> getUserProfile(String userId) {
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX_USER_PROFILE + userId;
            
            UserProfileEntity cachedProfile = (UserProfileEntity) redisTemplate.opsForValue().get(cacheKey);
            if (cachedProfile != null) {
                log.debug("Retrieved user profile from cache: {}", userId);
                return Optional.of(cachedProfile);
            }
        }

        Optional<UserProfileEntity> profile = userProfileRepository.findByUserId(userId);
        
        if (profile.isPresent() && redisTemplate != null) {
            // 缓存30分钟
            String cacheKey = CACHE_PREFIX_USER_PROFILE + userId;
            redisTemplate.opsForValue().set(cacheKey, profile.get(), 30, TimeUnit.MINUTES);
            log.debug("Cached user profile: {}", userId);
        }
        
        return profile;
    }

    /**
     * 更新用户画像
     */
    @Transactional
    public UserProfileEntity updateUserProfile(UserProfileEntity userProfile) {
        UserProfileEntity saved = userProfileRepository.save(userProfile);
        
        // 清除缓存
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX_USER_PROFILE + userProfile.getUserId();
            redisTemplate.delete(cacheKey);
            log.debug("Updated and cleared cache for user profile: {}", userProfile.getUserId());
        }
        
        return saved;
    }

    /**
     * 获取广告位库存（带缓存）
     */
    public Optional<InventoryEntity> getInventory(String placementId) {
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX_INVENTORY + placementId;
            
            InventoryEntity cachedInventory = (InventoryEntity) redisTemplate.opsForValue().get(cacheKey);
            if (cachedInventory != null) {
                log.debug("Retrieved inventory from cache: {}", placementId);
                return Optional.of(cachedInventory);
            }
        }

        Optional<InventoryEntity> inventory = inventoryRepository.findByPlacementId(placementId);
        
        if (inventory.isPresent() && redisTemplate != null) {
            // 缓存15分钟
            String cacheKey = CACHE_PREFIX_INVENTORY + placementId;
            redisTemplate.opsForValue().set(cacheKey, inventory.get(), 15, TimeUnit.MINUTES);
            log.debug("Cached inventory: {}", placementId);
        }
        
        return inventory;
    }

    /**
     * 获取匹配的广告位
     */
    public List<InventoryEntity> getMatchingInventory(String adType, Integer width, Integer height, BigDecimal maxFloorPrice) {
        return inventoryRepository.findByStatus("active")
            .stream()
            .filter(inventory -> inventory.getSpec().getAdType().equals(adType))
            .filter(inventory -> hasMatchingFormat(inventory, width, height))
            .filter(inventory -> inventory.getPricing().getFloorPrice().compareTo(maxFloorPrice) <= 0)
            .toList();
    }

    /**
     * 保存竞价统计
     */
    @Transactional
    public BidStatisticsEntity saveBidStatistics(BidStatisticsEntity statistics) {
        BidStatisticsEntity saved = bidStatisticsRepository.save(statistics);
        
        // 清除相关缓存
        if (redisTemplate != null) {
            String cacheKey = CACHE_PREFIX_BID_STATS + statistics.getDate() + ":" + statistics.getCampaignId();
            redisTemplate.delete(cacheKey);
        }
        
        return saved;
    }

    /**
     * 获取活动的日统计数据
     */
    public List<BidStatisticsEntity> getCampaignDailyStats(String campaignId, LocalDate startDate, LocalDate endDate) {
        return bidStatisticsRepository.findByDateBetweenAndCampaignId(startDate, endDate, campaignId);
    }

    /**
     * 获取发布商的统计数据
     */
    public List<BidStatisticsEntity> getPublisherStats(String publisherId, LocalDate startDate, LocalDate endDate) {
        return bidStatisticsRepository.findByDateBetweenAndPublisherId(startDate, endDate, publisherId);
    }

    /**
     * 清理过期数据
     */
    @Transactional
    public void cleanupExpiredData() {
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(7);
        LocalDate expiredDate = LocalDate.now().minusDays(30);
        
        log.info("Starting cleanup of expired data");
        
        // 清理过期的竞价请求和响应
        bidRequestRepository.deleteByExpiresAtBefore(expiredTime);
        bidResponseRepository.deleteByExpiresAtBefore(expiredTime);
        
        // 清理过期的用户画像
        userProfileRepository.deleteByExpiresAtBefore(expiredTime);
        
        // 清理过期的统计数据
        bidStatisticsRepository.deleteByDateBefore(expiredDate);
        
        log.info("Completed cleanup of expired data");
    }

    /**
     * 获取系统健康状态
     */
    public SystemHealthStatus getSystemHealth() {
        try {
            long totalCampaigns = campaignRepository.count();
            long activeCampaigns = campaignRepository.countByStatus("active");
            long totalInventory = inventoryRepository.count();
            long activeInventory = inventoryRepository.countByStatus("active");
            
            return SystemHealthStatus.builder()
                .totalCampaigns(totalCampaigns)
                .activeCampaigns(activeCampaigns)
                .totalInventory(totalInventory)
                .activeInventory(activeInventory)
                .healthy(true)
                .timestamp(LocalDateTime.now())
                .build();
        } catch (Exception e) {
            log.error("Error checking system health", e);
            return SystemHealthStatus.builder()
                .healthy(false)
                .timestamp(LocalDateTime.now())
                .error(e.getMessage())
                .build();
        }
    }

    // 私有辅助方法
    private boolean isTargetingMatch(CampaignEntity campaign, String country, Integer deviceType) {
        if (campaign.getTargeting() == null) {
            return true;
        }
        
        // 检查地理定向
        if (campaign.getTargeting().getGeo() != null) {
            List<String> includedCountries = campaign.getTargeting().getGeo().getIncludedCountries();
            if (includedCountries != null && !includedCountries.isEmpty() && !includedCountries.contains(country)) {
                return false;
            }
            
            List<String> excludedCountries = campaign.getTargeting().getGeo().getExcludedCountries();
            if (excludedCountries != null && excludedCountries.contains(country)) {
                return false;
            }
        }
        
        // 检查设备定向
        if (campaign.getTargeting().getDevice() != null) {
            List<Integer> deviceTypes = campaign.getTargeting().getDevice().getDeviceTypes();
            if (deviceTypes != null && !deviceTypes.isEmpty() && !deviceTypes.contains(deviceType)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean hasMatchingFormat(InventoryEntity inventory, Integer width, Integer height) {
        if (inventory.getSpec() == null || inventory.getSpec().getSupportedFormats() == null) {
            return false;
        }
        
        return inventory.getSpec().getSupportedFormats().stream()
            .anyMatch(format -> format.getWidth().equals(width) && format.getHeight().equals(height));
    }

    /**
     * 系统健康状态数据类
     */
    @lombok.Data
    @lombok.Builder
    public static class SystemHealthStatus {
        private boolean healthy;
        private long totalCampaigns;
        private long activeCampaigns;
        private long totalInventory;
        private long activeInventory;
        private LocalDateTime timestamp;
        private String error;
    }
}