package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentMappingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户分段映射仓储接口
 */
@Repository
public interface UserSegmentMappingRepository extends MongoRepository<UserSegmentMappingEntity, String> {

    /**
     * 根据用户ID查找所有分段映射
     */
    List<UserSegmentMappingEntity> findByUserId(String userId);

    /**
     * 根据分段ID查找所有用户映射
     */
    List<UserSegmentMappingEntity> findBySegmentId(String segmentId);

    /**
     * 根据用户ID和分段ID查找映射
     */
    Optional<UserSegmentMappingEntity> findByUserIdAndSegmentId(String userId, String segmentId);

    /**
     * 查找用户的激活分段映射
     */
    @Query("{'user_id': ?0, 'is_active': true}")
    List<UserSegmentMappingEntity> findActiveSegmentsByUserId(String userId);

    /**
     * 查找分段的激活用户映射
     */
    @Query("{'segment_id': ?0, 'is_active': true}")
    List<UserSegmentMappingEntity> findActiveUsersBySegmentId(String segmentId);

    /**
     * 根据用户ID和分段类型查找映射
     */
    @Query("{'user_id': ?0, 'segment_type': ?1}")
    List<UserSegmentMappingEntity> findByUserIdAndSegmentType(String userId, String segmentType);

    /**
     * 根据用户ID和分段类型查找激活映射
     */
    @Query("{'user_id': ?0, 'segment_type': ?1, 'is_active': true}")
    List<UserSegmentMappingEntity> findActiveByUserIdAndSegmentType(String userId, String segmentType);

    /**
     * 根据分段类型查找所有映射
     */
    List<UserSegmentMappingEntity> findBySegmentType(String segmentType);

    /**
     * 查找匹配分数高于阈值的映射
     */
    @Query("{'match_score': {$gte: ?0}}")
    List<UserSegmentMappingEntity> findByMatchScoreGreaterThanEqual(Double minScore);

    /**
     * 查找置信度高于阈值的映射
     */
    @Query("{'confidence': {$gte: ?0}}")
    List<UserSegmentMappingEntity> findByConfidenceGreaterThanEqual(Double minConfidence);

    /**
     * 查找指定时间后分配的映射
     */
    @Query("{'assigned_at': {$gte: ?0}}")
    List<UserSegmentMappingEntity> findAssignedAfter(LocalDateTime dateTime);

    /**
     * 查找指定时间前最后验证的映射
     */
    @Query("{'last_validated_at': {$lt: ?0}}")
    List<UserSegmentMappingEntity> findLastValidatedBefore(LocalDateTime dateTime);

    /**
     * 查找过期的映射
     */
    @Query("{'expires_at': {$lt: ?0}}")
    List<UserSegmentMappingEntity> findExpiredMappings(LocalDateTime now);

    /**
     * 查找即将过期的映射
     */
    @Query("{'expires_at': {$gte: ?0, $lt: ?1}}")
    List<UserSegmentMappingEntity> findExpiringMappings(LocalDateTime now, LocalDateTime threshold);

    /**
     * 查找需要重新验证的映射
     */
    @Query("{'is_active': true, 'last_validated_at': {$lt: ?0}}")
    List<UserSegmentMappingEntity> findMappingsNeedingValidation(LocalDateTime threshold);

    /**
     * 统计用户的激活分段数量
     */
    @Query(value = "{'user_id': ?0, 'is_active': true}", count = true)
    long countActiveSegmentsByUserId(String userId);

    /**
     * 统计分段的激活用户数量
     */
    @Query(value = "{'segment_id': ?0, 'is_active': true}", count = true)
    long countActiveUsersBySegmentId(String segmentId);

    /**
     * 统计指定分段类型的映射数量
     */
    @Query(value = "{'segment_type': ?0}", count = true)
    long countBySegmentType(String segmentType);

    /**
     * 统计激活映射数量
     */
    @Query(value = "{'is_active': true}", count = true)
    long countActiveMappings();

    /**
     * 检查用户是否属于指定分段
     */
    @Query(value = "{'user_id': ?0, 'segment_id': ?1, 'is_active': true}", count = true)
    long countActiveByUserIdAndSegmentId(String userId, String segmentId);

    /**
     * 停用用户的所有分段映射
     */
    @Query("{'user_id': ?0}")
    List<UserSegmentMappingEntity> findAllByUserId(String userId);

    /**
     * 停用分段的所有用户映射
     */
    @Query("{'segment_id': ?0}")
    List<UserSegmentMappingEntity> findAllBySegmentId(String segmentId);

    /**
     * 删除过期的映射
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * 删除用户的所有映射
     */
    void deleteByUserId(String userId);

    /**
     * 删除分段的所有映射
     */
    void deleteBySegmentId(String segmentId);

    /**
     * 删除指定时间前创建的非激活映射
     */
    @Query(delete = true, value = "{'is_active': false, 'created_at': {$lt: ?0}}")
    void deleteInactiveMappingsCreatedBefore(LocalDateTime dateTime);

    /**
     * 批量更新映射状态
     */
    @Query("{'user_id': ?0, 'segment_id': {$in: ?1}}")
    List<UserSegmentMappingEntity> findByUserIdAndSegmentIds(String userId, List<String> segmentIds);

    /**
     * 查找用户在指定分段列表中的映射
     */
    @Query("{'user_id': ?0, 'segment_id': {$in: ?1}, 'is_active': true}")
    List<UserSegmentMappingEntity> findActiveByUserIdAndSegmentIds(String userId, List<String> segmentIds);

    /**
     * 按匹配分数降序查找用户的分段映射
     */
    @Query(value = "{'user_id': ?0, 'is_active': true}", sort = "{'match_score': -1}")
    List<UserSegmentMappingEntity> findActiveByUserIdOrderByMatchScoreDesc(String userId);

    /**
     * 按分配时间降序查找映射
     */
    @Query(value = "{'user_id': ?0}", sort = "{'assigned_at': -1}")
    List<UserSegmentMappingEntity> findByUserIdOrderByAssignedAtDesc(String userId);
}