package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentMappingEntity;
import fei.song.play_spring_boot_api.ads.service.SegmentFilterService;
import fei.song.play_spring_boot_api.ads.service.UserSegmentMappingService;
import fei.song.play_spring_boot_api.ads.service.UserSegmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户分段管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ads/segments")
@RequiredArgsConstructor
public class UserSegmentController {

    private final UserSegmentService userSegmentService;
    private final UserSegmentMappingService mappingService;
    private final SegmentFilterService segmentFilterService;

    /**
     * 创建用户分段
     */
    @PostMapping
    public ResponseEntity<UserSegmentEntity> createSegment(@Valid @RequestBody UserSegmentEntity segment) {
        try {
            UserSegmentEntity createdSegment = userSegmentService.createSegment(segment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSegment);
        } catch (IllegalArgumentException e) {
            log.warn("创建分段失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("创建分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新用户分段
     */
    @PutMapping("/{segmentId}")
    public ResponseEntity<UserSegmentEntity> updateSegment(
            @PathVariable String segmentId,
            @Valid @RequestBody UserSegmentEntity segment) {
        try {
            UserSegmentEntity updatedSegment = userSegmentService.updateSegment(segmentId, segment);
            return ResponseEntity.ok(updatedSegment);
        } catch (IllegalArgumentException e) {
            log.warn("更新分段失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("更新分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取分段
     */
    @GetMapping("/{segmentId}")
    public ResponseEntity<UserSegmentEntity> getSegment(@PathVariable String segmentId) {
        Optional<UserSegmentEntity> segment = userSegmentService.findSegmentById(segmentId);
        return segment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据名称获取分段
     */
    @GetMapping("/name/{segmentName}")
    public ResponseEntity<UserSegmentEntity> getSegmentByName(@PathVariable String segmentName) {
        Optional<UserSegmentEntity> segment = userSegmentService.findSegmentByName(segmentName);
        return segment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有激活的分段
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserSegmentEntity>> getActiveSegments() {
        List<UserSegmentEntity> segments = userSegmentService.findActiveSegments();
        return ResponseEntity.ok(segments);
    }

    /**
     * 根据类型获取分段
     */
    @GetMapping("/type/{segmentType}")
    public ResponseEntity<List<UserSegmentEntity>> getSegmentsByType(@PathVariable String segmentType) {
        List<UserSegmentEntity> segments = userSegmentService.findSegmentsByType(segmentType);
        return ResponseEntity.ok(segments);
    }

    /**
     * 根据状态获取分段
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserSegmentEntity>> getSegmentsByStatus(@PathVariable String status) {
        List<UserSegmentEntity> segments = userSegmentService.findSegmentsByStatus(status);
        return ResponseEntity.ok(segments);
    }

    /**
     * 分页查询分段
     */
    @GetMapping
    public ResponseEntity<Page<UserSegmentEntity>> getSegments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserSegmentEntity> segments = userSegmentService.findSegments(pageable);
        return ResponseEntity.ok(segments);
    }

    /**
     * 搜索分段
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserSegmentEntity>> searchSegments(@RequestParam String keyword) {
        List<UserSegmentEntity> segments = userSegmentService.searchSegments(keyword);
        return ResponseEntity.ok(segments);
    }

    /**
     * 激活分段
     */
    @PostMapping("/{segmentId}/activate")
    public ResponseEntity<UserSegmentEntity> activateSegment(@PathVariable String segmentId) {
        try {
            UserSegmentEntity segment = userSegmentService.activateSegment(segmentId);
            return ResponseEntity.ok(segment);
        } catch (IllegalArgumentException e) {
            log.warn("激活分段失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("激活分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 停用分段
     */
    @PostMapping("/{segmentId}/deactivate")
    public ResponseEntity<UserSegmentEntity> deactivateSegment(@PathVariable String segmentId) {
        try {
            UserSegmentEntity segment = userSegmentService.deactivateSegment(segmentId);
            return ResponseEntity.ok(segment);
        } catch (IllegalArgumentException e) {
            log.warn("停用分段失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("停用分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除分段
     */
    @DeleteMapping("/{segmentId}")
    public ResponseEntity<Void> deleteSegment(@PathVariable String segmentId) {
        try {
            userSegmentService.deleteSegment(segmentId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("删除分段失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("删除分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取分段统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSegmentStats() {
        Map<String, Object> stats = Map.of(
            "totalSegments", userSegmentService.countSegments(),
            "activeSegments", userSegmentService.countActiveSegments()
        );
        return ResponseEntity.ok(stats);
    }

    /**
     * 为用户匹配分段
     */
    @PostMapping("/match/user/{userId}")
    public ResponseEntity<List<UserSegmentMappingEntity>> matchUserSegments(@PathVariable String userId) {
        try {
            List<UserSegmentMappingEntity> mappings = segmentFilterService.matchUserSegments(userId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("用户分段匹配异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 为分段匹配用户
     */
    @PostMapping("/{segmentId}/match/users")
    public ResponseEntity<List<UserSegmentMappingEntity>> matchSegmentUsers(@PathVariable String segmentId) {
        try {
            List<UserSegmentMappingEntity> mappings = segmentFilterService.matchSegmentUsers(segmentId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("分段用户匹配异常: segmentId={}", segmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重新评估用户分段
     */
    @PostMapping("/reevaluate/user/{userId}")
    public ResponseEntity<List<UserSegmentMappingEntity>> reevaluateUserSegments(@PathVariable String userId) {
        try {
            List<UserSegmentMappingEntity> mappings = segmentFilterService.reevaluateUserSegments(userId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("重新评估用户分段异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重新评估分段用户
     */
    @PostMapping("/{segmentId}/reevaluate/users")
    public ResponseEntity<List<UserSegmentMappingEntity>> reevaluateSegmentUsers(@PathVariable String segmentId) {
        try {
            List<UserSegmentMappingEntity> mappings = segmentFilterService.reevaluateSegmentUsers(segmentId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("重新评估分段用户异常: segmentId={}", segmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 检查用户是否匹配分段
     */
    @GetMapping("/check/{userId}/{segmentId}")
    public ResponseEntity<Map<String, Boolean>> checkUserSegmentMatch(
            @PathVariable String userId,
            @PathVariable String segmentId) {
        try {
            boolean matches = segmentFilterService.checkUserSegmentMatch(userId, segmentId);
            Map<String, Boolean> result = Map.of("matches", matches);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("检查用户分段匹配异常: userId={}, segmentId={}", userId, segmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取用户的分段匹配分数
     */
    @GetMapping("/scores/user/{userId}")
    public ResponseEntity<Map<String, Double>> getUserSegmentScores(@PathVariable String userId) {
        try {
            Map<String, Double> scores = segmentFilterService.getUserSegmentScores(userId);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            log.error("获取用户分段分数异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取用户的分段映射
     */
    @GetMapping("/mappings/user/{userId}")
    public ResponseEntity<List<UserSegmentMappingEntity>> getUserSegmentMappings(@PathVariable String userId) {
        try {
            List<UserSegmentMappingEntity> mappings = mappingService.findUserSegments(userId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("获取用户分段映射异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取分段的用户映射
     */
    @GetMapping("/{segmentId}/mappings")
    public ResponseEntity<List<UserSegmentMappingEntity>> getSegmentUserMappings(@PathVariable String segmentId) {
        try {
            List<UserSegmentMappingEntity> mappings = mappingService.findSegmentUsers(segmentId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("获取分段用户映射异常: segmentId={}", segmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取激活的用户分段映射
     */
    @GetMapping("/mappings/user/{userId}/active")
    public ResponseEntity<List<UserSegmentMappingEntity>> getActiveUserSegmentMappings(@PathVariable String userId) {
        try {
            List<UserSegmentMappingEntity> mappings = mappingService.findActiveUserSegments(userId);
            return ResponseEntity.ok(mappings);
        } catch (Exception e) {
            log.error("获取激活用户分段映射异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 停用用户的所有分段映射
     */
    @PostMapping("/mappings/user/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUserMappings(@PathVariable String userId) {
        try {
            mappingService.deactivateAllUserMappings(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("停用用户分段映射异常: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 停用分段的所有用户映射
     */
    @PostMapping("/{segmentId}/mappings/deactivate")
    public ResponseEntity<Void> deactivateSegmentMappings(@PathVariable String segmentId) {
        try {
            mappingService.deactivateAllSegmentMappings(segmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("停用分段用户映射异常: segmentId={}", segmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 清理过期的分段映射
     */
    @PostMapping("/mappings/cleanup")
    public ResponseEntity<Void> cleanupExpiredMappings() {
        try {
            mappingService.cleanupExpiredMappings();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("清理过期分段映射异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 清理过期的草稿分段
     */
    @PostMapping("/cleanup/drafts")
    public ResponseEntity<Void> cleanupExpiredDrafts(@RequestParam(defaultValue = "30") int daysOld) {
        try {
            userSegmentService.cleanupExpiredDrafts(daysOld);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("清理过期草稿分段异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 验证分段规则
     */
    @PostMapping("/validate-rules")
    public ResponseEntity<Map<String, Boolean>> validateSegmentRules(
            @RequestBody List<UserSegmentEntity.SegmentRule> rules) {
        try {
            boolean isValid = userSegmentService.validateSegmentRules(rules);
            Map<String, Boolean> result = Map.of("valid", isValid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("验证分段规则异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}