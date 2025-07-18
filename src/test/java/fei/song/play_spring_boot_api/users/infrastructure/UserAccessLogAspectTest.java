package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccessLogAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @Mock
    private Signature signature;
    
    @Mock
    private ServletRequestAttributes requestAttributes;
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    private UserAccessLogAspect userAccessLogAspect;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        userAccessLogAspect = new UserAccessLogAspect();
        testUser = User.builder()
                .id(1L)
                .name("测试用户")
                .email("test@example.com")
                .build();
    }
    
    @Test
    void testLogUserAccess_GetAllUsers_Success() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getAllUsers");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        
        List<User> userList = Arrays.asList(testUser);
        when(joinPoint.proceed()).thenReturn(userList);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(userList, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_GetUserById_Success() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getUserById");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn(testUser);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(testUser, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_CreateUser_Success() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("createUser");
        when(joinPoint.getArgs()).thenReturn(new Object[]{testUser});
        when(joinPoint.proceed()).thenReturn(testUser);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(testUser, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_UpdateUser_Success() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("updateUser");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, testUser});
        when(joinPoint.proceed()).thenReturn(testUser);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(testUser, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_DeleteUser_Success() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("deleteUser");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn(null);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertNull(result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_WithException() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getUserById");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        
        RuntimeException expectedException = new RuntimeException("测试异常");
        when(joinPoint.proceed()).thenThrow(expectedException);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When & Then
            RuntimeException thrownException = assertThrows(RuntimeException.class, 
                () -> userAccessLogAspect.logUserAccess(joinPoint));
            
            assertEquals(expectedException, thrownException);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_NoRequestContext() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getAllUsers");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        
        List<User> userList = Arrays.asList(testUser);
        when(joinPoint.proceed()).thenReturn(userList);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(userList, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_NoRequestIdHeader() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getAllUsers");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        
        List<User> userList = Arrays.asList(testUser);
        when(joinPoint.proceed()).thenReturn(userList);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn(null);
            when(httpServletRequest.getHeader("X-Request-Id")).thenReturn(null);
            when(httpServletRequest.getHeader("Request-ID")).thenReturn(null);
            when(httpServletRequest.getHeader("Request-Id")).thenReturn(null);
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(userList, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_AlternativeRequestIdHeaders() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("getAllUsers");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        
        List<User> userList = Arrays.asList(testUser);
        when(joinPoint.proceed()).thenReturn(userList);
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn(null);
            when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("alternative-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals(userList, result);
            verify(joinPoint).proceed();
        }
    }
    
    @Test
    void testLogUserAccess_UnknownMethod() throws Throwable {
        // Given
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("unknownMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("unknown result");
        
        try (MockedStatic<RequestContextHolder> mockedRequestContextHolder = mockStatic(RequestContextHolder.class)) {
            mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(requestAttributes);
            when(requestAttributes.getRequest()).thenReturn(httpServletRequest);
            when(httpServletRequest.getHeader("X-Request-ID")).thenReturn("test-request-id");
            
            // When
            Object result = userAccessLogAspect.logUserAccess(joinPoint);
            
            // Then
            assertEquals("unknown result", result);
            verify(joinPoint).proceed();
        }
    }
}