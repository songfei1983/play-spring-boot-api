package fei.song.play_spring_boot_api.ads.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 制作者信息对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Producer {
    
    /**
     * 制作者ID
     */
    private String id;
    
    /**
     * 制作者名称
     */
    private String name;
    
    /**
     * 制作者类别数组
     */
    @JsonProperty("cat")
    private List<String> cat;
    
    /**
     * 制作者域名
     */
    private String domain;
    
    /**
     * 扩展字段
     */
    private Object ext;
}