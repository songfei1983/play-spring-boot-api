package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.config.DataSourceConfig;
import fei.song.play_spring_boot_api.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryServiceTest {

    @Mock
    private DataSourceConfig.DataSourceProperties dataSourceProperties;
    
    @Mock
    private UserRepository memoryUserRepository;
    
    @Mock
    private UserJpaRepository jpaUserRepository;
    
    private UserRepositoryService userRepositoryService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("测试用户")
                .email("test@example.com")
                .build();
    }
    
    @Test
    void testFindAll_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        List<User> expectedUsers = Arrays.asList(testUser);
        when(jpaUserRepository.findAll()).thenReturn(expectedUsers);
        
        // When
        List<User> result = userRepositoryService.findAll();
        
        // Then
        assertEquals(expectedUsers, result);
        verify(jpaUserRepository).findAll();
        verify(memoryUserRepository, never()).findAll();
    }
    
    @Test
    void testFindAll_WithJpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        List<User> expectedUsers = Arrays.asList(testUser);
        when(memoryUserRepository.findAll()).thenReturn(expectedUsers);
        
        // When
        List<User> result = userRepositoryService.findAll();
        
        // Then
        assertEquals(expectedUsers, result);
        verify(memoryUserRepository).findAll();
    }
    
    @Test
    void testFindAll_NoRepositoryAvailable() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, null, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> userRepositoryService.findAll());
    }
    
    @Test
    void testFindById_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userRepositoryService.findById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(jpaUserRepository).findById(1L);
        verify(memoryUserRepository, never()).findById(1L);
    }
    
    @Test
    void testFindById_WithJpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        when(memoryUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userRepositoryService.findById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(memoryUserRepository).findById(1L);
    }
    
    @Test
    void testFindById_NoRepositoryAvailable() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, null, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When
        Optional<User> result = userRepositoryService.findById(1L);
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void testSave_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(jpaUserRepository.save(testUser)).thenReturn(testUser);
        
        // When
        User result = userRepositoryService.save(testUser);
        
        // Then
        assertEquals(testUser, result);
        verify(jpaUserRepository).save(testUser);
        verify(memoryUserRepository, never()).save(testUser);
    }
    
    @Test
    void testSave_WithJpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        when(memoryUserRepository.save(testUser)).thenReturn(testUser);
        
        // When
        User result = userRepositoryService.save(testUser);
        
        // Then
        assertEquals(testUser, result);
        verify(memoryUserRepository).save(testUser);
    }
    
    @Test
    void testSave_NoRepositoryAvailable() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, null, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> userRepositoryService.save(testUser));
    }
    
    @Test
    void testDeleteById_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        
        // When
        userRepositoryService.deleteById(1L);
        
        // Then
        verify(jpaUserRepository).deleteById(1L);
        verify(memoryUserRepository, never()).deleteById(1L);
    }
    
    @Test
    void testDeleteById_WithJpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When
        userRepositoryService.deleteById(1L);
        
        // Then
        verify(memoryUserRepository).deleteById(1L);
    }
    
    @Test
    void testExistsById_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(jpaUserRepository.existsById(1L)).thenReturn(true);
        
        // When
        boolean result = userRepositoryService.existsById(1L);
        
        // Then
        assertTrue(result);
        verify(jpaUserRepository).existsById(1L);
        verify(memoryUserRepository, never()).existsById(1L);
    }
    
    @Test
    void testExistsById_WithJpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        when(memoryUserRepository.existsById(1L)).thenReturn(true);
        
        // When
        boolean result = userRepositoryService.existsById(1L);
        
        // Then
        assertTrue(result);
        verify(memoryUserRepository).existsById(1L);
    }
    
    @Test
    void testExistsById_NoRepositoryAvailable() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, null, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When
        boolean result = userRepositoryService.existsById(1L);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void testFindByEmail_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(jpaUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // When
        Optional<User> result = userRepositoryService.findByEmail("test@example.com");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(jpaUserRepository).findByEmail("test@example.com");
        verify(memoryUserRepository, never()).findByEmail("test@example.com");
    }
    
    @Test
    void testFindByNameContaining_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        List<User> expectedUsers = Arrays.asList(testUser);
        when(jpaUserRepository.findByNameContaining("测试")).thenReturn(expectedUsers);
        
        // When
        List<User> result = userRepositoryService.findByNameContaining("测试");
        
        // Then
        assertEquals(expectedUsers, result);
        verify(jpaUserRepository).findByNameContaining("测试");
        verify(memoryUserRepository, never()).findByNameContaining("测试");
    }
    
    @Test
    void testExistsByEmail_WithJpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(jpaUserRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // When
        boolean result = userRepositoryService.existsByEmail("test@example.com");
        
        // Then
        assertTrue(result);
        verify(jpaUserRepository).existsByEmail("test@example.com");
        verify(memoryUserRepository, never()).existsByEmail("test@example.com");
    }
    
    @Test
    void testGetCurrentDataSourceType_JpaEnabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, jpaUserRepository);
        when(dataSourceProperties.isEnableJpa()).thenReturn(true);
        when(dataSourceProperties.getType()).thenReturn(DataSourceConfig.DataSourceType.H2);
        
        // When
        String result = userRepositoryService.getCurrentDataSourceType();
        
        // Then
        assertEquals("JPA Database (H2)", result);
    }
    
    @Test
    void testGetCurrentDataSourceType_JpaDisabled() {
        // Given
        userRepositoryService = new UserRepositoryService(dataSourceProperties, memoryUserRepository, null);
        when(dataSourceProperties.isEnableJpa()).thenReturn(false);
        
        // When
        String result = userRepositoryService.getCurrentDataSourceType();
        
        // Then
        assertEquals("Memory Storage", result);
    }
}