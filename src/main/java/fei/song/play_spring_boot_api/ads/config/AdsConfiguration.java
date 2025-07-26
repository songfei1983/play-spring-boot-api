package fei.song.play_spring_boot_api.ads.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 广告系统配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ads")
public class AdsConfiguration {
    
    /**
     * 反欺诈配置
     */
    private FraudDetection fraudDetection = new FraudDetection();
    
    /**
     * 竞价算法配置
     */
    private Bidding bidding = new Bidding();
    
    /**
     * 服务器配置
     */
    private Server server = new Server();
    
    /**
     * 预算管理配置
     */
    private Budget budget = new Budget();
    
    @Data
    public static class FraudDetection {
        /**
         * 是否启用反欺诈检测
         */
        private boolean enabled = true;
        
        /**
         * 欺诈风险阈值
         */
        private double riskThreshold = 0.7;
        
        /**
         * 每小时最大点击数
         */
        private int maxClicksPerHour = 100;
        
        /**
         * 每小时最大展示数
         */
        private int maxImpressionsPerHour = 1000;
        
        /**
         * IP黑名单
         */
        private List<String> ipBlacklist = List.of();
        
        /**
         * 域名白名单
         */
        private List<String> domainWhitelist = List.of();
        
        /**
         * 可疑User Agent模式
         */
        private List<String> suspiciousUserAgentPatterns = List.of(
            ".*bot.*",
            ".*crawler.*",
            ".*spider.*",
            ".*scraper.*"
        );
    }
    
    @Data
    public static class Bidding {
        /**
         * 默认货币
         */
        private String defaultCurrency = "USD";
        
        /**
         * 拍卖类型 (1=第一价格, 2=第二价格)
         */
        private int auctionType = 2;
        
        /**
         * 算法权重配置
         */
        private Weights weights = new Weights();
        
        /**
         * 最小竞价价格
         */
        private double minBidPrice = 0.01;
        
        /**
         * 最大竞价价格
         */
        private double maxBidPrice = 100.0;
        
        /**
         * 竞价超时时间(毫秒)
         */
        private long timeoutMs = 100;
        
        @Data
        public static class Weights {
            private double userValue = 0.3;
            private double contextRelevance = 0.25;
            private double competition = 0.2;
            private double quality = 0.25;
        }
    }
    
    @Data
    public static class Server {
        /**
         * 服务器座位ID
         */
        private String seatId = "seat_1";
        
        /**
         * 最大并发请求数
         */
        private int maxConcurrentRequests = 1000;
        
        /**
         * 请求处理超时时间(毫秒)
         */
        private long requestTimeoutMs = 200;
        
        /**
         * 是否启用请求日志
         */
        private boolean enableRequestLogging = true;
        
        /**
         * 是否启用性能监控
         */
        private boolean enablePerformanceMonitoring = true;
        
        /**
         * 支持的OpenRTB版本
         */
        private String openRtbVersion = "2.5";
    }
    
    @Data
    public static class Budget {
        /**
         * 是否启用预算控制
         */
        private boolean enabled = true;
        
        /**
         * 预算检查超时时间(毫秒)
         */
        private long checkTimeoutMs = 50;
        
        /**
         * 预算预扣有效期(秒)
         */
        private int reservationTtlSeconds = 300;
        
        /**
         * 默认日预算
         */
        private double defaultDailyBudget = 1000.0;
        
        /**
         * 预算告警阈值
         */
        private double alertThreshold = 0.8;
    }
}