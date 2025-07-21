package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.BidStatisticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 竞价统计仓储接口
 */
@Repository
public interface BidStatisticsRepository extends MongoRepository<BidStatisticsEntity, String> {

    /**
     * 根据日期和活动ID查找统计
     */
    Optional<BidStatisticsEntity> findByDateAndCampaignId(LocalDate date, String campaignId);

    /**
     * 根据日期和发布商ID查找统计
     */
    Optional<BidStatisticsEntity> findByDateAndPublisherId(LocalDate date, String publisherId);

    /**
     * 根据日期、小时和活动ID查找统计
     */
    Optional<BidStatisticsEntity> findByDateAndHourAndCampaignId(LocalDate date, Integer hour, String campaignId);

    /**
     * 根据活动ID查找统计
     */
    List<BidStatisticsEntity> findByCampaignId(String campaignId);

    /**
     * 根据发布商ID查找统计
     */
    List<BidStatisticsEntity> findByPublisherId(String publisherId);

    /**
     * 根据广告位ID查找统计
     */
    List<BidStatisticsEntity> findByPlacementId(String placementId);

    /**
     * 根据日期范围查找统计
     */
    List<BidStatisticsEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据日期范围和活动ID查找统计
     */
    List<BidStatisticsEntity> findByDateBetweenAndCampaignId(LocalDate startDate, LocalDate endDate, String campaignId);

    /**
     * 根据日期范围和发布商ID查找统计
     */
    List<BidStatisticsEntity> findByDateBetweenAndPublisherId(LocalDate startDate, LocalDate endDate, String publisherId);

    /**
     * 查找高收入的统计记录
     */
    @Query("{'revenue_stats.total_revenue': {$gte: ?0}}")
    List<BidStatisticsEntity> findByRevenueGreaterThanEqual(BigDecimal minRevenue);

    /**
     * 查找高点击率的统计记录
     */
    @Query("{'performance_stats.ctr': {$gte: ?0}}")
    List<BidStatisticsEntity> findByHighCtr(Double minCtr);

    /**
     * 查找高转化率的统计记录
     */
    @Query("{'performance_stats.conversion_rate': {$gte: ?0}}")
    List<BidStatisticsEntity> findByHighConversionRate(Double minConversionRate);

    /**
     * 查找高获胜率的统计记录
     */
    @Query("{'bid_stats.win_rate': {$gte: ?0}}")
    List<BidStatisticsEntity> findByHighWinRate(Double minWinRate);

    /**
     * 查找高填充率的统计记录
     */
    @Query("{'bid_stats.fill_rate': {$gte: ?0}}")
    List<BidStatisticsEntity> findByHighFillRate(Double minFillRate);

    /**
     * 聚合查询：按活动ID汇总收入
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$campaign_id', totalRevenue: { $sum: '$revenue_stats.total_revenue' }, totalImpressions: { $sum: '$bid_stats.impressions' } } }",
        "{ $sort: { totalRevenue: -1 } }"
    })
    List<CampaignRevenueAggregation> aggregateRevenueByCampaign(LocalDate startDate, LocalDate endDate);

    /**
     * 聚合查询：按发布商ID汇总统计
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$publisher_id', totalRevenue: { $sum: '$revenue_stats.total_revenue' }, totalRequests: { $sum: '$bid_stats.bid_requests' }, totalImpressions: { $sum: '$bid_stats.impressions' } } }",
        "{ $sort: { totalRevenue: -1 } }"
    })
    List<PublisherStatsAggregation> aggregateStatsByPublisher(LocalDate startDate, LocalDate endDate);

    /**
     * 聚合查询：按日期汇总统计
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$date', totalRevenue: { $sum: '$revenue_stats.total_revenue' }, totalImpressions: { $sum: '$bid_stats.impressions' }, totalClicks: { $sum: '$bid_stats.clicks' }, avgCtr: { $avg: '$performance_stats.ctr' } } }",
        "{ $sort: { _id: 1 } }"
    })
    List<DailyStatsAggregation> aggregateStatsByDate(LocalDate startDate, LocalDate endDate);

    /**
     * 聚合查询：按小时汇总统计
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': ?0 } }",
        "{ $group: { _id: '$hour', totalRevenue: { $sum: '$revenue_stats.total_revenue' }, totalImpressions: { $sum: '$bid_stats.impressions' }, avgResponseTime: { $avg: '$performance_stats.avg_response_time' } } }",
        "{ $sort: { _id: 1 } }"
    })
    List<HourlyStatsAggregation> aggregateStatsByHour(LocalDate date);

    /**
     * 统计指定日期范围内的总收入
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: null, totalRevenue: { $sum: '$revenue_stats.total_revenue' } } }"
    })
    BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate);

    /**
     * 统计指定活动的总展示数
     */
    @Aggregation(pipeline = {
        "{ $match: { 'campaign_id': ?0, 'date': { $gte: ?1, $lte: ?2 } } }",
        "{ $group: { _id: null, totalImpressions: { $sum: '$bid_stats.impressions' } } }"
    })
    Long getTotalImpressionsByCampaign(String campaignId, LocalDate startDate, LocalDate endDate);

    /**
     * 删除指定日期之前的统计数据
     */
    void deleteByDateBefore(LocalDate date);

    // 聚合结果接口
    interface CampaignRevenueAggregation {
        String getId();
        BigDecimal getTotalRevenue();
        Long getTotalImpressions();
    }

    interface PublisherStatsAggregation {
        String getId();
        BigDecimal getTotalRevenue();
        Long getTotalRequests();
        Long getTotalImpressions();
    }

    interface DailyStatsAggregation {
        LocalDate getId();
        BigDecimal getTotalRevenue();
        Long getTotalImpressions();
        Long getTotalClicks();
        Double getAvgCtr();
    }

    interface HourlyStatsAggregation {
        Integer getId();
        BigDecimal getTotalRevenue();
        Long getTotalImpressions();
        Double getAvgResponseTime();
    }
}