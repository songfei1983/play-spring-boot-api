package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.UserSegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户分段服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSegmentService {

    private final UserSegmentRepository userSegmentRepository;

    /**
     * 创建用户分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public UserSegmentEntity createSegment(UserSegmentEntity segment) {
        log.info("创建用户分段: segmentName={}, segmentType={}", 
            segment.getSegmentName(), segment.getSegmentType());
        
        // 检查分段名称是否已存在
        if (userSegmentRepository.existsBySegmentName(segment.getSegmentName())) {
            throw new IllegalArgumentException("分段名称已存在: " + segment.getSegmentName());
        }
        
        // 设置默认值
        if (segment.getCreatedAt() == null) {
            segment.setCreatedAt(LocalDateTime.now());
        }
        if (segment.getUpdatedAt() == null) {
            segment.setUpdatedAt(LocalDateTime.now());
        }
        if (segment.getStatus() == null) {
            segment.setStatus("DRAFT");
        }
        if (segment.getPriority() == null) {
            segment.setPriority(0);
        }
        
        UserSegmentEntity savedSegment = userSegmentRepository.save(segment);
        log.info("用户分段创建成功: segmentId={}, segmentName={}", 
            savedSegment.getId(), savedSegment.getSegmentName());
        
        return savedSegment;
    }

    /**
     * 更新用户分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public UserSegmentEntity updateSegment(String segmentId, UserSegmentEntity segment) {
        log.info("更新用户分段: segmentId={}", segmentId);
        
        UserSegmentEntity existingSegment = userSegmentRepository.findById(segmentId)
            .orElseThrow(() -> new IllegalArgumentException("分段不存在: " + segmentId));
        
        // 检查分段名称是否与其他分段冲突
        if (segment.getSegmentName() != null && 
            !segment.getSegmentName().equals(existingSegment.getSegmentName()) &&
            userSegmentRepository.existsBySegmentNameAndIdNot(segment.getSegmentName(), segmentId)) {
            throw new IllegalArgumentException("分段名称已存在: " + segment.getSegmentName());
        }
        
        // 更新字段
        if (segment.getSegmentName() != null) {
            existingSegment.setSegmentName(segment.getSegmentName());
        }
        if (segment.getDescription() != null) {
            existingSegment.setDescription(segment.getDescription());
        }
        if (segment.getSegmentType() != null) {
            existingSegment.setSegmentType(segment.getSegmentType());
        }
        if (segment.getRules() != null) {
            existingSegment.setRules(segment.getRules());
        }
        if (segment.getStatus() != null) {
            existingSegment.setStatus(segment.getStatus());
        }
        if (segment.getPriority() != null) {
            existingSegment.setPriority(segment.getPriority());
        }
        if (segment.getTags() != null) {
            existingSegment.setTags(segment.getTags());
        }
        if (segment.getMetadata() != null) {
            existingSegment.setMetadata(segment.getMetadata());
        }
        
        existingSegment.updateTimestamp();
        
        UserSegmentEntity updatedSegment = userSegmentRepository.save(existingSegment);
        log.info("用户分段更新成功: segmentId={}", updatedSegment.getId());
        
        return updatedSegment;
    }

    /**
     * 根据ID查找分段
     */
    @Cacheable(value = "segments", key = "#segmentId")
    public Optional<UserSegmentEntity> findSegmentById(String segmentId) {
        return userSegmentRepository.findById(segmentId);
    }

    /**
     * 根据名称查找分段
     */
    @Cacheable(value = "segments", key = "'name:' + #segmentName")
    public Optional<UserSegmentEntity> findSegmentByName(String segmentName) {
        return userSegmentRepository.findBySegmentName(segmentName);
    }

    /**
     * 查找所有激活的分段
     */
    @Cacheable(value = "segments", key = "'active'")
    public List<UserSegmentEntity> findActiveSegments() {
        return userSegmentRepository.findActiveSegments();
    }

    /**
     * 根据类型查找分段
     */
    @Cacheable(value = "segments", key = "'type:' + #segmentType")
    public List<UserSegmentEntity> findSegmentsByType(String segmentType) {
        return userSegmentRepository.findBySegmentType(segmentType);
    }

    /**
     * 根据状态查找分段
     */
    public List<UserSegmentEntity> findSegmentsByStatus(String status) {
        return userSegmentRepository.findByStatus(status);
    }

    /**
     * 根据标签查找分段
     */
    public List<UserSegmentEntity> findSegmentsByTags(List<String> tags) {
        return userSegmentRepository.findByTags(tags);
    }

    /**
     * 按优先级降序查找激活分段
     */
    @Cacheable(value = "segments", key = "'active_priority'")
    public List<UserSegmentEntity> findActiveSegmentsByPriority() {
        return userSegmentRepository.findActiveSegmentsByPriorityDesc();
    }

    /**
     * 分页查询分段
     */
    public Page<UserSegmentEntity> findSegments(Pageable pageable) {
        return userSegmentRepository.findAll(pageable);
    }

    /**
     * 搜索分段
     */
    public List<UserSegmentEntity> searchSegments(String keyword) {
        List<UserSegmentEntity> nameResults = userSegmentRepository.findBySegmentNameContaining(keyword);
        List<UserSegmentEntity> descResults = userSegmentRepository.findByDescriptionContaining(keyword);
        
        // 合并结果并去重
        nameResults.addAll(descResults);
        return nameResults.stream().distinct().toList();
    }

    /**
     * 激活分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public UserSegmentEntity activateSegment(String segmentId) {
        log.info("激活用户分段: segmentId={}", segmentId);
        
        UserSegmentEntity segment = userSegmentRepository.findById(segmentId)
            .orElseThrow(() -> new IllegalArgumentException("分段不存在: " + segmentId));
        
        segment.setStatus("ACTIVE");
        segment.updateTimestamp();
        
        UserSegmentEntity activatedSegment = userSegmentRepository.save(segment);
        log.info("用户分段激活成功: segmentId={}", activatedSegment.getId());
        
        return activatedSegment;
    }

    /**
     * 停用分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public UserSegmentEntity deactivateSegment(String segmentId) {
        log.info("停用用户分段: segmentId={}", segmentId);
        
        UserSegmentEntity segment = userSegmentRepository.findById(segmentId)
            .orElseThrow(() -> new IllegalArgumentException("分段不存在: " + segmentId));
        
        segment.setStatus("INACTIVE");
        segment.updateTimestamp();
        
        UserSegmentEntity deactivatedSegment = userSegmentRepository.save(segment);
        log.info("用户分段停用成功: segmentId={}", deactivatedSegment.getId());
        
        return deactivatedSegment;
    }

    /**
     * 删除分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public void deleteSegment(String segmentId) {
        log.info("删除用户分段: segmentId={}", segmentId);
        
        if (!userSegmentRepository.existsById(segmentId)) {
            throw new IllegalArgumentException("分段不存在: " + segmentId);
        }
        
        userSegmentRepository.deleteById(segmentId);
        log.info("用户分段删除成功: segmentId={}", segmentId);
    }

    /**
     * 统计分段数量
     */
    public long countSegments() {
        return userSegmentRepository.count();
    }

    /**
     * 统计激活分段数量
     */
    public long countActiveSegments() {
        return userSegmentRepository.countActiveSegments();
    }

    /**
     * 统计指定类型的分段数量
     */
    public long countSegmentsByType(String segmentType) {
        return userSegmentRepository.countBySegmentType(segmentType);
    }

    /**
     * 清理过期的草稿分段
     */
    @Transactional
    @CacheEvict(value = "segments", allEntries = true)
    public void cleanupExpiredDrafts(int daysOld) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysOld);
        log.info("清理过期草稿分段: threshold={}", threshold);
        
        userSegmentRepository.deleteDraftSegmentsCreatedBefore(threshold);
        log.info("过期草稿分段清理完成");
    }

    /**
     * 验证分段规则
     */
    public boolean validateSegmentRules(List<UserSegmentEntity.SegmentRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return false;
        }
        
        for (UserSegmentEntity.SegmentRule rule : rules) {
            if (rule.getField() == null || rule.getField().trim().isEmpty()) {
                return false;
            }
            if (rule.getOperator() == null || rule.getOperator().trim().isEmpty()) {
                return false;
            }
            if (rule.getValue() == null) {
                return false;
            }
        }
        
        return true;
    }
}