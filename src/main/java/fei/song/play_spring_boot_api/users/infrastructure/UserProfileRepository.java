package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class UserProfileRepository {
    private final List<UserProfile> profiles = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserProfileRepository() {
        // 初始化示例数据
        initializeData();
    }
    
    private void initializeData() {
        UserProfile profile1 = new UserProfile(1L, 25, "男", LocalDate.of(1998, 5, 15), 
                "13800138000", "北京市朝阳区", "软件工程师", "热爱编程的技术人员");
        profile1.setId(idGenerator.getAndIncrement());
        profile1.setAvatarUrl("https://example.com/avatar1.jpg");
        profiles.add(profile1);
        
        UserProfile profile2 = new UserProfile(2L, 30, "女", LocalDate.of(1993, 8, 20), 
                "13900139000", "上海市浦东新区", "产品经理", "专注用户体验设计");
        profile2.setId(idGenerator.getAndIncrement());
        profile2.setAvatarUrl("https://example.com/avatar2.jpg");
        profiles.add(profile2);
        
        UserProfile profile3 = new UserProfile(3L, 28, "男", LocalDate.of(1995, 12, 10), 
                "13700137000", "广州市天河区", "设计师", "创意无限的视觉设计师");
        profile3.setId(idGenerator.getAndIncrement());
        profile3.setAvatarUrl("https://example.com/avatar3.jpg");
        profiles.add(profile3);
    }
    
    /**
     * 查找所有用户档案
     */
    public List<UserProfile> findAll() {
        return new ArrayList<>(profiles);
    }
    
    /**
     * 根据ID查找用户档案
     */
    public Optional<UserProfile> findById(Long id) {
        return profiles.stream()
                .filter(profile -> profile.getId().equals(id))
                .findFirst();
    }
    
    /**
     * 根据用户ID查找用户档案
     */
    public Optional<UserProfile> findByUserId(Long userId) {
        return profiles.stream()
                .filter(profile -> profile.getUserId().equals(userId))
                .findFirst();
    }
    
    /**
     * 根据性别查找用户档案
     */
    public List<UserProfile> findByGender(String gender) {
        return profiles.stream()
                .filter(profile -> gender.equals(profile.getGender()))
                .toList();
    }
    
    /**
     * 根据年龄范围查找用户档案
     */
    public List<UserProfile> findByAgeRange(Integer minAge, Integer maxAge) {
        return profiles.stream()
                .filter(profile -> profile.getAge() != null && 
                        profile.getAge() >= minAge && profile.getAge() <= maxAge)
                .toList();
    }
    
    /**
     * 根据职业查找用户档案
     */
    public List<UserProfile> findByOccupation(String occupation) {
        return profiles.stream()
                .filter(profile -> occupation.equals(profile.getOccupation()))
                .toList();
    }
    
    /**
     * 根据地址关键词查找用户档案
     */
    public List<UserProfile> findByAddressContaining(String keyword) {
        return profiles.stream()
                .filter(profile -> profile.getAddress() != null && 
                        profile.getAddress().contains(keyword))
                .toList();
    }
    
    /**
     * 保存用户档案
     */
    public UserProfile save(UserProfile profile) {
        if (profile.getId() == null) {
            // 新增
            profile.setId(idGenerator.getAndIncrement());
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            profiles.add(profile);
        } else {
            // 更新
            Optional<UserProfile> existingProfile = findById(profile.getId());
            if (existingProfile.isPresent()) {
                profiles.remove(existingProfile.get());
                profile.setUpdatedAt(LocalDateTime.now());
                profiles.add(profile);
            } else {
                throw new RuntimeException("用户档案不存在，ID: " + profile.getId());
            }
        }
        return profile;
    }
    
    /**
     * 删除用户档案
     */
    public boolean deleteById(Long id) {
        return profiles.removeIf(profile -> profile.getId().equals(id));
    }
    
    /**
     * 根据用户ID删除用户档案
     */
    public boolean deleteByUserId(Long userId) {
        return profiles.removeIf(profile -> profile.getUserId().equals(userId));
    }
    
    /**
     * 检查用户档案是否存在
     */
    public boolean existsById(Long id) {
        return profiles.stream().anyMatch(profile -> profile.getId().equals(id));
    }
    
    /**
     * 检查用户是否已有档案
     */
    public boolean existsByUserId(Long userId) {
        return profiles.stream().anyMatch(profile -> profile.getUserId().equals(userId));
    }
    
    /**
     * 获取用户档案总数
     */
    public long count() {
        return profiles.size();
    }
}