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
    public ResponseEntity<List<ActivityTrack>> getTracksByActivityType(@PathVariable String activityType) {
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
    public ResponseEntity<List<ActivityTrack>> getTracksByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
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
    public ResponseEntity<List<ActivityTrack>> getTracksByDeviceType(@PathVariable String deviceType) {
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
    public ResponseEntity<List<ActivityTrack>> searchTracksByLocation(@RequestParam String keyword) {
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
    public ResponseEntity<List<ActivityTrack>> getTracksBySessionId(@PathVariable String sessionId) {
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
    public ResponseEntity<List<ActivityTrack>> getTracksByPageUrl(@RequestParam String pageUrl) {
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
    public ResponseEntity<List<ActivityTrack>> getRecentTracksByUserId(
            @PathVariable Long userId, 
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
    public ResponseEntity<List<ActivityTrack>> getUserTracksInTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
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
    public ResponseEntity<ActivityTrack> recordTrack(@RequestBody ActivityTrack track) {
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
    public ResponseEntity<List<ActivityTrack>> recordTracks(@RequestBody List<ActivityTrack> tracks) {
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
    public ResponseEntity<ActivityTrack> updateTrack(@PathVariable Long id, @RequestBody ActivityTrack track) {
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
    public ResponseEntity<Map<String, Object>> deleteTrack(@PathVariable Long id) {
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
    public ResponseEntity<Map<String, Object>> deleteTracksByUserId(@PathVariable Long userId) {
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
    public ResponseEntity<Map<String, Object>> deleteTracksBeforeTime(
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
    public ResponseEntity<Map<String, Object>> getUserTrackCount(@PathVariable Long userId) {
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
    public ResponseEntity<Map<String, Object>> getUserActivityStats(@PathVariable Long userId) {
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