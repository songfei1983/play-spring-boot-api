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
import java.util.List;
import java.util.Map;

/**
 * 用户分段实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_segments")
@CompoundIndexes({
    @CompoundIndex(name = "segment_status_idx", def = "{'status': 1, 'created_at': -1}"),
    @CompoundIndex(name = "segment_type_idx", def = "{'segment_type': 1, 'priority': -1}")
})
public class UserSegmentEntity {

    @Id
    private String id;

    /**
     * 分段名称
     */
    @Indexed
    @Field("segment_name")
    private String segmentName;

    /**
     * 分段描述
     */
    private String description;

    /**
     * 分段类型
     */
    @Field("segment_type")
    private String segmentType; // DEMOGRAPHIC, BEHAVIORAL, INTEREST, CUSTOM

    /**
     * 分段规则
     */
    private List<SegmentRule> rules;

    /**
     * 分段状态
     */
    @Indexed
    private String status; // ACTIVE, INACTIVE, DRAFT

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 分段标签
     */
    private List<String> tags;

    /**
     * 分段元数据
     */
    private Map<String, Object> metadata;

    /**
     * 创建者
     */
    @Field("created_by")
    private String createdBy;

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
     * 分段规则内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SegmentRule {
        /**
         * 规则字段
         */
        private String field;

        /**
         * 操作符
         */
        private String operator; // EQ, NE, GT, LT, GTE, LTE, IN, NOT_IN, CONTAINS, REGEX

        /**
         * 规则值
         */
        private Object value;

        /**
         * 逻辑操作符
         */
        @Field("logical_operator")
        private String logicalOperator; // AND, OR

        /**
         * 规则权重
         */
        private Double weight;

        /**
         * 规则描述
         */
        private String description;
    }

    /**
     * 预构建方法 - 设置默认值
     */
    public static class UserSegmentEntityBuilder {
        public UserSegmentEntity build() {
            if (this.createdAt == null) {
                this.createdAt = LocalDateTime.now();
            }
            if (this.updatedAt == null) {
                this.updatedAt = LocalDateTime.now();
            }
            if (this.status == null) {
                this.status = "DRAFT";
            }
            if (this.priority == null) {
                this.priority = 0;
            }
            return new UserSegmentEntity(id, segmentName, description, segmentType, rules, 
                status, priority, tags, metadata, createdBy, createdAt, updatedAt);
        }
    }

    /**
     * 更新时间戳
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}