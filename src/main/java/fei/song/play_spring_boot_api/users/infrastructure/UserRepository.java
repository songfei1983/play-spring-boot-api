package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserRepository() {
        // 初始化一些示例数据
        save(new User(null, "张三", "zhangsan@example.com"));
        save(new User(null, "李四", "lisi@example.com"));
        save(new User(null, "王五", "wangwu@example.com"));
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
}