package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.BidRequestMetricsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Bid Request 统计服务
 */
@Slf4j
@Service
public class BidRequestMetricsService {
    
    private final BidRequestMetricsRepository metricsRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public BidRequestMetricsService(
            BidRequestMetricsRepository metricsRepository,
            @Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.metricsRepository = metricsRepository;
        this.redisTemplate = redisTemplate;
    }
    
    private static final String REDIS_KEY_PREFIX = "bid_request_metrics:";
    private static final String COUNTER_KEY = "bid_request_counter";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
    
    /**
     * 记录Bid Request
     */
    public void recordBidRequest(String adSlotType, String dspSource, boolean success, long responseTime) {
        try {
            // 增加Redis计数器
            if (redisTemplate != null) {
                incrementRedisCounter();
            }
            
            // 异步保存到MongoDB
            saveBidRequestMetrics(adSlotType, dspSource, success, responseTime);
            
        } catch (Exception e) {
            log.error("记录Bid Request统计失败", e);
        }
    }
    
    /**
     * 获取实时统计数据
     */
    public BidRequestStatsDTO getRealTimeStats() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String today = now.format(DATE_FORMATTER);
            String currentHour = now.format(HOUR_FORMATTER);
            
            // 从Redis获取实时计数
            Long totalRequests = redisTemplate != null ? getTotalRequestsFromRedis() : 0L;
            
            // 从MongoDB获取今日和当前小时数据
            List<BidRequestMetrics> todayMetrics = metricsRepository.findByDateBetweenOrderByTimestampDesc(today, today);
            List<BidRequestMetrics> hourMetrics = metricsRepository.findByHourBetweenOrderByTimestampDesc(currentHour, currentHour);
            
            Long todayRequests = todayMetrics.stream().mapToLong(BidRequestMetrics::getRequestCount).sum();
            Long currentHourRequests = hourMetrics.stream().mapToLong(BidRequestMetrics::getRequestCount).sum();
            
            // 计算成功率和平均响应时间
            Double successRate = calculateSuccessRate(todayMetrics);
            Double avgResponseTime = calculateAvgResponseTime(todayMetrics);
            
