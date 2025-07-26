package fei.song.play_spring_boot_api.ads.service;

import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserProfileEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentEntity;
import fei.song.play_spring_boot_api.ads.infrastructure.persistence.entity.UserSegmentMappingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 分段过滤服务
 * 负责根据用户画像和分段规则进行用户分段匹配
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentFilterService {

    private final AdsUserProfileService userProfileService;
    private final UserSegmentService userSegmentService;
    private final UserSegmentMappingService mappingService;

    /**
     * 为用户匹配所有适用的分段
     */
    @Transactional
    public List<UserSegmentMappingEntity> matchUserSegments(String userId) {
        log.info("开始为用户匹配分段: userId={}", userId);
        
        // 获取用户画像
        Optional<UserProfileEntity> profileOpt = userProfileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            log.warn("用户画像不存在，无法进行分段匹配: userId={}", userId);
            return Collections.emptyList();
        }
        
        UserProfileEntity profile = profileOpt.get();
        
        // 获取所有激活的分段
        List<UserSegmentEntity> activeSegments = userSegmentService.findActiveSegments();
        
        List<UserSegmentMappingEntity> matchedMappings = new ArrayList<>();
        
        for (UserSegmentEntity segment : activeSegments) {
            SegmentMatchResult matchResult = evaluateSegmentMatch(profile, segment);
            
            if (matchResult.isMatched()) {
                // 创建或更新用户分段映射
                UserSegmentMappingEntity mapping = createOrUpdateMapping(
                    userId, segment, matchResult);
                matchedMappings.add(mapping);
                
                log.debug("用户匹配分段: userId={}, segmentId={}, score={}", 
                    userId, segment.getId(), matchResult.getMatchScore());
            }
        }
        
        log.info("用户分段匹配完成: userId={}, matchedCount={}", userId, matchedMappings.size());
        return matchedMappings;
    }

    /**
     * 批量为多个用户匹配分段
     */
    @Async
    @Transactional
    public CompletableFuture<Map<String, List<UserSegmentMappingEntity>>> batchMatchUserSegments(
            List<String> userIds) {
        log.info("开始批量用户分段匹配: userCount={}", userIds.size());
        
        Map<String, List<UserSegmentMappingEntity>> results = new HashMap<>();
        
        for (String userId : userIds) {
            try {
                List<UserSegmentMappingEntity> mappings = matchUserSegments(userId);
                results.put(userId, mappings);
            } catch (Exception e) {
                log.error("用户分段匹配失败: userId={}", userId, e);
                results.put(userId, Collections.emptyList());
            }
        }
        
        log.info("批量用户分段匹配完成: userCount={}, successCount={}", 
            userIds.size(), results.size());
        
        return CompletableFuture.completedFuture(results);
    }

    /**
     * 为指定分段匹配所有符合条件的用户
     */
    @Transactional
    public List<UserSegmentMappingEntity> matchSegmentUsers(String segmentId) {
        log.info("开始为分段匹配用户: segmentId={}", segmentId);
        
        // 获取分段信息
        Optional<UserSegmentEntity> segmentOpt = userSegmentService.findSegmentById(segmentId);
        if (segmentOpt.isEmpty()) {
            log.warn("分段不存在: segmentId={}", segmentId);
            return Collections.emptyList();
        }
        
        UserSegmentEntity segment = segmentOpt.get();
        
        if (!"ACTIVE".equals(segment.getStatus())) {
            log.warn("分段未激活，跳过匹配: segmentId={}, status={}", segmentId, segment.getStatus());
            return Collections.emptyList();
        }
        
        // 根据分段规则预筛选用户
        List<UserProfileEntity> candidateUsers = findCandidateUsers(segment);
        
        List<UserSegmentMappingEntity> matchedMappings = new ArrayList<>();
        
        for (UserProfileEntity profile : candidateUsers) {
            SegmentMatchResult matchResult = evaluateSegmentMatch(profile, segment);
            
            if (matchResult.isMatched()) {
                UserSegmentMappingEntity mapping = createOrUpdateMapping(
                    profile.getUserId(), segment, matchResult);
                matchedMappings.add(mapping);
            }
        }
        
        log.info("分段用户匹配完成: segmentId={}, candidateCount={}, matchedCount={}", 
            segmentId, candidateUsers.size(), matchedMappings.size());
        
        return matchedMappings;
    }

    /**
     * 重新评估用户的所有分段匹配
     */
    @Transactional
    public List<UserSegmentMappingEntity> reevaluateUserSegments(String userId) {
        log.info("重新评估用户分段: userId={}", userId);
        
        // 停用用户当前的所有分段映射
        mappingService.deactivateAllUserMappings(userId);
        
        // 重新匹配分段
        return matchUserSegments(userId);
    }

    /**
     * 重新评估分段的所有用户匹配
     */
    @Transactional
    public List<UserSegmentMappingEntity> reevaluateSegmentUsers(String segmentId) {
        log.info("重新评估分段用户: segmentId={}", segmentId);
        
        // 停用分段当前的所有用户映射
        mappingService.deactivateAllSegmentMappings(segmentId);
        
        // 重新匹配用户
        return matchSegmentUsers(segmentId);
    }

    /**
     * 检查用户是否匹配指定分段
     */
    public boolean checkUserSegmentMatch(String userId, String segmentId) {
        Optional<UserProfileEntity> profileOpt = userProfileService.findByUserId(userId);
        Optional<UserSegmentEntity> segmentOpt = userSegmentService.findSegmentById(segmentId);
        
        if (profileOpt.isEmpty() || segmentOpt.isEmpty()) {
            return false;
        }
        
        SegmentMatchResult result = evaluateSegmentMatch(profileOpt.get(), segmentOpt.get());
        return result.isMatched();
    }

    /**
     * 获取用户的分段匹配分数
     */
    public Map<String, Double> getUserSegmentScores(String userId) {
        Optional<UserProfileEntity> profileOpt = userProfileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            return Collections.emptyMap();
        }
        
        UserProfileEntity profile = profileOpt.get();
        List<UserSegmentEntity> activeSegments = userSegmentService.findActiveSegments();
        
        Map<String, Double> scores = new HashMap<>();;
        
        for (UserSegmentEntity segment : activeSegments) {
            SegmentMatchResult result = evaluateSegmentMatch(profile, segment);
            scores.put(segment.getId(), result.getMatchScore());
        }
        
        return scores;
    }

    /**
     * 评估用户画像与分段的匹配度
     */
    private SegmentMatchResult evaluateSegmentMatch(UserProfileEntity profile, UserSegmentEntity segment) {
        if (segment.getRules() == null || segment.getRules().isEmpty()) {
            return new SegmentMatchResult(false, 0.0, 0.0, "无规则定义");
        }
        
        double totalScore = 0.0;
        double totalWeight = 0.0;
        int matchedRules = 0;
        List<String> matchDetails = new ArrayList<>();
        
        for (UserSegmentEntity.SegmentRule rule : segment.getRules()) {
            RuleEvaluationResult ruleResult = evaluateRule(profile, rule);
            
            if (ruleResult.isMatched()) {
                totalScore += ruleResult.getScore() * rule.getWeight();
                matchedRules++;
                matchDetails.add(String.format("规则[%s]: %.2f", rule.getField(), ruleResult.getScore()));
            }
            
            totalWeight += rule.getWeight();
        }
        
        // 计算最终匹配分数
        double finalScore = totalWeight > 0 ? totalScore / totalWeight : 0.0;
        
        // 计算置信度（匹配规则数 / 总规则数）
        double confidence = (double) matchedRules / segment.getRules().size();
        
        // 判断是否匹配（分数超过阈值且置信度足够）
        boolean isMatched = finalScore >= 0.6 && confidence >= 0.5;
        
        String reason = isMatched ? 
            String.format("匹配成功: %s", String.join(", ", matchDetails)) :
            String.format("匹配失败: 分数=%.2f, 置信度=%.2f", finalScore, confidence);
        
        return new SegmentMatchResult(isMatched, finalScore, confidence, reason);
    }

    /**
     * 评估单个规则
     */
    private RuleEvaluationResult evaluateRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        try {
            switch (rule.getField()) {
                case "age":
                    return evaluateAgeRule(profile, rule);
                case "gender":
                    return evaluateGenderRule(profile, rule);
                case "country":
                    return evaluateCountryRule(profile, rule);
                case "city":
                    return evaluateCityRule(profile, rule);
                case "interest_category":
                    return evaluateInterestRule(profile, rule);
                case "device_type":
                    return evaluateDeviceTypeRule(profile, rule);
                case "operating_system":
                    return evaluateOperatingSystemRule(profile, rule);
                case "purchase_amount":
                    return evaluatePurchaseAmountRule(profile, rule);
                case "session_count":
                    return evaluateSessionCountRule(profile, rule);
                default:
                    log.warn("未知的规则字段: {}", rule.getField());
                    return new RuleEvaluationResult(false, 0.0, "未知字段");
            }
        } catch (Exception e) {
            log.error("规则评估失败: field={}, operator={}", rule.getField(), rule.getOperator(), e);
            return new RuleEvaluationResult(false, 0.0, "评估异常: " + e.getMessage());
        }
    }

    /**
     * 评估年龄规则
     */
    private RuleEvaluationResult evaluateAgeRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDemographics() == null || profile.getDemographics().getAge() == null) {
            return new RuleEvaluationResult(false, 0.0, "年龄信息缺失");
        }
        
        Integer userAge = profile.getDemographics().getAge();
        
        switch (rule.getOperator()) {
            case "eq":
                boolean matches = userAge.equals(Integer.valueOf(rule.getValue().toString()));
                return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0, 
                    String.format("年龄%s=%d", matches ? "匹配" : "不匹配", userAge));
            case "gt":
                boolean gtMatches = userAge > Integer.valueOf(rule.getValue().toString());
                return new RuleEvaluationResult(gtMatches, gtMatches ? 1.0 : 0.0,
                    String.format("年龄%s>%s", gtMatches ? "满足" : "不满足", rule.getValue()));
            case "lt":
                boolean ltMatches = userAge < Integer.valueOf(rule.getValue().toString());
                return new RuleEvaluationResult(ltMatches, ltMatches ? 1.0 : 0.0,
                    String.format("年龄%s<%s", ltMatches ? "满足" : "不满足", rule.getValue()));
            case "between":
                String[] range = rule.getValue().toString().split(",");
                if (range.length == 2) {
                    int minAge = Integer.parseInt(range[0].trim());
                    int maxAge = Integer.parseInt(range[1].trim());
                    boolean betweenMatches = userAge >= minAge && userAge <= maxAge;
                    return new RuleEvaluationResult(betweenMatches, betweenMatches ? 1.0 : 0.0,
                        String.format("年龄%s在[%d,%d]范围内", betweenMatches ? "" : "不", minAge, maxAge));
                }
                break;
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的年龄操作符: " + rule.getOperator());
    }

    /**
     * 评估性别规则
     */
    private RuleEvaluationResult evaluateGenderRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDemographics() == null || profile.getDemographics().getGender() == null) {
            return new RuleEvaluationResult(false, 0.0, "性别信息缺失");
        }
        
        String userGender = profile.getDemographics().getGender();
        
        if ("eq".equals(rule.getOperator())) {
            boolean matches = userGender.equalsIgnoreCase(rule.getValue().toString());
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("性别%s=%s", matches ? "匹配" : "不匹配", userGender));
        } else if ("in".equals(rule.getOperator())) {
            String[] values = rule.getValue().toString().split(",");
            boolean matches = Arrays.stream(values)
                .anyMatch(v -> v.trim().equalsIgnoreCase(userGender));
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("性别%s在列表中", matches ? "" : "不"));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的性别操作符: " + rule.getOperator());
    }

    /**
     * 评估国家规则
     */
    private RuleEvaluationResult evaluateCountryRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDemographics() == null || 
            profile.getDemographics().getGeo() == null || 
            profile.getDemographics().getGeo().getCountry() == null) {
            return new RuleEvaluationResult(false, 0.0, "国家信息缺失");
        }
        
        String userCountry = profile.getDemographics().getGeo().getCountry();
        
        if ("eq".equals(rule.getOperator())) {
            boolean matches = userCountry.equalsIgnoreCase(rule.getValue().toString());
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("国家%s=%s", matches ? "匹配" : "不匹配", userCountry));
        } else if ("in".equals(rule.getOperator())) {
            String[] values = rule.getValue().toString().split(",");
            boolean matches = Arrays.stream(values)
                .anyMatch(v -> v.trim().equalsIgnoreCase(userCountry));
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("国家%s在列表中", matches ? "" : "不"));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的国家操作符: " + rule.getOperator());
    }

    /**
     * 评估城市规则
     */
    private RuleEvaluationResult evaluateCityRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDemographics() == null || 
            profile.getDemographics().getGeo() == null || 
            profile.getDemographics().getGeo().getCity() == null) {
            return new RuleEvaluationResult(false, 0.0, "城市信息缺失");
        }
        
        String userCity = profile.getDemographics().getGeo().getCity();
        
        if ("eq".equals(rule.getOperator())) {
            boolean matches = userCity.equalsIgnoreCase(rule.getValue().toString());
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("城市%s=%s", matches ? "匹配" : "不匹配", userCity));
        } else if ("in".equals(rule.getOperator())) {
            String[] values = rule.getValue().toString().split(",");
            boolean matches = Arrays.stream(values)
                .anyMatch(v -> v.trim().equalsIgnoreCase(userCity));
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("城市%s在列表中", matches ? "" : "不"));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的城市操作符: " + rule.getOperator());
    }

    /**
     * 评估兴趣规则
     */
    private RuleEvaluationResult evaluateInterestRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getInterests() == null || profile.getInterests().isEmpty()) {
            return new RuleEvaluationResult(false, 0.0, "兴趣信息缺失");
        }
        
        if ("contains".equals(rule.getOperator())) {
            boolean matches = profile.getInterests().stream()
                .anyMatch(interest -> interest.getCategory().equalsIgnoreCase(rule.getValue().toString()));
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("兴趣%s包含%s", matches ? "" : "不", rule.getValue()));
        } else if ("score_gt".equals(rule.getOperator())) {
            String[] parts = rule.getValue().toString().split(":");
            if (parts.length == 2) {
                String category = parts[0];
                double threshold = Double.parseDouble(parts[1]);
                
                Optional<UserProfileEntity.Interest> interest = profile.getInterests().stream()
                    .filter(i -> i.getCategory().equalsIgnoreCase(category))
                    .findFirst();
                
                if (interest.isPresent() && interest.get().getScore() > threshold) {
                    return new RuleEvaluationResult(true, interest.get().getScore(),
                        String.format("兴趣%s分数%.2f>%.2f", category, interest.get().getScore(), threshold));
                }
            }
        }
        
        return new RuleEvaluationResult(false, 0.0, "兴趣规则不匹配");
    }

    /**
     * 评估设备类型规则
     */
    private RuleEvaluationResult evaluateDeviceTypeRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDeviceInfo() == null || profile.getDeviceInfo().getDeviceType() == null) {
            return new RuleEvaluationResult(false, 0.0, "设备类型信息缺失");
        }
        
        Integer userDeviceType = profile.getDeviceInfo().getDeviceType();
        
        if ("eq".equals(rule.getOperator())) {
            boolean matches = userDeviceType.equals(Integer.valueOf(rule.getValue().toString()));
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("设备类型%s=%s", matches ? "匹配" : "不匹配", userDeviceType));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的设备类型操作符: " + rule.getOperator());
    }

    /**
     * 评估操作系统规则
     */
    private RuleEvaluationResult evaluateOperatingSystemRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getDeviceInfo() == null || profile.getDeviceInfo().getOperatingSystem() == null) {
            return new RuleEvaluationResult(false, 0.0, "操作系统信息缺失");
        }
        
        String userOS = profile.getDeviceInfo().getOperatingSystem();
        
        if ("eq".equals(rule.getOperator())) {
            boolean matches = userOS.equalsIgnoreCase(rule.getValue().toString());
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("操作系统%s=%s", matches ? "匹配" : "不匹配", userOS));
        } else if ("contains".equals(rule.getOperator())) {
            boolean matches = userOS.toLowerCase().contains(rule.getValue().toString().toLowerCase());
            return new RuleEvaluationResult(matches, matches ? 1.0 : 0.0,
                String.format("操作系统%s包含%s", matches ? "" : "不", rule.getValue()));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的操作系统操作符: " + rule.getOperator());
    }

    /**
     * 评估购买金额规则
     */
    private RuleEvaluationResult evaluatePurchaseAmountRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getBehavior() == null || 
            profile.getBehavior().getPurchaseHistory() == null || 
            profile.getBehavior().getPurchaseHistory().isEmpty()) {
            return new RuleEvaluationResult(false, 0.0, "购买历史缺失");
        }
        
        double totalAmount = profile.getBehavior().getPurchaseHistory().stream()
            .mapToDouble(UserProfileEntity.Purchase::getAmount)
            .sum();
        
        if ("gt".equals(rule.getOperator())) {
            double threshold = Double.parseDouble(rule.getValue().toString());
            boolean matches = totalAmount > threshold;
            return new RuleEvaluationResult(matches, matches ? Math.min(totalAmount / threshold, 2.0) : 0.0,
                String.format("购买总额%.2f%s>%.2f", totalAmount, matches ? "" : "不", threshold));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的购买金额操作符: " + rule.getOperator());
    }

    /**
     * 评估会话数量规则
     */
    private RuleEvaluationResult evaluateSessionCountRule(UserProfileEntity profile, UserSegmentEntity.SegmentRule rule) {
        if (profile.getBehavior() == null || 
            profile.getBehavior().getSessionData() == null || 
            profile.getBehavior().getSessionData().getSessionCount() == null) {
            return new RuleEvaluationResult(false, 0.0, "会话数据缺失");
        }
        
        Integer sessionCount = profile.getBehavior().getSessionData().getSessionCount();
        
        if ("gt".equals(rule.getOperator())) {
            int threshold = Integer.parseInt(rule.getValue().toString());
            boolean matches = sessionCount > threshold;
            return new RuleEvaluationResult(matches, matches ? Math.min((double) sessionCount / threshold, 2.0) : 0.0,
                String.format("会话数%d%s>%d", sessionCount, matches ? "" : "不", threshold));
        }
        
        return new RuleEvaluationResult(false, 0.0, "不支持的会话数操作符: " + rule.getOperator());
    }

    /**
     * 根据分段规则查找候选用户
     */
    private List<UserProfileEntity> findCandidateUsers(UserSegmentEntity segment) {
        // 这里可以根据分段规则进行预筛选，提高匹配效率
        // 目前简单返回所有用户画像，实际应用中可以根据主要规则进行预筛选
        return userProfileService.findAll(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    /**
     * 创建或更新用户分段映射
     */
    private UserSegmentMappingEntity createOrUpdateMapping(
            String userId, UserSegmentEntity segment, SegmentMatchResult matchResult) {
        
        UserSegmentMappingEntity mapping = new UserSegmentMappingEntity();
        mapping.setUserId(userId);
        mapping.setSegmentId(segment.getId());
        mapping.setSegmentName(segment.getSegmentName());
        mapping.setSegmentType(segment.getSegmentType());
        mapping.setIsActive(true);
        mapping.setMatchScore(matchResult.getMatchScore());
        mapping.setConfidence(matchResult.getConfidence());
        
        // 设置属性
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("matchReason", matchResult.getReason());
        attributes.put("matchTime", LocalDateTime.now().toString());
        mapping.setAttributes(attributes);
        
        // 设置过期时间（如果分段有TTL设置）
        if (segment.getMetadata() != null && segment.getMetadata().containsKey("ttlDays")) {
            int ttlDays = Integer.parseInt(segment.getMetadata().get("ttlDays").toString());
            mapping.setExpiresAt(LocalDateTime.now().plusDays(ttlDays));
        }
        
        return mappingService.createMapping(mapping);
    }

    /**
     * 分段匹配结果
     */
    public static class SegmentMatchResult {
        private final boolean matched;
        private final double matchScore;
        private final double confidence;
        private final String reason;
        
        public SegmentMatchResult(boolean matched, double matchScore, double confidence, String reason) {
            this.matched = matched;
            this.matchScore = matchScore;
            this.confidence = confidence;
            this.reason = reason;
        }
        
        public boolean isMatched() { return matched; }
        public double getMatchScore() { return matchScore; }
        public double getConfidence() { return confidence; }
        public String getReason() { return reason; }
    }

    /**
     * 规则评估结果
     */
    public static class RuleEvaluationResult {
        private final boolean matched;
        private final double score;
        private final String reason;
        
        public RuleEvaluationResult(boolean matched, double score, String reason) {
            this.matched = matched;
            this.score = score;
            this.reason = reason;
        }
        
        public boolean isMatched() { return matched; }
        public double getScore() { return score; }
        public String getReason() { return reason; }
    }
}