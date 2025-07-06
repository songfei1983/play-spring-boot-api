package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.User;
import fei.song.play_spring_boot_api.users.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        // 业务逻辑验证
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        return userRepository.save(user);
    }
    
    public Optional<User> updateUser(Long id, User userDetails) {
        if (!userRepository.existsById(id)) {
            return Optional.empty();
        }
        
        // 业务逻辑验证
        if (userDetails.getName() == null || userDetails.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (userDetails.getEmail() == null || userDetails.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!isValidEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        userDetails.setId(id);
        return Optional.of(userRepository.save(userDetails));
    }
    
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
    
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
    
    private boolean isValidEmail(String email) {
        // 简单的邮箱格式验证
        return email.contains("@") && email.contains(".");
    }
}