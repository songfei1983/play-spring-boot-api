package fei.song.play_spring_boot_api.ads.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.ads.controller.AdsUserProfileController;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.service.AdsUserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdsUserProfileController.class)
class AdsUserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdsUserProfileService adsUserProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileEntity testProfile;

    @BeforeEach
    void setUp() {
        testProfile = new UserProfileEntity();
        testProfile.setUserId("user1");
        testProfile.setCreatedAt(LocalDateTime.now());
        testProfile.setUpdatedAt(LocalDateTime.now());
        
        // 设置Demographics
        UserProfileEntity.Demographics demographics = new UserProfileEntity.Demographics();
        demographics.setGender("male");
        demographics.setAge(25);
        testProfile.setDemographics(demographics);
        
        // 设置Geo
        UserProfileEntity.Geo geo = new UserProfileEntity.Geo();
        geo.setCountry("US");
        geo.setCity("New York");
        demographics.setGeo(geo);
        
        // 设置Interest
        UserProfileEntity.Interest interest = new UserProfileEntity.Interest();
        interest.setCategory("technology");
        interest.setSubcategory("programming");
        interest.setScore(0.8);
        testProfile.setInterests(Arrays.asList(interest));
    }

    @Test
    void testGetUsersByGender_Success() throws Exception {
        List<UserProfileEntity> profiles = Arrays.asList(testProfile);
        when(adsUserProfileService.findByGender("male")).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/male"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));

        verify(adsUserProfileService).findByGender("male");
    }

    @Test
    void testGetUsersByGender_EmptyResult() throws Exception {
        when(adsUserProfileService.findByGender("female")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/female"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(adsUserProfileService).findByGender("female");
    }

    @Test
    void testGetUsersByCountry_Success() throws Exception {
        List<UserProfileEntity> profiles = Arrays.asList(testProfile);
        when(adsUserProfileService.findByCountry("US")).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/ads/user-profiles/country/US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));

        verify(adsUserProfileService).findByCountry("US");
    }

    @Test
    void testGetUsersByCity_Success() throws Exception {
        List<UserProfileEntity> profiles = Arrays.asList(testProfile);
        when(adsUserProfileService.findByCity("New York")).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/ads/user-profiles/city/New York"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));

        verify(adsUserProfileService).findByCity("New York");
    }

    @Test
    void testGetUsersByInterest_Success() throws Exception {
        List<UserProfileEntity> profiles = Arrays.asList(testProfile);
        when(adsUserProfileService.findByInterestCategory("technology")).thenReturn(profiles);

        mockMvc.perform(get("/api/v1/ads/user-profiles/interest/technology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("user1"));

        verify(adsUserProfileService).findByInterestCategory("technology");
    }

    @Test
    void testGetProfiles_Success() throws Exception {
        Page<UserProfileEntity> page = new PageImpl<>(Arrays.asList(testProfile), PageRequest.of(0, 10), 1);
        when(adsUserProfileService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/ads/user-profiles")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value("user1"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(adsUserProfileService).findAll(any(PageRequest.class));
    }

    @Test
    void testAddPurchase_Success() throws Exception {
        UserProfileEntity.Purchase purchase = new UserProfileEntity.Purchase();
        purchase.setProductId("product1");
        purchase.setAmount(100.0);
        purchase.setTimestamp(LocalDateTime.now());
        
        when(adsUserProfileService.addPurchase(eq("user1"), any(UserProfileEntity.Purchase.class)))
            .thenReturn(testProfile);

        mockMvc.perform(post("/api/v1/ads/user-profiles/user1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"));

        verify(adsUserProfileService).addPurchase(eq("user1"), any(UserProfileEntity.Purchase.class));
    }

    @Test
    void testAddPurchase_InvalidRequest() throws Exception {
        // 发送空的JSON对象会导致NullPointerException，返回500状态码
        mockMvc.perform(post("/api/v1/ads/user-profiles/user1/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetProfileStats_Success() throws Exception {
        // Mock countProfiles方法，因为controller调用的是这个方法
        when(adsUserProfileService.countProfiles()).thenReturn(100L);

        mockMvc.perform(get("/api/v1/ads/user-profiles/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProfiles").value(100))
                .andExpect(jsonPath("$.activeProfiles").value(100));

        // 验证countProfiles被调用了两次（totalProfiles和activeProfiles各一次）
        verify(adsUserProfileService, times(2)).countProfiles();
    }

    @Test
    void testDeleteProfile_Success() throws Exception {
        doNothing().when(adsUserProfileService).deleteProfile("user1");

        mockMvc.perform(delete("/api/v1/ads/user-profiles/user1"))
                .andExpect(status().isNoContent());

        verify(adsUserProfileService).deleteProfile("user1");
    }

    @Test
    void testDeleteProfile_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("用户画像不存在: user1"))
            .when(adsUserProfileService).deleteProfile("user1");

        mockMvc.perform(delete("/api/v1/ads/user-profiles/user1"))
                .andExpect(status().isBadRequest());

        verify(adsUserProfileService).deleteProfile("user1");
    }

    @Test
    void testServiceException_InternalServerError() throws Exception {
        when(adsUserProfileService.findByGender("male"))
            .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/ads/user-profiles/gender/male"))
                .andExpect(status().isInternalServerError());

        verify(adsUserProfileService).findByGender("male");
    }
}