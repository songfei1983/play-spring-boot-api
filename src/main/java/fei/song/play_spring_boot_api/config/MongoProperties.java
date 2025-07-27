package fei.song.play_spring_boot_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * MongoDB 配置属性类
 * 统一管理MongoDB相关的配置参数
 */
@Component
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Validated
public class MongoProperties {
    
    /**
     * MongoDB连接URI
     */
    private String uri = "mongodb://localhost:27017";
    
    /**
     * 数据库名称
     */
    private String database = "openrtb";
    
    /**
     * 是否自动创建索引
     */
    private boolean autoIndexCreation = true;
    
    /**
     * 连接池配置
     */
    private ConnectionPool connectionPool = new ConnectionPool();
    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public void setDatabase(String database) {
        this.database = database;
    }
    
    public boolean isAutoIndexCreation() {
        return autoIndexCreation;
    }
    
    public void setAutoIndexCreation(boolean autoIndexCreation) {
        this.autoIndexCreation = autoIndexCreation;
    }
    
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
    
    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    /**
     * 连接池配置内部类
     */
    public static class ConnectionPool {
        
        /**
         * 最大连接数
         */
        private int maxSize = 20;
        
        /**
         * 最小连接数
         */
        private int minSize = 5;
        
        /**
         * 最大等待时间（秒）
         */
        private int maxWaitTimeSeconds = 2;
        
        /**
         * 连接最大空闲时间（秒）
         */
        private int maxConnectionIdleTimeSeconds = 600;
        
        /**
         * 连接最大生存时间（秒）
         */
        private int maxConnectionLifeTimeSeconds = 1800;
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
        
        public int getMinSize() {
            return minSize;
        }
        
        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }
        
        public int getMaxWaitTimeSeconds() {
            return maxWaitTimeSeconds;
        }
        
        public void setMaxWaitTimeSeconds(int maxWaitTimeSeconds) {
            this.maxWaitTimeSeconds = maxWaitTimeSeconds;
        }
        
        public int getMaxConnectionIdleTimeSeconds() {
            return maxConnectionIdleTimeSeconds;
        }
        
        public void setMaxConnectionIdleTimeSeconds(int maxConnectionIdleTimeSeconds) {
            this.maxConnectionIdleTimeSeconds = maxConnectionIdleTimeSeconds;
        }
        
        public int getMaxConnectionLifeTimeSeconds() {
            return maxConnectionLifeTimeSeconds;
        }
        
        public void setMaxConnectionLifeTimeSeconds(int maxConnectionLifeTimeSeconds) {
            this.maxConnectionLifeTimeSeconds = maxConnectionLifeTimeSeconds;
        }
    }
}