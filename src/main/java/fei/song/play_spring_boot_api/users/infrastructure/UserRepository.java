package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@ConditionalOnProperty(name = "app.datasource.enable-jpa", havingValue = "false", matchIfMissing = true)
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserRepository() {
        // 直接在构造函数中初始化数据，避免调用可能被重写的方法
        initializeDataDirectly();
    }
    
    private void initializeDataDirectly() {
        // 直接初始化数据，避免调用save方法
        User user1 = User.builder().name("张三").email("zhangsan@example.com").build();
        user1.setId(idGenerator.getAndIncrement());
        users.put(user1.getId(), user1);
        
        User user2 = User.builder().name("李四").email("lisi@example.com").build();
        user2.setId(idGenerator.getAndIncrement());
        users.put(user2.getId(), user2);
        
        User user3 = User.builder().name("王五").email("wangwu@example.com").build();
        user3.setId(idGenerator.getAndIncrement());
        users.put(user3.getId(), user3);
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    public void deleteById(Long id) {
        users.remove(id);
    }
    
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    public List<User> findByNameContaining(String name) {
        return users.values().stream()
                .filter(user -> user.getName().contains(name))
                .toList();
    }
    
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
}