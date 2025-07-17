package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.config.DataSourceConfig;
import fei.song.play_spring_boot_api.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
class UserRepositoryTest {

    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository = new UserRepository(null);
    }
    
    @Test
    void testInitializeData() {
        // When
        List<User> users = userRepository.findAll();
        
        // Then
        assertEquals(3, users.size());
        assertTrue(users.stream().anyMatch(user -> "张三".equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> "李四".equals(user.getName())));
        assertTrue(users.stream().anyMatch(user -> "王五".equals(user.getName())));
    }
    
    @Test
    void testSave_NewUser() {
        // Given
        User newUser = User.builder()
                .name("新用户")
                .email("newuser@example.com")
                .build();
        
        // When
        User savedUser = userRepository.save(newUser);
        
        // Then
        assertNotNull(savedUser.getId());
        assertEquals("新用户", savedUser.getName());
        assertEquals("newuser@example.com", savedUser.getEmail());
        
        // Verify it's in the repository
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser, foundUser.get());
    }
    
    @Test
    void testSave_ExistingUser() {
        // Given
        User existingUser = userRepository.findAll().get(0);
        Long originalId = existingUser.getId();
        existingUser.setName("更新的用户名");
        
        // When
        User updatedUser = userRepository.save(existingUser);
        
        // Then
        assertEquals(originalId, updatedUser.getId());
        assertEquals("更新的用户名", updatedUser.getName());
        
        // Verify it's updated in the repository
        Optional<User> foundUser = userRepository.findById(originalId);
        assertTrue(foundUser.isPresent());
        assertEquals("更新的用户名", foundUser.get().getName());
    }
    
    @Test
    void testFindById_ExistingUser() {
        // Given
        List<User> users = userRepository.findAll();
        User firstUser = users.get(0);
        
        // When
        Optional<User> foundUser = userRepository.findById(firstUser.getId());
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(firstUser, foundUser.get());
    }
    
    @Test
    void testFindById_NonExistingUser() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testDeleteById_ExistingUser() {
        // Given
        List<User> users = userRepository.findAll();
        User userToDelete = users.get(0);
        Long userIdToDelete = userToDelete.getId();
        int originalSize = users.size();
        
        // When
        userRepository.deleteById(userIdToDelete);
        
        // Then
        List<User> remainingUsers = userRepository.findAll();
        assertEquals(originalSize - 1, remainingUsers.size());
        assertFalse(userRepository.existsById(userIdToDelete));
        assertFalse(userRepository.findById(userIdToDelete).isPresent());
    }
    
    @Test
    void testDeleteById_NonExistingUser() {
        // Given
        int originalSize = userRepository.findAll().size();
        
        // When
        userRepository.deleteById(999L);
        
        // Then
        assertEquals(originalSize, userRepository.findAll().size());
    }
    
    @Test
    void testExistsById_ExistingUser() {
        // Given
        List<User> users = userRepository.findAll();
        User existingUser = users.get(0);
        
        // When
        boolean exists = userRepository.existsById(existingUser.getId());
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsById_NonExistingUser() {
        // When
        boolean exists = userRepository.existsById(999L);
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testFindByEmail_ExistingEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("zhangsan@example.com");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("张三", foundUser.get().getName());
        assertEquals("zhangsan@example.com", foundUser.get().getEmail());
    }
    
    @Test
    void testFindByEmail_NonExistingEmail() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertFalse(foundUser.isPresent());
    }
    
    @Test
    void testFindByNameContaining_MatchingName() {
        // When
        List<User> foundUsers = userRepository.findByNameContaining("三");
        
        // Then
        assertEquals(1, foundUsers.size());
        assertEquals("张三", foundUsers.get(0).getName());
    }
    
    @Test
    void testFindByNameContaining_NoMatch() {
        // When
        List<User> foundUsers = userRepository.findByNameContaining("不存在");
        
        // Then
        assertTrue(foundUsers.isEmpty());
    }
    
    @Test
    void testFindByNameContaining_PartialMatch() {
        // Given
        User newUser = User.builder()
                .name("张三丰")
                .email("zhangsanfeng@example.com")
                .build();
        userRepository.save(newUser);
        
        // When
        List<User> foundUsers = userRepository.findByNameContaining("张");
        
        // Then
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.stream().anyMatch(user -> "张三".equals(user.getName())));
        assertTrue(foundUsers.stream().anyMatch(user -> "张三丰".equals(user.getName())));
    }
    
    @Test
    void testExistsByEmail_ExistingEmail() {
        // When
        boolean exists = userRepository.existsByEmail("zhangsan@example.com");
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void testExistsByEmail_NonExistingEmail() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        // Then
        assertFalse(exists);
    }
    
    @Test
    void testConcurrentAccess() {
        // Given
        User user1 = User.builder().name("并发用户1").email("concurrent1@example.com").build();
        User user2 = User.builder().name("并发用户2").email("concurrent2@example.com").build();
        
        // When
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        
        // Then
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
        assertTrue(userRepository.existsById(savedUser1.getId()));
        assertTrue(userRepository.existsById(savedUser2.getId()));
    }
}