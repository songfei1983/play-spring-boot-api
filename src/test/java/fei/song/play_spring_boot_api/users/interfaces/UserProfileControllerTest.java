package fei.song.play_spring_boot_api.users.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.users.application.UserProfileService;
import fei.song.play_spring_boot_api.users.domain.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfile testProfile;
    private List<UserProfile> testProfiles;

    @BeforeEach
    void setUp() {
        testProfile = new UserProfile();
        testProfile.setId(1L);
        testProfile.setUserId(1L);
        testProfile.setAge(25);
        testProfile.setGender("Male");
        testProfile.setOccupation("Engineer");
        testProfile.setAddress("123 Main St");
        testProfile.setCreatedAt(LocalDateTime.now());
        testProfile.setUpdatedAt(LocalDateTime.now());

        UserProfile profile2 = new UserProfile();
        profile2.setId(2L);
        profile2.setUserId(2L);
        profile2.setAge(30);
        profile2.setGender("Female");
        profile2.setOccupation("Designer");
        profile2.setAddress("456 Oak Ave");
        profile2.setCreatedAt(LocalDateTime.now());
        profile2.setUpdatedAt(LocalDateTime.now());

        testProfiles = Arrays.asList(testProfile, profile2);
    }

    @Test
    void getAllProfiles_ShouldReturnProfileList() throws Exception {
        // Given
        when(userProfileService.getAllProfiles()).thenReturn(testProfiles);

        // When & Then
        mockMvc.perform(get("/api/users/profiles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(userProfileService, times(1)).getAllProfiles();
    }

    @Test
    void getProfileById_ShouldReturnProfile() throws Exception {
        // Given
        when(userProfileService.getProfileById(1L)).thenReturn(testProfile);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.age").value(25));

        verify(userProfileService, times(1)).getProfileById(1L);
    }

    @Test
    void getProfileById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userProfileService.getProfileById(999L)).thenThrow(new RuntimeException("Profile not found"));

        // When & Then
        mockMvc.perform(get("/api/users/profiles/999"))
                .andExpect(status().isNotFound());

        verify(userProfileService, times(1)).getProfileById(999L);
    }

    @Test
    void getProfileByUserId_ShouldReturnProfile() throws Exception {
        // Given
        when(userProfileService.getProfileByUserId(1L)).thenReturn(testProfile);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1));

        verify(userProfileService, times(1)).getProfileByUserId(1L);
    }

    @Test
    void getProfilesByGender_ShouldReturnFilteredProfiles() throws Exception {
        // Given
        List<UserProfile> maleProfiles = Arrays.asList(testProfile);
        when(userProfileService.getProfilesByGender("Male")).thenReturn(maleProfiles);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/gender/Male"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].gender").value("Male"));

        verify(userProfileService, times(1)).getProfilesByGender("Male");
    }

    @Test
    void getProfilesByOccupation_ShouldReturnFilteredProfiles() throws Exception {
        // Given
        List<UserProfile> engineerProfiles = Arrays.asList(testProfile);
        when(userProfileService.getProfilesByOccupation("Engineer")).thenReturn(engineerProfiles);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/occupation/Engineer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].occupation").value("Engineer"));

        verify(userProfileService, times(1)).getProfilesByOccupation("Engineer");
    }

    @Test
    void searchProfilesByAddress_ShouldReturnMatchingProfiles() throws Exception {
        // Given
        when(userProfileService.searchProfilesByAddress("Main")).thenReturn(Arrays.asList(testProfile));

        // When & Then
        mockMvc.perform(get("/api/users/profiles/search")
                        .param("keyword", "Main"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userProfileService, times(1)).searchProfilesByAddress("Main");
    }

    @Test
    void createProfile_ShouldReturnCreatedProfile() throws Exception {
        // Given
        UserProfile newProfile = new UserProfile();
        newProfile.setUserId(3L);
        newProfile.setAge(35);
        newProfile.setGender("Male");
        newProfile.setOccupation("Manager");
        newProfile.setAddress("789 Pine St");

        UserProfile createdProfile = new UserProfile();
        createdProfile.setId(3L);
        createdProfile.setUserId(3L);
        createdProfile.setAge(35);
        createdProfile.setGender("Male");
        createdProfile.setOccupation("Manager");
        createdProfile.setAddress("789 Pine St");
        createdProfile.setCreatedAt(LocalDateTime.now());
        createdProfile.setUpdatedAt(LocalDateTime.now());

        when(userProfileService.createProfile(any(UserProfile.class))).thenReturn(createdProfile);

        // When & Then
        mockMvc.perform(post("/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProfile)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3));

        verify(userProfileService, times(1)).createProfile(any(UserProfile.class));
    }

    @Test
    void createProfile_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userProfileService.createProfile(any(UserProfile.class)))
                .thenThrow(new IllegalArgumentException("Invalid profile data"));

        // When & Then
        mockMvc.perform(post("/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserProfile())))
                .andExpect(status().isBadRequest());

        verify(userProfileService, times(1)).createProfile(any(UserProfile.class));
    }

    @Test
    void updateProfile_ShouldReturnUpdatedProfile() throws Exception {
        // Given
        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setId(1L);
        updatedProfile.setUserId(1L);
        updatedProfile.setAge(26);
        updatedProfile.setGender("Male");
        updatedProfile.setOccupation("Senior Engineer");
        updatedProfile.setAddress("123 Main St Updated");
        updatedProfile.setCreatedAt(testProfile.getCreatedAt());
        updatedProfile.setUpdatedAt(LocalDateTime.now());

        when(userProfileService.updateProfile(eq(1L), any(UserProfile.class))).thenReturn(updatedProfile);

        // When & Then
        mockMvc.perform(put("/api/users/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.occupation").value("Senior Engineer"));

        verify(userProfileService, times(1)).updateProfile(eq(1L), any(UserProfile.class));
    }

    @Test
    void updateProfileByUserId_ShouldReturnUpdatedProfile() throws Exception {
        // Given
        when(userProfileService.updateProfileByUserId(eq(1L), any(UserProfile.class))).thenReturn(testProfile);

        // When & Then
        mockMvc.perform(put("/api/users/profiles/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1));

        verify(userProfileService, times(1)).updateProfileByUserId(eq(1L), any(UserProfile.class));
    }

    @Test
    void deleteProfile_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(userProfileService.deleteProfile(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户档案删除成功"));

        verify(userProfileService, times(1)).deleteProfile(1L);
    }

    @Test
    void hasProfile_ShouldReturnExistsResponse() throws Exception {
        // Given
        when(userProfileService.hasProfile(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/user/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exists").value(true))
                .andExpect(jsonPath("$.userId").value(1));

        verify(userProfileService, times(1)).hasProfile(1L);
    }

    @Test
    void getProfileCount_ShouldReturnCountResponse() throws Exception {
        // Given
        when(userProfileService.getProfileCount()).thenReturn(10L);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.message").value("用户档案总数"));

        verify(userProfileService, times(1)).getProfileCount();
    }

    @Test
    void getProfileStats_ShouldReturnStatsResponse() throws Exception {
        // Given
        when(userProfileService.getProfileCount()).thenReturn(2L);
        when(userProfileService.getAllProfiles()).thenReturn(testProfiles);

        // When & Then
        mockMvc.perform(get("/api/users/profiles/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.genderStats").exists())
                .andExpect(jsonPath("$.ageGroupStats").exists());

        verify(userProfileService, times(1)).getProfileCount();
        verify(userProfileService, times(1)).getAllProfiles();
    }

    @Test
    void getAllProfiles_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userProfileService.getAllProfiles()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/profiles"))
                .andExpect(status().isInternalServerError());

        verify(userProfileService, times(1)).getAllProfiles();
    }

    @Test
    void getProfilesByGender_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userProfileService.getProfilesByGender(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid gender"));

        // When & Then
        mockMvc.perform(get("/api/users/profiles/gender/Invalid"))
                .andExpect(status().isBadRequest());

        verify(userProfileService, times(1)).getProfilesByGender("Invalid");
    }

    @Test
    void createProfile_WhenProfileAlreadyExists_ShouldReturnConflict() throws Exception {
        // Given
        when(userProfileService.createProfile(any(UserProfile.class)))
                .thenThrow(new RuntimeException("Profile already exists"));

        // When & Then
        mockMvc.perform(post("/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isConflict());

        verify(userProfileService, times(1)).createProfile(any(UserProfile.class));
    }

    @Test
    void updateProfile_WhenProfileNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userProfileService.updateProfile(eq(999L), any(UserProfile.class)))
                .thenThrow(new RuntimeException("Profile not found"));

        // When & Then
        mockMvc.perform(put("/api/users/profiles/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProfile)))
                .andExpect(status().isNotFound());

        verify(userProfileService, times(1)).updateProfile(eq(999L), any(UserProfile.class));
    }

    @Test
    void deleteProfile_WhenProfileNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userProfileService.deleteProfile(999L)).thenThrow(new RuntimeException("Profile not found"));

        // When & Then
        mockMvc.perform(delete("/api/users/profiles/999"))
                .andExpect(status().isNotFound());

        verify(userProfileService, times(1)).deleteProfile(999L);
    }
}