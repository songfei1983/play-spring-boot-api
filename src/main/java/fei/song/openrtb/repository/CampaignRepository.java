package fei.song.openrtb.repository;

import fei.song.openrtb.entity.CampaignEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 广告活动仓储接口
 * 提供对 CampaignEntity 的数据访问操作
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
     * 根据广告主ID和状态查找广告活动
     */
    List<CampaignEntity> findByAdvertiserIdAndStatus(String advertiserId, String status);

    /**
     * 查找活跃的广告活动
     */
    @Query("{'status': 'ACTIVE'}")
    List<CampaignEntity> findActiveCampaigns();

    /**
     * 查找暂停的广告活动
     */
    @Query("{'status': 'PAUSED'}")
    List<CampaignEntity> findPausedCampaigns();

    /**
     * 根据预算范围查找广告活动
     */
    @Query("{'budget.dailyBudget': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findByDailyBudgetBetween(Long minBudget, Long maxBudget);

    /**
     * 根据总预算范围查找广告活动
     */
    @Query("{'budget.totalBudget': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findByTotalBudgetBetween(Long minBudget, Long maxBudget);

    /**
     * 查找预算即将耗尽的活动（已花费预算超过总预算的80%）
     */
    @Query("{'$expr': {'$gt': ['$budget.spentBudget', {'$multiply': ['$budget.totalBudget', 0.8]}]}}")
    List<CampaignEntity> findCampaignsNearBudgetLimit();

    /**
     * 根据地理定向查找广告活动
     */
    @Query("{'targeting.geo.countries': {$in: ?0}}")
    List<CampaignEntity> findByTargetingCountries(List<String> countries);

    /**
     * 根据设备类型定向查找广告活动
     */
    @Query("{'targeting.device.deviceTypes': {$in: ?0}}")
    List<CampaignEntity> findByTargetingDeviceTypes(List<String> deviceTypes);

    /**
     * 根据兴趣定向查找广告活动
     */
    @Query("{'targeting.audience.interests': {$in: ?0}}")
    List<CampaignEntity> findByTargetingInterests(List<String> interests);

    /**
     * 根据年龄范围定向查找广告活动
     */
    @Query("{'targeting.audience.ageRange.minAge': {$lte: ?0}, 'targeting.audience.ageRange.maxAge': {$gte: ?1}}")
    List<CampaignEntity> findByTargetingAgeRange(Integer minAge, Integer maxAge);

    /**
     * 根据性别定向查找广告活动
     */
    @Query("{'targeting.audience.genders': {$in: ?0}}")
    List<CampaignEntity> findByTargetingGenders(List<String> genders);

    /**
     * 根据竞价策略查找广告活动
     */
    @Query("{'bidding.bidStrategy': ?0}")
    List<CampaignEntity> findByBiddingStrategy(String bidStrategy);

    /**
     * 根据最大竞价范围查找广告活动
     */
    @Query("{'bidding.maxBid': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findByMaxBidBetween(Long minBid, Long maxBid);

    /**
     * 查找即将开始的活动（开始时间在未来24小时内）
     */
    @Query("{'schedule.startDate': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findCampaignsStartingSoon(LocalDateTime now, LocalDateTime tomorrow);

    /**
     * 查找即将结束的活动（结束时间在未来24小时内）
     */
    @Query("{'schedule.endDate': {$gte: ?0, $lte: ?1}}")
    List<CampaignEntity> findCampaignsEndingSoon(LocalDateTime now, LocalDateTime tomorrow);

    /**
     * 查找已过期的活动
     */
    @Query("{'schedule.endDate': {$lt: ?0}}")
    List<CampaignEntity> findExpiredCampaigns(LocalDateTime now);

    /**
     * 查找当前时间范围内活跃的活动
     */
    @Query("{'status': 'ACTIVE', 'schedule.startDate': {$lte: ?0}, 'schedule.endDate': {$gte: ?0}}")
    List<CampaignEntity> findCurrentlyActiveCampaigns(LocalDateTime now);

    /**
     * 根据创意类型查找广告活动
     */
    @Query("{'creatives.type': ?0}")
    List<CampaignEntity> findByCreativeType(String creativeType);

    /**
     * 根据创意状态查找广告活动
     */
    @Query("{'creatives.status': ?0}")
    List<CampaignEntity> findByCreativeStatus(String creativeStatus);

    /**
     * 查找启用频次控制的活动
     */
    @Query("{'frequencyCap.enabled': true}")
    List<CampaignEntity> findCampaignsWithFrequencyCap();

    /**
     * 根据频次控制周期查找广告活动
     */
    @Query("{'frequencyCap.period': ?0}")
    List<CampaignEntity> findByFrequencyCapPeriod(String period);

    /**
     * 统计指定广告主的活动数量
     */
    long countByAdvertiserId(String advertiserId);

    /**
     * 统计指定状态的活动数量
     */
    long countByStatus(String status);

    /**
     * 统计活跃活动数量
     */
    @Query(value = "{'status': 'ACTIVE'}", count = true)
    long countActiveCampaigns();

    /**
     * 统计指定广告主的活跃活动数量
     */
    @Query(value = "{'advertiserId': ?0, 'status': 'ACTIVE'}", count = true)
    long countActiveCampaignsByAdvertiser(String advertiserId);

    /**
     * 根据更新时间范围查找广告活动
     */
    List<CampaignEntity> findByUpdatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找最近创建的活动
     */
    List<CampaignEntity> findTop10ByOrderByCreatedAtDesc();

    /**
     * 查找最近更新的活动
     */
    List<CampaignEntity> findTop10ByOrderByUpdatedAtDesc();

    /**
     * 根据货币查找广告活动
     */
    @Query("{'budget.currency': ?0}")
    List<CampaignEntity> findByCurrency(String currency);

    /**
     * 查找有私有交易的活动
     */
    @Query("{'creatives': {$exists: true, $not: {$size: 0}}}")
    List<CampaignEntity> findCampaignsWithCreatives();

    /**
     * 根据时区查找广告活动
     */
    @Query("{'schedule.timezone': ?0}")
    List<CampaignEntity> findByTimezone(String timezone);

    /**
     * 查找指定时间段内定向的活动
     */
    @Query("{'targeting.time.hours': {$in: ?0}}")
    List<CampaignEntity> findByTargetingHours(List<Integer> hours);

    /**
     * 查找指定星期几定向的活动
     */
    @Query("{'targeting.time.daysOfWeek': {$in: ?0}}")
    List<CampaignEntity> findByTargetingDaysOfWeek(List<Integer> daysOfWeek);

    /**
     * 分页查询活跃活动
     */
    @Query("{'status': 'ACTIVE'}")
    Page<CampaignEntity> findActiveCampaigns(Pageable pageable);

    /**
     * 分页查询指定广告主的活动
     */
    Page<CampaignEntity> findByAdvertiserId(String advertiserId, Pageable pageable);
}