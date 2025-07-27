package fei.song.play_spring_boot_api.users.interfaces;

import fei.song.play_spring_boot_api.users.application.UserProfileService;
import fei.song.play_spring_boot_api.users.domain.UserProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/profiles")
@CrossOrigin(origins = "*")
@Tag(name = "用户档案", description = "用户档案信息管理")
public class UserProfileController {
    
    @Autowired
    private UserProfileService userProfileService;
    
    /**
     * 获取所有用户档案
     */
    @GetMapping
    @Operation(summary = "获取所有用户档案", description = "返回系统中所有用户档案的列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        try {
            List<UserProfile> profiles = userProfileService.getAllProfiles();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取用户档案
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户档案", description = "根据档案ID获取特定用户档案信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfile> getProfileById(
            @Parameter(description = "档案ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            UserProfile profile = userProfileService.getProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID获取用户档案
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取用户档案", description = "根据用户ID获取对应的用户档案信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfile> getProfileByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据性别获取用户档案列表
     */
    @GetMapping("/gender/{gender}")
    @Operation(summary = "根据性别获取用户档案列表", description = "根据性别筛选用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfile>> getProfilesByGender(
            @Parameter(description = "性别", required = true, example = "男")
            @PathVariable String gender) {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesByGender(gender);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据年龄范围获取用户档案列表
     */
    @GetMapping("/age")
    @Operation(summary = "根据年龄范围获取用户档案列表", description = "根据最小年龄和最大年龄范围筛选用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfile>> getProfilesByAgeRange(
            @Parameter(description = "最小年龄", required = true, example = "18")
            @RequestParam Integer minAge,
            @Parameter(description = "最大年龄", required = true, example = "65")
            @RequestParam Integer maxAge) {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesByAgeRange(minAge, maxAge);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据职业获取用户档案列表
     */
    @GetMapping("/occupation/{occupation}")
    @Operation(summary = "根据职业获取用户档案列表", description = "根据职业筛选用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfile>> getProfilesByOccupation(
            @Parameter(description = "职业", required = true, example = "软件工程师")
            @PathVariable String occupation) {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesByOccupation(occupation);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据地址关键词搜索用户档案
     */
    @GetMapping("/search")
    @Operation(summary = "根据地址关键词搜索用户档案", description = "根据地址关键词搜索相关的用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户档案列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<UserProfile>> searchProfilesByAddress(
            @Parameter(description = "地址关键词", required = true, example = "北京")
            @RequestParam String keyword) {
        try {
            List<UserProfile> profiles = userProfileService.searchProfilesByAddress(keyword);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 创建用户档案
     */
    @PostMapping
    @Operation(summary = "创建用户档案", description = "创建一个新的用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "用户档案创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "用户档案已存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfile> createProfile(
            @Parameter(description = "用户档案信息", required = true)
            @RequestBody UserProfile profile) {
        try {
            UserProfile createdProfile = userProfileService.createProfile(profile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新用户档案
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户档案", description = "根据档案ID更新用户档案信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户档案更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfile> updateProfile(
            @Parameter(description = "档案ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "更新的用户档案信息", required = true)
            @RequestBody UserProfile profile) {
        try {
            UserProfile updatedProfile = userProfileService.updateProfile(id, profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新用户档案（根据用户ID）
     */
    @PutMapping("/user/{userId}")
    @Operation(summary = "根据用户ID更新用户档案", description = "根据用户ID更新对应的用户档案信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户档案更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfile.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<UserProfile> updateProfileByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "更新的用户档案信息", required = true)
            @RequestBody UserProfile profile) {
        try {
            UserProfile updatedProfile = userProfileService.updateProfileByUserId(userId, profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除用户档案
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户档案", description = "根据档案ID删除用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户档案删除成功",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deleteProfile(
            @Parameter(description = "档案ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            boolean deleted = userProfileService.deleteProfile(id);
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "用户档案删除成功" : "用户档案删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID删除用户档案
     */
    @DeleteMapping("/user/{userId}")
    @Operation(summary = "根据用户ID删除用户档案", description = "根据用户ID删除对应的用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户档案删除成功",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "用户档案不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deleteProfileByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            boolean deleted = userProfileService.deleteProfileByUserId(userId);
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "用户档案删除成功" : "用户档案删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 检查用户是否有档案
     */
    @GetMapping("/user/{userId}/exists")
    @Operation(summary = "检查用户是否有档案", description = "检查指定用户ID是否存在对应的用户档案")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功检查用户档案存在性",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> hasProfile(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        try {
            boolean exists = userProfileService.hasProfile(userId);
            Map<String, Object> response = Map.of(
                "exists", exists,
                "userId", userId
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户档案总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getProfileCount() {
        try {
            long count = userProfileService.getProfileCount();
            Map<String, Object> response = Map.of(
                "count", count,
                "message", "用户档案总数"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户档案统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProfileStats() {
        try {
            long totalCount = userProfileService.getProfileCount();
            List<UserProfile> allProfiles = userProfileService.getAllProfiles();
            
            // 按性别统计
            Map<String, Long> genderStats = allProfiles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    profile -> profile.getGender() != null ? profile.getGender() : "未知",
                    java.util.stream.Collectors.counting()
                ));
            
            // 按年龄段统计
            Map<String, Long> ageGroupStats = allProfiles.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    profile -> {
                        if (profile.getAge() == null) return "未知";
                        int age = profile.getAge();
                        if (age < 18) return "未成年";
                        else if (age < 30) return "18-29岁";
                        else if (age < 40) return "30-39岁";
                        else if (age < 50) return "40-49岁";
                        else if (age < 60) return "50-59岁";
                        else return "60岁以上";
                    },
                    java.util.stream.Collectors.counting()
                ));
            
            Map<String, Object> stats = Map.of(
                "totalCount", totalCount,
                "genderStats", genderStats,
                "ageGroupStats", ageGroupStats
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}