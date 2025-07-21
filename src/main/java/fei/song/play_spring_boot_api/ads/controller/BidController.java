package fei.song.play_spring_boot_api.ads.controller;

import fei.song.play_spring_boot_api.ads.domain.model.BidRequest;
import fei.song.play_spring_boot_api.ads.domain.model.BidResponse;
import fei.song.play_spring_boot_api.ads.service.BidServer;
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
public class BidController {
    
    private final BidServer bidServer;
    
    /**
     * 处理竞价请求
     */
    @PostMapping(value = "/request", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BidResponse> handleBidRequest(
            @RequestBody BidRequest bidRequest,
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
            
            if (response.getSeatbid() != null && !response.getSeatbid().isEmpty()) {
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
    public ResponseEntity<Void> handleWinNotification(
            @PathVariable String bidId,
            @RequestParam(required = false) Double winPrice,
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
    public ResponseEntity<Void> handleLossNotification(
            @PathVariable String bidId,
            @RequestParam(required = false) Double winPrice,
            @RequestParam(required = false) Integer lossReason,
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