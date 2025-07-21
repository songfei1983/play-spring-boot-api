package fei.song.play_spring_boot_api.ads.scheduler;

import fei.song.play_spring_boot_api.ads.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 预算清理定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BudgetCleanupScheduler {
    
    private final BudgetService budgetService;
    
    /**
     * 每5分钟清理一次过期的预算预扣记录
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void cleanupExpiredReservations() {
        try {
            log.debug("开始清理过期预算预扣记录");
            budgetService.cleanupExpiredReservations();
            log.debug("完成清理过期预算预扣记录");
        } catch (Exception e) {
            log.error("清理过期预算预扣记录异常", e);
        }
    }
}