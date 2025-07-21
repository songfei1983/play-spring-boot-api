package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.BidRequestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 竞价请求仓储接口
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
     * 根据时间范围查找竞价请求
     */
    List<BidRequestEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据来源IP查找竞价请求
     */
    List<BidRequestEntity> findBySourceIp(String sourceIp);

    /**
     * 根据交易平台ID查找竞价请求
     */
    List<BidRequestEntity> findByExchangeId(String exchangeId);

    /**
     * 查找处理时间超过指定阈值的请求
     */
    @Query("{'processing_time_ms': {$gt: ?0}}")
    List<BidRequestEntity> findByProcessingTimeGreaterThan(Long thresholdMs);

    /**
     * 查找指定时间之前的过期请求
     */
    List<BidRequestEntity> findByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * 统计指定时间范围内的请求数量
     */
    @Query(value = "{'timestamp': {$gte: ?0, $lte: ?1}}", count = true)
    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据状态统计请求数量
     */
    long countByStatus(String status);

    /**
     * 删除过期的请求
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}