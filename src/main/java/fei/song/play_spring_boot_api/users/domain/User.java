package fei.song.play_spring_boot_api.users.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户实体")
public class User {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "张三")
    private String name;
    
    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;
    

}