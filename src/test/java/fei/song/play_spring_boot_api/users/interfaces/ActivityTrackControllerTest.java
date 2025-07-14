package fei.song.play_spring_boot_api.users.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.users.application.ActivityTrackService;
import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityTrackController.class)
class ActivityTrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityTrackService activityTrackService;

    @Autowired
    private ObjectMapper objectMapper;

    private ActivityTrack testActivity;
    private List<ActivityTrack> testActivities;

    @BeforeEach
    void setUp() {
        testActivity = new ActivityTrack();
        testActivity.setId(1L);
        testActivity.setUserId(1L);
        testActivity.setActivityType("LOGIN");
        testActivity.setDeviceType("WEB");
        testActivity.setLocation("New York");
        testActivity.setSessionId("session123");
        testActivity.setPageUrl("/dashboard");
        testActivity.setCreatedAt(LocalDateTime.now());

        ActivityTrack activity2 = new ActivityTrack();
        activity2.setId(2L);
        activity2.setUserId(2L);
        activity2.setActivityType("LOGOUT");
        activity2.setDeviceType("MOBILE");
        activity2.setLocation("Los Angeles");
        activity2.setSessionId("session456");
        activity2.setPageUrl("/profile");
        activity2.setCreatedAt(LocalDateTime.now());

        testActivities = Arrays.asList(testActivity, activity2);
    }

    @Test
    void getAllTracks_ShouldReturnActivityList() throws Exception {
        // Given
        when(activityTrackService.getAllActivities()).thenReturn(testActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activityType").value("LOGIN"))
                .andExpect(jsonPath("$[0].deviceType").value("WEB"))
                .andExpect(jsonPath("$[1].activityType").value("LOGOUT"));

        verify(activityTrackService, times(1)).getAllActivities();
    }

    @Test
    void getTrackById_ShouldReturnActivity() throws Exception {
        // Given
        when(activityTrackService.getActivityById(1L)).thenReturn(testActivity);

        // When & Then
        mockMvc.perform(get("/api/users/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activityType").value("LOGIN"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(activityTrackService, times(1)).getActivityById(1L);
    }

    @Test
    void getTrackById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(activityTrackService.getActivityById(999L)).thenThrow(new RuntimeException("Activity not found"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/999"))
                .andExpect(status().isNotFound());

        verify(activityTrackService, times(1)).getActivityById(999L);
    }

    @Test
    void getTracksByUserId_ShouldReturnUserActivities() throws Exception {
        // Given
        List<ActivityTrack> userActivities = Arrays.asList(testActivity);
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(userActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
    }

    @Test
    void getTracksByActivityType_ShouldReturnFilteredActivities() throws Exception {
        // Given
        List<ActivityTrack> loginActivities = Arrays.asList(testActivity);
        when(activityTrackService.getActivitiesByType("LOGIN")).thenReturn(loginActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities/type/LOGIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].activityType").value("LOGIN"));

        verify(activityTrackService, times(1)).getActivitiesByType("LOGIN");
    }

    @Test
    void getTracksByTimeRange_ShouldReturnFilteredActivities() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(activityTrackService.getActivitiesByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities/time-range")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(activityTrackService, times(1)).getActivitiesByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getTracksByDeviceType_ShouldReturnFilteredActivities() throws Exception {
        // Given
        List<ActivityTrack> webActivities = Arrays.asList(testActivity);
        when(activityTrackService.getActivitiesByDeviceType("WEB")).thenReturn(webActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities/device/WEB"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].deviceType").value("WEB"));

        verify(activityTrackService, times(1)).getActivitiesByDeviceType("WEB");
    }

    @Test
    void searchTracksByLocation_ShouldReturnMatchingActivities() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByLocationKeyword("New")).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/search")
                        .param("keyword", "New"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(activityTrackService, times(1)).getActivitiesByLocationKeyword("New");
    }

    @Test
    void getTracksBySessionId_ShouldReturnSessionActivities() throws Exception {
        // Given
        when(activityTrackService.getActivitiesBySessionId("session123")).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/session/session123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sessionId").value("session123"));

        verify(activityTrackService, times(1)).getActivitiesBySessionId("session123");
    }

    @Test
    void getTracksByPageUrl_ShouldReturnPageActivities() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByPageUrl("/dashboard")).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/page")
                        .param("pageUrl", "/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].pageUrl").value("/dashboard"));

        verify(activityTrackService, times(1)).getActivitiesByPageUrl("/dashboard");
    }

    @Test
    void getRecentTracksByUserId_ShouldReturnRecentActivities() throws Exception {
        // Given
        when(activityTrackService.getRecentActivitiesByUserId(1L, 5)).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/1/recent")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(activityTrackService, times(1)).getRecentActivitiesByUserId(1L, 5);
    }

    @Test
    void recordTrack_ShouldReturnCreatedActivity() throws Exception {
        // Given
        ActivityTrack newActivity = new ActivityTrack();
        newActivity.setUserId(3L);
        newActivity.setActivityType("CLICK");
        newActivity.setDeviceType("MOBILE");
        newActivity.setLocation("Chicago");
        newActivity.setSessionId("session789");
        newActivity.setPageUrl("/home");

        ActivityTrack createdActivity = new ActivityTrack();
        createdActivity.setId(3L);
        createdActivity.setUserId(3L);
        createdActivity.setActivityType("CLICK");
        createdActivity.setDeviceType("MOBILE");
        createdActivity.setLocation("Chicago");
        createdActivity.setSessionId("session789");
        createdActivity.setPageUrl("/home");
        createdActivity.setCreatedAt(LocalDateTime.now());

        when(activityTrackService.recordActivity(any(ActivityTrack.class))).thenReturn(createdActivity);

        // When & Then
        mockMvc.perform(post("/api/users/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivity)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.activityType").value("CLICK"))
                .andExpect(jsonPath("$.deviceType").value("MOBILE"));

        verify(activityTrackService, times(1)).recordActivity(any(ActivityTrack.class));
    }

    @Test
    void recordTracks_ShouldReturnCreatedActivities() throws Exception {
        // Given
        List<ActivityTrack> newActivities = Arrays.asList(testActivity);
        when(activityTrackService.recordActivities(anyList())).thenReturn(newActivities);

        // When & Then
        mockMvc.perform(post("/api/users/activities/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newActivities)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(activityTrackService, times(1)).recordActivities(anyList());
    }

    @Test
    void updateTrack_ShouldReturnUpdatedActivity() throws Exception {
        // Given
        ActivityTrack updatedActivity = new ActivityTrack();
        updatedActivity.setId(1L);
        updatedActivity.setUserId(1L);
        updatedActivity.setActivityType("LOGIN_UPDATED");
        updatedActivity.setDeviceType("WEB");
        updatedActivity.setLocation("New York Updated");
        updatedActivity.setSessionId("session123");
        updatedActivity.setPageUrl("/dashboard");
        updatedActivity.setCreatedAt(testActivity.getCreatedAt());

        when(activityTrackService.updateActivity(eq(1L), any(ActivityTrack.class))).thenReturn(updatedActivity);

        // When & Then
        mockMvc.perform(put("/api/users/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActivity)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activityType").value("LOGIN_UPDATED"))
                .andExpect(jsonPath("$.location").value("New York Updated"));

        verify(activityTrackService, times(1)).updateActivity(eq(1L), any(ActivityTrack.class));
    }

    @Test
    void deleteTrack_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(activityTrackService.deleteActivity(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/activities/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("行动轨迹删除成功"));

        verify(activityTrackService, times(1)).deleteActivity(1L);
    }

    @Test
    void getTrackCount_ShouldReturnCountResponse() throws Exception {
        // Given
        when(activityTrackService.getActivityCount()).thenReturn(100L);

        // When & Then
        mockMvc.perform(get("/api/users/activities/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.message").value("行动轨迹总数"));

        verify(activityTrackService, times(1)).getActivityCount();
    }

    @Test
    void getUserTrackCount_ShouldReturnUserCountResponse() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.message").value("用户行动轨迹总数"));

        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
    }

    @Test
    void getUserActivityStats_ShouldReturnStatsResponse() throws Exception {
        // Given
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalActivities", 10);
        userStats.put("loginCount", 5);
        when(activityTrackService.getUserActivityStats(1L)).thenReturn(userStats);
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/1/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.activityStats").exists());

        verify(activityTrackService, times(1)).getUserActivityStats(1L);
        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
    }

    @Test
    void getTrackStats_ShouldReturnOverallStatsResponse() throws Exception {
        // Given
        when(activityTrackService.getActivityCount()).thenReturn(2L);
        when(activityTrackService.getAllActivities()).thenReturn(testActivities);

        // When & Then
        mockMvc.perform(get("/api/users/activities/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.activityTypeStats").exists())
                .andExpect(jsonPath("$.deviceTypeStats").exists());

        verify(activityTrackService, times(1)).getActivityCount();
        verify(activityTrackService, times(1)).getAllActivities();
    }

    // Error handling tests
    @Test
    void getAllTracks_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(activityTrackService.getAllActivities()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/activities"))
                .andExpect(status().isInternalServerError());

        verify(activityTrackService, times(1)).getAllActivities();
    }

    @Test
    void getTracksByUserId_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recordTrack_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.recordActivity(any(ActivityTrack.class)))
                .thenThrow(new IllegalArgumentException("Invalid activity data"));

        // When & Then
        mockMvc.perform(post("/api/users/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ActivityTrack())))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).recordActivity(any(ActivityTrack.class));
    }

    @Test
    void updateTrack_WhenActivityNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(activityTrackService.updateActivity(eq(999L), any(ActivityTrack.class)))
                .thenThrow(new RuntimeException("Activity not found"));

        // When & Then
        mockMvc.perform(put("/api/users/activities/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testActivity)))
                .andExpect(status().isNotFound());

        verify(activityTrackService, times(1)).updateActivity(eq(999L), any(ActivityTrack.class));
    }

    @Test
    void deleteTrack_WhenActivityNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(activityTrackService.deleteActivity(999L)).thenThrow(new RuntimeException("Activity not found"));

        // When & Then
        mockMvc.perform(delete("/api/users/activities/999"))
                .andExpect(status().isNotFound());

        verify(activityTrackService, times(1)).deleteActivity(999L);
    }

    @Test
    void getUserTracksInTimeRange_ShouldReturnFilteredActivities() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        testActivity.setCreatedAt(LocalDateTime.now().minusHours(12)); // 在时间范围内
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(Arrays.asList(testActivity));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/1/time-range")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
    }

    @Test
    void getUserTracksInTimeRange_WhenInvalidUserId_ShouldReturnBadRequest() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(activityTrackService.getActivitiesByUserId(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/999/time-range")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByUserId(999L);
    }

    @Test
    void deleteTracksByUserId_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(Arrays.asList(testActivity));
        when(activityTrackService.deleteActivity(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/activities/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户行动轨迹删除成功"));

        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
        verify(activityTrackService, times(1)).deleteActivity(1L);
    }

    @Test
    void deleteTracksByUserId_WhenInvalidUserId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(delete("/api/users/activities/user/999"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByUserId(999L);
    }

    @Test
    void deleteTracksByUserId_WhenPartialFailure_ShouldReturnFailureResponse() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(1L)).thenReturn(Arrays.asList(testActivity));
        when(activityTrackService.deleteActivity(1L)).thenThrow(new RuntimeException("Delete failed"));

        // When & Then
        mockMvc.perform(delete("/api/users/activities/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户行动轨迹删除失败"));

        verify(activityTrackService, times(1)).getActivitiesByUserId(1L);
        verify(activityTrackService, times(1)).deleteActivity(1L);
    }

    @Test
    void deleteTracksBeforeTime_ShouldReturnCleanupResponse() throws Exception {
        // Given
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(30);
        testActivity.setCreatedAt(LocalDateTime.now().minusDays(60)); // 应该被删除
        when(activityTrackService.getAllActivities()).thenReturn(Arrays.asList(testActivity));
        when(activityTrackService.deleteActivity(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/activities/cleanup")
                        .param("beforeTime", beforeTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(1))
                .andExpect(jsonPath("$.message").value("清理完成，删除了 1 条记录"));

        verify(activityTrackService, times(1)).getAllActivities();
        verify(activityTrackService, times(1)).deleteActivity(1L);
    }

    @Test
    void deleteTracksBeforeTime_WhenInvalidTime_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getAllActivities())
                .thenThrow(new IllegalArgumentException("Invalid time parameter"));

        // When & Then
        mockMvc.perform(delete("/api/users/activities/cleanup")
                        .param("beforeTime", "invalid-time"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTracksBeforeTime_WhenServiceError_ShouldReturnInternalServerError() throws Exception {
        // Given
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(30);
        when(activityTrackService.getAllActivities())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(delete("/api/users/activities/cleanup")
                        .param("beforeTime", beforeTime.toString()))
                .andExpect(status().isInternalServerError());

        verify(activityTrackService, times(1)).getAllActivities();
    }

    // 添加更多异常处理测试
    @Test
    void getTracksByActivityType_WhenInvalidType_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByType(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid activity type"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/type/INVALID"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByType("INVALID");
    }

    @Test
    void getTracksByDeviceType_WhenInvalidDevice_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByDeviceType(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid device type"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/device/INVALID"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByDeviceType("INVALID");
    }

    @Test
    void searchTracksByLocation_WhenInvalidKeyword_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByLocationKeyword(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid keyword"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/search")
                        .param("keyword", ""))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByLocationKeyword("");
    }

    @Test
    void getTracksBySessionId_WhenInvalidSession_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesBySessionId(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid session ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/session/invalid"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesBySessionId("invalid");
    }

    @Test
    void getTracksByPageUrl_WhenInvalidUrl_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByPageUrl(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid page URL"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/page")
                        .param("pageUrl", ""))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByPageUrl("");
    }

    @Test
    void getRecentTracksByUserId_WhenInvalidUserId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getRecentActivitiesByUserId(anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/999/recent")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getRecentActivitiesByUserId(999L, 10);
    }

    @Test
    void getTracksByTimeRange_WhenInvalidTimeRange_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new IllegalArgumentException("Invalid time range"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/time-range")
                        .param("startTime", "invalid")
                        .param("endTime", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recordTracks_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.recordActivities(anyList()))
                .thenThrow(new IllegalArgumentException("Invalid activities data"));

        // When & Then
        mockMvc.perform(post("/api/users/activities/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).recordActivities(anyList());
    }

    @Test
    void updateTrack_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.updateActivity(anyLong(), any(ActivityTrack.class)))
                .thenThrow(new IllegalArgumentException("Invalid activity data"));

        // When & Then
        mockMvc.perform(put("/api/users/activities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ActivityTrack())))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).updateActivity(eq(1L), any(ActivityTrack.class));
    }

    @Test
    void getUserTrackCount_WhenInvalidUserId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getActivitiesByUserId(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/999/count"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getActivitiesByUserId(999L);
    }

    @Test
    void getUserActivityStats_WhenInvalidUserId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(activityTrackService.getUserActivityStats(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/user/999/stats"))
                .andExpect(status().isBadRequest());

        verify(activityTrackService, times(1)).getUserActivityStats(999L);
    }

    @Test
    void getTrackCount_WhenServiceError_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(activityTrackService.getActivityCount())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/count"))
                .andExpect(status().isInternalServerError());

        verify(activityTrackService, times(1)).getActivityCount();
    }

    @Test
    void getTrackStats_WhenServiceError_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(activityTrackService.getActivityCount())
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/activities/stats"))
                .andExpect(status().isInternalServerError());

        verify(activityTrackService, times(1)).getActivityCount();
    }
}