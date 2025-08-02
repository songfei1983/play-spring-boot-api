package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.dto.CampaignDTO;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository.CampaignRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Campaign Management", description = "广告活动管理API")
public class CampaignController {

    @Autowired
    private CampaignRepository campaignRepository;

    // 获取所有广告活动（分页）
    @GetMapping
    @Operation(summary = "获取所有广告活动", description = "分页获取所有广告活动列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取广告活动列表",
                content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Page<CampaignEntity>> getAllCampaigns(
            @Parameter(description = "页码", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size) {
        
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
    @Operation(summary = "根据ID获取广告活动", description = "通过广告活动ID获取详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取广告活动",
                content = @Content(schema = @Schema(implementation = CampaignEntity.class))),
        @ApiResponse(responseCode = "404", description = "广告活动不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<CampaignEntity> getCampaignById(
            @Parameter(description = "广告活动ID", example = "campaign123") @PathVariable String id) {
        Optional<CampaignEntity> campaign = campaignRepository.findByCampaignId(id);
        return campaign.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 创建新的广告活动
    @PostMapping
    @Operation(summary = "创建新的广告活动", description = "创建一个新的广告活动")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功创建广告活动",
                content = @Content(schema = @Schema(implementation = CampaignEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<CampaignEntity> createCampaign(
            @Parameter(description = "广告活动信息") @RequestBody CampaignEntity campaign) {
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setUpdatedAt(LocalDateTime.now());
        CampaignEntity savedCampaign = campaignRepository.save(campaign);
        return ResponseEntity.ok(savedCampaign);
    }

    // 更新广告活动
    @PutMapping("/{id}")
    @Operation(summary = "更新广告活动", description = "更新指定ID的广告活动信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新广告活动",
                content = @Content(schema = @Schema(implementation = CampaignEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "广告活动不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<CampaignDTO> updateCampaign(
            @Parameter(description = "广告活动ID", example = "campaign123") @PathVariable String id, 
            @Parameter(description = "更新的广告活动信息") @RequestBody CampaignDTO campaignDetails) {
        
        Optional<CampaignEntity> optionalCampaign = campaignRepository.findByCampaignId(id);
        if (optionalCampaign.isPresent()) {
            CampaignEntity campaign = optionalCampaign.get();
            
            // Convert DTO to entity and update fields
            CampaignEntity updatedFields = campaignDetails.toEntity();
            
            campaign.setName(updatedFields.getName());
            campaign.setStatus(updatedFields.getStatus());
            campaign.setAdvertiserId(updatedFields.getAdvertiserId());
            campaign.setBudget(updatedFields.getBudget());
            campaign.setTargeting(updatedFields.getTargeting());
            campaign.setBidding(updatedFields.getBidding());
            campaign.setCreatives(updatedFields.getCreatives());
            campaign.setFrequencyCap(updatedFields.getFrequencyCap());
            campaign.setSchedule(updatedFields.getSchedule());
            campaign.setUpdatedAt(LocalDateTime.now());
            
            CampaignEntity savedCampaign = campaignRepository.save(campaign);
            return ResponseEntity.ok(CampaignDTO.fromEntity(savedCampaign));
        }
        return ResponseEntity.notFound().build();
    }

    // 删除广告活动
    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告活动", description = "删除指定ID的广告活动")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功删除广告活动"),
        @ApiResponse(responseCode = "404", description = "广告活动不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> deleteCampaign(
            @Parameter(description = "广告活动ID", example = "campaign123") @PathVariable String id) {
        Optional<CampaignEntity> campaign = campaignRepository.findByCampaignId(id);
        if (campaign.isPresent()) {
            campaignRepository.delete(campaign.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 更新广告活动状态
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新广告活动状态", description = "更新指定广告活动的状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新广告活动状态",
                content = @Content(schema = @Schema(implementation = CampaignEntity.class))),
        @ApiResponse(responseCode = "400", description = "请求参数无效"),
        @ApiResponse(responseCode = "404", description = "广告活动不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<CampaignEntity> updateCampaignStatus(
            @Parameter(description = "广告活动ID", example = "campaign123") @PathVariable String id, 
            @Parameter(description = "状态更新信息") @RequestBody Map<String, String> statusUpdate) {
        
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
    @Operation(summary = "根据广告主ID获取广告活动", description = "获取指定广告主的所有广告活动")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取广告活动列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<CampaignEntity>> getCampaignsByAdvertiser(
            @Parameter(description = "广告主ID", example = "advertiser123") @PathVariable String advertiserId) {
        List<CampaignEntity> campaigns = campaignRepository.findByAdvertiserId(advertiserId);
        return ResponseEntity.ok(campaigns);
    }

    // 根据状态获取广告活动
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态获取广告活动", description = "获取指定状态的所有广告活动")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取广告活动列表",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<CampaignEntity>> getCampaignsByStatus(
            @Parameter(description = "广告活动状态", example = "ACTIVE") @PathVariable String status) {
        List<CampaignEntity> campaigns = campaignRepository.findByStatus(status);
        return ResponseEntity.ok(campaigns);
    }

    // 获取广告活动统计信息
    @GetMapping("/statistics")
    @Operation(summary = "获取广告活动统计信息", description = "获取广告活动的统计数据，包括总数、活跃数、暂停数、完成数")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取统计信息",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "搜索广告活动", description = "根据关键词搜索广告活动")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取搜索结果",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "搜索参数无效"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<CampaignEntity>> searchCampaigns(
            @Parameter(description = "搜索关键词", example = "summer") @RequestParam String query) {
        List<CampaignEntity> allCampaigns = campaignRepository.findAll();
        List<CampaignEntity> filteredCampaigns = allCampaigns.stream()
                .filter(campaign -> campaign.getName() != null && 
                        campaign.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(filteredCampaigns);
    }
}