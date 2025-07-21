package fei.song.openrtb.repository;

import fei.song.openrtb.entity.BidRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 竞价请求仓储接口
 * 提供对 BidRequestEntity 的数据访问操作
 */
@Repository
public interface BidRequestRepository extends MongoRepository<BidRequestEntity, String> {

    /**
     * 根据请求ID查找竞价请求
     */
    Optional<BidRequestEntity> findByRequestId(String requestId);

    /**
     * 根据状态查找竞价请求
     */
    List<BidRequestEntity> findByStatus(String status);

    /**
     * 根据状态和时间范围查找竞价请求
     */
    List<BidRequestEntity> findByStatusAndTimestampBetween(
        String status, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 根据来源IP查找竞价请求
     */
    List<BidRequestEntity> findBySourceIp(String sourceIp);

    /**
     * 根据交易平台ID查找竞价请求
     */
    List<BidRequestEntity> findByExchangeId(String exchangeId);

    /**
     * 根据交易平台ID和时间范围查找竞价请求
     */
    Page<BidRequestEntity> findByExchangeIdAndTimestampBetween(
        String exchangeId, 
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 根据时间范围查找竞价请求
     */
    Page<BidRequestEntity> findByTimestampBetween(
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );

    /**
     * 查找处理时间超过指定阈值的请求
     */
    @Query("{'metrics.processingTimeMs': {$gt: ?0}}")
    List<BidRequestEntity> findByProcessingTimeGreaterThan(Long thresholdMs);

    /**
     * 查找有获胜竞价的请求
     */
    @Query("{'processingResult.winningBidId': {$exists: true, $ne: null}}")
    List<BidRequestEntity> findRequestsWithWinningBids();

    /**
     * 查找指定时间范围内有获胜竞价的请求
     */
    @Query("{'processingResult.winningBidId': {$exists: true, $ne: null}, 'timestamp': {$gte: ?0, $lte: ?1}}")
    List<BidRequestEntity> findRequestsWithWinningBidsBetween(
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 统计指定状态的请求数量
     */
    long countByStatus(String status);

    /**
     * 统计指定时间范围内的请求数量
     */
    long countByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定交易平台的请求数量
     */
    long countByExchangeId(String exchangeId);

    /**
     * 统计指定时间范围内指定交易平台的请求数量
     */
    long countByExchangeIdAndTimestampBetween(
        String exchangeId, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 查找处理失败的请求
     */
    @Query("{'status': 'FAILED'}")
    List<BidRequestEntity> findFailedRequests();

    /**
     * 查找超时的请求
     */
    @Query("{'status': 'TIMEOUT'}")
    List<BidRequestEntity> findTimeoutRequests();

    /**
     * 查找指定时间范围内处理失败的请求
     */
    @Query("{'status': 'FAILED', 'timestamp': {$gte: ?0, $lte: ?1}}")
    List<BidRequestEntity> findFailedRequestsBetween(
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 删除过期的请求
     */
    void deleteByExpiresAtBefore(LocalDateTime expireTime);

    /**
     * 删除指定时间之前的请求
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);

    /**
     * 查找最近的请求
     */
    List<BidRequestEntity> findTop10ByOrderByTimestampDesc();

    /**
     * 根据来源IP和时间范围统计请求数量
     */
    long countBySourceIpAndTimestampBetween(
        String sourceIp, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    /**
     * 查找匹配广告活动数量大于指定值的请求
     */
    @Query("{'processingResult.matchedCampaigns': {$gt: ?0}}")
    List<BidRequestEntity> findByMatchedCampaignsGreaterThan(Integer threshold);

    /**
     * 查找生成竞价数量大于指定值的请求
     */
    @Query("{'processingResult.generatedBids': {$gt: ?0}}")
    List<BidRequestEntity> findByGeneratedBidsGreaterThan(Integer threshold);
}