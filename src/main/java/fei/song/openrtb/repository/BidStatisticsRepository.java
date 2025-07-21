package fei.song.openrtb.repository;

import fei.song.openrtb.entity.BidStatisticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 竞价统计仓储接口
 * 提供对 BidStatisticsEntity 的数据访问操作
 */
@Repository
public interface BidStatisticsRepository extends MongoRepository<BidStatisticsEntity, String> {

    /**
     * 根据日期查找统计数据
     */
    List<BidStatisticsEntity> findByDate(LocalDate date);

    /**
     * 根据日期和小时查找统计数据
     */
    Optional<BidStatisticsEntity> findByDateAndHour(LocalDate date, Integer hour);

    /**
     * 根据日期范围查找统计数据
     */
    List<BidStatisticsEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据广告活动ID查找统计数据
     */
    List<BidStatisticsEntity> findByCampaignId(String campaignId);

    /**
     * 根据发布商ID查找统计数据
     */
    List<BidStatisticsEntity> findByPublisherId(String publisherId);

    /**
     * 根据广告位ID查找统计数据
     */
    List<BidStatisticsEntity> findBySlotId(String slotId);

    /**
     * 根据广告活动ID和日期范围查找统计数据
     */
    List<BidStatisticsEntity> findByCampaignIdAndDateBetween(String campaignId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据发布商ID和日期范围查找统计数据
     */
    List<BidStatisticsEntity> findByPublisherIdAndDateBetween(String publisherId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据广告位ID和日期范围查找统计数据
     */
    List<BidStatisticsEntity> findBySlotIdAndDateBetween(String slotId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据竞价请求数量范围查找统计数据
     */
    @Query("{'bidStats.bidRequests': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByBidRequestsRange(Long minRequests, Long maxRequests);

    /**
     * 根据竞价响应数量范围查找统计数据
     */
    @Query("{'bidStats.bidResponses': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByBidResponsesRange(Long minResponses, Long maxResponses);

    /**
     * 根据获胜竞价数量范围查找统计数据
     */
    @Query("{'bidStats.winningBids': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByWinningBidsRange(Long minWins, Long maxWins);

    /**
     * 根据竞价率范围查找统计数据
     */
    @Query("{'bidStats.bidRate': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByBidRateRange(Double minRate, Double maxRate);

    /**
     * 根据获胜率范围查找统计数据
     */
    @Query("{'bidStats.winRate': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByWinRateRange(Double minRate, Double maxRate);

    /**
     * 根据总收入范围查找统计数据
     */
    @Query("{'revenueStats.totalRevenue': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByTotalRevenueRange(Long minRevenue, Long maxRevenue);

    /**
     * 根据平均CPM范围查找统计数据
     */
    @Query("{'revenueStats.avgCpm': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByAvgCpmRange(Double minCpm, Double maxCpm);

    /**
     * 根据展示数量范围查找统计数据
     */
    @Query("{'performanceStats.impressions': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByImpressionsRange(Long minImpressions, Long maxImpressions);

    /**
     * 根据点击数量范围查找统计数据
     */
    @Query("{'performanceStats.clicks': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByClicksRange(Long minClicks, Long maxClicks);

    /**
     * 根据点击率范围查找统计数据
     */
    @Query("{'performanceStats.ctr': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByCtrRange(Double minCtr, Double maxCtr);

    /**
     * 根据转化数量范围查找统计数据
     */
    @Query("{'performanceStats.conversions': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByConversionsRange(Long minConversions, Long maxConversions);

    /**
     * 根据转化率范围查找统计数据
     */
    @Query("{'performanceStats.conversionRate': {$gte: ?0, $lte: ?1}}")
    List<BidStatisticsEntity> findByConversionRateRange(Double minRate, Double maxRate);

    /**
     * 根据国家查找统计数据
     */
    @Query("{'geoStats.country': ?0}")
    List<BidStatisticsEntity> findByCountry(String country);

    /**
     * 根据地区查找统计数据
     */
    @Query("{'geoStats.region': ?0}")
    List<BidStatisticsEntity> findByRegion(String region);

    /**
     * 根据城市查找统计数据
     */
    @Query("{'geoStats.city': ?0}")
    List<BidStatisticsEntity> findByCity(String city);

    /**
     * 根据设备类型查找统计数据
     */
    @Query("{'deviceStats.deviceType': ?0}")
    List<BidStatisticsEntity> findByDeviceType(String deviceType);

    /**
     * 根据操作系统查找统计数据
     */
    @Query("{'deviceStats.os': ?0}")
    List<BidStatisticsEntity> findByOperatingSystem(String os);

    /**
     * 根据浏览器查找统计数据
     */
    @Query("{'deviceStats.browser': ?0}")
    List<BidStatisticsEntity> findByBrowser(String browser);

    /**
     * 查找高竞价率的统计数据
     */
    @Query("{'bidStats.bidRate': {$gte: ?0}}")
    List<BidStatisticsEntity> findHighBidRateStats(Double minBidRate);

    /**
     * 查找高获胜率的统计数据
     */
    @Query("{'bidStats.winRate': {$gte: ?0}}")
    List<BidStatisticsEntity> findHighWinRateStats(Double minWinRate);

    /**
     * 查找高收入的统计数据
     */
    @Query("{'revenueStats.totalRevenue': {$gte: ?0}}")
    List<BidStatisticsEntity> findHighRevenueStats(Long minRevenue);

    /**
     * 查找高点击率的统计数据
     */
    @Query("{'performanceStats.ctr': {$gte: ?0}}")
    List<BidStatisticsEntity> findHighCtrStats(Double minCtr);

    /**
     * 查找高转化率的统计数据
     */
    @Query("{'performanceStats.conversionRate': {$gte: ?0}}")
    List<BidStatisticsEntity> findHighConversionRateStats(Double minConversionRate);

    /**
     * 根据多个条件组合查找统计数据（广告活动、日期、国家）
     */
    @Query("{'campaignId': ?0, 'date': {$gte: ?1, $lte: ?2}, 'geoStats.country': ?3}")
    List<BidStatisticsEntity> findByCampaignDateRangeAndCountry(String campaignId, LocalDate startDate, LocalDate endDate, String country);

    /**
     * 根据发布商、日期范围和设备类型查找统计数据
     */
    @Query("{'publisherId': ?0, 'date': {$gte: ?1, $lte: ?2}, 'deviceStats.deviceType': ?3}")
    List<BidStatisticsEntity> findByPublisherDateRangeAndDeviceType(String publisherId, LocalDate startDate, LocalDate endDate, String deviceType);

    /**
     * 统计指定日期的总竞价请求数
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': ?0 } }",
        "{ $group: { _id: null, totalBidRequests: { $sum: '$bidStats.bidRequests' } } }"
    })
    Optional<Long> getTotalBidRequestsByDate(LocalDate date);

    /**
     * 统计指定日期范围的总收入
     */
    @Aggregation(pipeline = {
        "{ $match: { 'date': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: null, totalRevenue: { $sum: '$revenueStats.totalRevenue' } } }"
    })
    Optional<Long> getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 统计指定广告活动的总展示数
     */
    @Aggregation(pipeline = {
        "{ $match: { 'campaignId': ?0 } }",
        "{ $group: { _id: null, totalImpressions: { $sum: '$performanceStats.impressions' } } }"
    })
    Optional<Long> getTotalImpressionsByCampaign(String campaignId);

    /**
     * 统计指定发布商的总点击数
     */
    @Aggregation(pipeline = {
        "{ $match: { 'publisherId': ?0 } }",
        "{ $group: { _id: null, totalClicks: { $sum: '$performanceStats.clicks' } } }"
    })
    Optional<Long> getTotalClicksByPublisher(String publisherId);

    /**
     * 按国家统计收入排行
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$geoStats.country', totalRevenue: { $sum: '$revenueStats.totalRevenue' } } }",
        "{ $sort: { totalRevenue: -1 } }",
        "{ $limit: ?0 }"
    })
    List<Object> getTopRevenueByCountry(int limit);

    /**
     * 按设备类型统计展示数排行
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$deviceStats.deviceType', totalImpressions: { $sum: '$performanceStats.impressions' } } }",
        "{ $sort: { totalImpressions: -1 } }",
        "{ $limit: ?0 }"
    })
    List<Object> getTopImpressionsByDeviceType(int limit);

    /**
     * 按广告活动统计获胜率排行
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$campaignId', avgWinRate: { $avg: '$bidStats.winRate' } } }",
        "{ $sort: { avgWinRate: -1 } }",
        "{ $limit: ?0 }"
    })
    List<Object> getTopWinRateByCampaign(int limit);

    /**
     * 按发布商统计平均CPM排行
     */
    @Aggregation(pipeline = {
        "{ $group: { _id: '$publisherId', avgCpm: { $avg: '$revenueStats.avgCpm' } } }",
        "{ $sort: { avgCpm: -1 } }",
        "{ $limit: ?0 }"
    })
    List<Object> getTopCpmByPublisher(int limit);

    /**
     * 统计指定日期的记录数量
     */
    long countByDate(LocalDate date);

    /**
     * 统计指定日期范围的记录数量
     */
    long countByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 统计指定广告活动的记录数量
     */
    long countByCampaignId(String campaignId);

    /**
     * 统计指定发布商的记录数量
     */
    long countByPublisherId(String publisherId);

    /**
     * 统计指定国家的记录数量
     */
    @Query(value = "{'geoStats.country': ?0}", count = true)
    long countByCountry(String country);

    /**
     * 统计指定设备类型的记录数量
     */
    @Query(value = "{'deviceStats.deviceType': ?0}", count = true)
    long countByDeviceType(String deviceType);

    /**
     * 删除指定日期之前的统计数据
     */
    @Query(delete = true, value = "{'date': {$lt: ?0}}")
    long deleteByDateBefore(LocalDate date);

    /**
     * 查找最近的统计数据
     */
    List<BidStatisticsEntity> findTop10ByOrderByDateDescHourDesc();

    /**
     * 查找最高收入的统计数据
     */
    List<BidStatisticsEntity> findTop10ByOrderByRevenueStatsTotalRevenueDesc();

    /**
     * 查找最高展示数的统计数据
     */
    List<BidStatisticsEntity> findTop10ByOrderByPerformanceStatsImpressionsDesc();

    /**
     * 查找最高点击率的统计数据
     */
    List<BidStatisticsEntity> findTop10ByOrderByPerformanceStatsCtrDesc();

    /**
     * 分页查询指定日期范围的统计数据
     */
    Page<BidStatisticsEntity> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * 分页查询指定广告活动的统计数据
     */
    Page<BidStatisticsEntity> findByCampaignId(String campaignId, Pageable pageable);

    /**
     * 分页查询指定发布商的统计数据
     */
    Page<BidStatisticsEntity> findByPublisherId(String publisherId, Pageable pageable);

    /**
     * 分页查询高收入的统计数据
     */
    @Query("{'revenueStats.totalRevenue': {$gte: ?0}}")
    Page<BidStatisticsEntity> findHighRevenueStats(Long minRevenue, Pageable pageable);
}