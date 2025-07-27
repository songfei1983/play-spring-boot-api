package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.BidRequest;
import fei.song.play_spring_boot_api.ads.domain.model.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 反欺诈检测服务
 */
@Slf4j
@Service
public class FraudDetectionService {
    
    // IP黑名单
    private final Set<String> ipBlacklist = ConcurrentHashMap.newKeySet();
    
    // 设备指纹黑名单
    private final Set<String> deviceBlacklist = ConcurrentHashMap.newKeySet();
    
    // 域名白名单
    private final Set<String> domainWhitelist = ConcurrentHashMap.newKeySet();
    
    // 点击频率记录 <IP, 点击时间列表>
    private final Map<String, List<LocalDateTime>> clickFrequencyMap = new ConcurrentHashMap<>();
    
    // 显示频率记录 <IP, 显示时间列表>
    private final Map<String, List<LocalDateTime>> impressionFrequencyMap = new ConcurrentHashMap<>();
    
    // 可疑User Agent模式
    private final List<Pattern> suspiciousUserAgentPatterns = Arrays.asList(
        Pattern.compile(".*bot.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*crawler.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*spider.*", Pattern.CASE_INSENSITIVE),
        Pattern.compile(".*scraper.*", Pattern.CASE_INSENSITIVE)
    );
    
    // 配置参数
    private static final int MAX_CLICKS_PER_HOUR = 100;
    private static final int MAX_IMPRESSIONS_PER_HOUR = 1000;
    private static final double FRAUD_RISK_THRESHOLD = 0.7;
    
    /**
     * 检测竞价请求是否存在欺诈风险
     */
    public boolean isFraudulent(BidRequest bidRequest) {
        try {
            double riskScore = calculateFraudRiskScore(bidRequest);
            boolean isFraud = riskScore >= FRAUD_RISK_THRESHOLD;
            
            if (isFraud) {
                log.warn("检测到欺诈风险: requestId={}, riskScore={}, ip={}", 
                    bidRequest.getId(), riskScore, bidRequest.getDevice().getIp());
            }
            
            return isFraud;
        } catch (Exception e) {
            log.error("反欺诈检测异常: requestId={}", bidRequest.getId(), e);
            return false; // 异常情况下不阻止竞价
        }
    }
    
    /**
     * 计算欺诈风险分数
     */
    private double calculateFraudRiskScore(BidRequest bidRequest) {
        double riskScore = 0.0;
        
        // IP黑名单检查
        if (isIpBlacklisted(bidRequest.getDevice().getIp())) {
            riskScore += 0.8;
        }
        
        // 点击频率检查
        if (isClickFrequencyAbnormal(bidRequest.getDevice().getIp())) {
            riskScore += 0.6;
        }
        
        // User Agent异常检查
        if (isUserAgentSuspicious(bidRequest.getDevice().getUa())) {
            riskScore += 0.5;
        }
        
        // 地理位置一致性检查
        if (isGeoLocationInconsistent(bidRequest)) {
            riskScore += 0.4;
        }
        
        // 设备指纹检查
        if (isDeviceBlacklisted(bidRequest.getDevice())) {
            riskScore += 0.7;
        }
        
        // 显示频率检查
        if (isImpressionFrequencyAbnormal(bidRequest.getDevice().getIp())) {
            riskScore += 0.3;
        }
        
        // 域名白名单检查
        if (!isDomainWhitelisted(bidRequest)) {
            riskScore += 0.2;
        }
        
        return Math.min(riskScore, 1.0);
    }
    
    /**
     * 检查IP是否在黑名单中
     */
    private boolean isIpBlacklisted(String ip) {
        return ipBlacklist.contains(ip);
    }
    
    /**
     * 检查点击频率是否异常
     */
    private boolean isClickFrequencyAbnormal(String ip) {
        List<LocalDateTime> clickTimes = clickFrequencyMap.computeIfAbsent(ip, k -> new ArrayList<>());
        
        // 清理1小时前的记录
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        clickTimes.removeIf(time -> time.isBefore(oneHourAgo));
        
        // 记录当前点击
        clickTimes.add(LocalDateTime.now());
        
        return clickTimes.size() > MAX_CLICKS_PER_HOUR;
    }
    
