package fei.song.play_spring_boot_api.users.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@Schema(description = "用户实体")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "用户名", example = "张三")
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;
}