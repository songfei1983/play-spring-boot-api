package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*")
public class CampaignController {

    @Autowired
    private CampaignRepository campaignRepository;

    // 获取所有广告活动（分页）
    @GetMapping
    public ResponseEntity<Page<CampaignEntity>> getAllCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<CampaignEntity> allCampaigns = campaignRepository.findAll();
        
        // 手动实现分页
        int start = page * size;
        int end = Math.min(start + size, allCampaigns.size());
        List<CampaignEntity> pagedCampaigns = allCampaigns.subList(start, end);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignEntity> campaignPage = new PageImpl<>(pagedCampaigns, pageable, allCampaigns.size());
        
        return ResponseEntity.ok(campaignPage);
    }

    // 根据ID获取广告活动
    @GetMapping("/{id}")
    public ResponseEntity<CampaignEntity> getCampaignById(@PathVariable String id) {
        Optional<CampaignEntity> campaign = campaignRepository.findByCampaignId(id);
        return campaign.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 创建新的广告活动
    @PostMapping
    public ResponseEntity<CampaignEntity> createCampaign(@RequestBody CampaignEntity campaign) {
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());
        CampaignEntity savedCampaign = campaignRepository.save(campaign);
        return ResponseEntity.ok(savedCampaign);
    }

    // 更新广告活动
    @PutMapping("/{id}")
    public ResponseEntity<CampaignEntity> updateCampaign(
            @PathVariable String id, 
            @RequestBody CampaignEntity campaignDetails) {
        
        Optional<CampaignEntity> optionalCampaign = campaignRepository.findByCampaignId(id);
        if (optionalCampaign.isPresent()) {
            CampaignEntity campaign = optionalCampaign.get();
            
            // 更新字段
            campaign.setName(campaignDetails.getName());
            campaign.setStatus(campaignDetails.getStatus());
            campaign.setBudget(campaignDetails.getBudget());
            campaign.setTargeting(campaignDetails.getTargeting());
            campaign.setBidding(campaignDetails.getBidding());
            campaign.setCreatives(campaignDetails.getCreatives());
            campaign.setFrequencyCap(campaignDetails.getFrequencyCap());
            campaign.setSchedule(campaignDetails.getSchedule());
            campaign.setUpdatedAt(LocalDateTime.now());
            
            CampaignEntity updatedCampaign = campaignRepository.save(campaign);
            return ResponseEntity.ok(updatedCampaign);
        }
        return ResponseEntity.notFound().build();
    }

    // 删除广告活动
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable String id) {
        Optional<CampaignEntity> campaign = campaignRepository.findByCampaignId(id);
        if (campaign.isPresent()) {
            campaignRepository.delete(campaign.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 更新广告活动状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<CampaignEntity> updateCampaignStatus(
            @PathVariable String id, 
            @RequestBody Map<String, String> statusUpdate) {
        
        Optional<CampaignEntity> optionalCampaign = campaignRepository.findByCampaignId(id);
        if (optionalCampaign.isPresent()) {
            CampaignEntity campaign = optionalCampaign.get();
            campaign.setStatus(statusUpdate.get("status"));
            campaign.setUpdatedAt(LocalDateTime.now());
            
            CampaignEntity updatedCampaign = campaignRepository.save(campaign);
            return ResponseEntity.ok(updatedCampaign);
        }
        return ResponseEntity.notFound().build();
    }

    // 根据广告主ID获取广告活动
    @GetMapping("/advertiser/{advertiserId}")
    public ResponseEntity<List<CampaignEntity>> getCampaignsByAdvertiser(@PathVariable String advertiserId) {
        List<CampaignEntity> campaigns = campaignRepository.findByAdvertiserId(advertiserId);
        return ResponseEntity.ok(campaigns);
    }

    // 根据状态获取广告活动
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CampaignEntity>> getCampaignsByStatus(@PathVariable String status) {
        List<CampaignEntity> campaigns = campaignRepository.findByStatus(status);
        return ResponseEntity.ok(campaigns);
    }

    // 获取广告活动统计信息
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCampaignStatistics() {
        List<CampaignEntity> allCampaigns = campaignRepository.findAll();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCampaigns", allCampaigns.size());
        statistics.put("activeCampaigns", allCampaigns.stream().filter(c -> "ACTIVE".equals(c.getStatus())).count());
        statistics.put("pausedCampaigns", allCampaigns.stream().filter(c -> "PAUSED".equals(c.getStatus())).count());
        statistics.put("completedCampaigns", allCampaigns.stream().filter(c -> "COMPLETED".equals(c.getStatus())).count());
        
        return ResponseEntity.ok(statistics);
    }

    // 搜索广告活动
    @GetMapping("/search")
    public ResponseEntity<List<CampaignEntity>> searchCampaigns(@RequestParam String query) {
        List<CampaignEntity> allCampaigns = campaignRepository.findAll();
        List<CampaignEntity> filteredCampaigns = allCampaigns.stream()
                .filter(campaign -> campaign.getName() != null && 
                        campaign.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(filteredCampaigns);
    }
}