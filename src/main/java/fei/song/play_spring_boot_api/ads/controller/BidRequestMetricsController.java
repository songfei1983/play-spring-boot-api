package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.domain.model.AdSlotTypeStats;
import fei.song.play_spring_boot_api.ads.domain.model.BidRequestStatsDTO;
import fei.song.play_spring_boot_api.ads.domain.model.DspSourceStats;
import fei.song.play_spring_boot_api.ads.service.BidRequestMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Bid Request 统计指标控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bid-request-metrics")
@RequiredArgsConstructor
@Tag(name = "Bid Request Metrics", description = "Bid Request 统计指标API")
public class BidRequestMetricsController {
    
    private final BidRequestMetricsService metricsService;
    
    /**
     * 获取实时统计数据
     */
    @GetMapping("/realtime")
    @Operation(summary = "获取实时统计数据", description = "获取Bid Request的实时统计数据，包括总请求数、今日请求数、当前小时请求数、成功率和平均响应时间")
    public ResponseEntity<BidRequestStatsDTO> getRealTimeStats() {
        try {
            BidRequestStatsDTO stats = metricsService.getRealTimeStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取实时统计数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取广告位类型统计
     */
    @GetMapping("/ad-slot-types")
    @Operation(summary = "获取广告位类型统计", description = "按广告位类型统计Bid Request数据")
    public ResponseEntity<List<AdSlotTypeStats>> getAdSlotTypeStats(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            // 默认查询最近7天
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(7);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            List<AdSlotTypeStats> stats = metricsService.getAdSlotTypeStats(startDateStr, endDateStr);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取广告位类型统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取DSP来源统计
     */
    @GetMapping("/dsp-sources")
    @Operation(summary = "获取DSP来源统计", description = "按DSP来源统计Bid Request数据")
    public ResponseEntity<List<DspSourceStats>> getDspSourceStats(
            @Parameter(description = "开始日期", example = "2024-01-01")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            
            @Parameter(description = "结束日期", example = "2024-01-31")
            @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        try {
            // 默认查询最近7天
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(7);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            
            String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            List<DspSourceStats> stats = metricsService.getDspSourceStats(startDateStr, endDateStr);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取DSP来源统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 手动记录Bid Request（用于测试）
     */
    @PostMapping("/record")
    @Operation(summary = "记录Bid Request", description = "手动记录一个Bid Request（主要用于测试）")
    public ResponseEntity<String> recordBidRequest(
            @Parameter(description = "广告位类型", example = "banner")
            @RequestParam String adSlotType,
            
            @Parameter(description = "DSP来源", example = "google")
            @RequestParam String dspSource,
            
            @Parameter(description = "是否成功", example = "true")
            @RequestParam boolean success,
            
            @Parameter(description = "响应时间(毫秒)", example = "50")
            @RequestParam(defaultValue = "50") long responseTime) {
        
        try {
            metricsService.recordBidRequest(adSlotType, dspSource, success, responseTime);
            return ResponseEntity.ok("Bid Request记录成功");
        } catch (Exception e) {
            log.error("记录Bid Request失败", e);
            return ResponseEntity.internalServerError().body("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 清理过期数据
     */
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理过期数据", description = "清理指定天数之前的过期统计数据")
    public ResponseEntity<String> cleanupExpiredData(
            @Parameter(description = "保留天数", example = "30")
            @RequestParam(defaultValue = "30") int retentionDays) {
        
        try {
            metricsService.cleanupExpiredData(retentionDays);
            return ResponseEntity.ok("过期数据清理完成");
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
            return ResponseEntity.internalServerError().body("清理失败: " + e.getMessage());
        }
    }
}