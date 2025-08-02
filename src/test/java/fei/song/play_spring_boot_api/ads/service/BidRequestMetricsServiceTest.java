package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.BidRequestMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BidRequestMetricsService 单体测试
 */
@ExtendWith(MockitoExtension.class)
class BidRequestMetricsServiceTest {

    @Mock
    private BidRequestMetricsRepository metricsRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private BidRequestMetricsService bidRequestMetricsService;

    private BidRequestMetrics testMetrics;

    @BeforeEach
    void setUp() {
        testMetrics = BidRequestMetrics.builder()
            .id("metrics1")
            .timestamp(LocalDateTime.now())
            .hour("2024-01-15-10")
            .date("2024-01-15")
            .adSlotType("banner")
            .dspSource("test-dsp")
            .requestCount(1L)
            .successCount(1L)
            .failureCount(0L)
            .avgResponseTime(150.0)
            .build();
    }

    @Test
    void testRecordBidRequest_Success() {
        // 准备测试数据
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(metricsRepository.findByHourAndAdSlotTypeAndDspSource(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(metricsRepository.save(any(BidRequestMetrics.class))).thenReturn(testMetrics);

        // 执行测试
        bidRequestMetricsService.recordBidRequest("banner", "test-dsp", true, 150L);

        // 验证方法调用
        verify(metricsRepository).save(any(BidRequestMetrics.class));
        verify(valueOperations).increment(anyString());
        verify(redisTemplate).expire(anyString(), eq(30L), eq(TimeUnit.DAYS));
    }

    @Test
    void testRecordBidRequest_Failed() {
        // 准备测试数据
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(metricsRepository.findByHourAndAdSlotTypeAndDspSource(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(metricsRepository.save(any(BidRequestMetrics.class))).thenReturn(testMetrics);

        // 执行测试
        bidRequestMetricsService.recordBidRequest("banner", "test-dsp", false, 50L);

        // 验证方法调用
        verify(metricsRepository).save(any(BidRequestMetrics.class));
        verify(valueOperations).increment(anyString());
    }

    @Test
    void testRecordBidRequest_ExistingMetrics() {
        // 准备测试数据 - 已存在的统计记录
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        BidRequestMetrics existingMetrics = BidRequestMetrics.builder()
            .id("existing1")
            .hour("2024-01-15-10")
            .date("2024-01-15")
            .adSlotType("banner")
            .dspSource("test-dsp")
            .requestCount(5L)
            .successCount(4L)
            .failureCount(1L)
            .avgResponseTime(120.0)
            .build();

        when(metricsRepository.findByHourAndAdSlotTypeAndDspSource(anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(existingMetrics));
        when(metricsRepository.save(any(BidRequestMetrics.class))).thenReturn(existingMetrics);

        // 执行测试
        bidRequestMetricsService.recordBidRequest("banner", "test-dsp", true, 180L);

        // 验证方法调用
        verify(metricsRepository).save(any(BidRequestMetrics.class));
        verify(valueOperations).increment(anyString());
    }

    @Test
    void testGetRealTimeStats() {
        // 准备测试数据
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(100L);
        when(metricsRepository.findByDateBetweenOrderByTimestampDesc(anyString(), anyString()))
            .thenReturn(Arrays.asList(testMetrics));
        when(metricsRepository.findByHourBetweenOrderByTimestampDesc(anyString(), anyString()))
            .thenReturn(Arrays.asList(testMetrics));

        // 执行测试
        BidRequestStatsDTO result = bidRequestMetricsService.getRealTimeStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getTotalRequests());
        assertEquals(1L, result.getTodayRequests());
        assertEquals(1L, result.getCurrentHourRequests());
        assertNotNull(result.getTimestamp());

        // 验证方法调用
        verify(valueOperations).get(anyString());
        verify(metricsRepository).findByDateBetweenOrderByTimestampDesc(anyString(), anyString());
        verify(metricsRepository).findByHourBetweenOrderByTimestampDesc(anyString(), anyString());
    }

    @Test
    void testGetRealTimeStats_WithoutRedis() {
        // 创建没有Redis的服务实例
        BidRequestMetricsService serviceWithoutRedis = new BidRequestMetricsService(metricsRepository, null);
        
        when(metricsRepository.findByDateBetweenOrderByTimestampDesc(anyString(), anyString()))
            .thenReturn(Arrays.asList(testMetrics));
        when(metricsRepository.findByHourBetweenOrderByTimestampDesc(anyString(), anyString()))
            .thenReturn(Arrays.asList(testMetrics));

        // 执行测试
        BidRequestStatsDTO result = serviceWithoutRedis.getRealTimeStats();

        // 验证结果
        assertNotNull(result);
        assertEquals(0L, result.getTotalRequests()); // 没有Redis时应该为0
        assertEquals(1L, result.getTodayRequests());
        assertEquals(1L, result.getCurrentHourRequests());
    }

    @Test
    void testGetAdSlotTypeStats() {
        // 准备测试数据
        List<BidRequestMetrics> expectedMetrics = Arrays.asList(testMetrics);
        when(metricsRepository.findByDateRangeForAdSlotStats("2024-01-15", "2024-01-15"))
            .thenReturn(expectedMetrics);

        // 执行测试
        List<AdSlotTypeStats> result = bidRequestMetricsService.getAdSlotTypeStats("2024-01-15", "2024-01-15");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("banner", result.get(0).getAdSlotType());
        assertEquals(1L, result.get(0).getTotalRequests());
        assertEquals(1L, result.get(0).getSuccessCount());

        // 验证方法调用
        verify(metricsRepository).findByDateRangeForAdSlotStats("2024-01-15", "2024-01-15");
    }

    @Test
    void testGetDspSourceStats() {
        // 准备测试数据
        List<BidRequestMetrics> expectedMetrics = Arrays.asList(testMetrics);
        when(metricsRepository.findByDateRangeForDspStats("2024-01-15", "2024-01-15"))
            .thenReturn(expectedMetrics);

        // 执行测试
        List<DspSourceStats> result = bidRequestMetricsService.getDspSourceStats("2024-01-15", "2024-01-15");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-dsp", result.get(0).getDspSource());
        assertEquals(1L, result.get(0).getTotalRequests());
        assertEquals(1L, result.get(0).getSuccessCount());

        // 验证方法调用
        verify(metricsRepository).findByDateRangeForDspStats("2024-01-15", "2024-01-15");
    }

    @Test
    void testCleanupExpiredData() {
        // 执行测试
        bidRequestMetricsService.cleanupExpiredData(30);

        // 验证方法调用
        verify(metricsRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }

    @Test
    void testRecordBidRequest_WithNullParameters() {
        // 执行测试 - 测试空参数处理
        assertDoesNotThrow(() -> {
            bidRequestMetricsService.recordBidRequest(null, "test-dsp", true, 150L);
        });

        assertDoesNotThrow(() -> {
            bidRequestMetricsService.recordBidRequest("banner", null, true, 150L);
        });
    }

    @Test
    void testRecordBidRequest_WithZeroResponseTime() {
        // 准备测试数据
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(metricsRepository.findByHourAndAdSlotTypeAndDspSource(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(metricsRepository.save(any(BidRequestMetrics.class))).thenReturn(testMetrics);

        // 执行测试
        bidRequestMetricsService.recordBidRequest("banner", "test-dsp", true, 0L);

        // 验证方法调用
        verify(metricsRepository).save(any(BidRequestMetrics.class));
    }

    @Test
    void testRecordBidRequest_WithNegativeResponseTime() {
        // 准备测试数据
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(metricsRepository.findByHourAndAdSlotTypeAndDspSource(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(metricsRepository.save(any(BidRequestMetrics.class))).thenReturn(testMetrics);

        // 执行测试
        bidRequestMetricsService.recordBidRequest("banner", "test-dsp", true, -1L);

        // 验证方法调用
        verify(metricsRepository).save(any(BidRequestMetrics.class));
    }

    @Test
    void testGetRealTimeStats_Exception() {
        // 模拟redisTemplate抛出异常
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis error"));

        // 执行测试
        BidRequestStatsDTO result = bidRequestMetricsService.getRealTimeStats();

        // 验证结果 - 应该返回空统计数据
        assertNotNull(result);
        assertEquals(0L, result.getTotalRequests());
        assertEquals(0L, result.getTodayRequests());
        assertEquals(0L, result.getCurrentHourRequests());
        assertEquals(0.0, result.getSuccessRate());
        assertEquals(0.0, result.getAvgResponseTime());
    }

    @Test
    void testGetAdSlotTypeStats_Exception() {
        // 准备测试数据 - 模拟异常
        when(metricsRepository.findByDateRangeForAdSlotStats(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database error"));

        // 执行测试
        List<AdSlotTypeStats> result = bidRequestMetricsService.getAdSlotTypeStats("2024-01-15", "2024-01-15");

        // 验证结果 - 应该返回空列表
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDspSourceStats_Exception() {
        // 准备测试数据 - 模拟异常
        when(metricsRepository.findByDateRangeForDspStats(anyString(), anyString()))
            .thenThrow(new RuntimeException("Database error"));

        // 执行测试
        List<DspSourceStats> result = bidRequestMetricsService.getDspSourceStats("2024-01-15", "2024-01-15");

        // 验证结果 - 应该返回空列表
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCleanupExpiredData_Exception() {
        // 准备测试数据 - 模拟异常
        doThrow(new RuntimeException("Database error"))
            .when(metricsRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));

        // 执行测试 - 应该不抛出异常
        assertDoesNotThrow(() -> {
            bidRequestMetricsService.cleanupExpiredData(30);
        });
    }
}