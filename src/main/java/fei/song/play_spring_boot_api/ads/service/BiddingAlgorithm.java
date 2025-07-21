package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 竞价算法服务
 */
@Slf4j
@Service
public class BiddingAlgorithm {
    
    // 算法权重配置
    private static final double USER_VALUE_WEIGHT = 0.3;
    private static final double CONTEXT_RELEVANCE_WEIGHT = 0.25;
    private static final double COMPETITION_WEIGHT = 0.2;
    private static final double QUALITY_WEIGHT = 0.25;
    
    /**
     * 为广告位生成竞价候选
     */
    public List<BidCandidate> generateBidCandidates(Impression impression, BidRequest bidRequest) {
        List<BidCandidate> candidates = new ArrayList<>();
        
        // 模拟从广告库中获取候选广告
        // 实际实现中这里会查询广告数据库
        candidates.addAll(getMockAdCandidates(impression, bidRequest));
        
        // 计算每个候选的竞价价格和分数
        for (BidCandidate candidate : candidates) {
            calculateBidPrice(candidate, bidRequest);
            calculateQualityScore(candidate, bidRequest);
            calculateFinalScore(candidate);
        }
        
        return candidates;
    }
    
    /**
     * 对竞价候选进行排序
     */
    public List<BidCandidate> sortCandidates(List<BidCandidate> candidates) {
        return candidates.stream()
            .sorted((c1, c2) -> {
                // 首先按优先级排序
                int priorityCompare = Integer.compare(c2.getPriority(), c1.getPriority());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }
                
                // 然后按最终分数排序
                int scoreCompare = Double.compare(c2.getFinalScore(), c1.getFinalScore());
                if (scoreCompare != 0) {
                    return scoreCompare;
                }
                
                // 最后按竞价价格排序
                return Double.compare(c2.getBidPrice(), c1.getBidPrice());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 选择获胜竞价（第二价格拍卖）
     */
    public BidCandidate selectWinningBid(List<BidCandidate> sortedCandidates, Impression impression) {
        if (sortedCandidates.isEmpty()) {
            return null;
        }
        
        BidCandidate winner = sortedCandidates.get(0);
        
        // 实施第二价格拍卖
        if (sortedCandidates.size() > 1) {
            BidCandidate secondPlace = sortedCandidates.get(1);
            double secondPrice = Math.max(secondPlace.getBidPrice(), 
                impression.getBidfloor() != null ? impression.getBidfloor() : 0.0);
            
            // 设置获胜价格为第二高价格 + 0.01
            winner.setBidPrice(secondPrice + 0.01);
        } else {
            // 只有一个竞价者，使用底价
            if (impression.getBidfloor() != null && impression.getBidfloor() > 0) {
                winner.setBidPrice(Math.max(winner.getBidPrice(), impression.getBidfloor()));
            }
        }
        
        log.info("选择获胜竞价: adId={}, finalPrice={}, originalBid={}", 
            winner.getAdId(), winner.getBidPrice(), 
            sortedCandidates.get(0).getBidPrice());
        
        return winner;
    }
    
    /**
     * 计算竞价价格
     */
    private void calculateBidPrice(BidCandidate candidate, BidRequest bidRequest) {
        double baseBid = getBaseBidPrice(candidate);
        
        // 用户价值调整
        double userValueMultiplier = calculateUserValueMultiplier(bidRequest);
        
        // 上下文相关性调整
        double contextMultiplier = calculateContextRelevanceMultiplier(candidate, bidRequest);
        
        // 竞争强度调整
        double competitionMultiplier = calculateCompetitionMultiplier(bidRequest);
        
        double finalBid = baseBid * userValueMultiplier * contextMultiplier * competitionMultiplier;
        
        candidate.setBidPrice(finalBid);
        candidate.setUserValueScore(userValueMultiplier);
        candidate.setContextRelevanceScore(contextMultiplier);
        candidate.setCompetitionScore(competitionMultiplier);
    }
    
    /**
     * 获取基础竞价价格
     */
    private double getBaseBidPrice(BidCandidate candidate) {
        // 根据广告活动设置返回基础价格
        // 这里使用模拟数据
        return 1.0 + Math.random() * 4.0; // 1.0 - 5.0 之间的随机价格
    }
    
    /**
     * 计算用户价值倍数
     */
    private double calculateUserValueMultiplier(BidRequest bidRequest) {
        double multiplier = 1.0;
        
        if (bidRequest.getUser() != null) {
            User user = bidRequest.getUser();
            
            // 基于用户年龄调整
            if (user.getYob() != null) {
                int age = 2024 - user.getYob();
                if (age >= 25 && age <= 45) {
                    multiplier += 0.2; // 核心消费群体
                }
            }
            
            // 基于用户性别调整
            if (user.getGender() != null) {
                multiplier += 0.1; // 有性别信息的用户更有价值
            }
            
            // 基于用户兴趣关键词调整
            if (user.getKeywords() != null && !user.getKeywords().trim().isEmpty()) {
                multiplier += 0.15; // 有兴趣标签的用户更有价值
            }
        }
        
        return Math.min(multiplier, 2.0); // 最大不超过2倍
    }
    
    /**
     * 计算上下文相关性倍数
     */
    private double calculateContextRelevanceMultiplier(BidCandidate candidate, BidRequest bidRequest) {
        double multiplier = 1.0;
        
        // 基于网站类别匹配
        if (bidRequest.getSite() != null && bidRequest.getSite().getCat() != null) {
            if (candidate.getCategories() != null) {
                boolean hasMatch = candidate.getCategories().stream()
                    .anyMatch(adCat -> bidRequest.getSite().getCat().contains(adCat));
                if (hasMatch) {
                    multiplier += 0.3;
                }
            }
        }
        
        // 基于设备类型匹配
        if (bidRequest.getDevice() != null && bidRequest.getDevice().getDevicetype() != null) {
            // 移动设备上的移动优化广告
            if (bidRequest.getDevice().getDevicetype() == 1 || bidRequest.getDevice().getDevicetype() == 4) {
                multiplier += 0.2;
            }
        }
        
        return Math.min(multiplier, 1.8);
    }
    
    /**
     * 计算竞争强度倍数
     */
    private double calculateCompetitionMultiplier(BidRequest bidRequest) {
        // 基于时间段的竞争强度
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 19 && hour <= 23) {
            return 1.3; // 黄金时段竞争激烈
        } else if (hour >= 9 && hour <= 18) {
            return 1.1; // 工作时间适中竞争
        } else {
            return 0.9; // 其他时间竞争较少
        }
    }
    
    /**
     * 计算质量分数
     */
    private void calculateQualityScore(BidCandidate candidate, BidRequest bidRequest) {
        double qualityScore = 0.0;
        
        // 创意质量评分
        if (candidate.getAdMarkup() != null && !candidate.getAdMarkup().trim().isEmpty()) {
            qualityScore += 0.3;
        }
        
        // 广告主域名信誉评分
        if (candidate.getAdvertiserDomains() != null && !candidate.getAdvertiserDomains().isEmpty()) {
            qualityScore += 0.2;
        }
        
        // 点击率预估（模拟）
        double estimatedCTR = 0.02 + Math.random() * 0.08; // 2%-10%
        qualityScore += estimatedCTR * 5; // CTR权重
        
        // 转化率预估（模拟）
        double estimatedCVR = 0.01 + Math.random() * 0.04; // 1%-5%
        qualityScore += estimatedCVR * 10; // CVR权重
        
        candidate.setQualityScore(Math.min(qualityScore, 1.0));
    }
    
    /**
     * 计算最终分数
     */
    private void calculateFinalScore(BidCandidate candidate) {
        double finalScore = 
            candidate.getUserValueScore() * USER_VALUE_WEIGHT +
            candidate.getContextRelevanceScore() * CONTEXT_RELEVANCE_WEIGHT +
            candidate.getCompetitionScore() * COMPETITION_WEIGHT +
            candidate.getQualityScore() * QUALITY_WEIGHT;
        
        candidate.setFinalScore(finalScore);
    }
    
    /**
     * 获取模拟广告候选
     */
    private List<BidCandidate> getMockAdCandidates(Impression impression, BidRequest bidRequest) {
        List<BidCandidate> candidates = new ArrayList<>();
        
        // 生成3-8个模拟候选广告
        int candidateCount = 3 + (int)(Math.random() * 6);
        
        for (int i = 0; i < candidateCount; i++) {
            BidCandidate candidate = BidCandidate.builder()
                .impressionId(impression.getId())
                .adId("ad_" + UUID.randomUUID().toString().substring(0, 8))
                .creativeId("creative_" + UUID.randomUUID().toString().substring(0, 8))
                .campaignId("campaign_" + (i % 3 + 1))
                .priority(1 + (int)(Math.random() * 5))
                .adMarkup("<div>广告内容 " + (i + 1) + "</div>")
                .advertiserDomains(Arrays.asList("advertiser" + (i % 3 + 1) + ".com"))
                .categories(Arrays.asList("IAB1", "IAB2"))
                .width(impression.getBanner() != null ? impression.getBanner().getW() : 300)
                .height(impression.getBanner() != null ? impression.getBanner().getH() : 250)
                .clickUrl("https://advertiser" + (i % 3 + 1) + ".com/click")
                .impressionUrl("https://advertiser" + (i % 3 + 1) + ".com/impression")
                .notificationUrl("https://advertiser" + (i % 3 + 1) + ".com/win")
                .seatId("seat_" + (i % 2 + 1))
                .passedFraudCheck(true)
                .budgetAvailable(true)
                .targetingMatched(true)
                .build();
            
            candidates.add(candidate);
        }
        
        return candidates;
    }
    
    /**
     * 获取竞价算法统计信息
     */
    public Map<String, Object> getBiddingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userValueWeight", USER_VALUE_WEIGHT);
        stats.put("contextRelevanceWeight", CONTEXT_RELEVANCE_WEIGHT);
        stats.put("competitionWeight", COMPETITION_WEIGHT);
        stats.put("qualityWeight", QUALITY_WEIGHT);
        stats.put("auctionType", "second_price");
        return stats;
    }
}