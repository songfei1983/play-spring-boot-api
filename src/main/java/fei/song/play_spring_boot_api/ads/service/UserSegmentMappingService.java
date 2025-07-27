package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentMappingEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.UserSegmentMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 用户分段映射服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSegmentMappingService {

    private final UserSegmentMappingRepository mappingRepository;

    /**
     * 创建用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#mapping.userId")
    public UserSegmentMappingEntity createMapping(UserSegmentMappingEntity mapping) {
        log.info("创建用户分段映射: userId={}, segmentId={}", 
            mapping.getUserId(), mapping.getSegmentId());
        
        // 检查映射是否已存在
        Optional<UserSegmentMappingEntity> existing = mappingRepository
            .findByUserIdAndSegmentId(mapping.getUserId(), mapping.getSegmentId());
        
        if (existing.isPresent()) {
            // 如果存在但未激活，则激活它
            UserSegmentMappingEntity existingMapping = existing.get();
            if (!existingMapping.getIsActive()) {
                existingMapping.activate();
                existingMapping.setMatchScore(mapping.getMatchScore());
                existingMapping.setConfidence(mapping.getConfidence());
                existingMapping.setAttributes(mapping.getAttributes());
                existingMapping.setExpiresAt(mapping.getExpiresAt());
                
                UserSegmentMappingEntity updatedMapping = mappingRepository.save(existingMapping);
                log.info("用户分段映射已激活: mappingId={}", updatedMapping.getId());
                return updatedMapping;
            } else {
                log.warn("用户分段映射已存在且激活: userId={}, segmentId={}", 
                    mapping.getUserId(), mapping.getSegmentId());
                return existingMapping;
            }
        }
        
        // 设置默认值
        if (mapping.getCreatedAt() == null) {
            mapping.setCreatedAt(LocalDateTime.now());
        }
        if (mapping.getUpdatedAt() == null) {
            mapping.setUpdatedAt(LocalDateTime.now());
        }
        if (mapping.getAssignedAt() == null) {
            mapping.setAssignedAt(LocalDateTime.now());
        }
        if (mapping.getIsActive() == null) {
            mapping.setIsActive(true);
        }
        if (mapping.getConfidence() == null) {
            mapping.setConfidence(1.0);
        }
        
        UserSegmentMappingEntity savedMapping = mappingRepository.save(mapping);
        log.info("用户分段映射创建成功: mappingId={}, userId={}, segmentId={}", 
            savedMapping.getId(), savedMapping.getUserId(), savedMapping.getSegmentId());
        
        return savedMapping;
    }

    /**
     * 批量创建用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", allEntries = true)
    public List<UserSegmentMappingEntity> createMappings(List<UserSegmentMappingEntity> mappings) {
        log.info("批量创建用户分段映射: count={}", mappings.size());
        
        List<UserSegmentMappingEntity> savedMappings = mappings.stream()
            .map(this::createMapping)
            .collect(Collectors.toList());
        
        log.info("批量用户分段映射创建完成: count={}", savedMappings.size());
        return savedMappings;
    }

    /**
     * 更新用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#mapping.userId")
    public UserSegmentMappingEntity updateMapping(String mappingId, UserSegmentMappingEntity mapping) {
        log.info("更新用户分段映射: mappingId={}", mappingId);
        
        UserSegmentMappingEntity existingMapping = mappingRepository.findById(mappingId)
            .orElseThrow(() -> new IllegalArgumentException("映射不存在: " + mappingId));
        
        // 更新字段
        if (mapping.getIsActive() != null) {
            existingMapping.setIsActive(mapping.getIsActive());
        }
        if (mapping.getMatchScore() != null) {
            existingMapping.setMatchScore(mapping.getMatchScore());
        }
        if (mapping.getConfidence() != null) {
            existingMapping.setConfidence(mapping.getConfidence());
        }
        if (mapping.getAttributes() != null) {
            existingMapping.setAttributes(mapping.getAttributes());
        }
        if (mapping.getExpiresAt() != null) {
            existingMapping.setExpiresAt(mapping.getExpiresAt());
        }
        
        existingMapping.updateTimestamp();
        
        UserSegmentMappingEntity updatedMapping = mappingRepository.save(existingMapping);
        log.info("用户分段映射更新成功: mappingId={}", updatedMapping.getId());
        
        return updatedMapping;
    }

    /**
     * 查找用户的所有分段映射
     */
    @Cacheable(value = "userSegments", key = "#userId")
    public List<UserSegmentMappingEntity> findUserSegments(String userId) {
        return mappingRepository.findByUserId(userId);
    }

    /**
     * 查找用户的激活分段映射
     */
    @Cacheable(value = "userSegments", key = "#userId + ':active'")
    public List<UserSegmentMappingEntity> findActiveUserSegments(String userId) {
        return mappingRepository.findActiveSegmentsByUserId(userId);
    }

    /**
     * 查找用户指定类型的分段映射
     */
    public List<UserSegmentMappingEntity> findUserSegmentsByType(String userId, String segmentType) {
        return mappingRepository.findByUserIdAndSegmentType(userId, segmentType);
    }

    /**
     * 查找用户指定类型的激活分段映射
     */
    public List<UserSegmentMappingEntity> findActiveUserSegmentsByType(String userId, String segmentType) {
        return mappingRepository.findActiveByUserIdAndSegmentType(userId, segmentType);
    }

    /**
     * 查找分段的所有用户映射
     */
    public List<UserSegmentMappingEntity> findSegmentUsers(String segmentId) {
        return mappingRepository.findBySegmentId(segmentId);
    }

    /**
     * 查找分段的激活用户映射
     */
    public List<UserSegmentMappingEntity> findActiveSegmentUsers(String segmentId) {
        return mappingRepository.findActiveUsersBySegmentId(segmentId);
    }

    /**
     * 检查用户是否属于指定分段
     */
    public boolean isUserInSegment(String userId, String segmentId) {
        return mappingRepository.countActiveByUserIdAndSegmentId(userId, segmentId) > 0;
    }

    /**
     * 检查用户是否属于任一指定分段
     */
    public boolean isUserInAnySegment(String userId, List<String> segmentIds) {
        List<UserSegmentMappingEntity> mappings = mappingRepository
            .findActiveByUserIdAndSegmentIds(userId, segmentIds);
        return !mappings.isEmpty();
    }

    /**
     * 获取用户的分段ID列表
     */
    public List<String> getUserSegmentIds(String userId) {
        return mappingRepository.findActiveSegmentsByUserId(userId)
            .stream()
            .map(UserSegmentMappingEntity::getSegmentId)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户的分段名称列表
     */
    public List<String> getUserSegmentNames(String userId) {
        return mappingRepository.findActiveSegmentsByUserId(userId)
            .stream()
            .map(UserSegmentMappingEntity::getSegmentName)
            .collect(Collectors.toList());
    }

    /**
     * 按匹配分数排序获取用户分段
     */
    public List<UserSegmentMappingEntity> getUserSegmentsByScore(String userId) {
        return mappingRepository.findActiveByUserIdAndOrderByMatchScoreDesc(userId);
    }

    /**
     * 激活用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#userId")
    public UserSegmentMappingEntity activateMapping(String userId, String segmentId) {
        log.info("激活用户分段映射: userId={}, segmentId={}", userId, segmentId);
        
        UserSegmentMappingEntity mapping = mappingRepository
            .findByUserIdAndSegmentId(userId, segmentId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("映射不存在: userId=%s, segmentId=%s", userId, segmentId)));
        
        mapping.activate();
        
        UserSegmentMappingEntity activatedMapping = mappingRepository.save(mapping);
        log.info("用户分段映射激活成功: mappingId={}", activatedMapping.getId());
        
        return activatedMapping;
    }

    /**
     * 停用用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#userId")
    public UserSegmentMappingEntity deactivateMapping(String userId, String segmentId) {
        log.info("停用用户分段映射: userId={}, segmentId={}", userId, segmentId);
        
        UserSegmentMappingEntity mapping = mappingRepository
            .findByUserIdAndSegmentId(userId, segmentId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("映射不存在: userId=%s, segmentId=%s", userId, segmentId)));
        
        mapping.deactivate();
        
        UserSegmentMappingEntity deactivatedMapping = mappingRepository.save(mapping);
        log.info("用户分段映射停用成功: mappingId={}", deactivatedMapping.getId());
        
        return deactivatedMapping;
    }

    /**
     * 批量停用用户的所有分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#userId")
    public void deactivateAllUserMappings(String userId) {
        log.info("停用用户所有分段映射: userId={}", userId);
        
        List<UserSegmentMappingEntity> mappings = mappingRepository.findAllByUserId(userId);
        mappings.forEach(UserSegmentMappingEntity::deactivate);
        mappingRepository.saveAll(mappings);
        
        log.info("用户所有分段映射停用完成: userId={}, count={}", userId, mappings.size());
    }

    /**
     * 批量停用分段的所有用户映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", allEntries = true)
    public void deactivateAllSegmentMappings(String segmentId) {
        log.info("停用分段所有用户映射: segmentId={}", segmentId);
        
        List<UserSegmentMappingEntity> mappings = mappingRepository.findAllBySegmentId(segmentId);
        mappings.forEach(UserSegmentMappingEntity::deactivate);
        mappingRepository.saveAll(mappings);
        
        log.info("分段所有用户映射停用完成: segmentId={}, count={}", segmentId, mappings.size());
    }

    /**
     * 删除用户分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#userId")
    public void deleteMapping(String userId, String segmentId) {
        log.info("删除用户分段映射: userId={}, segmentId={}", userId, segmentId);
        
        UserSegmentMappingEntity mapping = mappingRepository
            .findByUserIdAndSegmentId(userId, segmentId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("映射不存在: userId=%s, segmentId=%s", userId, segmentId)));
        
        mappingRepository.delete(mapping);
        log.info("用户分段映射删除成功: mappingId={}", mapping.getId());
    }

    /**
     * 删除用户的所有分段映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", key = "#userId")
    public void deleteAllUserMappings(String userId) {
        log.info("删除用户所有分段映射: userId={}", userId);
        
        mappingRepository.deleteByUserId(userId);
        log.info("用户所有分段映射删除完成: userId={}", userId);
    }

    /**
     * 删除分段的所有用户映射
     */
    @Transactional
    @CacheEvict(value = "userSegments", allEntries = true)
    public void deleteAllSegmentMappings(String segmentId) {
        log.info("删除分段所有用户映射: segmentId={}", segmentId);
        
        mappingRepository.deleteBySegmentId(segmentId);
        log.info("分段所有用户映射删除完成: segmentId={}", segmentId);
    }

    /**
     * 验证用户分段映射
     */
    @Transactional
    public void validateMapping(String userId, String segmentId) {
        UserSegmentMappingEntity mapping = mappingRepository
            .findByUserIdAndSegmentId(userId, segmentId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("映射不存在: userId=%s, segmentId=%s", userId, segmentId)));
        
        mapping.validate();
        mappingRepository.save(mapping);
        
        log.debug("用户分段映射验证完成: mappingId={}", mapping.getId());
    }

    /**
     * 批量验证需要重新验证的映射
     */
    @Async
    @Transactional
    public CompletableFuture<Void> validateExpiredMappings(int hoursThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hoursThreshold);
        List<UserSegmentMappingEntity> mappings = mappingRepository
            .findMappingsNeedingValidation(threshold);
        
        log.info("开始批量验证过期映射: count={}, threshold={}", mappings.size(), threshold);
        
        mappings.forEach(mapping -> {
            mapping.validate();
            mappingRepository.save(mapping);
        });
        
        log.info("批量验证过期映射完成: count={}", mappings.size());
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 清理过期的映射
     */
    @Async
    @Transactional
    @CacheEvict(value = "userSegments", allEntries = true)
    public CompletableFuture<Void> cleanupExpiredMappings() {
        LocalDateTime now = LocalDateTime.now();
        List<UserSegmentMappingEntity> expiredMappings = mappingRepository.findExpiredMappings(now);
        
        log.info("开始清理过期映射: count={}", expiredMappings.size());
        
        mappingRepository.deleteByExpiresAtBefore(now);
        
        log.info("过期映射清理完成: count={}", expiredMappings.size());
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 统计用户的激活分段数量
     */
    public long countActiveUserSegments(String userId) {
        return mappingRepository.countActiveSegmentsByUserId(userId);
    }

    /**
     * 统计分段的激活用户数量
     */
    public long countActiveSegmentUsers(String segmentId) {
        return mappingRepository.countActiveUsersBySegmentId(segmentId);
    }

    /**
     * 统计总的激活映射数量
     */
    public long countActiveMappings() {
        return mappingRepository.countActiveMappings();
    }

    /**
     * 获取用户分段统计信息
     */
    public Map<String, Long> getUserSegmentStats(String userId) {
        List<UserSegmentMappingEntity> mappings = mappingRepository.findByUserId(userId);
        
        Map<String, Long> stats = mappings.stream()
            .collect(Collectors.groupingBy(
                UserSegmentMappingEntity::getSegmentType,
                Collectors.counting()
            ));
        
        stats.put("total", (long) mappings.size());
        stats.put("active", mappings.stream()
            .filter(UserSegmentMappingEntity::getIsActive)
            .count());
        
        return stats;
    }
}