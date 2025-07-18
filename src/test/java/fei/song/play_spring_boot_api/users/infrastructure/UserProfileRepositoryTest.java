package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileRepositoryTest {
    
    private UserProfileRepository repository;
    private UserProfile testProfile;
    
    @BeforeEach
    void setUp() {
        repository = new UserProfileRepository();
        testProfile = new UserProfile(1L, 25, "男", LocalDate.of(1990, 1, 1), "13800138000", "北京市朝阳区", "软件工程师", "测试用户");
    }
    
    @Test
    void testInitializeData() {
        // When
        List<UserProfile> profiles = repository.findAll();
        
        // Then
        assertEquals(3, profiles.size());
        assertTrue(profiles.stream().anyMatch(p -> p.getUserId().equals(1L)));
        assertTrue(profiles.stream().anyMatch(p -> p.getUserId().equals(2L)));
        assertTrue(profiles.stream().anyMatch(p -> p.getUserId().equals(3L)));
    }
    
    @Test
    void testSave_NewProfile() {
        // Given
        UserProfile newProfile = new UserProfile(4L, 30, "男", LocalDate.of(1985, 5, 15), "13900139000", "上海市浦东新区", "产品经理", "新用户");
        
        // When
        UserProfile savedProfile = repository.save(newProfile);
        
        // Then
        assertNotNull(savedProfile.getId());
        assertEquals(4L, savedProfile.getUserId());
        assertEquals("男", savedProfile.getGender());
        assertEquals(LocalDate.of(1985, 5, 15), savedProfile.getBirthday());
        
        // Verify it's in the repository
        Optional<UserProfile> foundProfile = repository.findById(savedProfile.getId());
        assertTrue(foundProfile.isPresent());
        assertEquals(savedProfile, foundProfile.get());
    }
    
    @Test
    void testSave_ExistingProfile() {
        // Given
        UserProfile savedProfile = repository.save(testProfile);
        Long originalId = savedProfile.getId();
        savedProfile.setAddress("北京市海淀区");
        
        // When
        UserProfile updatedProfile = repository.save(savedProfile);
        
        // Then
        assertEquals(originalId, updatedProfile.getId());
        assertEquals("北京市海淀区", updatedProfile.getAddress());
        
        // Verify it's updated in the repository
        Optional<UserProfile> foundProfile = repository.findById(originalId);
        assertTrue(foundProfile.isPresent());
        assertEquals("北京市海淀区", foundProfile.get().getAddress());
    }
    
    @Test
    void testFindById_ExistingProfile() {
        // Given
        UserProfile savedProfile = repository.save(testProfile);
        
        // When
        Optional<UserProfile> foundProfile = repository.findById(savedProfile.getId());
        
        // Then
        assertTrue(foundProfile.isPresent());
        assertEquals(savedProfile, foundProfile.get());
    }
    
    @Test
    void testFindById_NonExistingProfile() {
        // When
        Optional<UserProfile> foundProfile = repository.findById(999L);
        
        // Then
        assertFalse(foundProfile.isPresent());
    }
    
    @Test
    void testFindByUserId() {
        // Given
        repository.save(testProfile);
        
        // When
        Optional<UserProfile> foundProfile = repository.findByUserId(1L);
        
        // Then
        assertTrue(foundProfile.isPresent());
        assertEquals(1L, foundProfile.get().getUserId());
    }
    
    @Test
    void testFindByUserId_NonExisting() {
        // When
        Optional<UserProfile> foundProfile = repository.findByUserId(999L);
        
        // Then
        assertFalse(foundProfile.isPresent());
    }
    
    @Test
    void testFindByOccupation() {
        // Given
        repository.save(testProfile);
        
        // When
        List<UserProfile> profiles = repository.findByOccupation("软件工程师");
        
        // Then
        assertTrue(profiles.size() >= 1);
        assertTrue(profiles.stream().anyMatch(p -> "软件工程师".equals(p.getOccupation())));
    }
    
    @Test
    void testFindByGender() {
        // Given
        repository.save(testProfile);
        
        // When
        List<UserProfile> maleProfiles = repository.findByGender("男");
        List<UserProfile> femaleProfiles = repository.findByGender("女");
        
        // Then
        assertTrue(maleProfiles.size() >= 1);
        assertTrue(femaleProfiles.size() >= 0);
    }
    
    @Test
    void testFindByAgeRange() {
        // Given
        repository.save(testProfile);
        
        // When
        List<UserProfile> profiles = repository.findByAgeRange(30, 40);
        
        // Then
        assertTrue(profiles.size() >= 1);
    }
    
    @Test
    void testFindByAddressContaining() {
        // Given
        repository.save(testProfile);
        
        // When
        List<UserProfile> beijingProfiles = repository.findByAddressContaining("北京");
        List<UserProfile> shanghaiProfiles = repository.findByAddressContaining("上海");
        
        // Then
        assertTrue(beijingProfiles.size() >= 1);
        assertTrue(shanghaiProfiles.size() >= 0);
    }
    
    @Test
    void testCount() {
        // Given
        long initialCount = repository.count();
        repository.save(testProfile);
        
        // When
        long newCount = repository.count();
        
        // Then
        assertEquals(initialCount + 1, newCount);
    }
    
    @Test
    void testSaveMultiple() {
        // Given
        UserProfile profile1 = new UserProfile(4L, 30, "男", LocalDate.of(1990, 1, 1), "13000000001", "地址1", "工程师", "用户1");
        UserProfile profile2 = new UserProfile(5L, 28, "女", LocalDate.of(1995, 5, 5), "13000000002", "地址2", "设计师", "用户2");
        
        // When
        UserProfile savedProfile1 = repository.save(profile1);
        UserProfile savedProfile2 = repository.save(profile2);
        
        // Then
        assertNotNull(savedProfile1.getId());
        assertNotNull(savedProfile2.getId());
        assertEquals(4L, savedProfile1.getUserId());
        assertEquals(5L, savedProfile2.getUserId());
    }
    
    @Test
    void testDeleteById_ExistingProfile() {
        // Given
        UserProfile savedProfile = repository.save(testProfile);
        Long profileId = savedProfile.getId();
        
        // When
        boolean deleted = repository.deleteById(profileId);
        
        // Then
        assertTrue(deleted);
        assertFalse(repository.existsById(profileId));
        assertFalse(repository.findById(profileId).isPresent());
    }
    
    @Test
    void testDeleteById_NonExistingProfile() {
        // When
        boolean deleted = repository.deleteById(999L);
        
        // Then
        assertFalse(deleted);
    }
    
    @Test
    void testDeleteByUserId() {
        // Given
        repository.save(testProfile);
        long initialCount = repository.count();
        
        // When
        boolean deleted = repository.deleteByUserId(1L);
        
        // Then
        assertTrue(deleted);
        assertTrue(repository.count() < initialCount);
        assertFalse(repository.findByUserId(1L).isPresent());
    }
    
    @Test
    void testExistsById() {
        // Given
        UserProfile savedProfile = repository.save(testProfile);
        
        // When & Then
        assertTrue(repository.existsById(savedProfile.getId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    void testExistsByUserId() {
        // Given
        repository.save(testProfile);
        
        // When & Then
        assertTrue(repository.existsByUserId(1L));
        assertFalse(repository.existsByUserId(999L));
    }
    
}