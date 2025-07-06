package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.UserProfile;
import fei.song.play_spring_boot_api.users.infrastructure.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfile testProfile1;
    private UserProfile testProfile2;
    private List<UserProfile> testProfiles;

    @BeforeEach
    void setUp() {
        testProfile1 = UserProfile.builder()
                .id(1L)
                .userId(1L)
                .age(25)
                .gender("男")
                .birthday(LocalDate.of(1998, 5, 15))
                .phoneNumber("13800138000")
                .address("北京市朝阳区")
                .occupation("软件工程师")
                .bio("热爱编程的技术人员")
                .build();

        testProfile2 = UserProfile.builder()
                .id(2L)
                .userId(2L)
                .age(30)
                .gender("女")
                .birthday(LocalDate.of(1993, 8, 20))
                .phoneNumber("13900139000")
                .address("上海市浦东新区")
                .occupation("产品经理")
                .bio("专注用户体验")
                .build();

        testProfiles = Arrays.asList(testProfile1, testProfile2);
    }

    @Test
    void testGetAllProfiles() {
        // Given
        when(userProfileRepository.findAll()).thenReturn(testProfiles);

        // When
        List<UserProfile> result = userProfileService.getAllProfiles();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("软件工程师", result.get(0).getOccupation());
        assertEquals("产品经理", result.get(1).getOccupation());
        verify(userProfileRepository).findAll();
    }

    @Test
    void testGetProfileById_Success() {
        // Given
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(testProfile1));

        // When
        UserProfile result = userProfileService.getProfileById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("软件工程师", result.getOccupation());
        verify(userProfileRepository).findById(1L);
    }

    @Test
    void testGetProfileById_NotFound() {
        // Given
        when(userProfileRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.getProfileById(999L));
        assertEquals("用户档案不存在，ID: 999", exception.getMessage());
        verify(userProfileRepository).findById(999L);
    }

    @Test
    void testGetProfileByUserId_Success() {
        // Given
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile1));

        // When
        UserProfile result = userProfileService.getProfileByUserId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("软件工程师", result.getOccupation());
        verify(userProfileRepository).findByUserId(1L);
    }

    @Test
    void testGetProfileByUserId_NotFound() {
        // Given
        when(userProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.getProfileByUserId(999L));
        assertEquals("用户档案不存在，用户ID: 999", exception.getMessage());
        verify(userProfileRepository).findByUserId(999L);
    }

    @Test
    void testGetProfilesByGender_Success() {
        // Given
        when(userProfileRepository.findByGender("男")).thenReturn(Arrays.asList(testProfile1));

        // When
        List<UserProfile> result = userProfileService.getProfilesByGender("男");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("男", result.get(0).getGender());
        verify(userProfileRepository).findByGender("男");
    }

    @Test
    void testGetProfilesByGender_InvalidGender() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.getProfilesByGender("无效性别"));
        assertEquals("性别只能是：男、女、其他", exception.getMessage());
        verify(userProfileRepository, never()).findByGender(any());
    }

    @Test
    void testGetProfilesByAgeRange_Success() {
        // Given
        when(userProfileRepository.findByAgeRange(20, 30)).thenReturn(testProfiles);

        // When
        List<UserProfile> result = userProfileService.getProfilesByAgeRange(20, 30);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userProfileRepository).findByAgeRange(20, 30);
    }

    @Test
    void testGetProfilesByAgeRange_NullAge() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.getProfilesByAgeRange(null, 30));
        assertEquals("年龄范围不能为空", exception.getMessage());
        verify(userProfileRepository, never()).findByAgeRange(any(), any());
    }

    @Test
    void testGetProfilesByAgeRange_NegativeAge() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.getProfilesByAgeRange(-1, 30));
        assertEquals("年龄不能为负数", exception.getMessage());
        verify(userProfileRepository, never()).findByAgeRange(any(), any());
    }

    @Test
    void testGetProfilesByAgeRange_InvalidRange() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.getProfilesByAgeRange(30, 20));
        assertEquals("最小年龄不能大于最大年龄", exception.getMessage());
        verify(userProfileRepository, never()).findByAgeRange(any(), any());
    }

    @Test
    void testGetProfilesByOccupation_Success() {
        // Given
        when(userProfileRepository.findByOccupation("软件工程师")).thenReturn(Arrays.asList(testProfile1));

        // When
        List<UserProfile> result = userProfileService.getProfilesByOccupation("软件工程师");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("软件工程师", result.get(0).getOccupation());
        verify(userProfileRepository).findByOccupation("软件工程师");
    }

    @Test
    void testGetProfilesByOccupation_EmptyOccupation() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.getProfilesByOccupation(""));
        assertEquals("职业不能为空", exception.getMessage());
        verify(userProfileRepository, never()).findByOccupation(any());
    }

    @Test
    void testSearchProfilesByAddress_Success() {
        // Given
        when(userProfileRepository.findByAddressContaining("北京")).thenReturn(Arrays.asList(testProfile1));

        // When
        List<UserProfile> result = userProfileService.searchProfilesByAddress("北京");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAddress().contains("北京"));
        verify(userProfileRepository).findByAddressContaining("北京");
    }

    @Test
    void testSearchProfilesByAddress_EmptyKeyword() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.searchProfilesByAddress(""));
        assertEquals("搜索关键词不能为空", exception.getMessage());
        verify(userProfileRepository, never()).findByAddressContaining(any());
    }

    @Test
    void testCreateProfile_Success() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .age(28)
                .gender("男")
                .birthday(LocalDate.of(1995, 3, 10))
                .phoneNumber("13700137000")
                .occupation("设计师")
                .build();
        
        when(userProfileRepository.existsByUserId(3L)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(newProfile);

        // When
        UserProfile result = userProfileService.createProfile(newProfile);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getUserId());
        assertEquals("设计师", result.getOccupation());
        verify(userProfileRepository).existsByUserId(3L);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void testCreateProfile_UserAlreadyExists() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(1L)
                .age(28)
                .gender("男")
                .build();
        
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("用户已存在档案，用户ID: 1", exception.getMessage());
        verify(userProfileRepository).existsByUserId(1L);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_NullProfile() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(null));
        assertEquals("用户档案不能为空", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_NullUserId() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .age(28)
                .gender("男")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("用户ID不能为空", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_InvalidAge() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .age(-1)
                .gender("男")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("年龄不能为负数", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_AgeOver150() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .age(151)
                .gender("男")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("年龄不能超过150岁", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_FutureBirthday() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .age(25)
                .gender("男")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("生日不能是未来日期", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testCreateProfile_InvalidPhoneNumber() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .age(25)
                .gender("男")
                .phoneNumber("12345")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userProfileService.createProfile(newProfile));
        assertEquals("手机号码格式不正确", exception.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testUpdateProfile_Success() {
        // Given
        UserProfile updateProfile = UserProfile.builder()
                .userId(1L)
                .age(26)
                .gender("男")
                .occupation("高级软件工程师")
                .build();
        
        when(userProfileRepository.existsById(1L)).thenReturn(true);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updateProfile);

        // When
        UserProfile result = userProfileService.updateProfile(1L, updateProfile);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("高级软件工程师", result.getOccupation());
        verify(userProfileRepository).existsById(1L);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void testUpdateProfile_NotFound() {
        // Given
        UserProfile updateProfile = UserProfile.builder()
                .userId(1L)
                .age(26)
                .build();
        
        when(userProfileRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.updateProfile(999L, updateProfile));
        assertEquals("用户档案不存在，ID: 999", exception.getMessage());
        verify(userProfileRepository).existsById(999L);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testUpdateProfileByUserId_Success() {
        // Given
        UserProfile updateProfile = UserProfile.builder()
                .userId(1L)
                .age(26)
                .occupation("高级软件工程师")
                .build();
        
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile1));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(updateProfile);

        // When
        UserProfile result = userProfileService.updateProfileByUserId(1L, updateProfile);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("高级软件工程师", result.getOccupation());
        verify(userProfileRepository).findByUserId(1L);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void testUpdateProfileByUserId_NotFound() {
        // Given
        UserProfile updateProfile = UserProfile.builder()
                .userId(999L)
                .age(26)
                .build();
        
        when(userProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.updateProfileByUserId(999L, updateProfile));
        assertEquals("用户档案不存在，用户ID: 999", exception.getMessage());
        verify(userProfileRepository).findByUserId(999L);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void testDeleteProfile_Success() {
        // Given
        when(userProfileRepository.existsById(1L)).thenReturn(true);
        when(userProfileRepository.deleteById(1L)).thenReturn(true);

        // When
        boolean result = userProfileService.deleteProfile(1L);

        // Then
        assertTrue(result);
        verify(userProfileRepository).existsById(1L);
        verify(userProfileRepository).deleteById(1L);
    }

    @Test
    void testDeleteProfile_NotFound() {
        // Given
        when(userProfileRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.deleteProfile(999L));
        assertEquals("用户档案不存在，ID: 999", exception.getMessage());
        verify(userProfileRepository).existsById(999L);
        verify(userProfileRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteProfileByUserId_Success() {
        // Given
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);
        when(userProfileRepository.deleteByUserId(1L)).thenReturn(true);

        // When
        boolean result = userProfileService.deleteProfileByUserId(1L);

        // Then
        assertTrue(result);
        verify(userProfileRepository).existsByUserId(1L);
        verify(userProfileRepository).deleteByUserId(1L);
    }

    @Test
    void testDeleteProfileByUserId_NotFound() {
        // Given
        when(userProfileRepository.existsByUserId(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userProfileService.deleteProfileByUserId(999L));
        assertEquals("用户档案不存在，用户ID: 999", exception.getMessage());
        verify(userProfileRepository).existsByUserId(999L);
        verify(userProfileRepository, never()).deleteByUserId(any());
    }

    @Test
    void testHasProfile_True() {
        // Given
        when(userProfileRepository.existsByUserId(1L)).thenReturn(true);

        // When
        boolean result = userProfileService.hasProfile(1L);

        // Then
        assertTrue(result);
        verify(userProfileRepository).existsByUserId(1L);
    }

    @Test
    void testHasProfile_False() {
        // Given
        when(userProfileRepository.existsByUserId(999L)).thenReturn(false);

        // When
        boolean result = userProfileService.hasProfile(999L);

        // Then
        assertFalse(result);
        verify(userProfileRepository).existsByUserId(999L);
    }

    @Test
    void testGetProfileCount() {
        // Given
        when(userProfileRepository.count()).thenReturn(5L);

        // When
        long result = userProfileService.getProfileCount();

        // Then
        assertEquals(5L, result);
        verify(userProfileRepository).count();
    }

    @Test
    void testCreateProfile_WithBirthdayCalculatesAge() {
        // Given
        UserProfile newProfile = UserProfile.builder()
                .userId(3L)
                .gender("男")
                .birthday(LocalDate.of(1995, 3, 10))
                .phoneNumber("13700137000")
                .build();
        
        when(userProfileRepository.existsByUserId(3L)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile saved = invocation.getArgument(0);
            // 验证年龄是否被正确计算
            assertNotNull(saved.getAge());
            assertTrue(saved.getAge() >= 28); // 1995年出生的人至少28岁
            return saved;
        });

        // When
        UserProfile result = userProfileService.createProfile(newProfile);

        // Then
        assertNotNull(result);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void testUpdateProfile_WithBirthdayCalculatesAge() {
        // Given
        UserProfile updateProfile = UserProfile.builder()
                .userId(1L)
                .gender("男")
                .birthday(LocalDate.of(1990, 6, 15))
                .build();
        
        when(userProfileRepository.existsById(1L)).thenReturn(true);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile saved = invocation.getArgument(0);
            // 验证年龄是否被正确计算
            assertNotNull(saved.getAge());
            assertTrue(saved.getAge() >= 33); // 1990年出生的人至少33岁
            return saved;
        });

        // When
        UserProfile result = userProfileService.updateProfile(1L, updateProfile);

        // Then
        assertNotNull(result);
        verify(userProfileRepository).save(any(UserProfile.class));
    }
}