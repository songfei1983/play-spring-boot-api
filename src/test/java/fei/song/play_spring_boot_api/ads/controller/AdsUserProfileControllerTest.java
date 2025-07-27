package fei.song.play_spring_boot_api.ads.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.service.AdsUserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdsUserProfileController 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AdsUserProfileControllerTest {

    @Mock
    private AdsUserProfileService userProfileService;

    @InjectMocks
    private AdsUserProfileController adsUserProfileController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adsUserProfileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // 简单的异常处理器用于测试
    @org.springframework.web.bind.annotation.ControllerAdvice
    static class GlobalExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
        public org.springframework.http.ResponseEntity<String> handleRuntimeException(RuntimeException e) {
            return org.springframework.http.ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @Test
    void testGetUsersByGender_Success() throws Exception {
        // 准备测试数据
        UserProfileEntity profile1 = new UserProfileEntity();
        profile1.setUserId("user1");
        UserProfileEntity.Demographics demographics1 = new UserProfileEntity.Demographics();
        demographics1.setGender("male");
        profile1.setDemographics(demographics1);
        
        UserProfileEntity profile2 = new UserProfileEntity();
        profile2.setUserId("user2");
        UserProfileEntity.Demographics demographics2 = new UserProfileEntity.Demographics();
        demographics2.setGender("male");
        profile2.setDemographics(demographics2);
        
        List<UserProfileEntity> profiles = Arrays.asList(profile1, profile2);

        // Mock 服务调用
        when(userProfileService.findByGender("male")).thenReturn(profiles);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/male"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user2"));

        // 验证服务调用
        verify(userProfileService, times(1)).findByGender("male");
    }

    @Test
    void testGetUsersByCountry_Success() throws Exception {
        // 准备测试数据
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserId("user1");
        UserProfileEntity.Demographics demographics = new UserProfileEntity.Demographics();
        UserProfileEntity.Geo geo = new UserProfileEntity.Geo();
        geo.setCountry("China");
        demographics.setGeo(geo);
        profile.setDemographics(demographics);
        
        List<UserProfileEntity> profiles = Collections.singletonList(profile);

        // Mock 服务调用
        when(userProfileService.findByCountry("China")).thenReturn(profiles);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/country/China"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("user1"));

        // 验证服务调用
        verify(userProfileService, times(1)).findByCountry("China");
    }

    @Test
    void testGetUsersByCity_Success() throws Exception {
        // 准备测试数据
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserId("user1");
        UserProfileEntity.Demographics demographics = new UserProfileEntity.Demographics();
        UserProfileEntity.Geo geo = new UserProfileEntity.Geo();
        geo.setCity("Beijing");
        demographics.setGeo(geo);
        profile.setDemographics(demographics);
        
        List<UserProfileEntity> profiles = Collections.singletonList(profile);

        // Mock 服务调用
        when(userProfileService.findByCity("Beijing")).thenReturn(profiles);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/city/Beijing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value("user1"));

        // 验证服务调用
        verify(userProfileService, times(1)).findByCity("Beijing");
    }

    @Test
    void testGetUsersByInterest_Success() throws Exception {
        // 准备测试数据
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserId("user1");
        
        List<UserProfileEntity> profiles = Collections.singletonList(profile);

        // Mock 服务调用
        when(userProfileService.findByInterestCategory("technology")).thenReturn(profiles);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/interest/technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        // 验证服务调用
        verify(userProfileService, times(1)).findByInterestCategory("technology");
    }

    @Test
    void testGetProfiles_Success() throws Exception {
        // 准备测试数据
        UserProfileEntity profile = new UserProfileEntity();
        profile.setUserId("user1");
        
        List<UserProfileEntity> profiles = Collections.singletonList(profile);
        Page<UserProfileEntity> page = new PageImpl<>(profiles, PageRequest.of(0, 20), 1);

        // Mock 服务调用
        when(userProfileService.findAll(any(PageRequest.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        // 验证服务调用
        verify(userProfileService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testAddPurchase_Success() throws Exception {
        // 准备测试数据
        Map<String, Object> purchaseData = Map.of(
            "category", "electronics",
            "amount", 1000.0,
            "timestamp", "2023-12-01T10:00:00"
        );

        UserProfileEntity updatedProfile = new UserProfileEntity();
        updatedProfile.setUserId("user1");

        // Mock 服务调用
        when(userProfileService.addPurchase(eq("user1"), any(UserProfileEntity.Purchase.class))).thenReturn(updatedProfile);

        // 执行测试
        mockMvc.perform(post("/api/v1/ads/user-profiles/user1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"));

        // 验证服务调用
        verify(userProfileService, times(1)).addPurchase(eq("user1"), any(UserProfileEntity.Purchase.class));
    }

    @Test
    void testGetProfileStats_Success() throws Exception {
        // Mock 服务调用 - 控制器调用的是countProfiles方法
        when(userProfileService.countProfiles()).thenReturn(1000L);

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProfiles").value(1000))
                .andExpect(jsonPath("$.activeProfiles").value(1000));

        // 验证服务调用
        verify(userProfileService, times(2)).countProfiles();
    }

    @Test
    void testDeleteProfile_Success() throws Exception {
        // Mock 服务调用
        doNothing().when(userProfileService).deleteProfile("user1");

        // 执行测试
        mockMvc.perform(delete("/api/v1/ads/user-profiles/user1"))
                .andExpect(status().isNoContent());

        // 验证服务调用
        verify(userProfileService, times(1)).deleteProfile("user1");
    }

    @Test
    void testGetUsersByGender_ServiceException() throws Exception {
        // Mock 服务抛出异常
        when(userProfileService.findByGender("male")).thenThrow(new RuntimeException("Service error"));

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/male"))
                .andExpect(status().isInternalServerError());

        // 验证服务调用
        verify(userProfileService, times(1)).findByGender("male");
    }

    @Test
    void testGetUsersByGender_EmptyResult() throws Exception {
        // Mock 服务返回空列表
        when(userProfileService.findByGender("unknown")).thenReturn(Collections.emptyList());

        // 执行测试
        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        // 验证服务调用
        verify(userProfileService, times(1)).findByGender("unknown");
    }
}