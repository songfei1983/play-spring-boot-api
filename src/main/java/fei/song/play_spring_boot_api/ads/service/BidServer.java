package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenRTB 竞价服务器核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BidServer {
    
    private final FraudDetectionService fraudDetectionService;
    private final AdSlotFilterService adSlotFilterService;
    private final BiddingAlgorithm biddingAlgorithm;
    private final BudgetService budgetService;
    
    /**
     * 处理竞价请求
     */
    public BidResponse processBidRequest(BidRequest bidRequest) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始处理竞价请求: requestId={}, impressions={}", 
                bidRequest.getId(), bidRequest.getImp().size());
            
            // 1. 反欺诈检测
            if (fraudDetectionService.isFraudulent(bidRequest)) {
                log.warn("竞价请求被反欺诈系统拒绝: requestId={}", bidRequest.getId());
                return createNoBidResponse(bidRequest.getId(), 2); // 欺诈流量
            }
            
            // 2. 为每个广告位生成竞价
            List<BidResponse.SeatBid> seatBids = new ArrayList<>();
            
            for (Impression impression : bidRequest.getImp()) {
                BidResponse.Bid winningBid = processSingleImpression(impression, bidRequest);
                if (winningBid != null) {
                    // 检查预算
                    if (budgetService.checkBudget(winningBid.getCid(), winningBid.getPrice())) {
                        // 预扣预算
                        String reservationId = budgetService.reserveBudget(
                            winningBid.getCid(), 
                            winningBid.getPrice(), 
                            winningBid.getId());
                        
                        if (reservationId != null) {
                            // 创建座位竞价
                            BidResponse.SeatBid seatBid = BidResponse.SeatBid.builder()
                                .bid(Arrays.asList(winningBid))
                                .seat("seat_1") // 我们的座位ID
                                .group(0) // 非组竞价
                                .build();
                            
                            seatBids.add(seatBid);
                        } else {
                            log.debug("预算预扣失败: campaignId={}, bidPrice={}", 
                                winningBid.getCid(), winningBid.getPrice());
                        }
                    } else {
                        log.debug("预算不足: campaignId={}, bidPrice={}", 
                            winningBid.getCid(), winningBid.getPrice());
                    }
                }
            }
            
            // 3. 构建竞价响应
            if (seatBids.isEmpty()) {
                log.info("没有有效竞价: requestId={}", bidRequest.getId());
                return createNoBidResponse(bidRequest.getId(), 0); // 未知原因
            }
            
            BidResponse response = BidResponse.builder()
                .id(bidRequest.getId())
                .seatbid(seatBids)
                .bidid("bid_" + UUID.randomUUID().toString())
                .cur("USD") // 默认货币
                .build();
            
            long processingTime = System.currentTimeMillis() - startTime;
            log.info("竞价请求处理完成: requestId={}, bids={}, processingTime={}ms", 
                bidRequest.getId(), seatBids.size(), processingTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("处理竞价请求异常: requestId={}", bidRequest.getId(), e);
            return createNoBidResponse(bidRequest.getId(), 1); // 技术错误
        }
    }
    
    /**
     * 处理单个广告位
     */
    private BidResponse.Bid processSingleImpression(Impression impression, BidRequest bidRequest) {
        try {
            log.debug("处理广告位: impressionId={}, bidfloor={}", 
                impression.getId(), impression.getBidfloor());
            
            // 1. 生成候选广告
            List<BidCandidate> candidates = biddingAlgorithm.generateBidCandidates(impression, bidRequest);
            if (candidates.isEmpty()) {
                log.debug("没有候选广告: impressionId={}", impression.getId());
                return null;
            }
            
            // 2. 过滤候选广告
            List<BidCandidate> filteredCandidates = adSlotFilterService
                .filterCandidatesForImpression(impression, bidRequest, candidates);
            
            if (filteredCandidates.isEmpty()) {
                log.debug("所有候选广告被过滤: impressionId={}", impression.getId());
                return null;
            }
            
            // 3. 预扣预算检查
            filteredCandidates = filteredCandidates.stream()
                .filter(this::checkAndReserveBudget)
                .collect(Collectors.toList());
            
            if (filteredCandidates.isEmpty()) {
                log.debug("预算不足，无法竞价: impressionId={}", impression.getId());
                return null;
            }
            
            // 4. 排序并选择获胜者
            List<BidCandidate> sortedCandidates = biddingAlgorithm.sortCandidates(filteredCandidates);
            BidCandidate winner = biddingAlgorithm.selectWinningBid(sortedCandidates, impression);
            
            if (winner == null) {
                log.debug("没有获胜竞价: impressionId={}", impression.getId());
                return null;
            }
            
            // 5. 构建竞价响应
            return buildBidResponse(winner, impression);
            
        } catch (Exception e) {
            log.error("处理广告位异常: impressionId={}", impression.getId(), e);
            return null;
        }
    }
    
    /**
     * 检查并预扣预算
     */
    private boolean checkAndReserveBudget(BidCandidate candidate) {
        // 这里应该调用预算管理服务
        // 简化实现：模拟预算检查
        double availableBudget = 1000.0; // 模拟可用预算
        boolean hasEnoughBudget = availableBudget >= candidate.getBidPrice();
        
        candidate.setBudgetAvailable(hasEnoughBudget);
        
        if (hasEnoughBudget) {
            log.debug("预算预扣成功: adId={}, price={}", candidate.getAdId(), candidate.getBidPrice());
        } else {
            log.debug("预算不足: adId={}, price={}, available={}", 
                candidate.getAdId(), candidate.getBidPrice(), availableBudget);
        }
        
        return hasEnoughBudget;
    }
    
    /**
     * 构建竞价响应
     */
    private BidResponse.Bid buildBidResponse(BidCandidate candidate, Impression impression) {
        return BidResponse.Bid.builder()
            .id(candidate.getAdId())
            .impid(impression.getId())
            .price(candidate.getBidPrice())
            .adid(candidate.getAdId())
            .nurl(candidate.getNotificationUrl())
            .adm(candidate.getAdMarkup())
            .adomain(candidate.getAdvertiserDomains())
            .cid(candidate.getCampaignId())
            .crid(candidate.getCreativeId())
            .dealid(candidate.getDealId())
            .w(candidate.getWidth())
            .h(candidate.getHeight())
            .cat(candidate.getCategories())
            .exp(3600) // 1小时过期
            .build();
    }
    
    /**
     * 创建无竞价响应
     */
    private BidResponse createNoBidResponse(String requestId, int reason) {
        return BidResponse.builder()
            .id(requestId)
            .seatbid(Collections.emptyList())
            .nbr(reason)
            .build();
    }
    
    /**
     * 处理获胜通知
     */
    public void handleWinNotification(String bidId, Double winPrice) {
        try {
            log.info("收到获胜通知: bidId={}, winPrice={}", bidId, winPrice);
            
            // 1. 确认预算扣除
            if (winPrice != null) {
                budgetService.confirmBudgetSpend(bidId, winPrice);
            }
            confirmBudgetDeduction(bidId, winPrice);
            
            // 2. 记录获胜日志
            recordWinEvent(bidId, winPrice);
            
            // 3. 触发后续处理（如统计更新、报告生成等）
            triggerPostWinProcessing(bidId, winPrice);
            
        } catch (Exception e) {
            log.error("处理获胜通知异常: bidId={}", bidId, e);
        }
    }
    
    /**
     * 处理损失通知
     */
    public void handleLossNotification(String bidId, Double winPrice, Integer lossReason) {
        try {
            log.info("收到损失通知: bidId={}, winPrice={}, reason={}", bidId, winPrice, lossReason);
            
            // 1. 释放预扣预算
            budgetService.releaseBudgetReservation(bidId);
            releaseBudgetReservation(bidId);
            
            // 2. 记录损失日志
            recordLossEvent(bidId, winPrice, lossReason);
            
        } catch (Exception e) {
            log.error("处理损失通知异常: bidId={}", bidId, e);
        }
    }
    
    /**
     * 确认预算扣除
     */
    private void confirmBudgetDeduction(String bidId, Double winPrice) {
        // 调用预算管理服务确认扣除
        log.debug("确认预算扣除: bidId={}, amount={}", bidId, winPrice);
    }
    
    /**
     * 释放预算预扣
     */
    private void releaseBudgetReservation(String bidId) {
        // 调用预算管理服务释放预扣
        log.debug("释放预算预扣: bidId={}", bidId);
    }
    
    /**
     * 记录获胜事件
     */
    private void recordWinEvent(String bidId, Double winPrice) {
        // 记录到数据库或发送到数据管道
        log.debug("记录获胜事件: bidId={}, winPrice={}", bidId, winPrice);
    }
    
    /**
     * 记录损失事件
     */
    private void recordLossEvent(String bidId, Double winPrice, Integer lossReason) {
        // 记录到数据库或发送到数据管道
        log.debug("记录损失事件: bidId={}, winPrice={}, reason={}", bidId, winPrice, lossReason);
    }
    
    /**
     * 触发获胜后处理
     */
    private void triggerPostWinProcessing(String bidId, Double winPrice) {
        // 触发统计更新、报告生成等
        log.debug("触发获胜后处理: bidId={}", bidId);
    }
    
    /**
     * 获取服务器统计信息
     */
    public Map<String, Object> getServerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("serverStatus", "running");
        stats.put("fraudDetection", fraudDetectionService.getFraudStatistics());
        stats.put("filtering", adSlotFilterService.getFilterStatistics());
        stats.put("bidding", biddingAlgorithm.getBiddingStatistics());
        stats.put("budgetStats", budgetService.getBudgetStatistics());
        return stats;
    }
}