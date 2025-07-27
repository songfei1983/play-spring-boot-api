package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentMappingEntity;
import fei.song.play_spring_boot_api.ads.service.SegmentFilterService;
import fei.song.play_spring_boot_api.ads.service.UserSegmentMappingService;
import fei.song.play_spring_boot_api.ads.service.UserSegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Segment Management", description = "用户分段管理API")
public class UserSegmentController {

    private final UserSegmentService userSegmentService;
    private final UserSegmentMappingService mappingService;
    private final SegmentFilterService segmentFilterService;

    /**
     * 创建用户分段
     */
    @PostMapping
    @Operation(summary = "创建用户分段", description = "创建一个新的用户分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "成功创建用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> createSegment(
            @Parameter(description = "用户分段信息") @Valid @RequestBody UserSegmentEntity segment) {
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
    @Operation(summary = "更新用户分段", description = "更新指定ID的用户分段信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> updateSegment(
            @Parameter(description = "分段ID", example = "segment123") @PathVariable String segmentId,
            @Parameter(description = "更新的分段信息") @Valid @RequestBody UserSegmentEntity segment) {
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
    @Operation(summary = "根据ID获取分段", description = "通过分段ID获取用户分段详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> getSegment(
            @Parameter(description = "分段ID", example = "segment123") @PathVariable String segmentId) {
        Optional<UserSegmentEntity> segment = userSegmentService.findSegmentById(segmentId);
        return segment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据名称获取分段
     */
    @GetMapping("/name/{segmentName}")
    @Operation(summary = "根据名称获取分段", description = "通过分段名称获取用户分段详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> getSegmentByName(
            @Parameter(description = "分段名称", example = "高价值用户") @PathVariable String segmentName) {
        Optional<UserSegmentEntity> segment = userSegmentService.findSegmentByName(segmentName);
        return segment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取活跃分段
     */
    @GetMapping("/active")
    @Operation(summary = "获取活跃分段", description = "获取所有状态为活跃的用户分段列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取活跃分段列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentEntity>> getActiveSegments() {
        List<UserSegmentEntity> segments = userSegmentService.findActiveSegments();
        return ResponseEntity.ok(segments);
    }

    /**
     * 根据类型获取分段
     */
    @GetMapping("/type/{segmentType}")
    @Operation(summary = "根据类型获取分段", description = "获取指定类型的用户分段列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分段列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentEntity>> getSegmentsByType(
            @Parameter(description = "分段类型", example = "demographic") @PathVariable String segmentType) {
        List<UserSegmentEntity> segments = userSegmentService.findSegmentsByType(segmentType);
        return ResponseEntity.ok(segments);
    }

    /**
     * 根据状态获取分段
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取分段", description = "获取指定状态的用户分段列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分段列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentEntity>> getSegmentsByStatus(
            @Parameter(description = "分段状态", example = "active") @PathVariable String status) {
        List<UserSegmentEntity> segments = userSegmentService.findSegmentsByStatus(status);
        return ResponseEntity.ok(segments);
    }

    /**
     * 分页查询分段
     */
    @GetMapping
    @Operation(summary = "分页获取分段", description = "分页获取用户分段列表，支持排序")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分段列表",
                content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Page<UserSegmentEntity>> getSegments(
            @Parameter(description = "页码", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序字段", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserSegmentEntity> segments = userSegmentService.findSegments(pageable);
        return ResponseEntity.ok(segments);
    }

    /**
     * 搜索分段
     */
    @GetMapping("/search")
    @Operation(summary = "搜索分段", description = "根据关键词搜索用户分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取搜索结果",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentEntity>> searchSegments(
            @Parameter(description = "搜索关键词", example = "高价值") @RequestParam String keyword) {
        List<UserSegmentEntity> segments = userSegmentService.searchSegments(keyword);
        return ResponseEntity.ok(segments);
    }

    /**
     * 激活分段
     */
    @PostMapping("/{segmentId}/activate")
    @Operation(summary = "激活分段", description = "激活指定ID的用户分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功激活分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> activateSegment(
            @Parameter(description = "分段ID", example = "segment123") @PathVariable String segmentId) {
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
    @Operation(summary = "停用分段", description = "停用指定ID的用户分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功停用分段",
                content = @Content(schema = @Schema(implementation = UserSegmentEntity.class))),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserSegmentEntity> deactivateSegment(
            @Parameter(description = "分段ID", example = "segment123") @PathVariable String segmentId) {
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
    @Operation(summary = "删除分段", description = "删除指定ID的用户分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功删除分段"),
        @ApiResponse(responseCode = "404", description = "用户分段不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteSegment(
            @Parameter(description = "分段ID", example = "segment123") @PathVariable String segmentId) {
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
    @Operation(summary = "获取分段统计信息", description = "获取用户分段的统计信息，包括总数和活跃数量")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分段统计信息",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "为用户匹配分段", description = "根据用户特征为指定用户匹配合适的分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功匹配用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> matchUserSegments(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "为分段匹配用户", description = "为指定分段匹配符合条件的用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功匹配分段用户",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> matchSegmentUsers(
            @Parameter(description = "分段ID", required = true, example = "segment123")
            @PathVariable String segmentId) {
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
    @Operation(summary = "重新评估用户分段", description = "重新评估用户的分段匹配情况")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功重新评估用户分段",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> reevaluateUserSegments(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "重新评估分段用户", description = "重新评估分段的用户匹配情况")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功重新评估分段用户",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> reevaluateSegmentUsers(
            @Parameter(description = "分段ID", required = true, example = "segment123")
            @PathVariable String segmentId) {
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
    @Operation(summary = "检查用户是否匹配分段", description = "检查指定用户是否匹配指定分段的条件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功检查用户分段匹配",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Boolean>> checkUserSegmentMatch(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId,
            @Parameter(description = "分段ID", required = true, example = "segment123")
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
    @Operation(summary = "获取用户的分段匹配分数", description = "获取用户对各个分段的匹配分数")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户分段匹配分数",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Double>> getUserSegmentScores(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "获取用户的分段映射", description = "获取指定用户的所有分段映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户分段映射",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> getUserSegmentMappings(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "获取分段的用户映射", description = "获取指定分段的所有用户映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分段用户映射",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> getSegmentUserMappings(
            @Parameter(description = "分段ID", required = true, example = "segment123")
            @PathVariable String segmentId) {
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
    @Operation(summary = "获取激活的用户分段映射", description = "获取指定用户的所有激活状态的分段映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取激活的用户分段映射",
                content = @Content(schema = @Schema(implementation = UserSegmentMappingEntity.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserSegmentMappingEntity>> getActiveUserSegmentMappings(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "停用用户的所有分段映射", description = "停用指定用户的所有分段映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功停用用户分段映射"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deactivateUserMappings(
            @Parameter(description = "用户ID", required = true, example = "user123")
            @PathVariable String userId) {
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
    @Operation(summary = "停用分段的所有用户映射", description = "停用指定分段的所有用户映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功停用分段用户映射"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deactivateSegmentMappings(
            @Parameter(description = "分段ID", required = true, example = "segment123")
            @PathVariable String segmentId) {
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
    @Operation(summary = "清理过期的分段映射", description = "清理系统中过期的分段映射关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功清理过期分段映射"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "清理过期的草稿分段", description = "清理指定天数之前的草稿状态分段")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功清理过期草稿分段"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> cleanupExpiredDrafts(
            @Parameter(description = "过期天数", required = false, example = "30")
            @RequestParam(defaultValue = "30") int daysOld) {
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
    @Operation(summary = "验证分段规则", description = "验证分段规则的有效性和正确性")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功验证分段规则",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Boolean>> validateSegmentRules(
            @Parameter(description = "分段规则列表", required = true)
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