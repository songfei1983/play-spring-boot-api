package fei.song.openrtb.repository;

import fei.song.openrtb.entity.InventoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 广告位库存仓储接口
 * 提供对 InventoryEntity 的数据访问操作
 */
@Repository
public interface InventoryRepository extends MongoRepository<InventoryEntity, String> {

    /**
     * 根据广告位ID查找库存
     */
    Optional<InventoryEntity> findBySlotId(String slotId);

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
     * 根据发布商ID和状态查找库存
     */
    List<InventoryEntity> findByPublisherIdAndStatus(String publisherId, String status);

    /**
     * 根据站点ID和状态查找库存
     */
    List<InventoryEntity> findBySiteIdAndStatus(String siteId, String status);

    /**
     * 查找活跃的广告位
     */
    @Query("{'status': 'ACTIVE'}")
    List<InventoryEntity> findActiveSlots();

    /**
     * 查找暂停的广告位
     */
    @Query("{'status': 'PAUSED'}")
    List<InventoryEntity> findPausedSlots();

    /**
     * 根据广告位类型查找库存
     */
    @Query("{'specs.adType': ?0}")
    List<InventoryEntity> findByAdType(String adType);

    /**
     * 根据广告位尺寸查找库存
     */
    @Query("{'specs.width': ?0, 'specs.height': ?1}")
    List<InventoryEntity> findByDimensions(Integer width, Integer height);

    /**
     * 根据支持的格式查找库存
     */
    @Query("{'specs.supportedFormats': {$in: ?0}}")
    List<InventoryEntity> findBySupportedFormats(List<String> formats);

    /**
     * 根据位置查找库存
     */
    @Query("{'specs.position': ?0}")
    List<InventoryEntity> findByPosition(String position);

    /**
     * 根据是否支持视频查找库存
     */
    @Query("{'specs.videoSupported': ?0}")
    List<InventoryEntity> findByVideoSupported(Boolean videoSupported);

