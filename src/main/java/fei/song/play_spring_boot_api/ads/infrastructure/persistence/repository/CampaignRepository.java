package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.CampaignEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 广告活动仓储接口
 */
@Repository
public interface CampaignRepository extends MongoRepository<CampaignEntity, String> {

    /**
     * 根据活动ID查找广告活动
     */
    Optional<CampaignEntity> findByCampaignId(String campaignId);

    /**
     * 根据广告主ID查找广告活动
     */
    List<CampaignEntity> findByAdvertiserId(String advertiserId);

    /**
     * 根据状态查找广告活动
     */
    List<CampaignEntity> findByStatus(String status);

    /**
     * 查找活跃的广告活动
     */
    @Query("{'status': 'active', 'schedule.start_date': {$lte: ?0}, 'schedule.end_date': {$gte: ?0}}")
    List<CampaignEntity> findActiveCampaigns(LocalDateTime currentTime);

    /**
     * 根据预算范围查找广告活动
     */
    @Query("{'budget.total_budget': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findByBudgetRange(BigDecimal minBudget, BigDecimal maxBudget);

    /**
     * 查找预算即将耗尽的活动
     */
    @Query("{'budget.spent_total': {$gte: {$multiply: ['$budget.total_budget', ?0]}}}")
    List<CampaignEntity> findCampaignsNearBudgetLimit(double threshold);

    /**
     * 根据地理定向查找活动
     */
    @Query("{'targeting.geo.included_countries': {$in: [?0]}}")
    List<CampaignEntity> findByTargetCountry(String country);

    /**
     * 根据设备类型定向查找活动
     */
    @Query("{'targeting.device.device_types': {$in: [?0]}}")
    List<CampaignEntity> findByTargetDeviceType(Integer deviceType);

    /**
     * 根据竞价策略查找活动
     */
    @Query("{'bidding.bid_strategy': ?0}")
    List<CampaignEntity> findByBidStrategy(String bidStrategy);

    /**
     * 查找最大竞价高于指定值的活动
     */
    @Query("{'bidding.max_bid': {$gte: ?0}}")
    List<CampaignEntity> findByMaxBidGreaterThanEqual(BigDecimal minBid);

    /**
     * 根据创建者查找活动
     */
    List<CampaignEntity> findByCreatedBy(String createdBy);

    /**
     * 查找即将开始的活动
     */
    @Query("{'schedule.start_date': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findCampaignsStartingSoon(LocalDateTime start, LocalDateTime end);

    /**
     * 查找即将结束的活动
     */
    @Query("{'schedule.end_date': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findCampaignsEndingSoon(LocalDateTime start, LocalDateTime end);

    /**
     * 统计指定广告主的活动数量
     */
    long countByAdvertiserId(String advertiserId);

    /**
     * 统计指定状态的活动数量
     */
    long countByStatus(String status);

    /**
     * 查找需要更新预算统计的活动
     */
    @Query("{'budget.spent_today': {$exists: false}}")
    List<CampaignEntity> findCampaignsNeedingBudgetUpdate();
}