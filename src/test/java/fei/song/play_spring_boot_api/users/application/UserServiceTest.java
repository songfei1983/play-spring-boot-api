package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.User;
import fei.song.play_spring_boot_api.users.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("张三")
                .email("zhangsan@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("李四")
                .email("lisi@example.com")
                .build();

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    void testGetAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
        assertEquals("李四", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("张三", result.get().getName());
        assertEquals("zhangsan@example.com", result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        User newUser = User.builder()
                .name("王五")
                .email("wangwu@example.com")
                .build();

        User savedUser = User.builder()
                .id(3L)
                .name("王五")
                .email("wangwu@example.com")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("王五", result.getName());
        assertEquals("wangwu@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_EmptyName() {
        // Given
        User invalidUser = User.builder()
                .name("")
                .email("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidUser)
        );
        assertEquals("用户名不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_NullName() {
        // Given
        User invalidUser = User.builder()
                .name(null)
                .email("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidUser)
        );
        assertEquals("用户名不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_EmptyEmail() {
        // Given
        User invalidUser = User.builder()
                .name("测试用户")
                .email("")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidUser)
        );
        assertEquals("邮箱不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_NullEmail() {
        // Given
        User invalidUser = User.builder()
                .name("测试用户")
                .email(null)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidUser)
        );
        assertEquals("邮箱不能为空", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUser_InvalidEmail() {
        // Given
        User invalidUser = User.builder()
                .name("测试用户")
                .email("invalid-email")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(invalidUser)
        );
        assertEquals("邮箱格式不正确", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        User updateUser = User.builder()
                .name("张三更新")
                .email("zhangsan_updated@example.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("张三更新")
                .email("zhangsan_updated@example.com")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        Optional<User> result = userService.updateUser(1L, updateUser);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("张三更新", result.get().getName());
        assertEquals("zhangsan_updated@example.com", result.get().getEmail());
        verify(userRepository).existsById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_NotFound() {
        // Given
        User updateUser = User.builder()
                .name("测试用户")
                .email("test@example.com")
                .build();

        when(userRepository.existsById(999L)).thenReturn(false);

        // When
        Optional<User> result = userService.updateUser(999L, updateUser);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_InvalidName() {
        // Given
        User invalidUser = User.builder()
                .name("")
                .email("test@example.com")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, invalidUser)
        );
        assertEquals("用户名不能为空", exception.getMessage());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_InvalidEmail() {
        // Given
        User invalidUser = User.builder()
                .name("测试用户")
                .email("invalid-email")
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, invalidUser)
        );
        assertEquals("邮箱格式不正确", exception.getMessage());
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(999L);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    void testUserExists_True() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.userExists(1L);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(1L);
    }

    @Test
    void testUserExists_False() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = userService.userExists(999L);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(999L);
    }
}