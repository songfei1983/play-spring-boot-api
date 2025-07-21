package fei.song.openrtb.controller;

import fei.song.openrtb.entity.*;
import fei.song.openrtb.service.OpenRTBDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * OpenRTB 数据管理控制器
 * 提供竞价请求、响应、广告活动、用户画像、广告位库存和统计数据的 REST API
 */
@RestController
@RequestMapping("/api/openrtb")
@CrossOrigin(origins = "*")
public class OpenRTBController {

    @Autowired
    private OpenRTBDataService openRTBDataService;

    // ==================== 竞价请求相关接口 ====================

    /**
     * 保存竞价请求
     */
    @PostMapping("/bid-requests")
    public ResponseEntity<BidRequestEntity> saveBidRequest(@RequestBody BidRequestEntity bidRequest) {
        BidRequestEntity saved = openRTBDataService.saveBidRequest(bidRequest);
        return ResponseEntity.ok(saved);
    }

    /**
     * 根据请求ID获取竞价请求
     */
    @GetMapping("/bid-requests/{requestId}")
    public ResponseEntity<BidRequestEntity> getBidRequest(@PathVariable String requestId) {
        Optional<BidRequestEntity> bidRequest = openRTBDataService.getBidRequestById(requestId);
        return bidRequest.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取指定时间范围内的竞价请求
     */
    @GetMapping("/bid-requests/time-range")
    public ResponseEntity<List<BidRequestEntity>> getBidRequestsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<BidRequestEntity> requests = openRTBDataService.getBidRequestsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(requests);
    }

    /**
     * 获取处理超时的竞价请求
     */
    @GetMapping("/bid-requests/timeout")
    public ResponseEntity<List<BidRequestEntity>> getTimeoutBidRequests(
            @RequestParam(defaultValue = "1000") Long maxProcessingTime) {
        List<BidRequestEntity> requests = openRTBDataService.getTimeoutBidRequests(maxProcessingTime);
        return ResponseEntity.ok(requests);
    }

    // ==================== 竞价响应相关接口 ====================

    /**
     * 保存竞价响应
     */
    @PostMapping("/bid-responses")
    public ResponseEntity<BidResponseEntity> saveBidResponse(@RequestBody BidResponseEntity bidResponse) {
        BidResponseEntity saved = openRTBDataService.saveBidResponse(bidResponse);
        return ResponseEntity.ok(saved);
    }

