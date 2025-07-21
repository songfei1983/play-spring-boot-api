package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.BidResponseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 竞价响应仓储接口
 */
@Repository
public interface BidResponseRepository extends MongoRepository<BidResponseEntity, String> {

    /**
     * 根据请求ID查找竞价响应
     */
    Optional<BidResponseEntity> findByRequestId(String requestId);

    /**
     * 根据响应ID查找竞价响应
     */
    Optional<BidResponseEntity> findByResponseId(String responseId);

    /**
     * 根据时间范围查找竞价响应
     */
    List<BidResponseEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据获胜竞价ID查找响应
     */
    @Query("{'bid_result.winning_bid_id': ?0}")
    List<BidResponseEntity> findByWinningBidId(String winningBidId);

    /**
     * 查找处理时间超过指定阈值的响应
     */
    @Query("{'metrics.processing_time_ms': {$gt: ?0}}")
    List<BidResponseEntity> findByProcessingTimeGreaterThan(Long thresholdMs);

    /**
     * 查找有竞价的响应
     */
    @Query("{'bid_result.total_bids': {$gt: 0}}")
    List<BidResponseEntity> findResponsesWithBids();

    /**
     * 查找无竞价的响应
     */
    @Query("{'bid_result.total_bids': 0}")
    List<BidResponseEntity> findResponsesWithoutBids();

    /**
     * 统计指定时间范围内的响应数量
     */
    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 统计有竞价的响应数量
     */
    @Query(value = "{'bid_result.total_bids': {$gt: 0}}", count = true)
    long countResponsesWithBids();

    /**
     * 删除过期的响应
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * 查找平均处理时间
     */
    @Query(value = "[{$group: {_id: null, avgProcessingTime: {$avg: '$metrics.processing_time_ms'}}}]")
    Double findAverageProcessingTime();
}