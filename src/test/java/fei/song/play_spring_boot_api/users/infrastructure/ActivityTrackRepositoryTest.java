package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.ActivityTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTrackRepositoryTest {

    private ActivityTrackRepository repository;
    private ActivityTrack testTrack;
    
    @BeforeEach
    void setUp() {
        repository = new ActivityTrackRepository();
        testTrack = new ActivityTrack(1L, "测试活动", "测试描述", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "测试位置");
    }
    
    @Test
    void testInitializeData() {
        // When
        List<ActivityTrack> tracks = repository.findAll();
        
        // Then
        assertEquals(4, tracks.size());
        assertTrue(tracks.stream().anyMatch(track -> "登录".equals(track.getActivityType())));
        assertTrue(tracks.stream().anyMatch(track -> "浏览".equals(track.getActivityType())));
        assertTrue(tracks.stream().anyMatch(track -> "搜索".equals(track.getActivityType())));
        assertTrue(tracks.stream().anyMatch(track -> "购买".equals(track.getActivityType())));
    }
    
    @Test
    void testSave_NewTrack() {
        // When
        ActivityTrack savedTrack = repository.save(testTrack);
        
        // Then
        assertNotNull(savedTrack.getId());
        assertNotNull(savedTrack.getCreatedAt());
        assertEquals("测试活动", savedTrack.getActivityType());
        assertEquals("测试描述", savedTrack.getDescription());
        
        // Verify it's in the repository
        Optional<ActivityTrack> foundTrack = repository.findById(savedTrack.getId());
        assertTrue(foundTrack.isPresent());
        assertEquals(savedTrack, foundTrack.get());
    }
    
    @Test
    void testSave_UpdateExistingTrack() {
        // Given
        ActivityTrack savedTrack = repository.save(testTrack);
        Long trackId = savedTrack.getId();
        savedTrack.setDescription("更新的描述");
        
        // When
        ActivityTrack updatedTrack = repository.save(savedTrack);
        
        // Then
        assertEquals(trackId, updatedTrack.getId());
        assertEquals("更新的描述", updatedTrack.getDescription());
        
        // Verify it's updated in the repository
        Optional<ActivityTrack> foundTrack = repository.findById(trackId);
        assertTrue(foundTrack.isPresent());
        assertEquals("更新的描述", foundTrack.get().getDescription());
    }
    
    @Test
    void testSave_UpdateNonExistingTrack() {
        // Given
        testTrack.setId(999L);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> repository.save(testTrack));
    }
    
    @Test
    void testFindById_ExistingTrack() {
        // Given
        ActivityTrack savedTrack = repository.save(testTrack);
        
        // When
        Optional<ActivityTrack> foundTrack = repository.findById(savedTrack.getId());
        
        // Then
        assertTrue(foundTrack.isPresent());
        assertEquals(savedTrack, foundTrack.get());
    }
    
    @Test
    void testFindById_NonExistingTrack() {
        // When
        Optional<ActivityTrack> foundTrack = repository.findById(999L);
        
        // Then
        assertFalse(foundTrack.isPresent());
    }
    
    @Test
    void testFindByUserId() {
        // Given
        ActivityTrack track1 = new ActivityTrack(1L, "活动1", "描述1", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "位置1");
        ActivityTrack track2 = new ActivityTrack(1L, "活动2", "描述2", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "位置2");
        ActivityTrack track3 = new ActivityTrack(2L, "活动3", "描述3", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "位置3");
        
        repository.save(track1);
        repository.save(track2);
        repository.save(track3);
        
        // When
        List<ActivityTrack> user1Tracks = repository.findByUserId(1L);
        List<ActivityTrack> user2Tracks = repository.findByUserId(2L);
        
        // Then
        assertTrue(user1Tracks.size() >= 3); // 包括初始化数据中的用户1的记录
        assertEquals(2, user2Tracks.size());
    }
    
    @Test
    void testFindByActivityType() {
        // When
        List<ActivityTrack> loginTracks = repository.findByActivityType("登录");
        List<ActivityTrack> browseTracks = repository.findByActivityType("浏览");
        
        // Then
        assertEquals(1, loginTracks.size());
        assertEquals(1, browseTracks.size());
        assertEquals("登录", loginTracks.get(0).getActivityType());
        assertEquals("浏览", browseTracks.get(0).getActivityType());
    }
    
    @Test
    void testFindByUserIdAndActivityType() {
        // When
        List<ActivityTrack> user1LoginTracks = repository.findByUserIdAndActivityType(1L, "登录");
        List<ActivityTrack> user1BrowseTracks = repository.findByUserIdAndActivityType(1L, "浏览");
        
        // Then
        assertEquals(1, user1LoginTracks.size());
        assertEquals(1, user1BrowseTracks.size());
        assertEquals(1L, user1LoginTracks.get(0).getUserId());
        assertEquals("登录", user1LoginTracks.get(0).getActivityType());
    }
    
    @Test
    void testFindByCreatedAtBetween() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime oneHourLater = now.plusHours(1);
        
        ActivityTrack newTrack = new ActivityTrack(1L, "新活动", "新描述", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "新位置");
        repository.save(newTrack);
        
        // When
        List<ActivityTrack> recentTracks = repository.findByCreatedAtBetween(oneHourAgo, oneHourLater);
        
        // Then
        assertTrue(recentTracks.size() >= 1);
        assertTrue(recentTracks.stream().anyMatch(track -> "新活动".equals(track.getActivityType())));
    }
    
    @Test
    void testFindByDeviceType() {
        // When
        List<ActivityTrack> mobileTracks = repository.findByDeviceType("手机");
        List<ActivityTrack> desktopTracks = repository.findByDeviceType("电脑");
        
        // Then
        assertEquals(3, mobileTracks.size());
        assertEquals(1, desktopTracks.size());
    }
    
    @Test
    void testFindByLocationContaining() {
        // When
        List<ActivityTrack> beijingTracks = repository.findByLocationContaining("北京");
        List<ActivityTrack> shanghaiTracks = repository.findByLocationContaining("上海");
        
        // Then
        assertEquals(2, beijingTracks.size());
        assertEquals(1, shanghaiTracks.size());
    }
    
    @Test
    void testFindBySessionId() {
        // When
        List<ActivityTrack> session001Tracks = repository.findBySessionId("session_001");
        List<ActivityTrack> session002Tracks = repository.findBySessionId("session_002");
        
        // Then
        assertEquals(2, session001Tracks.size());
        assertEquals(1, session002Tracks.size());
    }
    
    @Test
    void testFindByPageUrl() {
        // When
        List<ActivityTrack> loginPageTracks = repository.findByPageUrl("/login");
        List<ActivityTrack> searchPageTracks = repository.findByPageUrl("/search");
        
        // Then
        assertEquals(1, loginPageTracks.size());
        assertEquals(1, searchPageTracks.size());
    }
    
    @Test
    void testFindRecentByUserId() {
        // Given
        ActivityTrack track1 = new ActivityTrack(1L, "活动1", "描述1", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "位置1");
        ActivityTrack track2 = new ActivityTrack(1L, "活动2", "描述2", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "位置2");
        
        repository.save(track1);
        try {
            Thread.sleep(10); // 确保时间差异
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        repository.save(track2);
        
        // When
        List<ActivityTrack> recentTracks = repository.findRecentByUserId(1L, 2);
        
        // Then
        assertTrue(recentTracks.size() >= 2);
        // 最新的应该在前面
        assertTrue(recentTracks.get(0).getCreatedAt().isAfter(recentTracks.get(1).getCreatedAt()) ||
                  recentTracks.get(0).getCreatedAt().equals(recentTracks.get(1).getCreatedAt()));
    }
    
    @Test
    void testSaveAll() {
        // Given
        ActivityTrack track1 = new ActivityTrack(1L, "批量活动1", "批量描述1", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "批量位置1");
        ActivityTrack track2 = new ActivityTrack(2L, "批量活动2", "批量描述2", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "批量位置2");
        List<ActivityTrack> tracksToSave = Arrays.asList(track1, track2);
        
        // When
        List<ActivityTrack> savedTracks = repository.saveAll(tracksToSave);
        
        // Then
        assertEquals(2, savedTracks.size());
        savedTracks.forEach(track -> {
            assertNotNull(track.getId());
            assertNotNull(track.getCreatedAt());
        });
    }
    
    @Test
    void testDeleteById_ExistingTrack() {
        // Given
        ActivityTrack savedTrack = repository.save(testTrack);
        Long trackId = savedTrack.getId();
        
        // When
        boolean deleted = repository.deleteById(trackId);
        
        // Then
        assertTrue(deleted);
        assertFalse(repository.existsById(trackId));
        assertFalse(repository.findById(trackId).isPresent());
    }
    
    @Test
    void testDeleteById_NonExistingTrack() {
        // When
        boolean deleted = repository.deleteById(999L);
        
        // Then
        assertFalse(deleted);
    }
    
    @Test
    void testDeleteByUserId() {
        // Given
        long initialCount = repository.count();
        
        // When
        boolean deleted = repository.deleteByUserId(1L);
        
        // Then
        assertTrue(deleted);
        assertTrue(repository.count() < initialCount);
        assertEquals(0, repository.findByUserId(1L).size());
    }
    
    @Test
    void testDeleteByCreatedAtBefore() {
        // Given
        LocalDateTime cutoffTime = LocalDateTime.now().plusMinutes(1);
        long initialCount = repository.count();
        
        // When
        int deletedCount = repository.deleteByCreatedAtBefore(cutoffTime);
        
        // Then
        assertTrue(deletedCount >= 0);
        assertEquals(initialCount - deletedCount, repository.count());
    }
    
    @Test
    void testExistsById() {
        // Given
        ActivityTrack savedTrack = repository.save(testTrack);
        
        // When & Then
        assertTrue(repository.existsById(savedTrack.getId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    void testCount() {
        // Given
        long initialCount = repository.count();
        
        // When
        repository.save(testTrack);
        
        // Then
        assertEquals(initialCount + 1, repository.count());
    }
    
    @Test
    void testCountByUserId() {
        // Given
        long initialUser1Count = repository.countByUserId(1L);
        
        ActivityTrack newTrack = new ActivityTrack(1L, "新活动", "新描述", 
                new BigDecimal("116.404"), new BigDecimal("39.915"), "新位置");
        repository.save(newTrack);
        
        // When
        long newUser1Count = repository.countByUserId(1L);
        long user999Count = repository.countByUserId(999L);
        
        // Then
        assertEquals(initialUser1Count + 1, newUser1Count);
        assertEquals(0, user999Count);
    }
}