package fei.song.play_spring_boot_api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

class UserProfileTest {

    private UserProfile userProfile;
    private LocalDateTime testTime;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        testDate = LocalDate.now();
        userProfile = new UserProfile();
    }

    @Test
    void testDefaultConstructor() {
        UserProfile profile = new UserProfile();
        assertNotNull(profile);
        assertNull(profile.getId());
        assertNull(profile.getUserId());
        assertNull(profile.getAge());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDateTime now = LocalDateTime.now();
        
        UserProfile profile = new UserProfile(
            1L, 100L, 25, "男", birthDate, "13800138000", "北京市朝阳区",
            "软件工程师", "个人简介", "avatar.jpg", now, now
        );
        
        assertEquals(1L, profile.getId());
        assertEquals(100L, profile.getUserId());
        assertEquals(25, profile.getAge());
        assertEquals("男", profile.getGender());
        assertEquals(birthDate, profile.getBirthday());
        assertEquals("13800138000", profile.getPhoneNumber());
        assertEquals("北京市朝阳区", profile.getAddress());
        assertEquals("软件工程师", profile.getOccupation());
        assertEquals("个人简介", profile.getBio());
        assertEquals("avatar.jpg", profile.getAvatarUrl());
        assertEquals(now, profile.getCreatedAt());
        assertEquals(now, profile.getUpdatedAt());
    }

    @Test
    void testBuilderPattern() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        
        UserProfile profile = UserProfile.builder()
            .id(1L)
            .userId(100L)
            .age(25)
            .gender("男")
            .birthday(birthDate)
            .phoneNumber("13800138000")
            .address("北京市朝阳区")
            .occupation("软件工程师")
            .bio("个人简介")
            .avatarUrl("avatar.jpg")
            .build();
        
        assertEquals(1L, profile.getId());
        assertEquals(100L, profile.getUserId());
        assertEquals(25, profile.getAge());
        assertEquals("男", profile.getGender());
        assertEquals(birthDate, profile.getBirthday());
        assertEquals("13800138000", profile.getPhoneNumber());
        assertEquals("北京市朝阳区", profile.getAddress());
        assertEquals("软件工程师", profile.getOccupation());
        assertEquals("个人简介", profile.getBio());
        assertEquals("avatar.jpg", profile.getAvatarUrl());
    }

    @Test
    void testCustomConstructorWithDefaultValues() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        
        UserProfile profile = new UserProfile(
            100L, 25, "男", birthDate, "13800138000", "北京市朝阳区", "软件工程师", "个人简介"
        );
        
        LocalDateTime afterCreation = LocalDateTime.now();
        
        assertEquals(100L, profile.getUserId());
        assertEquals(25, profile.getAge());
        assertEquals("男", profile.getGender());
        assertEquals(birthDate, profile.getBirthday());
        assertEquals("13800138000", profile.getPhoneNumber());
        assertEquals("北京市朝阳区", profile.getAddress());
        assertEquals("软件工程师", profile.getOccupation());
        assertEquals("个人简介", profile.getBio());
        
        // 验证时间戳设置
        assertNotNull(profile.getCreatedAt());
        assertNotNull(profile.getUpdatedAt());
        assertTrue(profile.getCreatedAt().isAfter(beforeCreation) || profile.getCreatedAt().isEqual(beforeCreation));
        assertTrue(profile.getCreatedAt().isBefore(afterCreation) || profile.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testUpdateTimestamp() {
        LocalDateTime originalTime = LocalDateTime.now().minusMinutes(1);
        userProfile.setUpdatedAt(originalTime);
        
        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        userProfile.updateTimestamp();
        
        assertNotNull(userProfile.getUpdatedAt());
        assertTrue(userProfile.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void testSettersAndGetters() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        
        userProfile.setId(1L);
        userProfile.setUserId(100L);
        userProfile.setAge(25);
        userProfile.setGender("男");
        userProfile.setBirthday(birthDate);
        userProfile.setPhoneNumber("13800138000");
        userProfile.setAddress("北京市朝阳区");
        userProfile.setOccupation("软件工程师");
        userProfile.setBio("个人简介");
        userProfile.setAvatarUrl("avatar.jpg");
        userProfile.setCreatedAt(testTime);
        userProfile.setUpdatedAt(testTime);
        
        assertEquals(1L, userProfile.getId());
        assertEquals(100L, userProfile.getUserId());
        assertEquals(25, userProfile.getAge());
        assertEquals("男", userProfile.getGender());
        assertEquals(birthDate, userProfile.getBirthday());
        assertEquals("13800138000", userProfile.getPhoneNumber());
        assertEquals("北京市朝阳区", userProfile.getAddress());
        assertEquals("软件工程师", userProfile.getOccupation());
        assertEquals("个人简介", userProfile.getBio());
        assertEquals("avatar.jpg", userProfile.getAvatarUrl());
        assertEquals(testTime, userProfile.getCreatedAt());
        assertEquals(testTime, userProfile.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfile profile1 = UserProfile.builder()
            .id(1L)
            .userId(100L)
            .age(25)
            .gender("男")
            .build();
        
        UserProfile profile2 = UserProfile.builder()
            .id(1L)
            .userId(100L)
            .age(25)
            .gender("男")
            .build();
        
        UserProfile profile3 = UserProfile.builder()
            .id(2L)
            .userId(101L)
            .age(30)
            .gender("女")
            .build();
        
        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
        assertNotEquals(profile1, profile3);
        assertNotEquals(profile1.hashCode(), profile3.hashCode());
    }

    @Test
    void testToString() {
        userProfile.setId(1L);
        userProfile.setUserId(100L);
        userProfile.setAge(25);
        userProfile.setGender("男");
        
        String toString = userProfile.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UserProfile"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("userId=100"));
        assertTrue(toString.contains("age=25"));
        assertTrue(toString.contains("gender=男"));
    }

    @Test
    void testNullValues() {
        UserProfile profile = new UserProfile();
        
        // 测试所有字段都可以为null
        assertNull(profile.getId());
        assertNull(profile.getUserId());
        assertNull(profile.getAge());
        assertNull(profile.getGender());
        assertNull(profile.getBirthday());
        assertNull(profile.getPhoneNumber());
        assertNull(profile.getAddress());
        assertNull(profile.getOccupation());
        assertNull(profile.getBio());
        assertNull(profile.getAvatarUrl());
        assertNull(profile.getCreatedAt());
        assertNull(profile.getUpdatedAt());
    }

    @Test
    void testEmptyStringValues() {
        UserProfile profile = UserProfile.builder()
            .gender("")
            .phoneNumber("")
            .address("")
            .occupation("")
            .bio("")
            .avatarUrl("")
            .build();
        
        assertEquals("", profile.getGender());
        assertEquals("", profile.getPhoneNumber());
        assertEquals("", profile.getAddress());
        assertEquals("", profile.getOccupation());
        assertEquals("", profile.getBio());
        assertEquals("", profile.getAvatarUrl());
    }

    @Test
    void testBirthdayEdgeCases() {
        // 测试过去的日期
        LocalDate pastDate = LocalDate.of(1990, 1, 1);
        userProfile.setBirthday(pastDate);
        assertEquals(pastDate, userProfile.getBirthday());
        
        // 测试今天的日期
        LocalDate today = LocalDate.now();
        userProfile.setBirthday(today);
        assertEquals(today, userProfile.getBirthday());
        
        // 测试很久以前的日期
        LocalDate veryOldDate = LocalDate.of(1900, 12, 31);
        userProfile.setBirthday(veryOldDate);
        assertEquals(veryOldDate, userProfile.getBirthday());
        
        // 测试年龄边界值
        userProfile.setAge(0);
        assertEquals(0, userProfile.getAge());
        
        userProfile.setAge(150);
        assertEquals(150, userProfile.getAge());
        
        userProfile.setAge(-1);
        assertEquals(-1, userProfile.getAge());
    }
}