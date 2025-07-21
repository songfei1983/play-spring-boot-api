package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发布者信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Publisher {
    
    /**
     * 交换特定发布者ID
     */
    private String id;
    
    /**
     * 发布者名称
     */
    private String name;
    
    /**
     * 发布者类别数组
     */
    @JsonProperty("cat")
    private List<String> cat;
    
    /**
     * 发布者域名
     */
    private String domain;
    
    /**
     * 扩展字段
     */
    private Object ext;
}