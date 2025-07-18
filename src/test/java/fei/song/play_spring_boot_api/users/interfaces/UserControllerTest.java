package fei.song.play_spring_boot_api.users.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.users.application.UserService;
import fei.song.play_spring_boot_api.users.domain.User;
import fei.song.play_spring_boot_api.users.infrastructure.UserRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private UserRepositoryService userRepositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("张三");
        testUser.setEmail("zhangsan@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("李四");
        user2.setEmail("lisi@example.com");

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(testUsers);

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("张三"))
                .andExpect(jsonPath("$[0].email").value("zhangsan@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("李四"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("张三"))
                .andExpect(jsonPath("$.email").value("zhangsan@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(java.util.Optional.empty());

        // When & Then
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("王五");
        newUser.setEmail("wangwu@example.com");

        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setName("王五");
        createdUser.setEmail("wangwu@example.com");

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("王五"))
                .andExpect(jsonPath("$.email").value("wangwu@example.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createUser_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        User invalidUser = new User();
        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("Invalid user data"));

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("赵六");
        updatedUser.setEmail("zhaoliu@example.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(java.util.Optional.of(updatedUser));

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("赵六"))
                .andExpect(jsonPath("$.email").value("zhaoliu@example.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void updateUser_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new IllegalArgumentException("Invalid user data"));

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.updateUser(eq(999L), any(User.class))).thenReturn(java.util.Optional.empty());

        // When & Then
        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(User.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.deleteUser(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    void getAllUsers_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUser_WhenServiceThrowsRuntimeException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_WhenServiceThrowsRuntimeException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void deleteUser_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).deleteUser(1L);
    }
}