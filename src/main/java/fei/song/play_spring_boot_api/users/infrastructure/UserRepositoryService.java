package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.User;
import fei.song.play_spring_boot_api.config.DataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户Repository服务类
 * 根据配置自动选择使用内存存储还是JPA数据库
 */
@Service
public class UserRepositoryService {
    
    private final DataSourceConfig.DataSourceProperties dataSourceProperties;
    private final UserRepository memoryUserRepository;
    private UserJpaRepository jpaUserRepository;
    
    public UserRepositoryService(
            DataSourceConfig.DataSourceProperties dataSourceProperties,
            UserRepository memoryUserRepository) {
        this.dataSourceProperties = dataSourceProperties;
        this.memoryUserRepository = memoryUserRepository;
    }
    
    @Autowired(required = false)
    public void setJpaUserRepository(UserJpaRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }
    
    public List<User> findAll() {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.findAll();
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.findAll();
        }
        throw new IllegalStateException("No user repository available");
    }
    
    public Optional<User> findById(Long id) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.findById(id);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.findById(id);
        }
        return Optional.empty();
    }
    
    public User save(User user) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.save(user);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.save(user);
        }
        throw new IllegalStateException("No user repository available");
    }
    
    public void deleteById(Long id) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            jpaUserRepository.deleteById(id);
        } else if (memoryUserRepository != null) {
            memoryUserRepository.deleteById(id);
        }
    }
    
    public boolean existsById(Long id) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.existsById(id);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.existsById(id);
        }
        return false;
    }
    
    public Optional<User> findByEmail(String email) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.findByEmail(email);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.findByEmail(email);
        }
        return Optional.empty();
    }
    
    public List<User> findByNameContaining(String name) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.findByNameContaining(name);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.findByNameContaining(name);
        }
        return List.of();
    }
    
    public boolean existsByEmail(String email) {
        if (dataSourceProperties.isEnableJpa() && jpaUserRepository != null) {
            return jpaUserRepository.existsByEmail(email);
        } else if (memoryUserRepository != null) {
            return memoryUserRepository.existsByEmail(email);
        }
        return false;
    }
    
    /**
     * 获取当前使用的数据源类型
     */
    public String getCurrentDataSourceType() {
        if (dataSourceProperties.isEnableJpa()) {
            return "JPA Database (" + dataSourceProperties.getType() + ")";
        } else {
            return "Memory Storage";
        }
    }
}