    /**
     * 根据底价范围查找库存
     */
    @Query("{'pricing.floorPrice': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByFloorPriceRange(Long minPrice, Long maxPrice);

    /**
     * 根据货币查找库存
     */
    @Query("{'pricing.currency': ?0}")
    List<InventoryEntity> findByCurrency(String currency);

    /**
     * 根据定价模式查找库存
     */
    @Query("{'pricing.pricingModel': ?0}")
    List<InventoryEntity> findByPricingModel(String pricingModel);

    /**
     * 查找有私有交易的广告位
     */
    @Query("{'pricing.privateDeals': {$exists: true, $not: {$size: 0}}}")
    List<InventoryEntity> findSlotsWithPrivateDeals();

    /**
     * 根据私有交易买方ID查找库存
     */
    @Query("{'pricing.privateDeals.buyerId': ?0}")
    List<InventoryEntity> findByPrivateDealBuyer(String buyerId);

    /**
     * 根据私有交易价格范围查找库存
     */
    @Query("{'pricing.privateDeals.price': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByPrivateDealPriceRange(Long minPrice, Long maxPrice);

    /**
     * 根据日流量范围查找库存
     */
    @Query("{'trafficStats.dailyImpressions': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByDailyImpressionsRange(Long minImpressions, Long maxImpressions);

    /**
     * 根据月流量范围查找库存
     */
    @Query("{'trafficStats.monthlyImpressions': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByMonthlyImpressionsRange(Long minImpressions, Long maxImpressions);

    /**
     * 根据点击率范围查找库存
     */
    @Query("{'trafficStats.ctr': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByCtrRange(Double minCtr, Double maxCtr);

    /**
     * 根据可见性范围查找库存
     */
    @Query("{'trafficStats.viewability': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByViewabilityRange(Double minViewability, Double maxViewability);

    /**
     * 根据质量评分范围查找库存
     */
    @Query("{'qualityScore': {$gte: ?0, $lte: ?1}}")
    List<InventoryEntity> findByQualityScoreRange(Double minScore, Double maxScore);

    /**
     * 查找高质量广告位（质量评分大于等于指定值）
     */
    @Query("{'qualityScore': {$gte: ?0}}")
    List<InventoryEntity> findHighQualitySlots(Double minQualityScore);

    /**
     * 查找低质量广告位（质量评分小于指定值）
     */
    @Query("{'qualityScore': {$lt: ?0}}")
    List<InventoryEntity> findLowQualitySlots(Double maxQualityScore);

    /**
     * 根据是否允许弹窗查找库存
     */
    @Query("{'settings.allowPopups': ?0}")
    List<InventoryEntity> findByAllowPopups(Boolean allowPopups);

    /**
     * 根据是否允许自动播放查找库存
     */
    @Query("{'settings.allowAutoplay': ?0}")
    List<InventoryEntity> findByAllowAutoplay(Boolean allowAutoplay);

    /**
     * 根据是否允许音频查找库存
     */
    @Query("{'settings.allowAudio': ?0}")
    List<InventoryEntity> findByAllowAudio(Boolean allowAudio);

    /**
     * 根据最大广告时长查找库存
     */
    @Query("{'settings.maxAdDuration': {$lte: ?0}}")
    List<InventoryEntity> findByMaxAdDuration(Integer maxDuration);

    /**
     * 根据允许的广告类别查找库存
     */
    @Query("{'settings.allowedCategories': {$in: ?0}}")
    List<InventoryEntity> findByAllowedCategories(List<String> categories);

    /**
     * 根据禁止的广告类别查找库存
     */
    @Query("{'settings.blockedCategories': {$nin: ?0}}")
    List<InventoryEntity> findByNotBlockedCategories(List<String> categories);

    /**
     * 根据允许的广告主查找库存
     */
    @Query("{'settings.allowedAdvertisers': {$in: ?0}}")
    List<InventoryEntity> findByAllowedAdvertisers(List<String> advertisers);

    /**
     * 根据禁止的广告主查找库存
     */
    @Query("{'settings.blockedAdvertisers': {$nin: ?0}}")
    List<InventoryEntity> findByNotBlockedAdvertisers(List<String> advertisers);

    /**
     * 查找高流量广告位（日展示量大于指定值）
     */
    @Query("{'trafficStats.dailyImpressions': {$gte: ?0}}")
    List<InventoryEntity> findHighTrafficSlots(Long minDailyImpressions);

    /**
     * 查找高点击率广告位
     */
    @Query("{'trafficStats.ctr': {$gte: ?0}}")
    List<InventoryEntity> findHighCtrSlots(Double minCtr);

    /**
     * 查找高可见性广告位
     */
    @Query("{'trafficStats.viewability': {$gte: ?0}}")
    List<InventoryEntity> findHighViewabilitySlots(Double minViewability);

    /**
     * 根据更新时间范围查找库存
     */
    List<InventoryEntity> findByUpdatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找最近创建的广告位
     */
    List<InventoryEntity> findTop10ByOrderByCreatedAtDesc();

    /**
     * 查找最近更新的广告位
     */
    List<InventoryEntity> findTop10ByOrderByUpdatedAtDesc();

    /**
     * 查找最高质量评分的广告位
     */
    List<InventoryEntity> findTop10ByOrderByQualityScoreDesc();

    /**
     * 查找最高流量的广告位
     */
    List<InventoryEntity> findTop10ByOrderByTrafficStatsDailyImpressionsDesc();

    /**
     * 统计指定发布商的广告位数量
     */
    long countByPublisherId(String publisherId);

    /**
     * 统计指定站点的广告位数量
     */
    long countBySiteId(String siteId);

    /**
     * 统计指定状态的广告位数量
     */
    long countByStatus(String status);

    /**
     * 统计活跃广告位数量
     */
    @Query(value = "{'status': 'ACTIVE'}", count = true)
    long countActiveSlots();

    /**
     * 统计指定发布商的活跃广告位数量
     */
    @Query(value = "{'publisherId': ?0, 'status': 'ACTIVE'}", count = true)
    long countActiveSlotsbyPublisher(String publisherId);

    /**
     * 统计指定广告类型的广告位数量
     */
    @Query(value = "{'specs.adType': ?0}", count = true)
    long countByAdType(String adType);

    /**
     * 统计支持视频的广告位数量
     */
    @Query(value = "{'specs.videoSupported': true}", count = true)
    long countVideoSupportedSlots();

    /**
     * 统计有私有交易的广告位数量
     */
    @Query(value = "{'pricing.privateDeals': {$exists: true, $not: {$size: 0}}}", count = true)
    long countSlotsWithPrivateDeals();

    /**
     * 统计高质量广告位数量（质量评分大于等于指定值）
     */
    @Query(value = "{'qualityScore': {$gte: ?0}}", count = true)
    long countHighQualitySlots(Double minQualityScore);

    /**
     * 根据多个条件组合查找库存（发布商、状态、广告类型）
     */
    @Query("{'publisherId': ?0, 'status': ?1, 'specs.adType': ?2}")
    List<InventoryEntity> findByPublisherStatusAndAdType(String publisherId, String status, String adType);

    /**
     * 根据尺寸和格式查找库存
     */
    @Query("{'specs.width': ?0, 'specs.height': ?1, 'specs.supportedFormats': {$in: ?2}}")
    List<InventoryEntity> findByDimensionsAndFormats(Integer width, Integer height, List<String> formats);

    /**
     * 分页查询活跃广告位
     */
    @Query("{'status': 'ACTIVE'}")
    Page<InventoryEntity> findActiveSlots(Pageable pageable);

    /**
     * 分页查询指定发布商的广告位
     */
    Page<InventoryEntity> findByPublisherId(String publisherId, Pageable pageable);

    /**
     * 分页查询指定广告类型的广告位
     */
    @Query("{'specs.adType': ?0}")
    Page<InventoryEntity> findByAdType(String adType, Pageable pageable);

    /**
     * 分页查询高质量广告位
     */
    @Query("{'qualityScore': {$gte: ?0}}")
    Page<InventoryEntity> findHighQualitySlots(Double minQualityScore, Pageable pageable);
}