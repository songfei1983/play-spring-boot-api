package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 用户画像服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    /**
     * 创建用户画像
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#profile.userId")
    public UserProfileEntity createProfile(UserProfileEntity profile) {
        log.info("创建用户画像: userId={}", profile.getUserId());
        
        // 检查用户画像是否已存在
        Optional<UserProfileEntity> existing = userProfileRepository.findByUserId(profile.getUserId());
        if (existing.isPresent()) {
            log.warn("用户画像已存在: userId={}", profile.getUserId());
            throw new IllegalArgumentException("用户画像已存在: " + profile.getUserId());
        }
        
        // 设置默认值
        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(LocalDateTime.now());
        }
        if (profile.getUpdatedAt() == null) {
            profile.setUpdatedAt(LocalDateTime.now());
        }
        
        UserProfileEntity savedProfile = userProfileRepository.save(profile);
        log.info("用户画像创建成功: profileId={}, userId={}", 
            savedProfile.getId(), savedProfile.getUserId());
        
        return savedProfile;
    }

    /**
     * 更新用户画像
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity updateProfile(String userId, UserProfileEntity profileUpdate) {
        log.info("更新用户画像: userId={}", userId);
        
        UserProfileEntity existingProfile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        // 更新人口统计信息
        if (profileUpdate.getDemographics() != null) {
            if (existingProfile.getDemographics() == null) {
                existingProfile.setDemographics(profileUpdate.getDemographics());
            } else {
                updateDemographics(existingProfile.getDemographics(), profileUpdate.getDemographics());
            }
        }
        
        // 更新兴趣信息
        if (profileUpdate.getInterests() != null && !profileUpdate.getInterests().isEmpty()) {
            existingProfile.setInterests(profileUpdate.getInterests());
        }
        
        // 更新行为信息
        if (profileUpdate.getBehavior() != null) {
            if (existingProfile.getBehavior() == null) {
                existingProfile.setBehavior(profileUpdate.getBehavior());
            } else {
                updateBehavior(existingProfile.getBehavior(), profileUpdate.getBehavior());
            }
        }
        
        // 更新设备信息
        if (profileUpdate.getDeviceInfo() != null) {
            existingProfile.setDeviceInfo(profileUpdate.getDeviceInfo());
        }
        
        // 更新频次数据
        if (profileUpdate.getFrequencyData() != null) {
            existingProfile.setFrequencyData(profileUpdate.getFrequencyData());
        }
        
        existingProfile.setUpdatedAt(LocalDateTime.now());
        
        UserProfileEntity updatedProfile = userProfileRepository.save(existingProfile);
        log.info("用户画像更新成功: profileId={}, userId={}", 
            updatedProfile.getId(), updatedProfile.getUserId());
        
        return updatedProfile;
    }

    /**
     * 根据用户ID查找用户画像
     */
    @Cacheable(value = "userProfiles", key = "#userId")
    public Optional<UserProfileEntity> findByUserId(String userId) {
        return userProfileRepository.findByUserId(userId);
    }

    /**
     * 根据ID查找用户画像
     */
    public Optional<UserProfileEntity> findById(String id) {
        return userProfileRepository.findById(id);
    }

    /**
     * 根据年龄范围查找用户画像
     */
    public List<UserProfileEntity> findByAgeRange(Integer minAge, Integer maxAge) {
        return userProfileRepository.findByAgeRange(minAge, maxAge);
    }

    /**
     * 根据性别查找用户画像
     */
    public List<UserProfileEntity> findByGender(String gender) {
        return userProfileRepository.findByGender(gender);
    }

    /**
     * 根据国家查找用户画像
     */
    public List<UserProfileEntity> findByCountry(String country) {
        return userProfileRepository.findByCountry(country);
    }

    /**
     * 根据城市查找用户画像
     */
    public List<UserProfileEntity> findByCity(String city) {
        return userProfileRepository.findByCity(city);
    }

    /**
     * 根据兴趣类别查找用户画像
     */
    public List<UserProfileEntity> findByInterestCategory(String category) {
        return userProfileRepository.findByInterestCategory(category);
    }

    /**
     * 根据设备类型查找用户画像
     */
    public List<UserProfileEntity> findByDeviceType(Integer deviceType) {
        return userProfileRepository.findByDeviceType(deviceType);
    }

    /**
     * 根据操作系统查找用户画像
     */
    public List<UserProfileEntity> findByOperatingSystem(String os) {
        return userProfileRepository.findByOperatingSystem(os);
    }

    /**
     * 查找活跃用户画像
     */
    public List<UserProfileEntity> findActiveUsers(LocalDateTime since) {
        return userProfileRepository.findActiveUsers(since);
    }

    /**
     * 根据购买类别查找用户画像
     */
    public List<UserProfileEntity> findByPurchaseCategory(String category) {
        return userProfileRepository.findByPurchaseCategory(category);
    }

    /**
     * 查找高价值用户（购买金额超过阈值）
     */
    public List<UserProfileEntity> findHighValueUsers(Double minAmount) {
        return userProfileRepository.findHighValueUsers(minAmount);
    }

    /**
     * 根据广告互动查找用户画像
     */
    public List<UserProfileEntity> findByAdInteractionCampaign(String campaignId) {
        return userProfileRepository.findByAdInteractionCampaign(campaignId);
    }

    /**
     * 查找超过日频次限制的用户
     */
    public List<UserProfileEntity> findUsersExceedingDailyFrequency(String date, Integer threshold) {
        return userProfileRepository.findUsersExceedingDailyFrequency(date, threshold);
    }

    /**
     * 根据地理范围查找用户画像
     */
    public List<UserProfileEntity> findByGeoRange(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        return userProfileRepository.findByGeoRange(minLat, maxLat, minLon, maxLon);
    }

    /**
     * 分页查找所有用户画像
     */
    public Page<UserProfileEntity> findAll(Pageable pageable) {
        return userProfileRepository.findAll(pageable);
    }

    /**
     * 添加用户兴趣
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity addInterest(String userId, UserProfileEntity.Interest interest) {
        log.info("添加用户兴趣: userId={}, category={}", userId, interest.getCategory());
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        if (profile.getInterests() == null) {
            profile.setInterests(List.of(interest));
        } else {
            // 检查兴趣是否已存在
            boolean exists = profile.getInterests().stream()
                .anyMatch(i -> i.getCategory().equals(interest.getCategory()) && 
                             i.getSubcategory().equals(interest.getSubcategory()));
            
            if (!exists) {
                profile.getInterests().add(interest);
            } else {
                // 更新现有兴趣的分数和时间
                profile.getInterests().stream()
                    .filter(i -> i.getCategory().equals(interest.getCategory()) && 
                               i.getSubcategory().equals(interest.getSubcategory()))
                    .findFirst()
                    .ifPresent(existingInterest -> {
                        existingInterest.setScore(interest.getScore());
                        existingInterest.setLastUpdated(interest.getLastUpdated());
                    });
            }
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        
        UserProfileEntity updatedProfile = userProfileRepository.save(profile);
        log.info("用户兴趣添加成功: userId={}, category={}", userId, interest.getCategory());
        
        return updatedProfile;
    }

    /**
     * 添加页面浏览记录
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity addPageView(String userId, UserProfileEntity.PageView pageView) {
        log.debug("添加页面浏览记录: userId={}, url={}", userId, pageView.getUrl());
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        if (profile.getBehavior() == null) {
            profile.setBehavior(new UserProfileEntity.Behavior());
        }
        
        if (profile.getBehavior().getPageViews() == null) {
            profile.getBehavior().setPageViews(List.of(pageView));
        } else {
            profile.getBehavior().getPageViews().add(pageView);
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * 添加广告互动记录
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity addAdInteraction(String userId, UserProfileEntity.AdInteraction adInteraction) {
        log.debug("添加广告互动记录: userId={}, adId={}, action={}", 
            userId, adInteraction.getAdId(), adInteraction.getAction());
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        if (profile.getBehavior() == null) {
            profile.setBehavior(new UserProfileEntity.Behavior());
        }
        
        if (profile.getBehavior().getAdInteractions() == null) {
            profile.getBehavior().setAdInteractions(List.of(adInteraction));
        } else {
            profile.getBehavior().getAdInteractions().add(adInteraction);
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * 添加购买记录
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity addPurchase(String userId, UserProfileEntity.Purchase purchase) {
        log.info("添加购买记录: userId={}, productId={}, amount={}", 
            userId, purchase.getProductId(), purchase.getAmount());
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        if (profile.getBehavior() == null) {
            profile.setBehavior(new UserProfileEntity.Behavior());
        }
        
        if (profile.getBehavior().getPurchaseHistory() == null) {
            profile.getBehavior().setPurchaseHistory(List.of(purchase));
        } else {
            profile.getBehavior().getPurchaseHistory().add(purchase);
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * 更新会话数据
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity updateSessionData(String userId, UserProfileEntity.SessionData sessionData) {
        log.debug("更新会话数据: userId={}", userId);
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        if (profile.getBehavior() == null) {
            profile.setBehavior(new UserProfileEntity.Behavior());
        }
        
        profile.getBehavior().setSessionData(sessionData);
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * 更新频次数据
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public UserProfileEntity updateFrequencyData(String userId, UserProfileEntity.FrequencyData frequencyData) {
        log.debug("更新频次数据: userId={}", userId);
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        profile.setFrequencyData(frequencyData);
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }

    /**
     * 删除用户画像
     */
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#userId")
    public void deleteProfile(String userId) {
        log.info("删除用户画像: userId={}", userId);
        
        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("用户画像不存在: " + userId));
        
        userProfileRepository.delete(profile);
        log.info("用户画像删除成功: userId={}", userId);
    }

    /**
     * 清理过期的用户画像
     */
    @Async
    @Transactional
    @CacheEvict(value = "userProfiles", allEntries = true)
    public CompletableFuture<Void> cleanupExpiredProfiles(int daysThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysThreshold);
        
        log.info("开始清理过期用户画像: threshold={}", threshold);
        
        userProfileRepository.deleteByExpiresAtBefore(threshold);
        long deletedCount = 0; // 实际删除数量需要在删除前统计
        
        log.info("过期用户画像清理完成: deletedCount={}", deletedCount);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 查找需要更新的用户画像
     */
    public List<UserProfileEntity> findProfilesNeedingUpdate(int hoursThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hoursThreshold);
        return userProfileRepository.findProfilesNeedingUpdate(threshold);
    }

    /**
     * 统计用户画像数量
     */
    public long countProfiles() {
        return userProfileRepository.count();
    }

    /**
     * 按国家统计用户画像数量
     */
    public long countByCountry(String country) {
        return userProfileRepository.countByCountry(country);
    }

    /**
     * 按性别统计用户画像数量
     */
    public long countByGender(String gender) {
        return userProfileRepository.countByGender(gender);
    }

    /**
     * 获取用户画像统计信息
     */
    public Map<String, Object> getProfileStats() {
        long totalProfiles = userProfileRepository.count();
        
        Map<String, Object> stats = Map.of(
            "totalProfiles", totalProfiles,
            "activeProfiles", userProfileRepository.findActiveUsers(LocalDateTime.now().minusDays(30)).size(),
            "profilesWithInterests", userProfileRepository.findAll().stream()
                .filter(p -> p.getInterests() != null && !p.getInterests().isEmpty())
                .count(),
            "profilesWithPurchaseHistory", userProfileRepository.findAll().stream()
                .filter(p -> p.getBehavior() != null && 
                           p.getBehavior().getPurchaseHistory() != null && 
                           !p.getBehavior().getPurchaseHistory().isEmpty())
                .count()
        );
        
        return stats;
    }

    /**
     * 更新人口统计信息的辅助方法
     */
    private void updateDemographics(UserProfileEntity.Demographics existing, UserProfileEntity.Demographics update) {
        if (update.getAge() != null) {
            existing.setAge(update.getAge());
        }
        if (update.getGender() != null) {
            existing.setGender(update.getGender());
        }
        if (update.getGeo() != null) {
            existing.setGeo(update.getGeo());
        }
        if (update.getLanguage() != null) {
            existing.setLanguage(update.getLanguage());
        }
    }

    /**
     * 更新行为信息的辅助方法
     */
    private void updateBehavior(UserProfileEntity.Behavior existing, UserProfileEntity.Behavior update) {
        if (update.getPageViews() != null) {
            if (existing.getPageViews() == null) {
                existing.setPageViews(update.getPageViews());
            } else {
                existing.getPageViews().addAll(update.getPageViews());
            }
        }
        
        if (update.getAdInteractions() != null) {
            if (existing.getAdInteractions() == null) {
                existing.setAdInteractions(update.getAdInteractions());
            } else {
                existing.getAdInteractions().addAll(update.getAdInteractions());
            }
        }
        
        if (update.getPurchaseHistory() != null) {
            if (existing.getPurchaseHistory() == null) {
                existing.setPurchaseHistory(update.getPurchaseHistory());
            } else {
                existing.getPurchaseHistory().addAll(update.getPurchaseHistory());
            }
        }
        
        if (update.getSessionData() != null) {
            existing.setSessionData(update.getSessionData());
        }
    }
}