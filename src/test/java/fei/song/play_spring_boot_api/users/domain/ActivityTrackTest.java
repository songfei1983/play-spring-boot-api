package fei.song.play_spring_boot_api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

class ActivityTrackTest {

    private ActivityTrack activityTrack;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        activityTrack = new ActivityTrack();
    }

    @Test
    void testDefaultConstructor() {
        ActivityTrack activity = new ActivityTrack();
        assertNotNull(activity);
        assertNull(activity.getId());
        assertNull(activity.getUserId());
        assertNull(activity.getActivityType());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ActivityTrack activity = new ActivityTrack(
            1L, 100L, "登录", "用户登录系统", new BigDecimal("116.404"), 
            new BigDecimal("39.915"), "北京", "192.168.1.1", "手机", 
            "iOS 15.0", "Safari", "Mozilla/5.0...", "session123", 
            "/products/123", "/home", 120, "{\"productId\": 123}", now
        );
        
        assertEquals(1L, activity.getId());
        assertEquals(100L, activity.getUserId());
        assertEquals("登录", activity.getActivityType());
        assertEquals("用户登录系统", activity.getDescription());
        assertEquals(new BigDecimal("116.404"), activity.getLongitude());
        assertEquals(new BigDecimal("39.915"), activity.getLatitude());
        assertEquals("北京", activity.getLocation());
        assertEquals("192.168.1.1", activity.getIpAddress());
        assertEquals("手机", activity.getDeviceType());
        assertEquals("iOS 15.0", activity.getOperatingSystem());
        assertEquals("Safari", activity.getBrowser());
        assertEquals("Mozilla/5.0...", activity.getUserAgent());
        assertEquals("session123", activity.getSessionId());
        assertEquals("/products/123", activity.getPageUrl());
        assertEquals("/home", activity.getReferrer());
        assertEquals(120, activity.getDuration());
        assertEquals("{\"productId\": 123}", activity.getExtraData());
        assertEquals(now, activity.getCreatedAt());
    }

    @Test
    void testBuilderPattern() {
        ActivityTrack activity = ActivityTrack.builder()
            .id(1L)
            .userId(100L)
            .activityType("登录")
            .description("用户登录系统")
            .longitude(new BigDecimal("116.404"))
            .latitude(new BigDecimal("39.915"))
            .location("北京")
            .ipAddress("192.168.1.1")
            .deviceType("手机")
            .operatingSystem("iOS 15.0")
            .browser("Safari")
            .userAgent("Mozilla/5.0...")
            .sessionId("session123")
            .pageUrl("/products/123")
            .referrer("/home")
            .duration(120)
            .extraData("{\"productId\": 123}")
            .build();
        
        assertEquals(1L, activity.getId());
        assertEquals(100L, activity.getUserId());
        assertEquals("登录", activity.getActivityType());
        assertEquals("用户登录系统", activity.getDescription());
        assertEquals(new BigDecimal("116.404"), activity.getLongitude());
        assertEquals(new BigDecimal("39.915"), activity.getLatitude());
        assertEquals("北京", activity.getLocation());
        assertEquals("192.168.1.1", activity.getIpAddress());
        assertEquals("手机", activity.getDeviceType());
    }

    @Test
    void testCustomConstructorWithDefaultValues() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        ActivityTrack activity = new ActivityTrack(
            100L, "登录", "用户登录系统", 
            new BigDecimal("116.404"), new BigDecimal("39.915"), "北京"
        );
        
        LocalDateTime afterCreation = LocalDateTime.now();
        
        assertEquals(100L, activity.getUserId());
        assertEquals("登录", activity.getActivityType());
        assertEquals("用户登录系统", activity.getDescription());
        assertEquals(new BigDecimal("116.404"), activity.getLongitude());
        assertEquals(new BigDecimal("39.915"), activity.getLatitude());
        assertEquals("北京", activity.getLocation());
        
        // 验证时间戳设置
        assertNotNull(activity.getCreatedAt());
        assertTrue(activity.getCreatedAt().isAfter(beforeCreation) || activity.getCreatedAt().isEqual(beforeCreation));
        assertTrue(activity.getCreatedAt().isBefore(afterCreation) || activity.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testCreatedAtTimestamp() {
        LocalDateTime originalTime = LocalDateTime.now().minusMinutes(1);
        activityTrack.setCreatedAt(originalTime);
        
        assertEquals(originalTime, activityTrack.getCreatedAt());
        
        // 测试设置新的创建时间
        LocalDateTime newTime = LocalDateTime.now();
        activityTrack.setCreatedAt(newTime);
        assertEquals(newTime, activityTrack.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        activityTrack.setId(1L);
        activityTrack.setUserId(100L);
        activityTrack.setActivityType("登录");
        activityTrack.setDescription("用户登录系统");
        activityTrack.setLongitude(new BigDecimal("116.404"));
        activityTrack.setLatitude(new BigDecimal("39.915"));
        activityTrack.setLocation("北京");
        activityTrack.setIpAddress("192.168.1.1");
        activityTrack.setDeviceType("手机");
        activityTrack.setOperatingSystem("iOS 15.0");
        activityTrack.setBrowser("Safari");
        activityTrack.setUserAgent("Mozilla/5.0...");
        activityTrack.setSessionId("session123");
        activityTrack.setPageUrl("/products/123");
        activityTrack.setReferrer("/home");
        activityTrack.setDuration(120);
        activityTrack.setExtraData("{\"productId\": 123}");
        activityTrack.setCreatedAt(testTime);
        
        assertEquals(1L, activityTrack.getId());
        assertEquals(100L, activityTrack.getUserId());
        assertEquals("登录", activityTrack.getActivityType());
        assertEquals("用户登录系统", activityTrack.getDescription());
        assertEquals(new BigDecimal("116.404"), activityTrack.getLongitude());
        assertEquals(new BigDecimal("39.915"), activityTrack.getLatitude());
        assertEquals("北京", activityTrack.getLocation());
        assertEquals("192.168.1.1", activityTrack.getIpAddress());
        assertEquals("手机", activityTrack.getDeviceType());
        assertEquals("iOS 15.0", activityTrack.getOperatingSystem());
        assertEquals("Safari", activityTrack.getBrowser());
        assertEquals("Mozilla/5.0...", activityTrack.getUserAgent());
        assertEquals("session123", activityTrack.getSessionId());
        assertEquals("/products/123", activityTrack.getPageUrl());
        assertEquals("/home", activityTrack.getReferrer());
        assertEquals(120, activityTrack.getDuration());
        assertEquals("{\"productId\": 123}", activityTrack.getExtraData());
        assertEquals(testTime, activityTrack.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        ActivityTrack activity1 = ActivityTrack.builder()
            .id(1L)
            .userId(100L)
            .activityType("登录")
            .description("用户登录系统")
            .longitude(new BigDecimal("116.404"))
            .latitude(new BigDecimal("39.915"))
            .build();
        
        ActivityTrack activity2 = ActivityTrack.builder()
            .id(1L)
            .userId(100L)
            .activityType("登录")
            .description("用户登录系统")
            .longitude(new BigDecimal("116.404"))
            .latitude(new BigDecimal("39.915"))
            .build();
        
        ActivityTrack activity3 = ActivityTrack.builder()
            .id(2L)
            .userId(100L)
            .activityType("登出")
            .description("用户登出系统")
            .longitude(new BigDecimal("116.405"))
            .latitude(new BigDecimal("39.916"))
            .build();
        
        assertEquals(activity1, activity2);
        assertEquals(activity1.hashCode(), activity2.hashCode());
        assertNotEquals(activity1, activity3);
        assertNotEquals(activity1.hashCode(), activity3.hashCode());
    }

    @Test
    void testToString() {
        activityTrack.setId(1L);
        activityTrack.setUserId(100L);
        activityTrack.setActivityType("登录");
        activityTrack.setDescription("用户登录系统");
        activityTrack.setLongitude(new BigDecimal("116.404"));
        activityTrack.setLatitude(new BigDecimal("39.915"));
        
        String toString = activityTrack.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ActivityTrack"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("userId=100"));
        assertTrue(toString.contains("activityType=登录"));
        assertTrue(toString.contains("description=用户登录系统"));
    }

    @Test
    void testNullValues() {
        ActivityTrack activity = new ActivityTrack();
        
        // 测试所有字段都可以为null
        assertNull(activity.getId());
        assertNull(activity.getUserId());
        assertNull(activity.getActivityType());
        assertNull(activity.getDescription());
        assertNull(activity.getLongitude());
        assertNull(activity.getLatitude());
        assertNull(activity.getLocation());
        assertNull(activity.getIpAddress());
        assertNull(activity.getDeviceType());
        assertNull(activity.getOperatingSystem());
        assertNull(activity.getBrowser());
        assertNull(activity.getUserAgent());
        assertNull(activity.getSessionId());
        assertNull(activity.getPageUrl());
        assertNull(activity.getReferrer());
        assertNull(activity.getDuration());
        assertNull(activity.getExtraData());
        assertNull(activity.getCreatedAt());
    }

    @Test
    void testEmptyStringValues() {
        ActivityTrack activity = ActivityTrack.builder()
            .activityType("")
            .description("")
            .location("")
            .ipAddress("")
            .deviceType("")
            .operatingSystem("")
            .browser("")
            .userAgent("")
            .sessionId("")
            .pageUrl("")
            .referrer("")
            .extraData("")
            .build();
        
        assertEquals("", activity.getActivityType());
        assertEquals("", activity.getDescription());
        assertEquals("", activity.getLocation());
        assertEquals("", activity.getIpAddress());
        assertEquals("", activity.getDeviceType());
        assertEquals("", activity.getOperatingSystem());
        assertEquals("", activity.getBrowser());
        assertEquals("", activity.getUserAgent());
        assertEquals("", activity.getSessionId());
        assertEquals("", activity.getPageUrl());
        assertEquals("", activity.getReferrer());
        assertEquals("", activity.getExtraData());
        
        // 测试数值字段
        assertNull(activity.getDuration());
        assertNull(activity.getLongitude());
        assertNull(activity.getLatitude());
    }
}