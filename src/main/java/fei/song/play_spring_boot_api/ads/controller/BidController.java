package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.domain.model.BidRequest;
import fei.song.play_spring_boot_api.ads.domain.model.BidResponse;
import fei.song.play_spring_boot_api.ads.service.BidServer;
import fei.song.play_spring_boot_api.ads.service.BidRequestMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * OpenRTB 竞价 API 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bid")
@RequiredArgsConstructor
@Tag(name = "OpenRTB Bidding", description = "OpenRTB竞价相关API")
public class BidController {
    
    private final BidServer bidServer;
    private final BidRequestMetricsService metricsService;
    
    /**
     * 处理竞价请求
     */
    @PostMapping(value = "/request", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "处理竞价请求", description = "处理OpenRTB竞价请求并返回竞价响应")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功处理竞价请求",
                content = @Content(schema = @Schema(implementation = BidResponse.class))),
        @ApiResponse(responseCode = "400", description = "竞价请求格式无效"),
        @ApiResponse(responseCode = "204", description = "无竞价"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<BidResponse> handleBidRequest(
            @Parameter(description = "OpenRTB竞价请求") @RequestBody BidRequest bidRequest,
            HttpServletRequest httpRequest) {
        
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIpAddress(httpRequest);
        
        try {
            log.info("收到竞价请求: requestId={}, ip={}, impressions={}", 
                bidRequest.getId(), clientIp, 
                bidRequest.getImp() != null ? bidRequest.getImp().size() : 0);
            
            // 基本请求验证
            if (bidRequest.getId() == null || bidRequest.getId().trim().isEmpty()) {
                log.warn("竞价请求ID为空: ip={}", clientIp);
                return ResponseEntity.badRequest().build();
            }
            
            if (bidRequest.getImp() == null || bidRequest.getImp().isEmpty()) {
                log.warn("竞价请求没有广告位: requestId={}, ip={}", bidRequest.getId(), clientIp);
                return ResponseEntity.badRequest().build();
            }
            
            // 设置客户端IP到设备信息中（如果设备信息存在但没有IP）
            if (bidRequest.getDevice() != null && bidRequest.getDevice().getIp() == null) {
                bidRequest.getDevice().setIp(clientIp);
            }
            
            // 处理竞价请求
            BidResponse response = bidServer.processBidRequest(bidRequest);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            // 记录统计数据
            String adSlotType = extractAdSlotType(bidRequest);
            String dspSource = extractDspSource(httpRequest);
            boolean success = response.getSeatbid() != null && !response.getSeatbid().isEmpty();
            
            try {
                metricsService.recordBidRequest(adSlotType, dspSource, success, processingTime);
            } catch (Exception e) {
                log.warn("记录统计数据失败: requestId={}", bidRequest.getId(), e);
            }
            
            if (success) {
                log.info("竞价成功: requestId={}, bids={}, processingTime={}ms", 
                    bidRequest.getId(), response.getSeatbid().size(), processingTime);
                return ResponseEntity.ok(response);
            } else {
                log.info("无竞价: requestId={}, reason={}, processingTime={}ms", 
                    bidRequest.getId(), response.getNbr(), processingTime);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            
        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("处理竞价请求异常: requestId={}, ip={}, processingTime={}ms", 
                bidRequest.getId(), clientIp, processingTime, e);
            
            // 记录失败统计
            try {
                String adSlotType = extractAdSlotType(bidRequest);
                String dspSource = extractDspSource(httpRequest);
                metricsService.recordBidRequest(adSlotType, dspSource, false, processingTime);
            } catch (Exception ex) {
                log.warn("记录失败统计数据异常: requestId={}", bidRequest.getId(), ex);
            }
            
            // 返回无竞价响应
            BidResponse errorResponse = BidResponse.builder()
                .id(bidRequest.getId())
                .nbr(1) // 技术错误
                .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 处理获胜通知
     */
    @PostMapping("/win/{bidId}")
    @Operation(summary = "处理获胜通知", description = "处理竞价获胜通知")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功处理获胜通知"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> handleWinNotification(
            @Parameter(description = "竞价ID") @PathVariable String bidId,
            @Parameter(description = "获胜价格") @RequestParam(required = false) Double winPrice,
            HttpServletRequest httpRequest) {
        
        String clientIp = getClientIpAddress(httpRequest);
        
        try {
            log.info("收到获胜通知: bidId={}, winPrice={}, ip={}", bidId, winPrice, clientIp);
            
            bidServer.handleWinNotification(bidId, winPrice);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("处理获胜通知异常: bidId={}, ip={}", bidId, clientIp, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 处理损失通知
     */
    @PostMapping("/loss/{bidId}")
    @Operation(summary = "处理损失通知", description = "处理竞价损失通知")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功处理损失通知"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Void> handleLossNotification(
            @Parameter(description = "竞价ID") @PathVariable String bidId,
            @Parameter(description = "获胜价格") @RequestParam(required = false) Double winPrice,
            @Parameter(description = "损失原因") @RequestParam(required = false) Integer lossReason,
            HttpServletRequest httpRequest) {
        
        String clientIp = getClientIpAddress(httpRequest);
        
        try {
            log.info("收到损失通知: bidId={}, winPrice={}, reason={}, ip={}", 
                bidId, winPrice, lossReason, clientIp);
            
            bidServer.handleLossNotification(bidId, winPrice, lossReason);
            
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("处理损失通知异常: bidId={}, ip={}", bidId, clientIp, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取服务器状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取服务器状态", description = "获取竞价服务器的运行状态信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取服务器状态",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        try {
            Map<String, Object> status = bidServer.getServerStatistics();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取服务器状态异常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查竞价服务器的健康状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "服务器健康",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "503", description = "服务不可用")
    })
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "OpenRTB Bid Server",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
        return ResponseEntity.ok(health);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 提取广告位类型
     */
    private String extractAdSlotType(BidRequest bidRequest) {
        if (bidRequest.getImp() != null && !bidRequest.getImp().isEmpty()) {
            // 根据第一个impression的类型判断广告位类型
            var imp = bidRequest.getImp().get(0);
            if (imp.getBanner() != null) {
                return "banner";
            } else if (imp.getVideo() != null) {
                return "video";
            } else if (imp.getNativeAd() != null) {
                return "native";
            } else if (imp.getAudio() != null) {
                return "audio";
            }
        }
        return "unknown";
    }
    
    /**
     * 提取DSP来源
     */
    private String extractDspSource(HttpServletRequest request) {
        // 从User-Agent或自定义头部提取DSP来源
        String userAgent = request.getHeader("User-Agent");
        String dspHeader = request.getHeader("X-DSP-Source");
        
        if (dspHeader != null && !dspHeader.isEmpty()) {
            return dspHeader.toLowerCase();
        }
        
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.contains("google")) {
                return "google";
            } else if (userAgent.contains("facebook")) {
                return "facebook";
            } else if (userAgent.contains("amazon")) {
                return "amazon";
            } else if (userAgent.contains("microsoft")) {
                return "microsoft";
            }
        }
        
        return "unknown";
    }
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        log.error("API异常", e);
        
        Map<String, String> error = Map.of(
            "error", "Internal Server Error",
            "message", e.getMessage() != null ? e.getMessage() : "Unknown error",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}