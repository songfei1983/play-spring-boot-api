package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 广告位库存仓储接口
 */
@Repository
public interface InventoryRepository extends MongoRepository<InventoryEntity, String> {

    /**
     * 根据广告位ID查找库存
     */
    Optional<InventoryEntity> findByPlacementId(String placementId);

    /**
     * 根据发布商ID查找库存
     */
    List<InventoryEntity> findByPublisherId(String publisherId);

    /**
     * 根据站点ID查找库存
     */
    List<InventoryEntity> findBySiteId(String siteId);

    /**
     * 根据状态查找库存
     */
    List<InventoryEntity> findByStatus(String status);

    /**
     * 查找活跃的广告位
     */
    List<InventoryEntity> findByStatusAndSpecAdType(String status, String adType);

    /**
     * 根据广告类型查找库存
     */
    @Query("{'spec.ad_type': ?0}")
    List<InventoryEntity> findByAdType(String adType);

    /**
     * 根据广告尺寸查找库存
     */
    @Query("{'spec.supported_formats': {$elemMatch: {'width': ?0, 'height': ?1}}}")
    List<InventoryEntity> findByAdSize(Integer width, Integer height);

    /**
     * 根据底价范围查找库存
     */
    @Query("{'pricing.floor_price': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByFloorPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 根据定价模式查找库存
     */
    @Query("{'pricing.pricing_model': ?0}")
    List<InventoryEntity> findByPricingModel(String pricingModel);

    /**
     * 查找高质量广告位
     */
    @Query("{'quality_score.overall_score': {$gte: ?0}}")
    List<InventoryEntity> findHighQualityPlacements(Double minScore);

    /**
     * 根据可见性评分查找库存
     */
    @Query("{'quality_score.viewability_score': {$gte: ?0}}")
    List<InventoryEntity> findByViewabilityScore(Double minScore);

    /**
     * 查找高流量广告位
     */
    @Query("{'traffic_stats.daily_requests': {$gte: ?0}}")
    List<InventoryEntity> findHighTrafficPlacements(Long minRequests);

    /**
     * 根据填充率查找库存
     */
    @Query("{'traffic_stats.fill_rate': {$gte: ?0}}")
    List<InventoryEntity> findByFillRate(Double minFillRate);

    /**
     * 根据平均CPM查找库存
     */
    @Query("{'traffic_stats.avg_cpm': {$gte: ?0}}")
    List<InventoryEntity> findByAvgCpm(BigDecimal minCpm);

    /**
     * 查找支持私有交易的广告位
     */
    @Query("{'pricing.private_deals': {$exists: true, $ne: []}}")
    List<InventoryEntity> findPlacementsWithPrivateDeals();

    /**
     * 根据买方ID查找私有交易
     */
    @Query("{'pricing.private_deals.buyer_id': ?0}")
    List<InventoryEntity> findByPrivateDealBuyer(String buyerId);

    /**
     * 查找允许特定广告类别的广告位
     */
    @Query("{'settings.allowed_categories': {$in: [?0]}}")
    List<InventoryEntity> findByAllowedCategory(String category);

    /**
     * 查找不允许特定广告类别的广告位
     */
    @Query("{'settings.blocked_categories': {$nin: [?0]}}")
    List<InventoryEntity> findByNotBlockedCategory(String category);

    /**
     * 查找允许特定广告主的广告位
     */
    @Query("{'settings.allowed_advertisers': {$in: [?0]}}")
    List<InventoryEntity> findByAllowedAdvertiser(String advertiserId);

    /**
     * 查找不禁止特定广告主的广告位
     */
    @Query("{'settings.blocked_advertisers': {$nin: [?0]}}")
    List<InventoryEntity> findByNotBlockedAdvertiser(String advertiserId);

    /**
     * 统计指定发布商的广告位数量
     */
    long countByPublisherId(String publisherId);

    /**
     * 统计指定状态的广告位数量
     */
    long countByStatus(String status);

    /**
     * 查找需要更新质量评分的广告位
     */
    @Query("{'quality_score.last_updated': {$lt: ?0}}")
    List<InventoryEntity> findPlacementsNeedingQualityUpdate(java.time.LocalDateTime threshold);
}