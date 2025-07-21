package fei.song.play_spring_boot_api.ads.infrastructure.persistence.repository;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户画像仓储接口
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
     * 根据国家查找用户
     */
    @Query("{'demographics.geo.country': ?0}")
    List<UserProfileEntity> findByCountry(String country);

    /**
     * 根据城市查找用户
     */
    @Query("{'demographics.geo.city': ?0}")
    List<UserProfileEntity> findByCity(String city);

    /**
     * 根据兴趣类别查找用户
     */
    @Query("{'interests.category': ?0}")
    List<UserProfileEntity> findByInterestCategory(String category);

    /**
     * 根据设备类型查找用户
     */
    @Query("{'device_info.device_type': ?0}")
    List<UserProfileEntity> findByDeviceType(Integer deviceType);

    /**
     * 根据操作系统查找用户
     */
    @Query("{'device_info.operating_system': ?0}")
    List<UserProfileEntity> findByOperatingSystem(String os);

    /**
     * 查找活跃用户（最近有会话的用户）
     */
    @Query("{'behavior.session_data.last_session': {$gte: ?0}}")
    List<UserProfileEntity> findActiveUsers(LocalDateTime since);

    /**
     * 查找有购买历史的用户
     */
    @Query("{'behavior.purchase_history': {$exists: true, $ne: []}}")
    List<UserProfileEntity> findUsersWithPurchaseHistory();

    /**
     * 根据购买类别查找用户
     */
    @Query("{'behavior.purchase_history.category': ?0}")
    List<UserProfileEntity> findByPurchaseCategory(String category);

    /**
     * 查找高价值用户（购买金额超过阈值）
     */
    @Query("{'behavior.purchase_history.amount': {$gte: ?0}}")
    List<UserProfileEntity> findHighValueUsers(Double minAmount);

    /**
     * 根据广告交互查找用户
     */
    @Query("{'behavior.ad_interactions.campaign_id': ?0}")
    List<UserProfileEntity> findByAdInteractionCampaign(String campaignId);

    /**
     * 查找频次超限的用户
     */
    @Query("{'frequency_data.daily_impressions.?0': {$gte: ?1}}")
    List<UserProfileEntity> findUsersExceedingDailyFrequency(String date, Integer maxImpressions);

    /**
     * 根据地理位置范围查找用户
     */
    @Query("{'demographics.geo.lat': {$gte: ?0, $lte: ?1}, 'demographics.geo.lon': {$gte: ?2, $lte: ?3}}")
    List<UserProfileEntity> findByGeoRange(Double minLat, Double maxLat, Double minLon, Double maxLon);

    /**
     * 统计指定国家的用户数量
     */
    @Query(value = "{'demographics.geo.country': ?0}", count = true)
    long countByCountry(String country);

    /**
     * 统计指定性别的用户数量
     */
    @Query(value = "{'demographics.gender': ?0}", count = true)
    long countByGender(String gender);

    /**
     * 删除过期的用户画像
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    /**
     * 查找需要更新的用户画像
     */
    @Query("{'updated_at': {$lt: ?0}}")
    List<UserProfileEntity> findProfilesNeedingUpdate(LocalDateTime threshold);
}