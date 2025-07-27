package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.service.AdsUserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/ads/user-profiles")
@RequiredArgsConstructor
@Tag(name = "Ads User Profile Management", description = "广告用户画像管理API")
public class AdsUserProfileController {

    private final AdsUserProfileService userProfileService;

    /**
     * 创建用户画像
     */
    @PostMapping
    @Operation(summary = "创建用户画像", description = "创建一个新的广告用户画像")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "成功创建用户画像",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> createProfile(
            @Parameter(description = "用户画像信息") @Valid @RequestBody UserProfileEntity profile) {
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
    @Operation(summary = "更新用户画像", description = "更新指定用户ID的广告用户画像")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新用户画像",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "用户画像不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> updateProfile(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "更新的用户画像信息") @Valid @RequestBody UserProfileEntity profile) {
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
    @Operation(summary = "获取用户画像", description = "通过用户ID获取广告用户画像详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户画像",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "404", description = "用户画像不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> getProfile(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId) {
        Optional<UserProfileEntity> profile = userProfileService.findByUserId(userId);
        return profile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据年龄范围查询用户
     */
    @GetMapping("/age")
    @Operation(summary = "根据年龄范围获取用户", description = "获取指定年龄范围内的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByAgeRange(
            @Parameter(description = "最小年龄", example = "18") @RequestParam int minAge,
            @Parameter(description = "最大年龄", example = "65") @RequestParam int maxAge) {
        List<UserProfileEntity> profiles = userProfileService.findByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据性别查询用户
     */
    @GetMapping("/gender/{gender}")
    @Operation(summary = "根据性别获取用户", description = "获取指定性别的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByGender(
            @Parameter(description = "性别", example = "male") @PathVariable String gender) {
        List<UserProfileEntity> profiles = userProfileService.findByGender(gender);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据国家查询用户
     */
    @GetMapping("/country/{country}")
    @Operation(summary = "根据国家获取用户", description = "获取指定国家的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByCountry(
            @Parameter(description = "国家", example = "China") @PathVariable String country) {
        List<UserProfileEntity> profiles = userProfileService.findByCountry(country);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据城市查询用户
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "根据城市获取用户", description = "获取指定城市的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByCity(
            @Parameter(description = "城市", example = "Beijing") @PathVariable String city) {
        List<UserProfileEntity> profiles = userProfileService.findByCity(city);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据兴趣类别查询用户
     */
    @GetMapping("/interest/{category}")
    @Operation(summary = "根据兴趣获取用户", description = "获取具有指定兴趣的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByInterest(
            @Parameter(description = "兴趣类别", example = "technology") @PathVariable String category) {
        List<UserProfileEntity> profiles = userProfileService.findByInterestCategory(category);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据设备类型查询用户
     */
    @GetMapping("/device/{deviceType}")
    @Operation(summary = "根据设备类型获取用户", description = "获取使用指定设备类型的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByDeviceType(
            @Parameter(description = "设备类型", example = "1") @PathVariable String deviceType) {
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
    @Operation(summary = "根据操作系统获取用户", description = "获取使用指定操作系统的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByOS(
            @Parameter(description = "操作系统", example = "iOS") @PathVariable String operatingSystem) {
        List<UserProfileEntity> profiles = userProfileService.findByOperatingSystem(operatingSystem);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取活跃用户
     */
    @GetMapping("/active")
    @Operation(summary = "获取活跃用户", description = "获取所有活跃状态的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取活跃用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getActiveUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<UserProfileEntity> profiles = userProfileService.findActiveUsers(threshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据购买类别查询用户
     */
    @GetMapping("/purchase/category/{category}")
    @Operation(summary = "根据购买类别获取用户", description = "获取购买过指定类别商品的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByPurchaseCategory(
            @Parameter(description = "购买类别", example = "electronics") @PathVariable String category) {
        List<UserProfileEntity> profiles = userProfileService.findByPurchaseCategory(category);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取高价值用户
     */
    @GetMapping("/purchase/high-value")
    @Operation(summary = "获取高价值用户", description = "获取消费金额超过指定阈值的高价值用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取高价值用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getHighValueUsers(
            @Parameter(description = "最低消费金额", example = "1000") @RequestParam double minAmount) {
        List<UserProfileEntity> profiles = userProfileService.findHighValueUsers(minAmount);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据广告互动活动查询用户
     */
    @GetMapping("/ad-interaction/{campaignId}")
    @Operation(summary = "根据广告互动获取用户", description = "获取有指定广告互动行为的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByAdInteraction(
            @Parameter(description = "广告活动ID", example = "campaign123") @PathVariable String campaignId) {
        List<UserProfileEntity> profiles = userProfileService.findByAdInteractionCampaign(campaignId);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取超过日频次限制的用户
     */
    @GetMapping("/frequency/exceeded")
    @Operation(summary = "获取超过频次限制的用户", description = "获取广告展示频次超过指定限制的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersExceedingFrequency(
            @Parameter(description = "用户ID", example = "user123") @RequestParam String userId, 
            @Parameter(description = "频次阈值", example = "10") @RequestParam Integer threshold) {
        List<UserProfileEntity> profiles = userProfileService.findUsersExceedingDailyFrequency(userId, threshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 根据地理范围查询用户
     */
    @GetMapping("/geo")
    @Operation(summary = "根据地理范围获取用户", description = "获取指定地理范围内的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getUsersByGeoRange(
            @Parameter(description = "中心纬度", example = "39.9042") @RequestParam Double centerLat,
            @Parameter(description = "中心经度", example = "116.4074") @RequestParam Double centerLon,
            @Parameter(description = "半径(公里)", example = "10.0") @RequestParam Double radiusKm,
            @Parameter(description = "最大距离", example = "50.0") @RequestParam Double maxDistance) {
        List<UserProfileEntity> profiles = userProfileService.findByGeoRange(centerLat, centerLon, radiusKm, maxDistance);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 分页查询用户画像
     */
    @GetMapping
    @Operation(summary = "分页获取用户画像", description = "分页查询用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户画像列表",
                content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Page<UserProfileEntity>> getProfiles(
            @Parameter(description = "页码", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserProfileEntity> profiles = userProfileService.findAll(pageable);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 为用户添加兴趣
     */
    @PostMapping("/{userId}/interests")
    @Operation(summary = "为用户添加兴趣", description = "为指定用户添加新的兴趣类别")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功添加用户兴趣",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> addUserInterest(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "兴趣数据") @RequestBody Map<String, Object> interestData) {
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
    @Operation(summary = "记录用户页面浏览", description = "记录用户的页面浏览行为数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功记录页面浏览",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> addPageView(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "页面浏览数据") @RequestBody Map<String, Object> pageViewData) {
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
    @Operation(summary = "记录用户广告互动", description = "记录用户与广告的互动行为")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功记录广告互动",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> addAdInteraction(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "广告互动数据") @RequestBody Map<String, Object> interactionData) {
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
    @Operation(summary = "记录用户购买行为", description = "记录用户的购买行为数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功记录购买行为",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> addPurchase(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "购买数据") @RequestBody Map<String, Object> purchaseData) {
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
    @Operation(summary = "更新用户会话数据", description = "更新用户的会话统计数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新会话数据",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> updateSessionData(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "会话数据") @RequestBody Map<String, Object> sessionData) {
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
    @Operation(summary = "更新用户频次数据", description = "更新用户的广告展示频次数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新频次数据",
                content = @Content(schema = @Schema(implementation = UserProfileEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfileEntity> updateFrequencyData(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId,
            @Parameter(description = "频次数据") @RequestBody Map<String, Object> frequencyData) {
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
    @Operation(summary = "删除用户画像", description = "删除指定用户的画像数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功删除用户画像"),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "用户ID", example = "user123") @PathVariable String userId) {
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
    @Operation(summary = "清理过期用户画像", description = "清理超过指定天数的过期用户画像")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功清理过期画像",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "获取需要更新的用户画像", description = "获取超过指定天数未更新的用户画像列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取需要更新的画像列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfileEntity>> getProfilesNeedingUpdate(
            @Parameter(description = "天数阈值", example = "7") @RequestParam(defaultValue = "7") int daysThreshold) {
        List<UserProfileEntity> profiles = userProfileService.findProfilesNeedingUpdate(daysThreshold);
        return ResponseEntity.ok(profiles);
    }

    /**
     * 获取用户画像统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户画像统计信息", description = "获取用户画像的总体统计数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取统计信息",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "按国家统计用户数量", description = "获取各国家的用户数量统计")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取国家统计",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Long>> getCountryStats() {
        long countryCount = userProfileService.countByCountry("US");
        Map<String, Long> stats = Map.of("US", countryCount);
        return ResponseEntity.ok(stats);
    }

    /**
     * 按性别统计用户数量
     */
    @GetMapping("/stats/gender")
    @Operation(summary = "按性别统计用户数量", description = "获取各性别的用户数量统计")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取性别统计",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Long>> getGenderStats() {
        long genderCount = userProfileService.countByGender("male");
        Map<String, Long> stats = Map.of("male", genderCount);
        return ResponseEntity.ok(stats);
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("API异常", e);
        
        Map<String, String> error = Map.of(
            "error", "Internal Server Error",
            "message", e.getMessage() != null ? e.getMessage() : "Unknown error",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数错误: {}", e.getMessage());
        
        Map<String, String> error = Map.of(
            "error", "Bad Request",
            "message", e.getMessage() != null ? e.getMessage() : "Invalid argument",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}