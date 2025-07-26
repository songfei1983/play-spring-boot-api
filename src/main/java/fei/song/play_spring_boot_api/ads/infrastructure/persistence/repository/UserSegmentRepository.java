package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户分段仓储接口
 */
@Repository
public interface UserSegmentRepository extends MongoRepository<UserSegmentEntity, String> {

    /**
     * 根据分段名称查找分段
     */
    Optional<UserSegmentEntity> findBySegmentName(String segmentName);

    /**
     * 根据分段类型查找分段
     */
    List<UserSegmentEntity> findBySegmentType(String segmentType);

    /**
     * 根据状态查找分段
     */
    List<UserSegmentEntity> findByStatus(String status);

    /**
     * 查找激活的分段
     */
    @Query("{'status': 'ACTIVE'}")
    List<UserSegmentEntity> findActiveSegments();

    /**
     * 根据分段类型和状态查找分段
     */
    List<UserSegmentEntity> findBySegmentTypeAndStatus(String segmentType, String status);

    /**
     * 根据优先级范围查找分段
     */
    @Query("{'priority': {$gte: ?0, $lte: ?1}}")
    List<UserSegmentEntity> findByPriorityRange(Integer minPriority, Integer maxPriority);

    /**
     * 根据标签查找分段
     */
    @Query("{'tags': {$in: ?0}}")
    List<UserSegmentEntity> findByTags(List<String> tags);

    /**
     * 根据创建者查找分段
     */
    List<UserSegmentEntity> findByCreatedBy(String createdBy);

    /**
     * 查找指定时间后创建的分段
     */
    @Query("{'created_at': {$gte: ?0}}")
    List<UserSegmentEntity> findCreatedAfter(LocalDateTime dateTime);

    /**
     * 查找指定时间后更新的分段
     */
    @Query("{'updated_at': {$gte: ?0}}")
    List<UserSegmentEntity> findUpdatedAfter(LocalDateTime dateTime);

    /**
     * 根据分段名称模糊查找
     */
    @Query("{'segment_name': {$regex: ?0, $options: 'i'}}")
    List<UserSegmentEntity> findBySegmentNameContaining(String keyword);

    /**
     * 根据描述模糊查找
     */
    @Query("{'description': {$regex: ?0, $options: 'i'}}")
    List<UserSegmentEntity> findByDescriptionContaining(String keyword);

    /**
     * 查找包含特定规则字段的分段
     */
    @Query("{'rules.field': ?0}")
    List<UserSegmentEntity> findByRuleField(String field);

    /**
     * 查找包含特定规则操作符的分段
     */
    @Query("{'rules.operator': ?0}")
    List<UserSegmentEntity> findByRuleOperator(String operator);

    /**
     * 按优先级降序查找激活分段
     */
    @Query(value = "{'status': 'ACTIVE'}", sort = "{'priority': -1}")
    List<UserSegmentEntity> findActiveSegmentsByPriorityDesc();

    /**
     * 按创建时间降序查找分段
     */
    @Query(sort = "{'created_at': -1}")
    List<UserSegmentEntity> findAllOrderByCreatedAtDesc();

    /**
     * 统计指定状态的分段数量
     */
    @Query(value = "{'status': ?0}", count = true)
    long countByStatus(String status);

    /**
     * 统计指定类型的分段数量
     */
    @Query(value = "{'segment_type': ?0}", count = true)
    long countBySegmentType(String segmentType);

    /**
     * 统计激活分段数量
     */
    @Query(value = "{'status': 'ACTIVE'}", count = true)
    long countActiveSegments();

    /**
     * 检查分段名称是否存在
     */
    boolean existsBySegmentName(String segmentName);

    /**
     * 检查分段名称是否存在（排除指定ID）
     */
    @Query("{'segment_name': ?0, '_id': {$ne: ?1}}")
    boolean existsBySegmentNameAndIdNot(String segmentName, String id);

    /**
     * 删除指定创建者的所有分段
     */
    void deleteByCreatedBy(String createdBy);

    /**
     * 删除指定时间前创建的草稿分段
     */
    @Query(delete = true, value = "{'status': 'DRAFT', 'created_at': {$lt: ?0}}")
    void deleteDraftSegmentsCreatedBefore(LocalDateTime dateTime);
}