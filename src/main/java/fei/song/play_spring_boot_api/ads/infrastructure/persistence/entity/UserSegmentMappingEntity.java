package fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户分段映射实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_segment_mappings")
@CompoundIndexes({
    @CompoundIndex(name = "user_segment_idx", def = "{'user_id': 1, 'segment_id': 1}", unique = true),
    @CompoundIndex(name = "user_active_idx", def = "{'user_id': 1, 'is_active': 1, 'expires_at': 1}"),
    @CompoundIndex(name = "segment_active_idx", def = "{'segment_id': 1, 'is_active': 1}")
})
public class UserSegmentMappingEntity {

    @Id
    private String id;

    /**
     * 用户ID
     */
    @Indexed
    @Field("user_id")
    private String userId;

    /**
     * 分段ID
     */
    @Indexed
    @Field("segment_id")
    private String segmentId;

    /**
     * 分段名称（冗余字段，便于查询）
     */
    @Field("segment_name")
    private String segmentName;

    /**
     * 分段类型（冗余字段，便于查询）
     */
    @Field("segment_type")
    private String segmentType;

    /**
     * 是否激活
     */
    @Indexed
    @Field("is_active")
    private Boolean isActive;

    /**
     * 匹配分数
     */
    @Field("match_score")
    private Double matchScore;

    /**
     * 匹配置信度
     */
    private Double confidence;

    /**
     * 分段属性（存储匹配时的具体属性值）
     */
    private Map<String, Object> attributes;

    /**
     * 过期时间
     */
    @Indexed(expireAfter = "0s")
    @Field("expires_at")
    private LocalDateTime expiresAt;

    /**
     * 分配时间
     */
    @Field("assigned_at")
    private LocalDateTime assignedAt;

    /**
     * 最后验证时间
     */
    @Field("last_validated_at")
    private LocalDateTime lastValidatedAt;

    /**
     * 创建时间
     */
    @Field("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Field("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 预构建方法 - 设置默认值
     */
    public static class UserSegmentMappingEntityBuilder {
        public UserSegmentMappingEntity build() {
            LocalDateTime now = LocalDateTime.now();
            if (this.createdAt == null) {
                this.createdAt = now;
            }
            if (this.updatedAt == null) {
                this.updatedAt = now;
            }
            if (this.assignedAt == null) {
                this.assignedAt = now;
            }
            if (this.isActive == null) {
                this.isActive = true;
            }
            if (this.confidence == null) {
                this.confidence = 1.0;
            }
            return new UserSegmentMappingEntity(id, userId, segmentId, segmentName, segmentType,
                isActive, matchScore, confidence, attributes, expiresAt, assignedAt, 
                lastValidatedAt, createdAt, updatedAt);
        }
    }

    /**
     * 更新时间戳
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 验证分段映射
     */
    public void validate() {
        this.lastValidatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活分段映射
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用分段映射
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}