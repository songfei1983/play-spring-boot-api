package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户画像管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ads/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 创建用户画像
     */
    @PostMapping
    public ResponseEntity<UserProfileEntity> createProfile(@Valid @RequestBody UserProfileEntity profile) {
        try {
            UserProfileEntity createdProfile = userProfileService.createProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        } catch (IllegalArgumentException e) {
            log.warn("创建用户画像失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("创建用户画像异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户画像
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileEntity> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileEntity profile) {
        try {
            UserProfileEntity updatedProfile = userProfileService.updateProfile(userId, profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            log.warn("更新用户画像失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("更新用户画像异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据用户ID获取画像
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileEntity> getProfile(@PathVariable String userId) {
        Optional<UserProfileEntity> profile = userProfileService.findByUserId(userId);
        return profile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据年龄范围查询用户
     */
    @GetMapping("/age")
    public ResponseEntity<List<UserProfileEntity>> getUsersByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        List<UserProfileEntity> profiles = userProfileService.findByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据性别查询用户
     */
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByGender(@PathVariable String gender) {
        List<UserProfileEntity> profiles = userProfileService.findByGender(gender);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据国家查询用户
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByCountry(@PathVariable String country) {
        List<UserProfileEntity> profiles = userProfileService.findByCountry(country);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据城市查询用户
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByCity(@PathVariable String city) {
        List<UserProfileEntity> profiles = userProfileService.findByCity(city);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据兴趣类别查询用户
     */
    @GetMapping("/interest/{category}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByInterest(@PathVariable String category) {
        List<UserProfileEntity> profiles = userProfileService.findByInterestCategory(category);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据设备类型查询用户
     */
    @GetMapping("/device/{deviceType}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByDeviceType(@PathVariable String deviceType) {
        // Convert string to integer for device type
        Integer deviceTypeInt;
        try {
            deviceTypeInt = Integer.parseInt(deviceType);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        List<UserProfileEntity> profiles = userProfileService.findByDeviceType(deviceTypeInt);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据操作系统查询用户
     */
    @GetMapping("/os/{operatingSystem}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByOS(@PathVariable String operatingSystem) {
        List<UserProfileEntity> profiles = userProfileService.findByOperatingSystem(operatingSystem);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取活跃用户
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserProfileEntity>> getActiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<UserProfileEntity> profiles = userProfileService.findActiveUsers(threshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据购买类别查询用户
     */
    @GetMapping("/purchase/category/{category}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByPurchaseCategory(@PathVariable String category) {
        List<UserProfileEntity> profiles = userProfileService.findByPurchaseCategory(category);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取高价值用户
     */
    @GetMapping("/purchase/high-value")
    public ResponseEntity<List<UserProfileEntity>> getHighValueUsers(@RequestParam double minAmount) {
        List<UserProfileEntity> profiles = userProfileService.findHighValueUsers(minAmount);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据广告互动活动查询用户
     */
    @GetMapping("/ad-interaction/{campaignId}")
    public ResponseEntity<List<UserProfileEntity>> getUsersByAdInteraction(@PathVariable String campaignId) {
        List<UserProfileEntity> profiles = userProfileService.findByAdInteractionCampaign(campaignId);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取超过日频次限制的用户
     */
    @GetMapping("/frequency/exceeded")
    public ResponseEntity<List<UserProfileEntity>> getUsersExceedingFrequency(
            @RequestParam String userId, 
            @RequestParam Integer threshold) {
        List<UserProfileEntity> profiles = userProfileService.findUsersExceedingDailyFrequency(userId, threshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据地理范围查询用户
     */
    @GetMapping("/geo")
    public ResponseEntity<List<UserProfileEntity>> getUsersByGeoRange(
            @RequestParam Double centerLat,
            @RequestParam Double centerLon,
            @RequestParam Double radiusKm,
            @RequestParam Double maxDistance) {
        List<UserProfileEntity> profiles = userProfileService.findByGeoRange(centerLat, centerLon, radiusKm, maxDistance);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 分页查询用户画像
     */
    @GetMapping
    public ResponseEntity<Page<UserProfileEntity>> getProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserProfileEntity> profiles = userProfileService.findAll(pageable);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 为用户添加兴趣
     */
    @PostMapping("/{userId}/interests")
    public ResponseEntity<UserProfileEntity> addUserInterest(
            @PathVariable String userId,
            @RequestBody Map<String, Object> interestData) {
        try {
            String category = (String) interestData.get("category");
            String interest = (String) interestData.get("interest");
            Double score = interestData.get("score") != null ? 
                ((Number) interestData.get("score")).doubleValue() : 1.0;
            
            UserProfileEntity.Interest interestObj = new UserProfileEntity.Interest();
            interestObj.setCategory(category);
            interestObj.setSubcategory(interest);
            interestObj.setScore(score);
            UserProfileEntity profile = userProfileService.addInterest(userId, interestObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("添加用户兴趣失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("添加用户兴趣异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 记录用户页面浏览
     */
    @PostMapping("/{userId}/pageviews")
    public ResponseEntity<UserProfileEntity> addPageView(
            @PathVariable String userId,
            @RequestBody Map<String, Object> pageViewData) {
        try {
            String url = (String) pageViewData.get("url");
            String category = (String) pageViewData.get("category");
            Long duration = pageViewData.get("duration") != null ? 
                ((Number) pageViewData.get("duration")).longValue() : 0L;
            
            UserProfileEntity.PageView pageViewObj = new UserProfileEntity.PageView();
            pageViewObj.setUrl(url);
            pageViewObj.setCategory(category);
            pageViewObj.setTimeSpent(duration);
            pageViewObj.setTimestamp(LocalDateTime.now());
            UserProfileEntity profile = userProfileService.addPageView(userId, pageViewObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("记录页面浏览失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("记录页面浏览异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 记录用户广告互动
     */
    @PostMapping("/{userId}/ad-interactions")
    public ResponseEntity<UserProfileEntity> addAdInteraction(
            @PathVariable String userId,
            @RequestBody Map<String, Object> interactionData) {
        try {
            String adId = (String) interactionData.get("adId");
            String campaignId = (String) interactionData.get("campaignId");
            String action = (String) interactionData.get("action");
            
            UserProfileEntity.AdInteraction adInteractionObj = new UserProfileEntity.AdInteraction();
            adInteractionObj.setAdId(adId);
            adInteractionObj.setCampaignId(campaignId);
            adInteractionObj.setAction(action);
            adInteractionObj.setTimestamp(LocalDateTime.now());
            UserProfileEntity profile = userProfileService.addAdInteraction(userId, adInteractionObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("记录广告互动失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("记录广告互动异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 记录用户购买行为
     */
    @PostMapping("/{userId}/purchases")
    public ResponseEntity<UserProfileEntity> addPurchase(
            @PathVariable String userId,
            @RequestBody Map<String, Object> purchaseData) {
        try {
            String productId = (String) purchaseData.get("productId");
            String category = (String) purchaseData.get("category");
            Double amount = ((Number) purchaseData.get("amount")).doubleValue();
            
            UserProfileEntity.Purchase purchaseObj = new UserProfileEntity.Purchase();
            purchaseObj.setProductId(productId);
            purchaseObj.setCategory(category);
            purchaseObj.setAmount(amount);
            purchaseObj.setTimestamp(LocalDateTime.now());
            UserProfileEntity profile = userProfileService.addPurchase(userId, purchaseObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("记录购买行为失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("记录购买行为异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户会话数据
     */
    @PostMapping("/{userId}/session")
    public ResponseEntity<UserProfileEntity> updateSessionData(
            @PathVariable String userId,
            @RequestBody Map<String, Object> sessionData) {
        try {
            Long sessionDuration = sessionData.get("duration") != null ? 
                ((Number) sessionData.get("duration")).longValue() : null;
            Integer pageViews = sessionData.get("pageViews") != null ? 
                ((Number) sessionData.get("pageViews")).intValue() : null;
            
            UserProfileEntity.SessionData sessionDataObj = new UserProfileEntity.SessionData();
            sessionDataObj.setAvgSessionDuration(sessionDuration);
            sessionDataObj.setSessionCount(pageViews);
            sessionDataObj.setLastSession(LocalDateTime.now());
            UserProfileEntity profile = userProfileService.updateSessionData(userId, sessionDataObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("更新会话数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("更新会话数据异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户频次数据
     */
    @PostMapping("/{userId}/frequency")
    public ResponseEntity<UserProfileEntity> updateFrequencyData(
            @PathVariable String userId,
            @RequestBody Map<String, Object> frequencyData) {
        try {
            Integer dailyAdViews = frequencyData.get("dailyAdViews") != null ? 
                ((Number) frequencyData.get("dailyAdViews")).intValue() : null;
            Integer weeklyAdViews = frequencyData.get("weeklyAdViews") != null ? 
                ((Number) frequencyData.get("weeklyAdViews")).intValue() : null;
            
            UserProfileEntity.FrequencyData frequencyDataObj = new UserProfileEntity.FrequencyData();
            Map<String, Integer> dailyMap = new HashMap<>();
            dailyMap.put(LocalDate.now().toString(), dailyAdViews);
            frequencyDataObj.setDailyImpressions(dailyMap);
            
            Map<String, Integer> hourlyMap = new HashMap<>();
            hourlyMap.put(String.valueOf(LocalDateTime.now().getHour()), weeklyAdViews);
            frequencyDataObj.setHourlyImpressions(hourlyMap);
            UserProfileEntity profile = userProfileService.updateFrequencyData(userId, frequencyDataObj);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.warn("更新频次数据失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("更新频次数据异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除用户画像
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        try {
            userProfileService.deleteProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("删除用户画像失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("删除用户画像异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 清理过期的用户画像
     */
    @PostMapping("/cleanup/expired")
    public ResponseEntity<Map<String, Long>> cleanupExpiredProfiles() {
        try {
            userProfileService.cleanupExpiredProfiles(30);
            long deletedCount = 0; // Async method returns CompletableFuture<Void>
            Map<String, Long> result = Map.of("deletedCount", deletedCount);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("清理过期用户画像异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取需要更新的用户画像
     */
    @GetMapping("/needs-update")
    public ResponseEntity<List<UserProfileEntity>> getProfilesNeedingUpdate(@RequestParam(defaultValue = "7") int daysThreshold) {
        List<UserProfileEntity> profiles = userProfileService.findProfilesNeedingUpdate(daysThreshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取用户画像统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProfileStats() {
        Map<String, Object> stats = Map.of(
            "totalProfiles", userProfileService.countProfiles(),
            "activeProfiles", userProfileService.countProfiles()
        );
        return ResponseEntity.ok(stats);
    }

    /**
     * 按国家统计用户数量
     */
    @GetMapping("/stats/country")
    public ResponseEntity<Map<String, Long>> getCountryStats() {
        long countryCount = userProfileService.countByCountry("US");
        Map<String, Long> stats = Map.of("US", countryCount);
        return ResponseEntity.ok(stats);
    }

    /**
     * 按性别统计用户数量
     */
    @GetMapping("/stats/gender")
    public ResponseEntity<Map<String, Long>> getGenderStats() {
        long genderCount = userProfileService.countByGender("male");
        Map<String, Long> stats = Map.of("male", genderCount);
        return ResponseEntity.ok(stats);
    }
}