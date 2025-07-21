package fei.song.openrtb.repository;

import fei.song.openrtb.entity.UserProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户画像仓储接口
 * 提供对 UserProfileEntity 的数据访问操作
 */
@Repository
public interface UserProfileRepository extends MongoRepository<UserProfileEntity, String> {

    /**
     * 根据用户ID查找用户画像
     */
    Optional<UserProfileEntity> findByUserId(String userId);

    /**
     * 根据年龄范围查找用户
     */
    @Query("{'demographics.age': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 根据性别查找用户
     */
    @Query("{'demographics.gender': ?0}")
    List<UserProfileEntity> findByGender(String gender);

    /**
     * 根据收入范围查找用户
     */
    @Query("{'demographics.income': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findByIncomeRange(Long minIncome, Long maxIncome);

    /**
     * 根据教育水平查找用户
     */
    @Query("{'demographics.education': ?0}")
    List<UserProfileEntity> findByEducation(String education);

    /**
     * 根据职业查找用户
     */
    @Query("{'demographics.occupation': ?0}")
    List<UserProfileEntity> findByOccupation(String occupation);

    /**
     * 根据婚姻状况查找用户
     */
    @Query("{'demographics.maritalStatus': ?0}")
    List<UserProfileEntity> findByMaritalStatus(String maritalStatus);

    /**
     * 根据兴趣标签查找用户
     */
    @Query("{'interests.categories': {$in: ?0}}")
    List<UserProfileEntity> findByInterestCategories(List<String> categories);

    /**
     * 根据兴趣关键词查找用户
     */
    @Query("{'interests.keywords': {$in: ?0}}")
    List<UserProfileEntity> findByInterestKeywords(List<String> keywords);

    /**
     * 根据品牌偏好查找用户
     */
    @Query("{'interests.brands': {$in: ?0}}")
    List<UserProfileEntity> findByBrandPreferences(List<String> brands);

    /**
     * 根据购买历史中的类别查找用户
     */
    @Query("{'behaviorData.purchases.category': {$in: ?0}}")
    List<UserProfileEntity> findByPurchaseCategories(List<String> categories);

    /**
     * 根据购买金额范围查找用户
     */
    @Query("{'behaviorData.purchases.amount': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findByPurchaseAmountRange(Long minAmount, Long maxAmount);

    /**
     * 根据最近购买时间查找用户
     */
    @Query("{'behaviorData.purchases.timestamp': {$gte: ?0}}")
    List<UserProfileEntity> findByRecentPurchases(LocalDateTime since);

    /**
     * 根据广告互动类型查找用户
     */
    @Query("{'behaviorData.adInteractions.type': {$in: ?0}}")
    List<UserProfileEntity> findByAdInteractionTypes(List<String> types);

    /**
     * 根据设备类型查找用户
     */
    @Query("{'deviceInfo.deviceType': ?0}")
    List<UserProfileEntity> findByDeviceType(String deviceType);

    /**
     * 根据操作系统查找用户
     */
    @Query("{'deviceInfo.os': ?0}")
    List<UserProfileEntity> findByOperatingSystem(String os);

    /**
     * 根据浏览器查找用户
     */
    @Query("{'deviceInfo.browser': ?0}")
    List<UserProfileEntity> findByBrowser(String browser);

    /**
     * 根据设备品牌查找用户
     */
    @Query("{'deviceInfo.brand': ?0}")
    List<UserProfileEntity> findByDeviceBrand(String brand);

    /**
     * 根据设备型号查找用户
     */
    @Query("{'deviceInfo.model': ?0}")
    List<UserProfileEntity> findByDeviceModel(String model);

    /**
     * 根据国家查找用户
     */
    @Query("{'location.country': ?0}")
    List<UserProfileEntity> findByCountry(String country);

    /**
     * 根据地区查找用户
     */
    @Query("{'location.region': ?0}")
    List<UserProfileEntity> findByRegion(String region);

    /**
     * 根据城市查找用户
     */
    @Query("{'location.city': ?0}")
    List<UserProfileEntity> findByCity(String city);

    /**
     * 根据邮政编码查找用户
     */
    @Query("{'location.postalCode': ?0}")
    List<UserProfileEntity> findByPostalCode(String postalCode);

    /**
     * 根据时区查找用户
     */
    @Query("{'location.timezone': ?0}")
    List<UserProfileEntity> findByTimezone(String timezone);

    /**
     * 根据地理坐标范围查找用户（矩形区域）
     */
    @Query("{'location.latitude': {$gte: ?0, $lte: ?1}, 'location.longitude': {$gte: ?2, $lte: ?3}}")
    List<UserProfileEntity> findByLocationBounds(Double minLat, Double maxLat, Double minLon, Double maxLon);

    /**
     * 根据频次控制数据查找用户
     */
    @Query("{'frequencyData.campaignId': ?0}")
    List<UserProfileEntity> findByFrequencyCampaign(String campaignId);

    /**
     * 根据广告展示次数范围查找用户
     */
    @Query("{'frequencyData.impressions': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findByImpressionRange(Integer minImpressions, Integer maxImpressions);

    /**
     * 根据广告点击次数范围查找用户
     */
    @Query("{'frequencyData.clicks': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findByClickRange(Integer minClicks, Integer maxClicks);

    /**
     * 查找高价值用户（基于购买金额）
     */
    @Query("{'behaviorData.purchases': {$elemMatch: {'amount': {$gte: ?0}}}}")
    List<UserProfileEntity> findHighValueUsers(Long minPurchaseAmount);

    /**
     * 查找活跃用户（最近有广告互动）
     */
    @Query("{'behaviorData.adInteractions.timestamp': {$gte: ?0}}")
    List<UserProfileEntity> findActiveUsers(LocalDateTime since);

    /**
     * 查找新用户（最近创建的用户画像）
     */
    @Query("{'createdAt': {$gte: ?0}}")
    List<UserProfileEntity> findNewUsers(LocalDateTime since);

    /**
     * 查找最近更新的用户画像
     */
    @Query("{'updatedAt': {$gte: ?0}}")
    List<UserProfileEntity> findRecentlyUpdatedUsers(LocalDateTime since);

    /**
     * 查找即将过期的用户画像
     */
    @Query("{'expiresAt': {$gte: ?0, $lte: ?1}}")
    List<UserProfileEntity> findExpiringUsers(LocalDateTime now, LocalDateTime soon);

    /**
     * 查找已过期的用户画像
     */
    @Query("{'expiresAt': {$lt: ?0}}")
    List<UserProfileEntity> findExpiredUsers(LocalDateTime now);

    /**
     * 根据多个条件组合查找用户（年龄、性别、兴趣）
     */
    @Query("{'demographics.age': {$gte: ?0, $lte: ?1}, 'demographics.gender': ?2, 'interests.categories': {$in: ?3}}")
    List<UserProfileEntity> findByDemographicsAndInterests(Integer minAge, Integer maxAge, String gender, List<String> interests);

    /**
     * 根据设备和地理位置查找用户
     */
    @Query("{'deviceInfo.deviceType': ?0, 'location.country': ?1}")
    List<UserProfileEntity> findByDeviceAndLocation(String deviceType, String country);

    /**
     * 统计指定年龄范围的用户数量
     */
    @Query(value = "{'demographics.age': {$gte: ?0, $lte: ?1}}", count = true)
    long countByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 统计指定性别的用户数量
     */
    @Query(value = "{'demographics.gender': ?0}", count = true)
    long countByGender(String gender);

    /**
     * 统计指定国家的用户数量
     */
    @Query(value = "{'location.country': ?0}", count = true)
    long countByCountry(String country);

    /**
     * 统计指定设备类型的用户数量
     */
    @Query(value = "{'deviceInfo.deviceType': ?0}", count = true)
    long countByDeviceType(String deviceType);

    /**
     * 统计有购买历史的用户数量
     */
    @Query(value = "{'behaviorData.purchases': {$exists: true, $not: {$size: 0}}}", count = true)
    long countUsersWithPurchases();

    /**
     * 统计有广告互动的用户数量
     */
    @Query(value = "{'behaviorData.adInteractions': {$exists: true, $not: {$size: 0}}}", count = true)
    long countUsersWithAdInteractions();

    /**
     * 删除过期的用户画像
     */
    @Query(delete = true, value = "{'expiresAt': {$lt: ?0}}")
    long deleteExpiredUsers(LocalDateTime now);

    /**
     * 查找最近活跃的用户（按更新时间排序）
     */
    List<UserProfileEntity> findTop100ByOrderByUpdatedAtDesc();

    /**
     * 查找最新创建的用户
     */
    List<UserProfileEntity> findTop100ByOrderByCreatedAtDesc();

    /**
     * 分页查询指定兴趣的用户
     */
    @Query("{'interests.categories': {$in: ?0}}")
    Page<UserProfileEntity> findByInterestCategories(List<String> categories, Pageable pageable);

    /**
     * 分页查询指定地区的用户
     */
    @Query("{'location.country': ?0}")
    Page<UserProfileEntity> findByCountry(String country, Pageable pageable);

    /**
     * 分页查询指定设备类型的用户
     */
    @Query("{'deviceInfo.deviceType': ?0}")
    Page<UserProfileEntity> findByDeviceType(String deviceType, Pageable pageable);
}