    /**
     * 根据请求ID获取竞价响应
     */
    @GetMapping("/bid-responses/request/{requestId}")
    public ResponseEntity<List<BidResponseEntity>> getBidResponsesByRequestId(@PathVariable String requestId) {
        List<BidResponseEntity> responses = openRTBDataService.getBidResponsesByRequestId(requestId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取获胜竞价响应
     */
    @GetMapping("/bid-responses/winning/{winningBidId}")
    public ResponseEntity<List<BidResponseEntity>> getWinningBidResponses(@PathVariable String winningBidId) {
        List<BidResponseEntity> responses = openRTBDataService.getWinningBidResponses(winningBidId);
        return ResponseEntity.ok(responses);
    }

    // ==================== 广告活动相关接口 ====================

    /**
     * 保存广告活动
     */
    @PostMapping("/campaigns")
    public ResponseEntity<CampaignEntity> saveCampaign(@RequestBody CampaignEntity campaign) {
        CampaignEntity saved = openRTBDataService.saveCampaign(campaign);
        return ResponseEntity.ok(saved);
    }

    /**
     * 根据活动ID获取广告活动
     */
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignEntity> getCampaign(@PathVariable String campaignId) {
        Optional<CampaignEntity> campaign = openRTBDataService.getCampaignById(campaignId);
        return campaign.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取活跃的广告活动
     */
    @GetMapping("/campaigns/active")
    public ResponseEntity<List<CampaignEntity>> getActiveCampaigns() {
        List<CampaignEntity> campaigns = openRTBDataService.getActiveCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    /**
     * 获取当前时间范围内活跃的活动
     */
    @GetMapping("/campaigns/currently-active")
    public ResponseEntity<List<CampaignEntity>> getCurrentlyActiveCampaigns() {
        List<CampaignEntity> campaigns = openRTBDataService.getCurrentlyActiveCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    // ==================== 用户画像相关接口 ====================

    /**
     * 保存用户画像
     */
    @PostMapping("/user-profiles")
    public ResponseEntity<UserProfileEntity> saveUserProfile(@RequestBody UserProfileEntity userProfile) {
        UserProfileEntity saved = openRTBDataService.saveUserProfile(userProfile);
        return ResponseEntity.ok(saved);
    }

    /**
     * 根据用户ID获取用户画像
     */
    @GetMapping("/user-profiles/{userId}")
    public ResponseEntity<UserProfileEntity> getUserProfile(@PathVariable String userId) {
        Optional<UserProfileEntity> userProfile = openRTBDataService.getUserProfileById(userId);
        return userProfile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取高价值用户
     */
    @GetMapping("/user-profiles/high-value")
    public ResponseEntity<List<UserProfileEntity>> getHighValueUsers(
            @RequestParam(defaultValue = "100000") Long minPurchaseAmount) {
        List<UserProfileEntity> users = openRTBDataService.getHighValueUsers(minPurchaseAmount);
        return ResponseEntity.ok(users);
    }

    // ==================== 广告位库存相关接口 ====================

    /**
     * 保存广告位库存
     */
    @PostMapping("/inventory")
    public ResponseEntity<InventoryEntity> saveInventory(@RequestBody InventoryEntity inventory) {
        InventoryEntity saved = openRTBDataService.saveInventory(inventory);
        return ResponseEntity.ok(saved);
    }

    /**
     * 根据广告位ID获取库存
     */
    @GetMapping("/inventory/{slotId}")
    public ResponseEntity<InventoryEntity> getInventory(@PathVariable String slotId) {
        Optional<InventoryEntity> inventory = openRTBDataService.getInventoryById(slotId);
        return inventory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取活跃的广告位
     */
    @GetMapping("/inventory/active")
    public ResponseEntity<List<InventoryEntity>> getActiveSlots() {
        List<InventoryEntity> slots = openRTBDataService.getActiveSlots();
        return ResponseEntity.ok(slots);
    }

    // ==================== 统计数据相关接口 ====================

    /**
     * 保存竞价统计
     */
    @PostMapping("/statistics")
    public ResponseEntity<BidStatisticsEntity> saveBidStatistics(@RequestBody BidStatisticsEntity statistics) {
        BidStatisticsEntity saved = openRTBDataService.saveBidStatistics(statistics);
        return ResponseEntity.ok(saved);
    }

    /**
     * 获取指定日期的统计数据
     */
    @GetMapping("/statistics/date/{date}")
    public ResponseEntity<List<BidStatisticsEntity>> getStatisticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BidStatisticsEntity> statistics = openRTBDataService.getStatisticsByDate(date);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取指定日期范围的统计数据
     */
    @GetMapping("/statistics/range")
    public ResponseEntity<List<BidStatisticsEntity>> getStatisticsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<BidStatisticsEntity> statistics = openRTBDataService.getStatisticsByDateRange(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取广告活动统计数据
     */
    @GetMapping("/statistics/campaign/{campaignId}")
    public ResponseEntity<List<BidStatisticsEntity>> getCampaignStatistics(@PathVariable String campaignId) {
        List<BidStatisticsEntity> statistics = openRTBDataService.getCampaignStatistics(campaignId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取发布商统计数据
     */
    @GetMapping("/statistics/publisher/{publisherId}")
    public ResponseEntity<List<BidStatisticsEntity>> getPublisherStatistics(@PathVariable String publisherId) {
        List<BidStatisticsEntity> statistics = openRTBDataService.getPublisherStatistics(publisherId);
        return ResponseEntity.ok(statistics);
    }

    // ==================== 业务逻辑接口 ====================

    /**
     * 竞价匹配
     */
    @GetMapping("/matching/campaigns")
    public ResponseEntity<List<CampaignEntity>> findMatchingCampaigns(
            @RequestParam String userId,
            @RequestParam String slotId,
            @RequestParam String country,
            @RequestParam String deviceType) {
        List<CampaignEntity> campaigns = openRTBDataService.findMatchingCampaigns(userId, slotId, country, deviceType);
        return ResponseEntity.ok(campaigns);
    }

    /**
     * 更新竞价统计
     */
    @PostMapping("/statistics/update")
    public ResponseEntity<String> updateBidStatistics(
            @RequestParam String campaignId,
            @RequestParam String publisherId,
            @RequestParam String slotId,
            @RequestParam boolean bidWon,
            @RequestParam Long bidPrice,
            @RequestParam String country,
            @RequestParam String deviceType) {
        openRTBDataService.updateBidStatistics(campaignId, publisherId, slotId, bidWon, bidPrice, country, deviceType);
        return ResponseEntity.ok("Statistics updated successfully");
    }

    // ==================== 缓存管理接口 ====================

    /**
     * 清除所有缓存
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearAllCaches() {
        openRTBDataService.clearAllCaches();
        return ResponseEntity.ok("All caches cleared successfully");
    }

    /**
     * 预热缓存
     */
    @PostMapping("/cache/warmup")
    public ResponseEntity<String> warmUpCaches() {
        openRTBDataService.warmUpCaches();
        return ResponseEntity.ok("Caches warmed up successfully");
    }

    // ==================== 聚合统计接口 ====================

    /**
     * 获取指定日期的总竞价请求数
     */
    @GetMapping("/analytics/total-requests/{date}")
    public ResponseEntity<Long> getTotalBidRequestsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long total = openRTBDataService.getTotalBidRequestsByDate(date);
        return ResponseEntity.ok(total);
    }

    /**
     * 获取指定日期范围的总收入
     */
    @GetMapping("/analytics/total-revenue")
    public ResponseEntity<Long> getTotalRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long totalRevenue = openRTBDataService.getTotalRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(totalRevenue);
    }

    /**
     * 获取广告活动的总展示数
     */
    @GetMapping("/analytics/campaign-impressions/{campaignId}")
    public ResponseEntity<Long> getCampaignTotalImpressions(@PathVariable String campaignId) {
        Long totalImpressions = openRTBDataService.getCampaignTotalImpressions(campaignId);
        return ResponseEntity.ok(totalImpressions);
    }

    /**
     * 获取发布商的总点击数
     */
    @GetMapping("/analytics/publisher-clicks/{publisherId}")
    public ResponseEntity<Long> getPublisherTotalClicks(@PathVariable String publisherId) {
        Long totalClicks = openRTBDataService.getPublisherTotalClicks(publisherId);
        return ResponseEntity.ok(totalClicks);
    }
}