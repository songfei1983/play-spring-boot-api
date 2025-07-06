package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import fei.song.play_spring_boot_api.users.infrastructure.ActivityTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityTrackService {
    
    @Autowired
    private ActivityTrackRepository activityTrackRepository;
    
    /**
     * 获取所有行动轨迹
     */
    public List<ActivityTrack> getAllActivities() {
        return activityTrackRepository.findAll();
    }
    
    /**
     * 根据ID获取行动轨迹
     */
    public ActivityTrack getActivityById(Long id) {
        Optional<ActivityTrack> activity = activityTrackRepository.findById(id);
        if (activity.isEmpty()) {
            throw new RuntimeException("行动轨迹不存在，ID: " + id);
        }
        return activity.get();
    }
    
    /**
     * 根据用户ID获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return activityTrackRepository.findByUserId(userId);
    }
    
    /**
     * 根据活动类型获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByType(String activityType) {
        if (activityType == null || activityType.trim().isEmpty()) {
            throw new IllegalArgumentException("活动类型不能为空");
        }
        return activityTrackRepository.findByActivityType(activityType.trim());
    }
    
    /**
     * 根据时间范围获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("时间范围不能为空");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        return activityTrackRepository.findByCreatedAtBetween(startTime, endTime);
    }
    
    /**
     * 根据设备类型获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByDeviceType(String deviceType) {
        if (deviceType == null || deviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("设备类型不能为空");
        }
        return activityTrackRepository.findByDeviceType(deviceType.trim());
    }
    
    /**
     * 根据位置关键词获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByLocationKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("位置关键词不能为空");
        }
        return activityTrackRepository.findByLocationContaining(keyword.trim());
    }
    
    /**
     * 根据会话ID获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesBySessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        return activityTrackRepository.findBySessionId(sessionId.trim());
    }
    
    /**
     * 根据页面URL获取行动轨迹列表
     */
    public List<ActivityTrack> getActivitiesByPageUrl(String pageUrl) {
        if (pageUrl == null || pageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("页面URL不能为空");
        }
        return activityTrackRepository.findByPageUrl(pageUrl.trim());
    }
    
    /**
     * 获取用户最近的行动轨迹
     */
    public List<ActivityTrack> getRecentActivitiesByUserId(Long userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("限制数量必须大于0");
        }
        return activityTrackRepository.findRecentByUserId(userId, limit);
    }
    
    /**
     * 记录行动轨迹
     */
    public ActivityTrack recordActivity(ActivityTrack activity) {
        validateActivity(activity);
        
        // 设置记录时间
        if (activity.getCreatedAt() == null) {
            activity.setCreatedAt(LocalDateTime.now());
        }
        
        return activityTrackRepository.save(activity);
    }
    
    /**
     * 批量记录行动轨迹
     */
    public List<ActivityTrack> recordActivities(List<ActivityTrack> activities) {
        if (activities == null || activities.isEmpty()) {
            throw new IllegalArgumentException("行动轨迹列表不能为空");
        }
        
        // 验证每个行动轨迹
        for (ActivityTrack activity : activities) {
            validateActivity(activity);
            
            // 设置记录时间
            if (activity.getCreatedAt() == null) {
                activity.setCreatedAt(LocalDateTime.now());
            }
        }
        
        return activityTrackRepository.saveAll(activities);
    }
    
    /**
     * 更新行动轨迹
     */
    public ActivityTrack updateActivity(Long id, ActivityTrack activity) {
        if (!activityTrackRepository.existsById(id)) {
            throw new RuntimeException("行动轨迹不存在，ID: " + id);
        }
        
        validateActivity(activity);
        activity.setId(id);
        
        return activityTrackRepository.save(activity);
    }
    
    /**
     * 删除行动轨迹
     */
    public boolean deleteActivity(Long id) {
        if (!activityTrackRepository.existsById(id)) {
            throw new RuntimeException("行动轨迹不存在，ID: " + id);
        }
        return activityTrackRepository.deleteById(id);
    }
    
    /**
     * 获取行动轨迹总数
     */
    public long getActivityCount() {
        return activityTrackRepository.count();
    }
    
    /**
     * 获取用户活动统计
     */
    public Map<String, Object> getUserActivityStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        List<ActivityTrack> userActivities = activityTrackRepository.findByUserId(userId);
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalActivities", userActivities.size());
        
        // 按活动类型统计
        Map<String, Long> typeStats = userActivities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getActivityType() != null ? activity.getActivityType() : "未知",
                        Collectors.counting()
                ));
        stats.put("typeStats", typeStats);
        
        // 按设备类型统计
        Map<String, Long> deviceStats = userActivities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getDeviceType() != null ? activity.getDeviceType() : "未知",
                        Collectors.counting()
                ));
        stats.put("deviceStats", deviceStats);
        
        // 计算平均停留时长
        double avgDuration = userActivities.stream()
                .filter(activity -> activity.getDuration() != null)
                .mapToLong(ActivityTrack::getDuration)
                .average()
                .orElse(0.0);
        stats.put("avgDuration", avgDuration);
        
        return stats;
    }
    
    /**
     * 获取活动类型统计
     */
    public Map<String, Long> getActivityTypeStats() {
        List<ActivityTrack> allActivities = activityTrackRepository.findAll();
        return allActivities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getActivityType() != null ? activity.getActivityType() : "未知",
                        Collectors.counting()
                ));
    }
    
    /**
     * 获取设备类型统计
     */
    public Map<String, Long> getDeviceTypeStats() {
        List<ActivityTrack> allActivities = activityTrackRepository.findAll();
        return allActivities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getDeviceType() != null ? activity.getDeviceType() : "未知",
                        Collectors.counting()
                ));
    }
    
    /**
     * 验证行动轨迹数据
     */
    private void validateActivity(ActivityTrack activity) {
        if (activity == null) {
            throw new IllegalArgumentException("行动轨迹不能为空");
        }
        
        if (activity.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (activity.getActivityType() == null || activity.getActivityType().trim().isEmpty()) {
            throw new IllegalArgumentException("活动类型不能为空");
        }
        
        // 验证经纬度范围
        if (activity.getLatitude() != null && 
            (activity.getLatitude().compareTo(new java.math.BigDecimal("-90")) < 0 || 
             activity.getLatitude().compareTo(new java.math.BigDecimal("90")) > 0)) {
            throw new IllegalArgumentException("纬度必须在-90到90之间");
        }
        
        if (activity.getLongitude() != null && 
            (activity.getLongitude().compareTo(new java.math.BigDecimal("-180")) < 0 || 
             activity.getLongitude().compareTo(new java.math.BigDecimal("180")) > 0)) {
            throw new IllegalArgumentException("经度必须在-180到180之间");
        }
        
        // 验证停留时长
        if (activity.getDuration() != null && activity.getDuration() < 0) {
            throw new IllegalArgumentException("停留时长不能为负数");
        }
        
        // 验证时间戳
        if (activity.getCreatedAt() != null && activity.getCreatedAt().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("时间戳不能是未来时间");
        }
    }
}