    /**
     * 检查User Agent是否可疑
     */
    private boolean isUserAgentSuspicious(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return true;
        }
        
        return suspiciousUserAgentPatterns.stream()
            .anyMatch(pattern -> pattern.matcher(userAgent).matches());
    }
    
    /**
     * 检查地理位置一致性
     */
    private boolean isGeoLocationInconsistent(BidRequest bidRequest) {
        // 简化实现：检查IP地址与地理位置是否匹配
        // 实际实现需要IP地理位置数据库
        return false;
    }
    
    /**
     * 检查设备是否在黑名单中
     */
    private boolean isDeviceBlacklisted(Device device) {
        String deviceFingerprint = generateDeviceFingerprint(device);
        return deviceBlacklist.contains(deviceFingerprint);
    }
    
    /**
     * 检查显示频率是否异常
     */
    private boolean isImpressionFrequencyAbnormal(String ip) {
        List<LocalDateTime> impressionTimes = impressionFrequencyMap.computeIfAbsent(ip, k -> new ArrayList<>());
        
        // 清理1小时前的记录
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        impressionTimes.removeIf(time -> time.isBefore(oneHourAgo));
        
        // 记录当前显示
        impressionTimes.add(LocalDateTime.now());
        
        return impressionTimes.size() > MAX_IMPRESSIONS_PER_HOUR;
    }
    
    /**
     * 检查域名是否在白名单中
     */
    private boolean isDomainWhitelisted(BidRequest bidRequest) {
        if (bidRequest.getSite() != null && bidRequest.getSite().getDomain() != null) {
            return domainWhitelist.isEmpty() || domainWhitelist.contains(bidRequest.getSite().getDomain());
        }
        return true; // 如果没有域名信息，默认通过
    }
    
    /**
     * 生成设备指纹
     */
    private String generateDeviceFingerprint(Device device) {
        StringBuilder fingerprint = new StringBuilder();
        
        if (device.getUa() != null) fingerprint.append(device.getUa());
        if (device.getIp() != null) fingerprint.append("|").append(device.getIp());
        if (device.getDevicetype() != null) fingerprint.append("|").append(device.getDevicetype());
        if (device.getMake() != null) fingerprint.append("|").append(device.getMake());
        if (device.getModel() != null) fingerprint.append("|").append(device.getModel());
        if (device.getOs() != null) fingerprint.append("|").append(device.getOs());
        if (device.getOsv() != null) fingerprint.append("|").append(device.getOsv());
        
        return Integer.toString(fingerprint.toString().hashCode());
    }
    
    /**
     * 添加IP到黑名单
     */
    public void addIpToBlacklist(String ip) {
        ipBlacklist.add(ip);
        log.info("IP已添加到黑名单: {}", ip);
    }
    
    /**
     * 添加设备到黑名单
     */
    public void addDeviceToBlacklist(Device device) {
        String fingerprint = generateDeviceFingerprint(device);
        deviceBlacklist.add(fingerprint);
        log.info("设备已添加到黑名单: {}", fingerprint);
    }
    
    /**
     * 添加域名到白名单
     */
    public void addDomainToWhitelist(String domain) {
        domainWhitelist.add(domain);
        log.info("域名已添加到白名单: {}", domain);
    }
    
    /**
     * 获取欺诈统计信息
     */
    public Map<String, Object> getFraudStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("ipBlacklistSize", ipBlacklist.size());
        stats.put("deviceBlacklistSize", deviceBlacklist.size());
        stats.put("domainWhitelistSize", domainWhitelist.size());
        stats.put("activeClickTracking", clickFrequencyMap.size());
        stats.put("activeImpressionTracking", impressionFrequencyMap.size());
        return stats;
    }
}