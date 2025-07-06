package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PurchaseHistoryRepository {
    private final List<PurchaseHistory> purchases = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public PurchaseHistoryRepository() {
        // 初始化示例数据
        initializeData();
    }
    
    private void initializeData() {
        PurchaseHistory purchase1 = new PurchaseHistory(1L, "ORD20231201001", 123L, "iPhone 15 Pro",
                1, new BigDecimal("8999.00"), new BigDecimal("8999.00"));
        purchase1.setId(idGenerator.getAndIncrement());
        purchase1.setCategory("电子产品");
        purchase1.setBrand("Apple");
        purchase1.setSku("IPH15P-256-BLU");
        purchase1.setDiscountAmount(new BigDecimal("500.00"));
        purchase1.setActualPrice(new BigDecimal("8499.00"));
        purchase1.setPaymentMethod("支付宝");
        purchase1.setPaymentStatus("已支付");
        purchase1.setOrderStatus("已完成");
        purchase1.setDeliveryAddress("北京市朝阳区xxx街道xxx号");
        purchase1.setDeliveryMethod("快递");
        purchase1.setCourierCompany("顺丰速运");
        purchase1.setTrackingNumber("SF1234567890");
        purchase1.setCouponId("COUPON123");
        purchase1.setCouponName("新用户专享券");
        purchase1.setRating(5);
        purchase1.setReview("商品质量很好，物流很快");
        purchase1.setChannel("APP");
        purchase1.setPaymentTime(LocalDateTime.now().minusDays(5));
        purchase1.setShipmentTime(LocalDateTime.now().minusDays(4));
        purchase1.setCompletionTime(LocalDateTime.now().minusDays(2));
        purchases.add(purchase1);
        
        PurchaseHistory purchase2 = new PurchaseHistory(2L, "ORD20231202002", 124L, "MacBook Pro 14",
                1, new BigDecimal("15999.00"), new BigDecimal("15999.00"));
        purchase2.setId(idGenerator.getAndIncrement());
        purchase2.setCategory("电子产品");
        purchase2.setBrand("Apple");
        purchase2.setSku("MBP14-512-SLV");
        purchase2.setDiscountAmount(new BigDecimal("1000.00"));
        purchase2.setActualPrice(new BigDecimal("14999.00"));
        purchase2.setPaymentMethod("微信支付");
        purchase2.setPaymentStatus("已支付");
        purchase2.setOrderStatus("配送中");
        purchase2.setDeliveryAddress("上海市浦东新区yyy路yyy号");
        purchase2.setDeliveryMethod("快递");
        purchase2.setCourierCompany("京东物流");
        purchase2.setTrackingNumber("JD2345678901");
        purchase2.setCouponId("COUPON456");
        purchase2.setCouponName("双十一优惠券");
        purchase2.setChannel("官网");
        purchase2.setPaymentTime(LocalDateTime.now().minusDays(3));
        purchase2.setShipmentTime(LocalDateTime.now().minusDays(2));
        purchases.add(purchase2);
        
        PurchaseHistory purchase3 = new PurchaseHistory(3L, "ORD20231203003", 125L, "AirPods Pro 2",
                2, new BigDecimal("1899.00"), new BigDecimal("3798.00"));
        purchase3.setId(idGenerator.getAndIncrement());
        purchase3.setCategory("电子产品");
        purchase3.setBrand("Apple");
        purchase3.setSku("APP2-WHT");
        purchase3.setDiscountAmount(new BigDecimal("200.00"));
        purchase3.setActualPrice(new BigDecimal("3598.00"));
        purchase3.setPaymentMethod("银行卡");
        purchase3.setPaymentStatus("已支付");
        purchase3.setOrderStatus("已完成");
        purchase3.setDeliveryAddress("广州市天河区zzz大道zzz号");
        purchase3.setDeliveryMethod("快递");
        purchase3.setCourierCompany("圆通速递");
        purchase3.setTrackingNumber("YT3456789012");
        purchase3.setRating(4);
        purchase3.setReview("音质不错，但价格有点贵");
        purchase3.setChannel("小程序");
        purchase3.setPaymentTime(LocalDateTime.now().minusDays(7));
        purchase3.setShipmentTime(LocalDateTime.now().minusDays(6));
        purchase3.setCompletionTime(LocalDateTime.now().minusDays(4));
        purchases.add(purchase3);
        
        PurchaseHistory purchase4 = new PurchaseHistory(1L, "ORD20231204004", 126L, "iPad Air",
                1, new BigDecimal("4399.00"), new BigDecimal("4399.00"));
        purchase4.setId(idGenerator.getAndIncrement());
        purchase4.setCategory("电子产品");
        purchase4.setBrand("Apple");
        purchase4.setSku("IPA-64-BLU");
        purchase4.setActualPrice(new BigDecimal("4399.00"));
        purchase4.setPaymentMethod("支付宝");
        purchase4.setPaymentStatus("待支付");
        purchase4.setOrderStatus("待确认");
        purchase4.setDeliveryAddress("北京市朝阳区xxx街道xxx号");
        purchase4.setDeliveryMethod("快递");
        purchase4.setChannel("APP");
        purchases.add(purchase4);
    }
    
    /**
     * 查找所有购买履历
     */
    public List<PurchaseHistory> findAll() {
        return new ArrayList<>(purchases);
    }
    
    /**
     * 根据ID查找购买履历
     */
    public Optional<PurchaseHistory> findById(Long id) {
        return purchases.stream()
                .filter(purchase -> purchase.getId().equals(id))
                .findFirst();
    }
    
    /**
     * 根据用户ID查找购买履历
     */
    public List<PurchaseHistory> findByUserId(Long userId) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId))
                .toList();
    }
    
    /**
     * 根据订单号查找购买履历
     */
    public Optional<PurchaseHistory> findByOrderNumber(String orderNumber) {
        return purchases.stream()
                .filter(purchase -> orderNumber.equals(purchase.getOrderNumber()))
                .findFirst();
    }
    
    /**
     * 根据商品ID查找购买履历
     */
    public List<PurchaseHistory> findByProductId(Long productId) {
        return purchases.stream()
                .filter(purchase -> purchase.getProductId().equals(productId))
                .toList();
    }
    
    /**
     * 根据商品分类查找购买履历
     */
    public List<PurchaseHistory> findByCategory(String category) {
        return purchases.stream()
                .filter(purchase -> category.equals(purchase.getCategory()))
                .toList();
    }
    
    /**
     * 根据品牌查找购买履历
     */
    public List<PurchaseHistory> findByBrand(String brand) {
        return purchases.stream()
                .filter(purchase -> brand.equals(purchase.getBrand()))
                .toList();
    }
    
    /**
     * 根据支付状态查找购买履历
     */
    public List<PurchaseHistory> findByPaymentStatus(String paymentStatus) {
        return purchases.stream()
                .filter(purchase -> paymentStatus.equals(purchase.getPaymentStatus()))
                .toList();
    }
    
    /**
     * 根据订单状态查找购买履历
     */
    public List<PurchaseHistory> findByOrderStatus(String orderStatus) {
        return purchases.stream()
                .filter(purchase -> orderStatus.equals(purchase.getOrderStatus()))
                .toList();
    }
    
    /**
     * 根据用户ID和订单状态查找购买履历
     */
    public List<PurchaseHistory> findByUserIdAndOrderStatus(Long userId, String orderStatus) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId) && 
                        orderStatus.equals(purchase.getOrderStatus()))
                .toList();
    }
    
    /**
     * 根据时间范围查找购买履历
     */
    public List<PurchaseHistory> findByPurchaseTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return purchases.stream()
                .filter(purchase -> purchase.getPurchaseTime().isAfter(startTime) && 
                        purchase.getPurchaseTime().isBefore(endTime))
                .toList();
    }
    
    /**
     * 根据用户ID和时间范围查找购买履历
     */
    public List<PurchaseHistory> findByUserIdAndPurchaseTimeBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId) &&
                        purchase.getPurchaseTime().isAfter(startTime) && 
                        purchase.getPurchaseTime().isBefore(endTime))
                .toList();
    }
    
    /**
     * 根据价格范围查找购买履历
     */
    public List<PurchaseHistory> findByActualPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return purchases.stream()
                .filter(purchase -> purchase.getActualPrice().compareTo(minPrice) >= 0 && 
                        purchase.getActualPrice().compareTo(maxPrice) <= 0)
                .toList();
    }
    
    /**
     * 根据支付方式查找购买履历
     */
    public List<PurchaseHistory> findByPaymentMethod(String paymentMethod) {
        return purchases.stream()
                .filter(purchase -> paymentMethod.equals(purchase.getPaymentMethod()))
                .toList();
    }
    
    /**
     * 根据购买渠道查找购买履历
     */
    public List<PurchaseHistory> findByChannel(String channel) {
        return purchases.stream()
                .filter(purchase -> channel.equals(purchase.getChannel()))
                .toList();
    }
    
    /**
     * 获取用户最近的购买履历
     */
    public List<PurchaseHistory> findRecentByUserId(Long userId, int limit) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId))
                .sorted((p1, p2) -> p2.getPurchaseTime().compareTo(p1.getPurchaseTime()))
                .limit(limit)
                .toList();
    }
    
    /**
     * 获取用户购买总金额
     */
    public BigDecimal getTotalAmountByUserId(Long userId) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId) && 
                        "已支付".equals(purchase.getPaymentStatus()))
                .map(PurchaseHistory::getActualPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 获取用户购买次数
     */
    public long getPurchaseCountByUserId(Long userId) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId) && 
                        "已支付".equals(purchase.getPaymentStatus()))
                .count();
    }
    
    /**
     * 获取用户最喜欢的品牌
     */
    public Optional<String> getFavoriteBrandByUserId(Long userId) {
        return purchases.stream()
                .filter(purchase -> purchase.getUserId().equals(userId) && 
                        purchase.getBrand() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        PurchaseHistory::getBrand,
                        java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey);
    }
    
    /**
     * 保存购买履历
     */
    public PurchaseHistory save(PurchaseHistory purchase) {
        if (purchase.getId() == null) {
            // 新增
            purchase.setId(idGenerator.getAndIncrement());
            purchase.setCreatedAt(LocalDateTime.now());
            purchase.setUpdatedAt(LocalDateTime.now());
            purchases.add(purchase);
        } else {
            // 更新
            Optional<PurchaseHistory> existingPurchase = findById(purchase.getId());
            if (existingPurchase.isPresent()) {
                purchases.remove(existingPurchase.get());
                purchase.setUpdatedAt(LocalDateTime.now());
                purchases.add(purchase);
            } else {
                throw new RuntimeException("购买履历不存在，ID: " + purchase.getId());
            }
        }
        return purchase;
    }
    
    /**
     * 批量保存购买履历
     */
    public List<PurchaseHistory> saveAll(List<PurchaseHistory> purchaseList) {
        List<PurchaseHistory> savedPurchases = new ArrayList<>();
        for (PurchaseHistory purchase : purchaseList) {
            savedPurchases.add(save(purchase));
        }
        return savedPurchases;
    }
    
    /**
     * 删除购买履历
     */
    public boolean deleteById(Long id) {
        return purchases.removeIf(purchase -> purchase.getId().equals(id));
    }
    
    /**
     * 根据用户ID删除购买履历
     */
    public boolean deleteByUserId(Long userId) {
        return purchases.removeIf(purchase -> purchase.getUserId().equals(userId));
    }
    
    /**
     * 检查购买履历是否存在
     */
    public boolean existsById(Long id) {
        return purchases.stream().anyMatch(purchase -> purchase.getId().equals(id));
    }
    
    /**
     * 检查订单号是否存在
     */
    public boolean existsByOrderNumber(String orderNumber) {
        return purchases.stream().anyMatch(purchase -> orderNumber.equals(purchase.getOrderNumber()));
    }
    
    /**
     * 获取购买履历总数
     */
    public long count() {
        return purchases.size();
    }
}