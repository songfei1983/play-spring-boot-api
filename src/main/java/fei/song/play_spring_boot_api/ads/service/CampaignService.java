package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.service.OpenRTBDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 广告活动服务
 * 提供广告活动相关的业务逻辑，从infrastructure层获取数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {
    
    private final OpenRTBDataService openRTBDataService;
    
    /**
     * 获取所有活跃的广告活动
     */
    public List<CampaignEntity> getActiveCampaigns() {
        log.debug("获取活跃的广告活动");
        return openRTBDataService.getActiveCampaigns();
    }
    
    /**
     * 根据定向条件获取匹配的广告活动
     */
    public List<CampaignEntity> getMatchingCampaigns(String country, Integer deviceType, BigDecimal minBid) {
        log.debug("获取匹配的广告活动: country={}, deviceType={}, minBid={}", country, deviceType, minBid);
        return openRTBDataService.getMatchingCampaigns(country, deviceType, minBid);
    }
    
    /**
     * 根据campaignId获取广告活动
     */
    public Optional<CampaignEntity> getCampaignById(String campaignId) {
        log.debug("根据ID获取广告活动: campaignId={}", campaignId);
        List<CampaignEntity> activeCampaigns = getActiveCampaigns();
        return activeCampaigns.stream()
            .filter(campaign -> campaignId.equals(campaign.getCampaignId()))
            .findFirst();
    }
    
    /**
     * 获取广告活动的预算信息
     */
    public CampaignEntity.Budget getCampaignBudget(String campaignId) {
        Optional<CampaignEntity> campaign = getCampaignById(campaignId);
        if (campaign.isPresent() && campaign.get().getBudget() != null) {
            return campaign.get().getBudget();
        }
        
        log.warn("未找到广告活动预算信息: campaignId={}", campaignId);
        return null;
    }
    
    /**
     * 获取广告活动的竞价信息
     */
    public CampaignEntity.Bidding getCampaignBidding(String campaignId) {
        Optional<CampaignEntity> campaign = getCampaignById(campaignId);
        if (campaign.isPresent() && campaign.get().getBidding() != null) {
            return campaign.get().getBidding();
        }
        
        log.warn("未找到广告活动竞价信息: campaignId={}", campaignId);
        return null;
    }
    
    /**
     * 检查广告活动是否活跃
     */
    public boolean isCampaignActive(String campaignId) {
        Optional<CampaignEntity> campaign = getCampaignById(campaignId);
        return campaign.isPresent() && "active".equals(campaign.get().getStatus());
    }
    
    /**
     * 获取广告活动的创意信息
     */
    public List<CampaignEntity.Creative> getCampaignCreatives(String campaignId) {
        Optional<CampaignEntity> campaign = getCampaignById(campaignId);
        if (campaign.isPresent() && campaign.get().getCreatives() != null) {
            return campaign.get().getCreatives();
        }
        
        log.warn("未找到广告活动创意信息: campaignId={}", campaignId);
        return List.of();
    }
    
    /**
     * 获取广告活动的定向信息
     */
    public CampaignEntity.Targeting getCampaignTargeting(String campaignId) {
        Optional<CampaignEntity> campaign = getCampaignById(campaignId);
        if (campaign.isPresent() && campaign.get().getTargeting() != null) {
            return campaign.get().getTargeting();
        }
        
        log.warn("未找到广告活动定向信息: campaignId={}", campaignId);
        return null;
    }
}