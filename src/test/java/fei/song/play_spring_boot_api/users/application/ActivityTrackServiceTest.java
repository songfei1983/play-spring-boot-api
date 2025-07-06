package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import fei.song.play_spring_boot_api.users.infrastructure.ActivityTrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityTrackServiceTest {

    @Mock
    private ActivityTrackRepository activityTrackRepository;

    @InjectMocks
    private ActivityTrackService activityTrackService;

    private ActivityTrack testActivity;
    private List<ActivityTrack> testActivities;

    @BeforeEach
    void setUp() {
        testActivity = ActivityTrack.builder()
                .id(1L)
                .userId(1L)
                .activityType("登录")
                .description("用户登录系统")
                .longitude(new BigDecimal("116.404"))
                .latitude(new BigDecimal("39.915"))
                .location("北京市朝阳区")
                .ipAddress("192.168.1.1")
                .deviceType("手机")
                .operatingSystem("iOS 15.0")
                .browser("Safari")
                .userAgent("Mozilla/5.0...")
                .sessionId("session123")
                .pageUrl("/login")
                .referrer("/home")
                .duration(120)
                .extraData("{\"loginType\": \"normal\"}")
                .createdAt(LocalDateTime.now())
                .build();

        testActivities = Arrays.asList(
                testActivity,
                ActivityTrack.builder()
                        .id(2L)
                        .userId(2L)
                        .activityType("浏览")
                        .description("浏览商品页面")
                        .deviceType("电脑")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    void getAllActivities_ShouldReturnAllActivities() {
        // Given
        when(activityTrackRepository.findAll()).thenReturn(testActivities);

        // When
        List<ActivityTrack> result = activityTrackService.getAllActivities();

        // Then
        assertEquals(2, result.size());
        verify(activityTrackRepository).findAll();
    }

    @Test
    void getActivityById_WithValidId_ShouldReturnActivity() {
        // Given
        when(activityTrackRepository.findById(1L)).thenReturn(Optional.of(testActivity));

        // When
        ActivityTrack result = activityTrackService.getActivityById(1L);

        // Then
        assertEquals(testActivity, result);
        verify(activityTrackRepository).findById(1L);
    }

    @Test
    void getActivityById_WithInvalidId_ShouldThrowException() {
        // Given
        when(activityTrackRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> activityTrackService.getActivityById(999L));
        assertEquals("行动轨迹不存在，ID: 999", exception.getMessage());
    }

    @Test
    void getActivitiesByUserId_WithValidUserId_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findByUserId(1L)).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testActivity, result.get(0));
        verify(activityTrackRepository).findByUserId(1L);
    }

    @Test
    void getActivitiesByUserId_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByUserId(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByType_WithValidType_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findByActivityType("登录")).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByType("登录");

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findByActivityType("登录");
    }

    @Test
    void getActivitiesByType_WithNullType_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByType(null));
        assertEquals("活动类型不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByType_WithEmptyType_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByType("  "));
        assertEquals("活动类型不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByTimeRange_WithValidRange_ShouldReturnActivities() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(activityTrackRepository.findByCreatedAtBetween(startTime, endTime))
                .thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByTimeRange(startTime, endTime);

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findByCreatedAtBetween(startTime, endTime);
    }

    @Test
    void getActivitiesByTimeRange_WithNullStartTime_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByTimeRange(null, LocalDateTime.now()));
        assertEquals("时间范围不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByTimeRange_WithInvalidRange_ShouldThrowException() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByTimeRange(startTime, endTime));
        assertEquals("开始时间不能晚于结束时间", exception.getMessage());
    }

    @Test
    void getActivitiesByDeviceType_WithValidDeviceType_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findByDeviceType("手机")).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByDeviceType("手机");

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findByDeviceType("手机");
    }

    @Test
    void getActivitiesByDeviceType_WithNullDeviceType_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByDeviceType(null));
        assertEquals("设备类型不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByLocationKeyword_WithValidKeyword_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findByLocationContaining("北京")).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByLocationKeyword("北京");

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findByLocationContaining("北京");
    }

    @Test
    void getActivitiesByLocationKeyword_WithNullKeyword_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByLocationKeyword(null));
        assertEquals("位置关键词不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesBySessionId_WithValidSessionId_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findBySessionId("session123")).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesBySessionId("session123");

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findBySessionId("session123");
    }

    @Test
    void getActivitiesBySessionId_WithNullSessionId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesBySessionId(null));
        assertEquals("会话ID不能为空", exception.getMessage());
    }

    @Test
    void getActivitiesByPageUrl_WithValidPageUrl_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findByPageUrl("/login")).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getActivitiesByPageUrl("/login");

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findByPageUrl("/login");
    }

    @Test
    void getActivitiesByPageUrl_WithNullPageUrl_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getActivitiesByPageUrl(null));
        assertEquals("页面URL不能为空", exception.getMessage());
    }

    @Test
    void getRecentActivitiesByUserId_WithValidParameters_ShouldReturnActivities() {
        // Given
        when(activityTrackRepository.findRecentByUserId(1L, 5)).thenReturn(Arrays.asList(testActivity));

        // When
        List<ActivityTrack> result = activityTrackService.getRecentActivitiesByUserId(1L, 5);

        // Then
        assertEquals(1, result.size());
        verify(activityTrackRepository).findRecentByUserId(1L, 5);
    }

    @Test
    void getRecentActivitiesByUserId_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getRecentActivitiesByUserId(null, 5));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getRecentActivitiesByUserId_WithInvalidLimit_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getRecentActivitiesByUserId(1L, 0));
        assertEquals("限制数量必须大于0", exception.getMessage());
    }

    @Test
    void recordActivity_WithValidActivity_ShouldReturnSavedActivity() {
        // Given
        ActivityTrack newActivity = ActivityTrack.builder()
                .userId(1L)
                .activityType("浏览")
                .description("浏览商品")
                .build();
        when(activityTrackRepository.save(any(ActivityTrack.class))).thenReturn(newActivity);

        // When
        ActivityTrack result = activityTrackService.recordActivity(newActivity);

        // Then
        assertNotNull(result);
        assertNotNull(newActivity.getCreatedAt());
        verify(activityTrackRepository).save(newActivity);
    }

    @Test
    void recordActivity_WithNullActivity_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(null));
        assertEquals("行动轨迹不能为空", exception.getMessage());
    }

    @Test
    void recordActivity_WithNullUserId_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .activityType("浏览")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void recordActivity_WithNullActivityType_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .userId(1L)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("活动类型不能为空", exception.getMessage());
    }

    @Test
    void recordActivity_WithInvalidLatitude_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .userId(1L)
                .activityType("浏览")
                .latitude(new BigDecimal("91"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("纬度必须在-90到90之间", exception.getMessage());
    }

    @Test
    void recordActivity_WithInvalidLongitude_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .userId(1L)
                .activityType("浏览")
                .longitude(new BigDecimal("181"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("经度必须在-180到180之间", exception.getMessage());
    }

    @Test
    void recordActivity_WithNegativeDuration_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .userId(1L)
                .activityType("浏览")
                .duration(-1)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("停留时长不能为负数", exception.getMessage());
    }

    @Test
    void recordActivity_WithFutureTimestamp_ShouldThrowException() {
        // Given
        ActivityTrack invalidActivity = ActivityTrack.builder()
                .userId(1L)
                .activityType("浏览")
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivity(invalidActivity));
        assertEquals("时间戳不能是未来时间", exception.getMessage());
    }

    @Test
    void recordActivities_WithValidActivities_ShouldReturnSavedActivities() {
        // Given
        List<ActivityTrack> newActivities = Arrays.asList(
                ActivityTrack.builder().userId(1L).activityType("浏览").build(),
                ActivityTrack.builder().userId(2L).activityType("搜索").build()
        );
        when(activityTrackRepository.saveAll(anyList())).thenReturn(newActivities);

        // When
        List<ActivityTrack> result = activityTrackService.recordActivities(newActivities);

        // Then
        assertEquals(2, result.size());
        verify(activityTrackRepository).saveAll(newActivities);
    }

    @Test
    void recordActivities_WithNullList_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivities(null));
        assertEquals("行动轨迹列表不能为空", exception.getMessage());
    }

    @Test
    void recordActivities_WithEmptyList_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.recordActivities(new ArrayList<>()));
        assertEquals("行动轨迹列表不能为空", exception.getMessage());
    }

    @Test
    void updateActivity_WithValidData_ShouldReturnUpdatedActivity() {
        // Given
        when(activityTrackRepository.existsById(1L)).thenReturn(true);
        when(activityTrackRepository.save(any(ActivityTrack.class))).thenReturn(testActivity);

        // When
        ActivityTrack result = activityTrackService.updateActivity(1L, testActivity);

        // Then
        assertEquals(testActivity, result);
        assertEquals(1L, testActivity.getId());
        verify(activityTrackRepository).existsById(1L);
        verify(activityTrackRepository).save(testActivity);
    }

    @Test
    void updateActivity_WithNonExistentId_ShouldThrowException() {
        // Given
        when(activityTrackRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityTrackService.updateActivity(999L, testActivity));
        assertEquals("行动轨迹不存在，ID: 999", exception.getMessage());
    }

    @Test
    void deleteActivity_WithValidId_ShouldReturnTrue() {
        // Given
        when(activityTrackRepository.existsById(1L)).thenReturn(true);
        when(activityTrackRepository.deleteById(1L)).thenReturn(true);

        // When
        boolean result = activityTrackService.deleteActivity(1L);

        // Then
        assertTrue(result);
        verify(activityTrackRepository).existsById(1L);
        verify(activityTrackRepository).deleteById(1L);
    }

    @Test
    void deleteActivity_WithNonExistentId_ShouldThrowException() {
        // Given
        when(activityTrackRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityTrackService.deleteActivity(999L));
        assertEquals("行动轨迹不存在，ID: 999", exception.getMessage());
    }

    @Test
    void getActivityCount_ShouldReturnCount() {
        // Given
        when(activityTrackRepository.count()).thenReturn(10L);

        // When
        long result = activityTrackService.getActivityCount();

        // Then
        assertEquals(10L, result);
        verify(activityTrackRepository).count();
    }

    @Test
    void getUserActivityStats_WithValidUserId_ShouldReturnStats() {
        // Given
        List<ActivityTrack> userActivities = Arrays.asList(
                ActivityTrack.builder().activityType("登录").deviceType("手机").duration(120).build(),
                ActivityTrack.builder().activityType("浏览").deviceType("手机").duration(180).build(),
                ActivityTrack.builder().activityType("登录").deviceType("电脑").duration(90).build()
        );
        when(activityTrackRepository.findByUserId(1L)).thenReturn(userActivities);

        // When
        Map<String, Object> result = activityTrackService.getUserActivityStats(1L);

        // Then
        assertEquals(3, result.get("totalActivities"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> typeStats = (Map<String, Long>) result.get("typeStats");
        assertEquals(2L, typeStats.get("登录"));
        assertEquals(1L, typeStats.get("浏览"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> deviceStats = (Map<String, Long>) result.get("deviceStats");
        assertEquals(2L, deviceStats.get("手机"));
        assertEquals(1L, deviceStats.get("电脑"));
        
        assertEquals(130.0, result.get("avgDuration"));
        
        verify(activityTrackRepository).findByUserId(1L);
    }

    @Test
    void getUserActivityStats_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> activityTrackService.getUserActivityStats(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getActivityTypeStats_ShouldReturnTypeStats() {
        // Given
        when(activityTrackRepository.findAll()).thenReturn(testActivities);

        // When
        Map<String, Long> result = activityTrackService.getActivityTypeStats();

        // Then
        assertNotNull(result);
        verify(activityTrackRepository).findAll();
    }

    @Test
    void getDeviceTypeStats_ShouldReturnDeviceStats() {
        // Given
        when(activityTrackRepository.findAll()).thenReturn(testActivities);

        // When
        Map<String, Long> result = activityTrackService.getDeviceTypeStats();

        // Then
        assertNotNull(result);
        verify(activityTrackRepository).findAll();
    }
}