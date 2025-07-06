package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import fei.song.play_spring_boot_api.users.infrastructure.PurchaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PurchaseHistoryService {
    
    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;
    
    /**
     * 获取所有购买履历
     */
    public List<PurchaseHistory> getAllPurchases() {
        return purchaseHistoryRepository.findAll();
    }
    
    /**
     * 根据ID获取购买履历
     */
    public PurchaseHistory getPurchaseById(Long id) {
        Optional<PurchaseHistory> purchase = purchaseHistoryRepository.findById(id);
        if (purchase.isEmpty()) {
            throw new RuntimeException("购买履历不存在，ID: " + id);
        }
        return purchase.get();
    }
    
    /**
     * 根据用户ID获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return purchaseHistoryRepository.findByUserId(userId);
    }
    
    /**
     * 根据订单号获取购买履历
     */
    public PurchaseHistory getPurchaseByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }
        Optional<PurchaseHistory> purchase = purchaseHistoryRepository.findByOrderNumber(orderNumber.trim());
        if (purchase.isEmpty()) {
            throw new RuntimeException("购买履历不存在，订单号: " + orderNumber);
        }
        return purchase.get();
    }
    
    /**
     * 根据商品ID获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        return purchaseHistoryRepository.findByProductId(Long.valueOf(productId.trim()));
    }
    
    /**
     * 根据商品分类获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("商品分类不能为空");
        }
        return purchaseHistoryRepository.findByCategory(category.trim());
    }
    
    /**
     * 根据品牌获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("品牌不能为空");
        }
        return purchaseHistoryRepository.findByBrand(brand.trim());
    }
    
    /**
     * 根据支付状态获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByPaymentStatus(String paymentStatus) {
        validatePaymentStatus(paymentStatus);
        return purchaseHistoryRepository.findByPaymentStatus(paymentStatus);
    }
    
    /**
     * 根据订单状态获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByOrderStatus(String orderStatus) {
        validateOrderStatus(orderStatus);
        return purchaseHistoryRepository.findByOrderStatus(orderStatus);
    }
    
    /**
     * 根据用户ID和订单状态获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByUserIdAndOrderStatus(Long userId, String orderStatus) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        validateOrderStatus(orderStatus);
        return purchaseHistoryRepository.findByUserIdAndOrderStatus(userId, orderStatus);
    }
    
    /**
     * 根据时间范围获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("时间范围不能为空");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        return purchaseHistoryRepository.findByPurchaseTimeBetween(startTime, endTime);
    }
    
    /**
     * 根据用户ID和时间范围获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("时间范围不能为空");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        return purchaseHistoryRepository.findByUserIdAndPurchaseTimeBetween(userId, startTime, endTime);
    }
    
    /**
     * 根据价格范围获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("价格范围不能为空");
        }
        if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("最小价格不能大于最大价格");
        }
        return purchaseHistoryRepository.findByActualPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * 根据支付方式获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("支付方式不能为空");
        }
        return purchaseHistoryRepository.findByPaymentMethod(paymentMethod.trim());
    }
    
    /**
     * 根据购买渠道获取购买履历列表
     */
    public List<PurchaseHistory> getPurchasesByChannel(String channel) {
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("购买渠道不能为空");
        }
        return purchaseHistoryRepository.findByChannel(channel.trim());
    }
    
    /**
     * 获取用户最近的购买履历
     */
    public List<PurchaseHistory> getRecentPurchasesByUserId(Long userId, int limit) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("限制数量必须大于0");
        }
        return purchaseHistoryRepository.findRecentByUserId(userId, limit);
    }
    
    /**
     * 获取用户购买总金额
     */
    public BigDecimal getUserTotalAmount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return purchaseHistoryRepository.getTotalAmountByUserId(userId);
    }
    
    /**
     * 获取用户购买次数
     */
    public long getUserPurchaseCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return purchaseHistoryRepository.getPurchaseCountByUserId(userId);
    }
    
    /**
     * 获取用户最喜欢的品牌
     */
    public String getUserFavoriteBrand(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        Optional<String> favoriteBrand = purchaseHistoryRepository.getFavoriteBrandByUserId(userId);
        return favoriteBrand.orElse(null);
    }
    
    /**
     * 创建购买履历
     */
    public PurchaseHistory createPurchase(PurchaseHistory purchase) {
        validatePurchase(purchase);
        
        // 检查订单号是否已存在
        if (purchase.getOrderNumber() != null && 
            purchaseHistoryRepository.existsByOrderNumber(purchase.getOrderNumber())) {
            throw new RuntimeException("订单号已存在: " + purchase.getOrderNumber());
        }
        
        // 设置创建时间
        if (purchase.getPurchaseTime() == null) {
            purchase.setPurchaseTime(LocalDateTime.now());
        }
        
        // 计算总金额
        if (purchase.getTotalPrice() == null && purchase.getUnitPrice() != null && purchase.getQuantity() != null) {
            BigDecimal totalAmount = purchase.getUnitPrice().multiply(BigDecimal.valueOf(purchase.getQuantity()));
            if (purchase.getDiscountAmount() != null) {
                totalAmount = totalAmount.subtract(purchase.getDiscountAmount());
            }
            purchase.setTotalPrice(totalAmount);
        }
        
        return purchaseHistoryRepository.save(purchase);
    }
    
    /**
     * 批量创建购买履历
     */
    public List<PurchaseHistory> createPurchases(List<PurchaseHistory> purchases) {
        if (purchases == null || purchases.isEmpty()) {
            throw new IllegalArgumentException("购买履历列表不能为空");
        }
        
        // 验证每个购买履历
        for (PurchaseHistory purchase : purchases) {
            validatePurchase(purchase);
            
            // 检查订单号是否已存在
            if (purchase.getOrderNumber() != null && 
                purchaseHistoryRepository.existsByOrderNumber(purchase.getOrderNumber())) {
                throw new RuntimeException("订单号已存在: " + purchase.getOrderNumber());
            }
            
            // 设置创建时间
            if (purchase.getPurchaseTime() == null) {
                purchase.setPurchaseTime(LocalDateTime.now());
            }
            
            // 计算总金额
            if (purchase.getTotalPrice() == null && purchase.getUnitPrice() != null && purchase.getQuantity() != null) {
                BigDecimal totalAmount = purchase.getUnitPrice().multiply(BigDecimal.valueOf(purchase.getQuantity()));
                if (purchase.getDiscountAmount() != null) {
                    totalAmount = totalAmount.subtract(purchase.getDiscountAmount());
                }
                purchase.setTotalPrice(totalAmount);
            }
        }
        
        return purchaseHistoryRepository.saveAll(purchases);
    }
    
    /**
     * 更新购买履历
     */
    public PurchaseHistory updatePurchase(Long id, PurchaseHistory purchase) {
        if (!purchaseHistoryRepository.existsById(id)) {
            throw new RuntimeException("购买履历不存在，ID: " + id);
        }
        
        validatePurchase(purchase);
        purchase.setId(id);
        
        // 重新计算总金额
        if (purchase.getUnitPrice() != null && purchase.getQuantity() != null) {
            BigDecimal totalAmount = purchase.getUnitPrice().multiply(BigDecimal.valueOf(purchase.getQuantity()));
            if (purchase.getDiscountAmount() != null) {
                totalAmount = totalAmount.subtract(purchase.getDiscountAmount());
            }
            purchase.setTotalPrice(totalAmount);
        }
        
        return purchaseHistoryRepository.save(purchase);
    }
    
    /**
     * 删除购买履历
     */
    public boolean deletePurchase(Long id) {
        if (!purchaseHistoryRepository.existsById(id)) {
            throw new RuntimeException("购买履历不存在，ID: " + id);
        }
        return purchaseHistoryRepository.deleteById(id);
    }
    
    /**
     * 删除用户的所有购买履历
     */
    public boolean deletePurchasesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return purchaseHistoryRepository.deleteByUserId(userId);
    }
    
    /**
     * 获取购买履历总数
     */
    public long getPurchaseCount() {
        return purchaseHistoryRepository.count();
    }
    
    /**
     * 获取用户购买统计
     */
    public Map<String, Object> getUserPurchaseStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        List<PurchaseHistory> userPurchases = purchaseHistoryRepository.findByUserId(userId);
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalPurchases", userPurchases.size());
        stats.put("totalAmount", getUserTotalAmount(userId));
        stats.put("favoriteBrand", getUserFavoriteBrand(userId));
        
        // 按分类统计
        Map<String, Long> categoryStats = userPurchases.stream()
                .collect(Collectors.groupingBy(
                        purchase -> purchase.getCategory() != null ? purchase.getCategory() : "未分类",
                        Collectors.counting()
                ));
        stats.put("categoryStats", categoryStats);
        
        // 按支付方式统计
        Map<String, Long> paymentMethodStats = userPurchases.stream()
                .collect(Collectors.groupingBy(
                        purchase -> purchase.getPaymentMethod() != null ? purchase.getPaymentMethod() : "未知",
                        Collectors.counting()
                ));
        stats.put("paymentMethodStats", paymentMethodStats);
        
        return stats;
    }
    
    /**
     * 验证购买履历数据
     */
    private void validatePurchase(PurchaseHistory purchase) {
        if (purchase == null) {
            throw new IllegalArgumentException("购买履历不能为空");
        }
        
        if (purchase.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (purchase.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        if (purchase.getProductName() == null || purchase.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        
        if (purchase.getUnitPrice() == null || purchase.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("单价不能为空或负数");
        }
        
        if (purchase.getQuantity() == null || purchase.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        
        if (purchase.getDiscountAmount() != null && purchase.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("优惠金额不能为负数");
        }
        
        if (purchase.getPaymentStatus() != null) {
            validatePaymentStatus(purchase.getPaymentStatus());
        }
        
        if (purchase.getOrderStatus() != null) {
            validateOrderStatus(purchase.getOrderStatus());
        }
        
        if (purchase.getPurchaseTime() != null && purchase.getPurchaseTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("购买时间不能是未来时间");
        }
    }
    
    /**
     * 验证支付状态
     */
    private void validatePaymentStatus(String paymentStatus) {
        if (paymentStatus != null && !paymentStatus.matches("^(待支付|已支付|支付失败|已退款)$")) {
            throw new IllegalArgumentException("支付状态只能是：待支付、已支付、支付失败、已退款");
        }
    }
    
    /**
     * 验证订单状态
     */
    private void validateOrderStatus(String orderStatus) {
        if (orderStatus != null && !orderStatus.matches("^(待确认|已确认|配送中|已送达|已取消|已退货)$")) {
            throw new IllegalArgumentException("订单状态只能是：待确认、已确认、配送中、已送达、已取消、已退货");
        }
    }
}