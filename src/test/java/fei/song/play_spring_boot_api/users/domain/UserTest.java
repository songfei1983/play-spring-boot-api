package fei.song.play_spring_boot_api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getName());
        assertNull(newUser.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        User newUser = new User(1L, "张三", "zhangsan@example.com");
        
        assertEquals(1L, newUser.getId());
        assertEquals("张三", newUser.getName());
        assertEquals("zhangsan@example.com", newUser.getEmail());
    }

    @Test
    void testBuilderPattern() {
        User newUser = User.builder()
            .id(1L)
            .name("张三")
            .email("zhangsan@example.com")
            .build();
        
        assertEquals(1L, newUser.getId());
        assertEquals("张三", newUser.getName());
        assertEquals("zhangsan@example.com", newUser.getEmail());
    }

    @Test
    void testSettersAndGetters() {
        user.setId(1L);
        user.setName("张三");
        user.setEmail("zhangsan@example.com");
        
        assertEquals(1L, user.getId());
        assertEquals("张三", user.getName());
        assertEquals("zhangsan@example.com", user.getEmail());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
            .id(1L)
            .name("张三")
            .email("zhangsan@example.com")
            .build();
        
        User user2 = User.builder()
            .id(1L)
            .name("张三")
            .email("zhangsan@example.com")
            .build();
        
        User user3 = User.builder()
            .id(2L)
            .name("李四")
            .email("lisi@example.com")
            .build();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setName("张三");
        user.setEmail("zhangsan@example.com");
        
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=张三"));
        assertTrue(toString.contains("email=zhangsan@example.com"));
    }

    @Test
    void testNullValues() {
        User newUser = new User();
        
        assertNull(newUser.getId());
        assertNull(newUser.getName());
        assertNull(newUser.getEmail());
        
        // 测试设置null值
        newUser.setId(null);
        newUser.setName(null);
        newUser.setEmail(null);
        
        assertNull(newUser.getId());
        assertNull(newUser.getName());
        assertNull(newUser.getEmail());
    }

    @Test
    void testEmptyStringValues() {
        User newUser = User.builder()
            .name("")
            .email("")
            .build();
        
        assertEquals("", newUser.getName());
        assertEquals("", newUser.getEmail());
    }

    @Test
    void testLongValues() {
        // 测试长字符串
        String longName = "这是一个非常长的用户名".repeat(10);
        String longEmail = "verylongemailaddress@" + "example".repeat(20) + ".com";
        
        user.setName(longName);
        user.setEmail(longEmail);
        
        assertEquals(longName, user.getName());
        assertEquals(longEmail, user.getEmail());
    }

    @Test
    void testSpecialCharacters() {
        // 测试特殊字符
        String nameWithSpecialChars = "张三@#$%^&*()";
        String emailWithSpecialChars = "test+tag@example-domain.co.uk";
        
        user.setName(nameWithSpecialChars);
        user.setEmail(emailWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, user.getName());
        assertEquals(emailWithSpecialChars, user.getEmail());
    }

    @Test
    void testUnicodeCharacters() {
        // 测试Unicode字符
        String unicodeName = "张三 🙂 Émilie";
        String unicodeEmail = "émilie@exämple.com";
        
        user.setName(unicodeName);
        user.setEmail(unicodeEmail);
        
        assertEquals(unicodeName, user.getName());
        assertEquals(unicodeEmail, user.getEmail());
    }

    @Test
    void testIdBoundaryValues() {
        // 测试ID边界值
        user.setId(0L);
        assertEquals(0L, user.getId());
        
        user.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, user.getId());
        
        user.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, user.getId());
        
        user.setId(-1L);
        assertEquals(-1L, user.getId());
    }

    @Test
    void testBuilderWithPartialData() {
        // 测试部分数据的Builder
        User userWithIdOnly = User.builder()
            .id(1L)
            .build();
        
        assertEquals(1L, userWithIdOnly.getId());
        assertNull(userWithIdOnly.getName());
        assertNull(userWithIdOnly.getEmail());
        
        User userWithNameOnly = User.builder()
            .name("张三")
            .build();
        
        assertNull(userWithNameOnly.getId());
        assertEquals("张三", userWithNameOnly.getName());
        assertNull(userWithNameOnly.getEmail());
        
        User userWithEmailOnly = User.builder()
            .email("zhangsan@example.com")
            .build();
        
        assertNull(userWithEmailOnly.getId());
        assertNull(userWithEmailOnly.getName());
        assertEquals("zhangsan@example.com", userWithEmailOnly.getEmail());
    }

    @Test
    void testEqualsWithNullFields() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        
        user1.setId(1L);
        assertNotEquals(user1, user2);
        
        user2.setId(1L);
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsWithSameReference() {
        assertEquals(user, user);
        assertEquals(user.hashCode(), user.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        assertNotEquals(user, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertNotEquals(user, "not a user");
        assertNotEquals(user, 123);
    }
}