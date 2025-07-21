package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.config.AdsConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * 预算管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {
    
    private final AdsConfiguration adsConfiguration;
    
    // 广告活动预算信息
    private final Map<String, CampaignBudget> campaignBudgets = new ConcurrentHashMap<>();
    
    // 预算预扣记录
    private final Map<String, BudgetReservation> budgetReservations = new ConcurrentHashMap<>();
    
    // 统计信息
    private final AtomicLong totalBudgetChecks = new AtomicLong(0);
    private final AtomicLong budgetCheckFailures = new AtomicLong(0);
    private final AtomicLong budgetReservationCount = new AtomicLong(0);
    private final AtomicLong budgetConfirmations = new AtomicLong(0);
    private final DoubleAdder totalSpent = new DoubleAdder();
    
    /**
     * 检查预算是否充足
     */
    public boolean checkBudget(String campaignId, double bidPrice) {
        totalBudgetChecks.incrementAndGet();
        
        if (!adsConfiguration.getBudget().isEnabled()) {
            return true;
        }
        
        try {
            CampaignBudget budget = getCampaignBudget(campaignId);
            
            // 检查日预算
            if (budget.getDailySpent() + bidPrice > budget.getDailyBudget()) {
                log.debug("广告活动日预算不足: campaignId={}, dailySpent={}, dailyBudget={}, bidPrice={}",
                    campaignId, budget.getDailySpent(), budget.getDailyBudget(), bidPrice);
                budgetCheckFailures.incrementAndGet();
                return false;
            }
            
            // 检查总预算
            if (budget.getTotalSpent() + bidPrice > budget.getTotalBudget()) {
                log.debug("广告活动总预算不足: campaignId={}, totalSpent={}, totalBudget={}, bidPrice={}",
                    campaignId, budget.getTotalSpent(), budget.getTotalBudget(), bidPrice);
                budgetCheckFailures.incrementAndGet();
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("预算检查异常: campaignId={}, bidPrice={}", campaignId, bidPrice, e);
            budgetCheckFailures.incrementAndGet();
            return false;
        }
    }
    
    /**
     * 预扣预算
     */
    public String reserveBudget(String campaignId, double amount, String bidId) {
        if (!adsConfiguration.getBudget().isEnabled()) {
            return bidId;
        }
        
        try {
            CampaignBudget budget = getCampaignBudget(campaignId);
            
            // 创建预扣记录
            BudgetReservation reservation = BudgetReservation.builder()
                .reservationId(bidId)
                .campaignId(campaignId)
                .amount(amount)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(
                    adsConfiguration.getBudget().getReservationTtlSeconds()))
                .build();
            
            budgetReservations.put(bidId, reservation);
            budget.addReservedAmount(amount);
            
            budgetReservationCount.incrementAndGet();
            
            log.debug("预算预扣成功: campaignId={}, amount={}, bidId={}", 
                campaignId, amount, bidId);
            
            return bidId;
            
        } catch (Exception e) {
            log.error("预算预扣异常: campaignId={}, amount={}, bidId={}", 
                campaignId, amount, bidId, e);
            return null;
        }
    }
    
    /**
     * 确认消费预算
     */
    public void confirmBudgetSpend(String bidId, double actualAmount) {
        if (!adsConfiguration.getBudget().isEnabled()) {
            return;
        }
        
        try {
            BudgetReservation reservation = budgetReservations.remove(bidId);
            if (reservation == null) {
                log.warn("未找到预算预扣记录: bidId={}", bidId);
                return;
            }
            
            CampaignBudget budget = getCampaignBudget(reservation.getCampaignId());
            
            // 释放预扣金额
            budget.removeReservedAmount(reservation.getAmount());
            
            // 增加实际消费
            budget.addSpent(actualAmount);
            totalSpent.add(actualAmount);
            
            budgetConfirmations.incrementAndGet();
            
            log.debug("预算消费确认: campaignId={}, reservedAmount={}, actualAmount={}, bidId={}",
                reservation.getCampaignId(), reservation.getAmount(), actualAmount, bidId);
            
            // 检查预算告警
            checkBudgetAlert(budget);
            
        } catch (Exception e) {
            log.error("预算消费确认异常: bidId={}, actualAmount={}", bidId, actualAmount, e);
        }
    }
    
    /**
     * 释放预算预扣
     */
    public void releaseBudgetReservation(String bidId) {
        if (!adsConfiguration.getBudget().isEnabled()) {
            return;
        }
        
        try {
            BudgetReservation reservation = budgetReservations.remove(bidId);
            if (reservation == null) {
                log.debug("未找到预算预扣记录: bidId={}", bidId);
                return;
            }
            
            CampaignBudget budget = getCampaignBudget(reservation.getCampaignId());
            budget.removeReservedAmount(reservation.getAmount());
            
            log.debug("预算预扣释放: campaignId={}, amount={}, bidId={}",
                reservation.getCampaignId(), reservation.getAmount(), bidId);
            
        } catch (Exception e) {
            log.error("预算预扣释放异常: bidId={}", bidId, e);
        }
    }
    
    /**
     * 设置广告活动预算
     */
    public void setCampaignBudget(String campaignId, double dailyBudget, double totalBudget) {
        CampaignBudget budget = campaignBudgets.computeIfAbsent(campaignId, 
            k -> new CampaignBudget(campaignId));
        
        budget.setDailyBudget(dailyBudget);
        budget.setTotalBudget(totalBudget);
        
        log.info("设置广告活动预算: campaignId={}, dailyBudget={}, totalBudget={}",
            campaignId, dailyBudget, totalBudget);
    }
    
    /**
     * 获取广告活动预算信息
     */
    public CampaignBudget getCampaignBudget(String campaignId) {
        return campaignBudgets.computeIfAbsent(campaignId, k -> {
            CampaignBudget budget = new CampaignBudget(campaignId);
            budget.setDailyBudget(adsConfiguration.getBudget().getDefaultDailyBudget());
            budget.setTotalBudget(adsConfiguration.getBudget().getDefaultDailyBudget() * 30);
            return budget;
        });
    }
    
    /**
     * 清理过期的预算预扣
     */
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        int cleanedCount = 0;
        
        for (Map.Entry<String, BudgetReservation> entry : budgetReservations.entrySet()) {
            BudgetReservation reservation = entry.getValue();
            if (reservation.getExpiresAt().isBefore(now)) {
                releaseBudgetReservation(entry.getKey());
                cleanedCount++;
            }
        }
        
        if (cleanedCount > 0) {
            log.info("清理过期预算预扣: count={}", cleanedCount);
        }
    }
    
    /**
     * 检查预算告警
     */
    private void checkBudgetAlert(CampaignBudget budget) {
        double dailyUsageRate = budget.getDailySpent() / budget.getDailyBudget();
        double totalUsageRate = budget.getTotalSpent() / budget.getTotalBudget();
        double alertThreshold = adsConfiguration.getBudget().getAlertThreshold();
        
        if (dailyUsageRate >= alertThreshold) {
            log.warn("广告活动日预算告警: campaignId={}, usageRate={:.2f}%, threshold={:.2f}%",
                budget.getCampaignId(), dailyUsageRate * 100, alertThreshold * 100);
        }
        
        if (totalUsageRate >= alertThreshold) {
            log.warn("广告活动总预算告警: campaignId={}, usageRate={:.2f}%, threshold={:.2f}%",
                budget.getCampaignId(), totalUsageRate * 100, alertThreshold * 100);
        }
    }
    
    /**
     * 获取预算统计信息
     */
    public Map<String, Object> getBudgetStatistics() {
        return Map.of(
            "totalBudgetChecks", totalBudgetChecks.get(),
            "budgetCheckFailures", budgetCheckFailures.get(),
            "budgetReservations", budgetReservationCount.get(),
            "budgetConfirmations", budgetConfirmations.get(),
            "totalSpent", totalSpent.sum(),
            "activeCampaigns", campaignBudgets.size(),
            "activeReservations", budgetReservations.size()
        );
    }
    
    /**
     * 广告活动预算信息
     */
    public static class CampaignBudget {
        private final String campaignId;
        private volatile double dailyBudget;
        private volatile double totalBudget;
        private final DoubleAdder dailySpent = new DoubleAdder();
        private final DoubleAdder totalSpent = new DoubleAdder();
        private final DoubleAdder reservedAmount = new DoubleAdder();
        private volatile LocalDate lastResetDate = LocalDate.now();
        
        public CampaignBudget(String campaignId) {
            this.campaignId = campaignId;
        }
        
        public String getCampaignId() { return campaignId; }
        public double getDailyBudget() { return dailyBudget; }
        public void setDailyBudget(double dailyBudget) { this.dailyBudget = dailyBudget; }
        public double getTotalBudget() { return totalBudget; }
        public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
        
        public double getDailySpent() {
            resetDailySpentIfNeeded();
            return dailySpent.sum();
        }
        
        public double getTotalSpent() { return totalSpent.sum(); }
        public double getReservedAmount() { return reservedAmount.sum(); }
        
        public void addSpent(double amount) {
            resetDailySpentIfNeeded();
            dailySpent.add(amount);
            totalSpent.add(amount);
        }
        
        public void addReservedAmount(double amount) {
            reservedAmount.add(amount);
        }
        
        public void removeReservedAmount(double amount) {
            reservedAmount.add(-amount);
        }
        
        private void resetDailySpentIfNeeded() {
            LocalDate today = LocalDate.now();
            if (!today.equals(lastResetDate)) {
                dailySpent.reset();
                lastResetDate = today;
            }
        }
    }
    
    /**
     * 预算预扣记录
     */
    public static class BudgetReservation {
        private final String reservationId;
        private final String campaignId;
        private final double amount;
        private final LocalDateTime createdAt;
        private final LocalDateTime expiresAt;
        
        private BudgetReservation(Builder builder) {
            this.reservationId = builder.reservationId;
            this.campaignId = builder.campaignId;
            this.amount = builder.amount;
            this.createdAt = builder.createdAt;
            this.expiresAt = builder.expiresAt;
        }
        
        public static Builder builder() { return new Builder(); }
        
        public String getReservationId() { return reservationId; }
        public String getCampaignId() { return campaignId; }
        public double getAmount() { return amount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        
        public static class Builder {
            private String reservationId;
            private String campaignId;
            private double amount;
            private LocalDateTime createdAt;
            private LocalDateTime expiresAt;
            
            public Builder reservationId(String reservationId) {
                this.reservationId = reservationId;
                return this;
            }
            
            public Builder campaignId(String campaignId) {
                this.campaignId = campaignId;
                return this;
            }
            
            public Builder amount(double amount) {
                this.amount = amount;
                return this;
            }
            
            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }
            
            public Builder expiresAt(LocalDateTime expiresAt) {
                this.expiresAt = expiresAt;
                return this;
            }
            
            public BudgetReservation build() {
                return new BudgetReservation(this);
            }
        }
    }
}