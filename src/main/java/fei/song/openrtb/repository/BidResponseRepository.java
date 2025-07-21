package fei.song.openrtb.repository;

import fei.song.openrtb.entity.BidResponseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 竞价响应仓储接口
 * 提供对 BidResponseEntity 的数据访问操作
 */
@Repository
public interface BidResponseRepository extends MongoRepository<BidResponseEntity, String> {

    /**
     * 根据请求ID查找竞价响应
     */
    List<BidResponseEntity> findByRequestId(String requestId);

    /**
     * 根据响应ID查找竞价响应
     */
    Optional<BidResponseEntity> findByResponseId(String responseId);

    /**
     * 根据请求ID和时间范围查找竞价响应
     */
    List<BidResponseEntity> findByRequestIdAndTimestampBetween(
        String requestId, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 根据时间范围查找竞价响应
     */
    Page<BidResponseEntity> findByTimestampBetween(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 根据获胜竞价ID查找响应
     */
    @Query("{'bidResult.winningBidId': ?0}")
    List<BidResponseEntity> findByWinningBidId(String winningBidId);

    /**
     * 根据获胜广告活动ID查找响应
     */
    @Query("{'bidResult.winningCampaignId': ?0}")
    List<BidResponseEntity> findByWinningCampaignId(String campaignId);

    /**
     * 根据获胜广告主ID查找响应
     */
    @Query("{'bidResult.winningAdvertiserId': ?0}")
    List<BidResponseEntity> findByWinningAdvertiserId(String advertiserId);

    /**
     * 查找有竞价的响应
     */
    @Query("{'bidResult.hasBid': true}")
    List<BidResponseEntity> findResponsesWithBids();

    /**
     * 查找无竞价的响应
     */
    @Query("{'bidResult.hasBid': false}")
    List<BidResponseEntity> findResponsesWithoutBids();

    /**
     * 根据时间范围查找有竞价的响应
     */
    @Query("{'bidResult.hasBid': true, 'timestamp': {$gte: ?0, $lte: ?1}}")
    List<BidResponseEntity> findResponsesWithBidsBetween(
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 根据获胜价格范围查找响应
     */
    @Query("{'bidResult.winningPrice': {$gte: ?0, $lte: ?1}}")
    List<BidResponseEntity> findByWinningPriceBetween(Long minPrice, Long maxPrice);

    /**
     * 查找获胜价格大于指定值的响应
     */
    @Query("{'bidResult.winningPrice': {$gt: ?0}}")
    List<BidResponseEntity> findByWinningPriceGreaterThan(Long price);

    /**
     * 根据货币查找响应
     */
    @Query("{'bidResult.currency': ?0}")
    List<BidResponseEntity> findByCurrency(String currency);

    /**
     * 查找响应构建时间超过指定阈值的响应
     */
    @Query("{'metrics.responseBuildingTimeMs': {$gt: ?0}}")
    List<BidResponseEntity> findByResponseBuildingTimeGreaterThan(Long thresholdMs);

    /**
     * 查找总处理时间超过指定阈值的响应
     */
    @Query("{'metrics.totalProcessingTimeMs': {$gt: ?0}}")
    List<BidResponseEntity> findByTotalProcessingTimeGreaterThan(Long thresholdMs);

    /**
     * 查找响应大小超过指定阈值的响应
     */
    @Query("{'metrics.responseSizeBytes': {$gt: ?0}}")
    List<BidResponseEntity> findByResponseSizeGreaterThan(Long sizeBytes);

    /**
     * 统计指定时间范围内的响应数量
     */
    long countByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计有竞价的响应数量
     */
    @Query(value = "{'bidResult.hasBid': true}", count = true)
    long countResponsesWithBids();

    /**
     * 统计无竞价的响应数量
     */
    @Query(value = "{'bidResult.hasBid': false}", count = true)
    long countResponsesWithoutBids();

    /**
     * 统计指定时间范围内有竞价的响应数量
     */
    @Query(value = "{'bidResult.hasBid': true, 'timestamp': {$gte: ?0, $lte: ?1}}", count = true)
    long countResponsesWithBidsBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定广告活动的获胜响应数量
     */
    @Query(value = "{'bidResult.winningCampaignId': ?0}", count = true)
    long countByWinningCampaignId(String campaignId);

    /**
     * 统计指定广告主的获胜响应数量
     */
    @Query(value = "{'bidResult.winningAdvertiserId': ?0}", count = true)
    long countByWinningAdvertiserId(String advertiserId);

    /**
     * 删除过期的响应
     */
    void deleteByExpiresAtBefore(LocalDateTime expireTime);

    /**
     * 删除指定时间之前的响应
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);

    /**
     * 查找最近的响应
     */
    List<BidResponseEntity> findTop10ByOrderByTimestampDesc();

    /**
     * 根据请求ID统计响应数量
     */
    long countByRequestId(String requestId);

    /**
     * 查找竞价数量大于指定值的响应
     */
    @Query("{'bidResult.totalBids': {$gt: ?0}}")
    List<BidResponseEntity> findByTotalBidsGreaterThan(Integer threshold);

    /**
     * 查找指定时间范围内的最高获胜价格
     */
    @Query(value = "{'timestamp': {$gte: ?0, $lte: ?1}}", sort = "{'bidResult.winningPrice': -1}")
    List<BidResponseEntity> findTopByTimestampBetweenOrderByWinningPriceDesc(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 查找指定时间范围内的最低获胜价格
     */
    @Query(value = "{'timestamp': {$gte: ?0, $lte: ?1}}", sort = "{'bidResult.winningPrice': 1}")
    List<BidResponseEntity> findTopByTimestampBetweenOrderByWinningPriceAsc(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );
}