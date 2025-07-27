package fei.song.play_spring_boot_api.users.interfaces;

import fei.song.play_spring_boot_api.users.application.ActivityTrackService;
import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/activities")
@CrossOrigin(origins = "*")
@Tag(name = "活动跟踪", description = "用户活动轨迹记录和查询")
public class ActivityTrackController {
    
    @Autowired
    private ActivityTrackService activityTrackService;
    
    /**
     * 获取所有行动轨迹
     */
    @GetMapping
    @Operation(summary = "获取所有活动轨迹", description = "返回系统中所有用户活动轨迹的列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getAllTracks() {
        try {
            List<ActivityTrack> tracks = activityTrackService.getAllActivities();
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取行动轨迹
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取活动轨迹", description = "根据轨迹ID获取特定活动轨迹信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "404", description = "活动轨迹不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ActivityTrack> getTrackById(
            @Parameter(description = "轨迹ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            ActivityTrack track = activityTrackService.getActivityById(id);
            return ResponseEntity.ok(track);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID获取行动轨迹列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取活动轨迹", description = "获取指定用户的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByUserId(userId);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据活动类型获取行动轨迹列表
     */
    @GetMapping("/type/{activityType}")
    @Operation(summary = "根据活动类型获取活动轨迹", description = "获取指定活动类型的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksByActivityType(
            @Parameter(description = "活动类型", required = true, example = "页面访问")
            @PathVariable String activityType) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByType(activityType);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据时间范围获取行动轨迹列表
     */
    @GetMapping("/time-range")
    @Operation(summary = "根据时间范围获取活动轨迹", description = "获取指定时间范围内的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksByTimeRange(
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByTimeRange(startTime, endTime);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据设备类型获取行动轨迹列表
     */
    @GetMapping("/device/{deviceType}")
    @Operation(summary = "根据设备类型获取活动轨迹", description = "获取指定设备类型的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksByDeviceType(
            @Parameter(description = "设备类型", required = true, example = "mobile")
            @PathVariable String deviceType) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByDeviceType(deviceType);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据位置关键词搜索行动轨迹
     */
    @GetMapping("/search")
    @Operation(summary = "根据位置关键词搜索活动轨迹", description = "根据位置关键词搜索相关的活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> searchTracksByLocation(
            @Parameter(description = "位置关键词", required = true, example = "北京")
            @RequestParam String keyword) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByLocationKeyword(keyword);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据会话ID获取行动轨迹列表
     */
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "根据会话ID获取活动轨迹", description = "获取指定会话ID的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksBySessionId(
            @Parameter(description = "会话ID", required = true, example = "session123")
            @PathVariable String sessionId) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesBySessionId(sessionId);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据页面URL获取行动轨迹列表
     */
    @GetMapping("/page")
    @Operation(summary = "根据页面URL获取活动轨迹", description = "获取指定页面URL的所有活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getTracksByPageUrl(
            @Parameter(description = "页面URL", required = true, example = "/home")
            @RequestParam String pageUrl) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByPageUrl(pageUrl);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户最近的行动轨迹
     */
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "获取用户最近的活动轨迹", description = "获取指定用户最近的活动轨迹记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户最近活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getRecentTracksByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "限制数量", required = false, example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getRecentActivitiesByUserId(userId, limit);
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户指定时间范围内的行动轨迹
     */
    @GetMapping("/user/{userId}/time-range")
    @Operation(summary = "获取用户指定时间范围内的活动轨迹", description = "获取指定用户在特定时间范围内的活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户时间范围内活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> getUserTracksInTimeRange(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<ActivityTrack> tracks = activityTrackService.getActivitiesByUserId(userId);
            // 过滤时间范围
            tracks = tracks.stream()
                .filter(track -> track.getCreatedAt() != null && 
                        !track.getCreatedAt().isBefore(startTime) && 
                        !track.getCreatedAt().isAfter(endTime))
                .collect(Collectors.toList());
            return ResponseEntity.ok(tracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 记录用户行动轨迹
     */
    @PostMapping
    @Operation(summary = "记录用户活动轨迹", description = "创建新的用户活动轨迹记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功创建活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ActivityTrack> recordTrack(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "活动轨迹信息", required = true,
                    content = @Content(schema = @Schema(implementation = ActivityTrack.class)))
            @RequestBody ActivityTrack track) {
        try {
            ActivityTrack recordedTrack = activityTrackService.recordActivity(track);
            return ResponseEntity.status(HttpStatus.CREATED).body(recordedTrack);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 批量记录行动轨迹
     */
    @PostMapping("/batch")
    @Operation(summary = "批量记录活动轨迹", description = "批量创建多个活动轨迹记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功批量创建活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<ActivityTrack>> recordTracks(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "活动轨迹列表", required = true,
                    content = @Content(schema = @Schema(implementation = ActivityTrack.class)))
            @RequestBody List<ActivityTrack> tracks) {
        try {
            List<ActivityTrack> recordedTracks = activityTrackService.recordActivities(tracks);
            return ResponseEntity.status(HttpStatus.CREATED).body(recordedTracks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新行动轨迹
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新活动轨迹", description = "根据ID更新活动轨迹信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新活动轨迹",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityTrack.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "活动轨迹不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<ActivityTrack> updateTrack(
            @Parameter(description = "轨迹ID", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "更新的活动轨迹信息", required = true,
                    content = @Content(schema = @Schema(implementation = ActivityTrack.class)))
            @RequestBody ActivityTrack track) {
        try {
            ActivityTrack updatedTrack = activityTrackService.updateActivity(id, track);
            return ResponseEntity.ok(updatedTrack);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除行动轨迹
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动轨迹", description = "根据ID删除活动轨迹")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功删除活动轨迹"),
            @ApiResponse(responseCode = "404", description = "活动轨迹不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deleteTrack(
            @Parameter(description = "轨迹ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            boolean deleted = activityTrackService.deleteActivity(id);
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "行动轨迹删除成功" : "行动轨迹删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除用户的所有行动轨迹
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "删除用户的所有活动轨迹", description = "删除指定用户的所有活动轨迹记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功删除用户活动轨迹"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deleteTracksByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            // 获取用户所有活动并逐个删除
            List<ActivityTrack> userTracks = activityTrackService.getActivitiesByUserId(userId);
            boolean deleted = true;
            for (ActivityTrack track : userTracks) {
                try {
                    activityTrackService.deleteActivity(track.getId());
                } catch (Exception e) {
                    deleted = false;
                }
            }
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "用户行动轨迹删除成功" : "用户行动轨迹删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除指定时间之前的行动轨迹
     */
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理指定时间之前的活动轨迹", description = "删除指定时间之前的所有活动轨迹记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功清理活动轨迹"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deleteTracksBeforeTime(
            @Parameter(description = "截止时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        try {
            // 获取所有活动并删除指定时间之前的记录
            List<ActivityTrack> allTracks = activityTrackService.getAllActivities();
            int deletedCount = 0;
            for (ActivityTrack track : allTracks) {
                if (track.getCreatedAt() != null && track.getCreatedAt().isBefore(beforeTime)) {
                    try {
                        activityTrackService.deleteActivity(track.getId());
                        deletedCount++;
                    } catch (Exception e) {
                        // 忽略删除失败的记录
                    }
                }
            }
            Map<String, Object> response = Map.of(
                "deletedCount", deletedCount,
                "message", "清理完成，删除了 " + deletedCount + " 条记录"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取行动轨迹总数
     */
    @GetMapping("/count")
    @Operation(summary = "获取活动轨迹总数", description = "获取系统中所有活动轨迹的总数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹总数"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getTrackCount() {
        try {
            long count = activityTrackService.getActivityCount();
            Map<String, Object> response = Map.of(
                "count", count,
                "message", "行动轨迹总数"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户行动轨迹总数
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "获取用户活动轨迹总数", description = "获取指定用户的活动轨迹总数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户活动轨迹总数"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserTrackCount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            long count = activityTrackService.getActivitiesByUserId(userId).size();
            Map<String, Object> response = Map.of(
                "userId", userId,
                "count", count,
                "message", "用户行动轨迹总数"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户活动统计
     */
    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "获取用户活动统计", description = "获取指定用户的详细活动统计信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户活动统计"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserActivityStats(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            Map<String, Object> userStats = activityTrackService.getUserActivityStats(userId);
            long totalCount = activityTrackService.getActivitiesByUserId(userId).size();
            
            Map<String, Object> response = Map.of(
                "userId", userId,
                "totalCount", totalCount,
                "activityStats", userStats,
                "message", "用户活动统计"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取行动轨迹统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取活动轨迹统计信息", description = "获取系统中所有活动轨迹的详细统计信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取活动轨迹统计信息"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getTrackStats() {
        try {
            long totalCount = activityTrackService.getActivityCount();
            List<ActivityTrack> allTracks = activityTrackService.getAllActivities();
            
            // 按活动类型统计
            Map<String, Long> activityTypeStats = allTracks.stream()
                .collect(Collectors.groupingBy(
                    track -> track.getActivityType() != null ? track.getActivityType() : "未知",
                    Collectors.counting()
                ));
            
            // 按设备类型统计
            Map<String, Long> deviceTypeStats = allTracks.stream()
                .collect(Collectors.groupingBy(
                    track -> track.getDeviceType() != null ? track.getDeviceType() : "未知",
                    Collectors.counting()
                ));
            
            Map<String, Object> stats = Map.of(
                "totalCount", totalCount,
                "activityTypeStats", activityTypeStats,
                "deviceTypeStats", deviceTypeStats
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}