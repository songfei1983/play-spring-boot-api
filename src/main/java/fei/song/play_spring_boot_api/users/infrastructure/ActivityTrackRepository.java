package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ActivityTrackRepository {
    private final List<ActivityTrack> tracks = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public ActivityTrackRepository() {
        // 初始化示例数据
        initializeData();
    }
    
    private void initializeData() {
        ActivityTrack track1 = new ActivityTrack(1L, "登录", "用户登录系统", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "北京市朝阳区");
        track1.setId(idGenerator.getAndIncrement());
        track1.setIpAddress("192.168.1.100");
        track1.setDeviceType("手机");
        track1.setOperatingSystem("iOS 15.0");
        track1.setBrowser("Safari");
        track1.setSessionId("session_001");
        track1.setPageUrl("/login");
        track1.setDuration(30);
        tracks.add(track1);
        
        ActivityTrack track2 = new ActivityTrack(1L, "浏览", "浏览商品详情页", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "北京市朝阳区");
        track2.setId(idGenerator.getAndIncrement());
        track2.setIpAddress("192.168.1.100");
        track2.setDeviceType("手机");
        track2.setOperatingSystem("iOS 15.0");
        track2.setBrowser("Safari");
        track2.setSessionId("session_001");
        track2.setPageUrl("/products/123");
        track2.setReferrer("/home");
        track2.setDuration(120);
        track2.setExtraData("{\"productId\": 123, \"category\": \"电子产品\"}");
        tracks.add(track2);
        
        ActivityTrack track3 = new ActivityTrack(2L, "搜索", "搜索iPhone相关产品", 
                new BigDecimal("121.473"), new BigDecimal("31.230"), "上海市浦东新区");
        track3.setId(idGenerator.getAndIncrement());
        track3.setIpAddress("192.168.1.101");
        track3.setDeviceType("电脑");
        track3.setOperatingSystem("Windows 11");
        track3.setBrowser("Chrome");
        track3.setSessionId("session_002");
        track3.setPageUrl("/search");
        track3.setDuration(45);
        track3.setExtraData("{\"keyword\": \"iPhone\", \"results\": 25}");
        tracks.add(track3);
        
        ActivityTrack track4 = new ActivityTrack(3L, "购买", "购买iPhone 15 Pro", 
                new BigDecimal("113.264"), new BigDecimal("23.129"), "广州市天河区");
        track4.setId(idGenerator.getAndIncrement());
        track4.setIpAddress("192.168.1.102");
        track4.setDeviceType("手机");
        track4.setOperatingSystem("Android 13");
        track4.setBrowser("Chrome Mobile");
        track4.setSessionId("session_003");
        track4.setPageUrl("/checkout");
        track4.setReferrer("/products/123");
        track4.setDuration(300);
        track4.setExtraData("{\"orderId\": \"ORD20231201001\", \"amount\": 8999.00}");
        tracks.add(track4);
    }
    
    /**
     * 查找所有行动轨迹
     */
    public List<ActivityTrack> findAll() {
        return new ArrayList<>(tracks);
    }
    
    /**
     * 根据ID查找行动轨迹
     */
    public Optional<ActivityTrack> findById(Long id) {
        return tracks.stream()
                .filter(track -> track.getId().equals(id))
                .findFirst();
    }
    
    /**
     * 根据用户ID查找行动轨迹
     */
    public List<ActivityTrack> findByUserId(Long userId) {
        return tracks.stream()
                .filter(track -> track.getUserId().equals(userId))
                .toList();
    }
    
    /**
     * 根据活动类型查找行动轨迹
     */
    public List<ActivityTrack> findByActivityType(String activityType) {
        return tracks.stream()
                .filter(track -> activityType.equals(track.getActivityType()))
                .toList();
    }
    
    /**
     * 根据用户ID和活动类型查找行动轨迹
     */
    public List<ActivityTrack> findByUserIdAndActivityType(Long userId, String activityType) {
        return tracks.stream()
                .filter(track -> track.getUserId().equals(userId) && 
                        activityType.equals(track.getActivityType()))
                .toList();
    }
    
    /**
     * 根据时间范围查找行动轨迹
     */
    public List<ActivityTrack> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return tracks.stream()
                .filter(track -> track.getCreatedAt().isAfter(startTime) && 
                        track.getCreatedAt().isBefore(endTime))
                .toList();
    }
    
    /**
     * 根据用户ID和时间范围查找行动轨迹
     */
    public List<ActivityTrack> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return tracks.stream()
                .filter(track -> track.getUserId().equals(userId) &&
                        track.getCreatedAt().isAfter(startTime) && 
                        track.getCreatedAt().isBefore(endTime))
                .toList();
    }
    
    /**
     * 根据设备类型查找行动轨迹
     */
    public List<ActivityTrack> findByDeviceType(String deviceType) {
        return tracks.stream()
                .filter(track -> deviceType.equals(track.getDeviceType()))
                .toList();
    }
    
    /**
     * 根据位置关键词查找行动轨迹
     */
    public List<ActivityTrack> findByLocationContaining(String keyword) {
        return tracks.stream()
                .filter(track -> track.getLocation() != null && 
                        track.getLocation().contains(keyword))
                .toList();
    }
    
    /**
     * 根据会话ID查找行动轨迹
     */
    public List<ActivityTrack> findBySessionId(String sessionId) {
        return tracks.stream()
                .filter(track -> sessionId.equals(track.getSessionId()))
                .toList();
    }
    
    /**
     * 根据页面URL查找行动轨迹
     */
    public List<ActivityTrack> findByPageUrl(String pageUrl) {
        return tracks.stream()
                .filter(track -> pageUrl.equals(track.getPageUrl()))
                .toList();
    }
    
    /**
     * 获取用户最近的行动轨迹
     */
    public List<ActivityTrack> findRecentByUserId(Long userId, int limit) {
        return tracks.stream()
                .filter(track -> track.getUserId().equals(userId))
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
    
    /**
     * 保存行动轨迹
     */
    public ActivityTrack save(ActivityTrack track) {
        if (track.getId() == null) {
            // 新增
            track.setId(idGenerator.getAndIncrement());
            track.setCreatedAt(LocalDateTime.now());
            tracks.add(track);
        } else {
            // 更新
            Optional<ActivityTrack> existingTrack = findById(track.getId());
            if (existingTrack.isPresent()) {
                tracks.remove(existingTrack.get());
                tracks.add(track);
            } else {
                throw new RuntimeException("行动轨迹不存在，ID: " + track.getId());
            }
        }
        return track;
    }
    
    /**
     * 批量保存行动轨迹
     */
    public List<ActivityTrack> saveAll(List<ActivityTrack> trackList) {
        List<ActivityTrack> savedTracks = new ArrayList<>();
        for (ActivityTrack track : trackList) {
            savedTracks.add(save(track));
        }
        return savedTracks;
    }
    
    /**
     * 删除行动轨迹
     */
    public boolean deleteById(Long id) {
        return tracks.removeIf(track -> track.getId().equals(id));
    }
    
    /**
     * 根据用户ID删除行动轨迹
     */
    public boolean deleteByUserId(Long userId) {
        return tracks.removeIf(track -> track.getUserId().equals(userId));
    }
    
    /**
     * 删除指定时间之前的行动轨迹
     */
    public int deleteByCreatedAtBefore(LocalDateTime cutoffTime) {
        int initialSize = tracks.size();
        tracks.removeIf(track -> track.getCreatedAt().isBefore(cutoffTime));
        return initialSize - tracks.size();
    }
    
    /**
     * 检查行动轨迹是否存在
     */
    public boolean existsById(Long id) {
        return tracks.stream().anyMatch(track -> track.getId().equals(id));
    }
    
    /**
     * 获取行动轨迹总数
     */
    public long count() {
        return tracks.size();
    }
    
    /**
     * 根据用户ID获取行动轨迹数量
     */
    public long countByUserId(Long userId) {
        return tracks.stream()
                .filter(track -> track.getUserId().equals(userId))
                .count();
    }
}