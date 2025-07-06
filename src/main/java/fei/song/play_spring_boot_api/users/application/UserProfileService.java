package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.UserProfile;
import fei.song.play_spring_boot_api.users.infrastructure.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    /**
     * 获取所有用户档案
     */
    public List<UserProfile> getAllProfiles() {
        return userProfileRepository.findAll();
    }
    
    /**
     * 根据ID获取用户档案
     */
    public UserProfile getProfileById(Long id) {
        Optional<UserProfile> profile = userProfileRepository.findById(id);
        if (profile.isEmpty()) {
            throw new RuntimeException("用户档案不存在，ID: " + id);
        }
        return profile.get();
    }
    
    /**
     * 根据用户ID获取用户档案
     */
    public UserProfile getProfileByUserId(Long userId) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isEmpty()) {
            throw new RuntimeException("用户档案不存在，用户ID: " + userId);
        }
        return profile.get();
    }
    
    /**
     * 根据性别获取用户档案列表
     */
    public List<UserProfile> getProfilesByGender(String gender) {
        validateGender(gender);
        return userProfileRepository.findByGender(gender);
    }
    
    /**
     * 根据年龄范围获取用户档案列表
     */
    public List<UserProfile> getProfilesByAgeRange(Integer minAge, Integer maxAge) {
        if (minAge == null || maxAge == null) {
            throw new IllegalArgumentException("年龄范围不能为空");
        }
        if (minAge < 0 || maxAge < 0) {
            throw new IllegalArgumentException("年龄不能为负数");
        }
        if (minAge > maxAge) {
            throw new IllegalArgumentException("最小年龄不能大于最大年龄");
        }
        return userProfileRepository.findByAgeRange(minAge, maxAge);
    }
    
    /**
     * 根据职业获取用户档案列表
     */
    public List<UserProfile> getProfilesByOccupation(String occupation) {
        if (occupation == null || occupation.trim().isEmpty()) {
            throw new IllegalArgumentException("职业不能为空");
        }
        return userProfileRepository.findByOccupation(occupation.trim());
    }
    
    /**
     * 根据地址关键词搜索用户档案
     */
    public List<UserProfile> searchProfilesByAddress(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        return userProfileRepository.findByAddressContaining(keyword.trim());
    }
    
    /**
     * 创建用户档案
     */
    public UserProfile createProfile(UserProfile profile) {
        validateProfile(profile);
        
        // 检查用户是否已有档案
        if (userProfileRepository.existsByUserId(profile.getUserId())) {
            throw new RuntimeException("用户已存在档案，用户ID: " + profile.getUserId());
        }
        
        // 根据生日自动计算年龄
        if (profile.getBirthday() != null) {
            int calculatedAge = calculateAge(profile.getBirthday());
            profile.setAge(calculatedAge);
        }
        
        return userProfileRepository.save(profile);
    }
    
    /**
     * 更新用户档案
     */
    public UserProfile updateProfile(Long id, UserProfile profile) {
        if (!userProfileRepository.existsById(id)) {
            throw new RuntimeException("用户档案不存在，ID: " + id);
        }
        
        validateProfile(profile);
        profile.setId(id);
        
        // 根据生日自动计算年龄
        if (profile.getBirthday() != null) {
            int calculatedAge = calculateAge(profile.getBirthday());
            profile.setAge(calculatedAge);
        }
        
        profile.updateTimestamp();
        return userProfileRepository.save(profile);
    }
    
    /**
     * 更新用户档案（根据用户ID）
     */
    public UserProfile updateProfileByUserId(Long userId, UserProfile profile) {
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);
        if (existingProfile.isEmpty()) {
            throw new RuntimeException("用户档案不存在，用户ID: " + userId);
        }
        
        validateProfile(profile);
        profile.setId(existingProfile.get().getId());
        profile.setUserId(userId);
        
        // 根据生日自动计算年龄
        if (profile.getBirthday() != null) {
            int calculatedAge = calculateAge(profile.getBirthday());
            profile.setAge(calculatedAge);
        }
        
        profile.updateTimestamp();
        return userProfileRepository.save(profile);
    }
    
    /**
     * 删除用户档案
     */
    public boolean deleteProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new RuntimeException("用户档案不存在，ID: " + id);
        }
        return userProfileRepository.deleteById(id);
    }
    
    /**
     * 根据用户ID删除用户档案
     */
    public boolean deleteProfileByUserId(Long userId) {
        if (!userProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("用户档案不存在，用户ID: " + userId);
        }
        return userProfileRepository.deleteByUserId(userId);
    }
    
    /**
     * 检查用户是否有档案
     */
    public boolean hasProfile(Long userId) {
        return userProfileRepository.existsByUserId(userId);
    }
    
    /**
     * 获取用户档案总数
     */
    public long getProfileCount() {
        return userProfileRepository.count();
    }
    
    /**
     * 验证用户档案数据
     */
    private void validateProfile(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("用户档案不能为空");
        }
        
        if (profile.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (profile.getAge() != null && profile.getAge() < 0) {
            throw new IllegalArgumentException("年龄不能为负数");
        }
        
        if (profile.getAge() != null && profile.getAge() > 150) {
            throw new IllegalArgumentException("年龄不能超过150岁");
        }
        
        if (profile.getGender() != null) {
            validateGender(profile.getGender());
        }
        
        if (profile.getBirthday() != null && profile.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("生日不能是未来日期");
        }
        
        if (profile.getPhoneNumber() != null && !isValidPhoneNumber(profile.getPhoneNumber())) {
            throw new IllegalArgumentException("手机号码格式不正确");
        }
    }
    
    /**
     * 验证性别
     */
    private void validateGender(String gender) {
        if (gender != null && !gender.matches("^(男|女|其他)$")) {
            throw new IllegalArgumentException("性别只能是：男、女、其他");
        }
    }
    
    /**
     * 验证手机号码格式
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // 简单的手机号码格式验证（中国大陆）
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 根据生日计算年龄
     */
    private int calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return 0;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }
}