            return BidRequestStatsDTO.builder()
                    .totalRequests(totalRequests != null ? totalRequests : 0L)
                    .todayRequests(todayRequests)
                    .currentHourRequests(currentHourRequests)
                    .successRate(successRate)
                    .avgResponseTime(avgResponseTime)
                    .timestamp(now)
                    .build();
                    
        } catch (Exception e) {
            log.error("获取实时统计数据失败", e);
            return createEmptyStats();
        }
    }
    
    /**
     * 获取广告位类型统计
     */
    public List<AdSlotTypeStats> getAdSlotTypeStats(String startDate, String endDate) {
        try {
            List<BidRequestMetrics> metrics = metricsRepository.findByDateRangeForAdSlotStats(startDate, endDate);
            
            Map<String, List<BidRequestMetrics>> groupedByAdSlot = metrics.stream()
                    .collect(Collectors.groupingBy(BidRequestMetrics::getAdSlotType));
            
            return groupedByAdSlot.entrySet().stream()
                    .map(entry -> {
                        String adSlotType = entry.getKey();
                        List<BidRequestMetrics> adSlotMetrics = entry.getValue();
                        
                        Long totalRequests = adSlotMetrics.stream().mapToLong(BidRequestMetrics::getRequestCount).sum();
                        Long successCount = adSlotMetrics.stream().mapToLong(BidRequestMetrics::getSuccessCount).sum();
                        Double successRate = totalRequests > 0 ? (double) successCount / totalRequests * 100 : 0.0;
                        
                        return AdSlotTypeStats.builder()
                                .adSlotType(adSlotType)
                                .totalRequests(totalRequests)
                                .successCount(successCount)
                                .successRate(successRate)
                                .build();
                    })
                    .sorted((a, b) -> Long.compare(b.getTotalRequests(), a.getTotalRequests()))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取广告位类型统计失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取DSP来源统计
     */
    public List<DspSourceStats> getDspSourceStats(String startDate, String endDate) {
        try {
            List<BidRequestMetrics> metrics = metricsRepository.findByDateRangeForDspStats(startDate, endDate);
            
            Map<String, List<BidRequestMetrics>> groupedByDsp = metrics.stream()
                    .collect(Collectors.groupingBy(BidRequestMetrics::getDspSource));
            
            return groupedByDsp.entrySet().stream()
                    .map(entry -> {
                        String dspSource = entry.getKey();
                        List<BidRequestMetrics> dspMetrics = entry.getValue();
                        
                        Long totalRequests = dspMetrics.stream().mapToLong(BidRequestMetrics::getRequestCount).sum();
                        Long successCount = dspMetrics.stream().mapToLong(BidRequestMetrics::getSuccessCount).sum();
                        Double successRate = totalRequests > 0 ? (double) successCount / totalRequests * 100 : 0.0;
                        
                        return DspSourceStats.builder()
                                .dspSource(dspSource)
                                .totalRequests(totalRequests)
                                .successCount(successCount)
                                .successRate(successRate)
                                .build();
                    })
                    .sorted((a, b) -> Long.compare(b.getTotalRequests(), a.getTotalRequests()))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("获取DSP来源统计失败", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 清理过期数据
     */
    public void cleanupExpiredData(int retentionDays) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            metricsRepository.deleteByCreatedAtBefore(cutoffDate);
            log.info("清理了{}天前的过期数据", retentionDays);
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
        }
    }
    
    // 私有方法
    
    private void incrementRedisCounter() {
        if (redisTemplate != null) {
            String key = REDIS_KEY_PREFIX + COUNTER_KEY;
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
        }
    }
    
    private Long getTotalRequestsFromRedis() {
        if (redisTemplate != null) {
            String key = REDIS_KEY_PREFIX + COUNTER_KEY;
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Long.valueOf(value.toString()) : 0L;
        }
        return 0L;
    }
    
    private void saveBidRequestMetrics(String adSlotType, String dspSource, boolean success, long responseTime) {
        LocalDateTime now = LocalDateTime.now();
        String hour = now.format(HOUR_FORMATTER);
        String date = now.format(DATE_FORMATTER);
        
        // 查找或创建当前小时的统计记录
        Optional<BidRequestMetrics> existingMetrics = metricsRepository
                .findByHourAndAdSlotTypeAndDspSource(hour, adSlotType, dspSource);
        
        BidRequestMetrics metrics;
        if (existingMetrics.isPresent()) {
            metrics = existingMetrics.get();
            metrics.setRequestCount(metrics.getRequestCount() + 1);
            if (success) {
                metrics.setSuccessCount(metrics.getSuccessCount() + 1);
            } else {
                metrics.setFailureCount(metrics.getFailureCount() + 1);
            }
            // 更新平均响应时间
            long totalResponseTime = (long) (metrics.getAvgResponseTime() * (metrics.getRequestCount() - 1)) + responseTime;
            metrics.setAvgResponseTime((double) totalResponseTime / metrics.getRequestCount());
            metrics.setUpdatedAt(now);
        } else {
            metrics = BidRequestMetrics.builder()
                    .hour(hour)
                    .date(date)
                    .adSlotType(adSlotType)
                    .dspSource(dspSource)
                    .requestCount(1L)
                    .successCount(success ? 1L : 0L)
                    .failureCount(success ? 0L : 1L)
                    .avgResponseTime((double) responseTime)
                    .timestamp(now)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
        }
        
        metricsRepository.save(metrics);
    }
    
    private Double calculateSuccessRate(List<BidRequestMetrics> metrics) {
        if (metrics.isEmpty()) return 0.0;
        
        long totalRequests = metrics.stream().mapToLong(BidRequestMetrics::getRequestCount).sum();
        long totalSuccess = metrics.stream().mapToLong(BidRequestMetrics::getSuccessCount).sum();
        
        return totalRequests > 0 ? (double) totalSuccess / totalRequests * 100 : 0.0;
    }
    
    private Double calculateAvgResponseTime(List<BidRequestMetrics> metrics) {
        if (metrics.isEmpty()) return 0.0;
        
        return metrics.stream()
                .mapToDouble(BidRequestMetrics::getAvgResponseTime)
                .average()
                .orElse(0.0);
    }
    
    private BidRequestStatsDTO createEmptyStats() {
        return BidRequestStatsDTO.builder()
                .totalRequests(0L)
                .todayRequests(0L)
                .currentHourRequests(0L)
                .successRate(0.0)
                .avgResponseTime(0.0)
                .timestamp(LocalDateTime.now())
                .build();
    }
}