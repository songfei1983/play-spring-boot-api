package fei.song.play_spring_boot_api.users.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户档案实体")
public class UserProfile {
    @Schema(description = "档案ID", example = "1")
    private Long id;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "年龄", example = "25")
    private Integer age;
    
    @Schema(description = "性别", example = "男", allowableValues = {"男", "女", "其他"})
    private String gender;
    
    @Schema(description = "生日", example = "1998-05-15")
    private LocalDate birthday;
    
    @Schema(description = "手机号码", example = "13800138000")
    private String phoneNumber;
    
    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;
    
    @Schema(description = "职业", example = "软件工程师")
    private String occupation;
    
    @Schema(description = "个人简介", example = "热爱编程的技术人员")
    private String bio;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    // 自定义构造函数，用于设置默认时间戳
    public UserProfile(Long userId, Integer age, String gender, LocalDate birthday, 
                      String phoneNumber, String address, String occupation, String bio) {
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.occupation = occupation;
        this.bio = bio;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 更新时间戳的自定义方法
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}