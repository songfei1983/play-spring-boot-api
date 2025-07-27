package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 广告位过滤服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdSlotFilterService {
    
    private final CampaignRepository campaignRepository;
    
    /**
     * 根据广告位要求过滤广告候选
     */
    public List<BidCandidate> filterCandidatesForImpression(Impression impression, BidRequest bidRequest, List<BidCandidate> candidates) {
        return candidates.stream()
            .filter(candidate -> matchesAdSize(impression, candidate))
            .filter(candidate -> meetsFloorPrice(impression, candidate))
            .filter(candidate -> matchesAdFormat(impression, candidate))
            .filter(candidate -> matchesTargeting(bidRequest, candidate))
            .filter(candidate -> isCreativeValid(candidate))
            .collect(Collectors.toList());
    }
    
    /**
     * 检查广告尺寸是否匹配
     */
    private boolean matchesAdSize(Impression impression, BidCandidate candidate) {
        // 检查横幅广告尺寸
        if (impression.getBanner() != null) {
            Banner banner = impression.getBanner();
            
            // 检查固定尺寸
            if (banner.getW() != null && banner.getH() != null) {
                return Objects.equals(banner.getW(), candidate.getWidth()) && 
                       Objects.equals(banner.getH(), candidate.getHeight());
            }
            
            // 检查支持的格式列表
            if (banner.getFormat() != null && !banner.getFormat().isEmpty()) {
                return banner.getFormat().stream()
                    .anyMatch(format -> Objects.equals(format.getW(), candidate.getWidth()) && 
                                      Objects.equals(format.getH(), candidate.getHeight()));
            }
        }
        
        // 检查视频广告尺寸
        if (impression.getVideo() != null) {
            Video video = impression.getVideo();
            return Objects.equals(video.getW(), candidate.getWidth()) && 
                   Objects.equals(video.getH(), candidate.getHeight());
        }
        
        return true; // 如果没有尺寸要求，默认匹配
    }
    
    /**
     * 检查是否满足底价要求
     */
    private boolean meetsFloorPrice(Impression impression, BidCandidate candidate) {
        if (impression.getBidfloor() != null && impression.getBidfloor() > 0) {
            return candidate.getBidPrice() >= impression.getBidfloor();
        }
        return true;
    }
    
    /**
     * 检查广告格式是否匹配
     */
    private boolean matchesAdFormat(Impression impression, BidCandidate candidate) {
        // 检查横幅广告格式
        if (impression.getBanner() != null) {
            Banner banner = impression.getBanner();
            
            // 检查支持的MIME类型
            if (banner.getMimes() != null && !banner.getMimes().isEmpty()) {
                // 这里需要根据创意内容判断MIME类型
                // 简化实现：假设所有创意都支持常见格式
                return true;
            }
            
            // 检查阻止的创意属性
            if (banner.getBlockedAttributes() != null && !banner.getBlockedAttributes().isEmpty()) {
                // 检查候选广告是否包含被阻止的属性
                // 简化实现
                return true;
            }
        }
        
        // 检查视频广告格式
        if (impression.getVideo() != null) {
            Video video = impression.getVideo();
            
            // 检查支持的MIME类型
            if (video.getMimes() != null && !video.getMimes().isEmpty()) {
                return true; // 简化实现
            }
            
            // 检查最小/最大持续时间
            if (video.getMinDuration() != null || video.getMaxDuration() != null) {
                // 这里需要检查视频创意的持续时间
                return true; // 简化实现
            }
        }
        
        return true;
    }
    
    /**
     * 检查定向匹配
     */
    private boolean matchesTargeting(BidRequest bidRequest, BidCandidate candidate) {
        // 地理位置定向
        if (!matchesGeoTargeting(bidRequest, candidate)) {
            return false;
        }
        
        // 设备定向
        if (!matchesDeviceTargeting(bidRequest, candidate)) {
            return false;
        }
        
        // 时间定向
        if (!matchesTimeTargeting(candidate)) {
            return false;
        }
        
        // 用户定向
        if (!matchesUserTargeting(bidRequest, candidate)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 地理位置定向匹配
     */
    private boolean matchesGeoTargeting(BidRequest bidRequest, BidCandidate candidate) {
        if (bidRequest.getDevice() != null && bidRequest.getDevice().getGeo() != null) {
            Geo geo = bidRequest.getDevice().getGeo();
            
            // 检查国家定向
            if (geo.getCountry() != null) {
                // 这里需要检查候选广告的地理定向设置
                // 简化实现：假设所有广告都支持全球投放
                return true;
            }
            
            // 检查城市定向
            if (geo.getCity() != null) {
                return true; // 简化实现
            }
        }
        
        return true;
    }
    
    /**
     * 设备定向匹配
     */
    private boolean matchesDeviceTargeting(BidRequest bidRequest, BidCandidate candidate) {
        if (bidRequest.getDevice() != null) {
            Device device = bidRequest.getDevice();
            
            // 检查设备类型定向
            if (device.getDevicetype() != null) {
                // 这里需要检查候选广告的设备类型定向
                return true; // 简化实现
            }
            
            // 检查操作系统定向
            if (device.getOs() != null) {
                return true; // 简化实现
            }
        }
        
        return true;
    }
    
    /**
     * 时间定向匹配
     */
    private boolean matchesTimeTargeting(BidCandidate candidate) {
        try {
            // 获取当前时间信息
            LocalDateTime now = LocalDateTime.now();
            int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
            int currentHour = now.getHour(); // 0-23
            
            // 根据campaignId获取Campaign信息
            Optional<CampaignEntity> campaignOpt = campaignRepository.findByCampaignId(candidate.getCampaignId());
            if (campaignOpt.isEmpty()) {
                log.warn("Campaign not found for campaignId: {}", candidate.getCampaignId());
                return false;
            }
            
            CampaignEntity campaign = campaignOpt.get();
            
            // 检查Campaign的时间定向设置
            if (campaign.getTargeting() != null && campaign.getTargeting().getTime() != null) {
                CampaignEntity.TimeTargeting timeTargeting = campaign.getTargeting().getTime();
                
                // 检查星期几定向
                if (timeTargeting.getDaysOfWeek() != null && !timeTargeting.getDaysOfWeek().isEmpty()) {
                    if (!timeTargeting.getDaysOfWeek().contains(currentDayOfWeek)) {
                        log.debug("Day of week targeting failed for campaign {}: current={}, allowed={}", 
                            candidate.getCampaignId(), currentDayOfWeek, timeTargeting.getDaysOfWeek());
                        return false;
                    }
                }
                
                // 检查小时定向
                if (timeTargeting.getHoursOfDay() != null && !timeTargeting.getHoursOfDay().isEmpty()) {
                    if (!timeTargeting.getHoursOfDay().contains(currentHour)) {
                        log.debug("Hour targeting failed for campaign {}: current={}, allowed={}", 
                            candidate.getCampaignId(), currentHour, timeTargeting.getHoursOfDay());
                        return false;
                    }
                }
            }
            
            // 检查Campaign的投放时间安排
            if (campaign.getSchedule() != null) {
                CampaignEntity.Schedule schedule = campaign.getSchedule();
                
                // 检查开始时间
                if (schedule.getStartDate() != null && now.isBefore(schedule.getStartDate())) {
                    log.debug("Campaign {} not started yet: current={}, start={}", 
                        candidate.getCampaignId(), now, schedule.getStartDate());
                    return false;
                }
                
                // 检查结束时间
                if (schedule.getEndDate() != null && now.isAfter(schedule.getEndDate())) {
                    log.debug("Campaign {} already ended: current={}, end={}", 
                        candidate.getCampaignId(), now, schedule.getEndDate());
                    return false;
                }
            }
            
            log.debug("Time targeting passed for campaign {}", candidate.getCampaignId());
            return true;
            
        } catch (Exception e) {
            log.error("Error checking time targeting for campaign {}: {}", 
                candidate.getCampaignId(), e.getMessage(), e);
            // 出现异常时，为了保证系统稳定性，默认返回false
            return false;
        }
    }
    
    /**
     * 用户定向匹配
     */
    private boolean matchesUserTargeting(BidRequest bidRequest, BidCandidate candidate) {
        try {
            if (bidRequest.getUser() == null) {
                return true; // 没有用户信息时默认通过
            }
            
            User user = bidRequest.getUser();
            
            // 根据campaignId获取Campaign信息
            Optional<CampaignEntity> campaignOpt = campaignRepository.findByCampaignId(candidate.getCampaignId());
            if (campaignOpt.isEmpty()) {
                log.warn("Campaign not found for user targeting check: {}", candidate.getCampaignId());
                return false;
            }
            
            CampaignEntity campaign = campaignOpt.get();
            
            // 检查Campaign的受众定向设置
            if (campaign.getTargeting() != null && campaign.getTargeting().getAudience() != null) {
                CampaignEntity.AudienceTargeting audienceTargeting = campaign.getTargeting().getAudience();
                
                // 检查年龄定向
                if (user.getYob() != null && audienceTargeting.getAgeRange() != null) {
                    int age = LocalDateTime.now().getYear() - user.getYob();
                    CampaignEntity.AgeRange ageRange = audienceTargeting.getAgeRange();
                    
                    if (ageRange.getMin() != null && age < ageRange.getMin()) {
                        log.debug("Age targeting failed for campaign {}: user age {} < min age {}", 
                            candidate.getCampaignId(), age, ageRange.getMin());
                        return false;
                    }
                    
                    if (ageRange.getMax() != null && age > ageRange.getMax()) {
                        log.debug("Age targeting failed for campaign {}: user age {} > max age {}", 
                            candidate.getCampaignId(), age, ageRange.getMax());
                        return false;
                    }
                }
                
                // 检查性别定向
                if (user.getGender() != null && audienceTargeting.getGenders() != null && !audienceTargeting.getGenders().isEmpty()) {
                    String userGender = user.getGender();
                    if (!audienceTargeting.getGenders().contains(userGender)) {
                        log.debug("Gender targeting failed for campaign {}: user gender {} not in allowed genders {}", 
                            candidate.getCampaignId(), userGender, audienceTargeting.getGenders());
                        return false;
                    }
                }
                
                // 检查兴趣定向
                if (user.getKeywords() != null && !user.getKeywords().trim().isEmpty() && 
                    audienceTargeting.getInterests() != null && !audienceTargeting.getInterests().isEmpty()) {
                    
                    String[] userKeywords = user.getKeywords().toLowerCase().split("[,\\s]+");
                    Set<String> userInterests = Arrays.stream(userKeywords)
                        .map(String::trim)
                        .filter(keyword -> !keyword.isEmpty())
                        .collect(Collectors.toSet());
                    
                    Set<String> targetInterests = audienceTargeting.getInterests().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                    
                    // 检查是否有交集
                    boolean hasInterestMatch = userInterests.stream()
                        .anyMatch(targetInterests::contains);
                    
                    if (!hasInterestMatch) {
                        log.debug("Interest targeting failed for campaign {}: user interests {} do not match target interests {}", 
                            candidate.getCampaignId(), userInterests, targetInterests);
                        return false;
                    }
                }
            }
            
            log.debug("User targeting passed for campaign {}", candidate.getCampaignId());
            return true;
            
        } catch (Exception e) {
            log.error("Error checking user targeting for campaign {}: {}", 
                candidate.getCampaignId(), e.getMessage(), e);
            // 出现异常时，为了保证系统稳定性，默认返回false
            return false;
        }
    }
    
    /**
     * 检查创意有效性
     */
    private boolean isCreativeValid(BidCandidate candidate) {
        // 检查创意内容是否存在
        if (candidate.getAdMarkup() == null || candidate.getAdMarkup().trim().isEmpty()) {
            return false;
        }
        
        // 检查必要的URL是否存在
        if (candidate.getClickUrl() == null || candidate.getClickUrl().trim().isEmpty()) {
            return false;
        }
        
        // 检查广告域名是否有效
        if (candidate.getAdvertiserDomains() == null || candidate.getAdvertiserDomains().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 获取过滤统计信息
     */
    public Map<String, Object> getFilterStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("filterEnabled", true);
        return stats;
    }
}