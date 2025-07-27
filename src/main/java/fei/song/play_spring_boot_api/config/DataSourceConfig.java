package fei.song.play_spring_boot_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
    
    /**
     * 数据源类型配置
     */
    public enum DataSourceType {
        MEMORY,  // 内存数据源（当前实现）
        H2,      // H2数据库
        MYSQL,   // MySQL数据库
        POSTGRESQL, // PostgreSQL数据库
        MONGODB  // MongoDB数据库
    }
    
    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }
    
    public static class DataSourceProperties {
        private DataSourceType type = DataSourceType.MEMORY;
        private boolean enableJpa = false;
        
        public DataSourceType getType() {
            return type;
        }
        
        public void setType(DataSourceType type) {
            this.type = type;
        }
        
        public boolean isEnableJpa() {
            return enableJpa;
        }
        
        public void setEnableJpa(boolean enableJpa) {
            this.enableJpa = enableJpa;
        }
    }
}