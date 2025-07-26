package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.domain.model.BidRequestMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Bid Request 统计指标仓储接口
 */
@Repository
public interface BidRequestMetricsRepository extends MongoRepository<BidRequestMetrics, String> {
    
    /**
     * 按日期范围查询
     */
    List<BidRequestMetrics> findByDateBetweenOrderByTimestampDesc(String startDate, String endDate);
    
    /**
     * 按小时范围查询
     */
    List<BidRequestMetrics> findByHourBetweenOrderByTimestampDesc(String startHour, String endHour);
    
    /**
     * 按日期、广告位类型和DSP来源查询
     */
    Optional<BidRequestMetrics> findByHourAndAdSlotTypeAndDspSource(
        String hour, String adSlotType, String dspSource);
    
    /**
     * 按日期查询总计
     */
    @Query(value = "{ 'date': { $gte: ?0, $lte: ?1 } }", 
           fields = "{ 'requestCount': 1, 'successCount': 1, 'failureCount': 1, 'avgResponseTime': 1 }")
    List<BidRequestMetrics> findMetricsByDateRange(String startDate, String endDate);
    
    /**
     * 按广告位类型统计
     */
    @Query(value = "{ 'date': { $gte: ?0, $lte: ?1 } }")
    List<BidRequestMetrics> findByDateRangeForAdSlotStats(String startDate, String endDate);
    
    /**
     * 按DSP来源统计
     */
    @Query(value = "{ 'date': { $gte: ?0, $lte: ?1 } }")
    List<BidRequestMetrics> findByDateRangeForDspStats(String startDate, String endDate);
    
    /**
     * 删除过期数据
     */
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
}