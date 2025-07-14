package fei.song.play_spring_boot_api.users.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

class PurchaseHistoryTest {

    private PurchaseHistory purchaseHistory;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        purchaseHistory = new PurchaseHistory();
    }

    @Test
    void testDefaultConstructor() {
        PurchaseHistory purchase = new PurchaseHistory();
        assertNotNull(purchase);
        assertNull(purchase.getId());
        assertNull(purchase.getUserId());
        assertNull(purchase.getOrderNumber());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        PurchaseHistory purchase = new PurchaseHistory(
            1L, 100L, "ORD001", 123L, "iPhone 15", "电子产品", "Apple", "IPH15-256",
            1, new BigDecimal("8999.00"), new BigDecimal("8999.00"), new BigDecimal("500.00"),
            new BigDecimal("8499.00"), "支付宝", "已支付", "已完成", "北京市朝阳区",
            "快递", "顺丰", "SF123", "COUPON001", "新用户券", 5, "很好",
            "官网", "SALES001", "备注", now, now, now, now, now, now
        );
        
        assertEquals(1L, purchase.getId());
        assertEquals(100L, purchase.getUserId());
        assertEquals("ORD001", purchase.getOrderNumber());
        assertEquals(123L, purchase.getProductId());
        assertEquals("iPhone 15", purchase.getProductName());
        assertEquals("电子产品", purchase.getCategory());
        assertEquals("Apple", purchase.getBrand());
        assertEquals(new BigDecimal("8999.00"), purchase.getUnitPrice());
        assertEquals(new BigDecimal("8499.00"), purchase.getActualPrice());
        assertEquals("支付宝", purchase.getPaymentMethod());
        assertEquals("已支付", purchase.getPaymentStatus());
        assertEquals("已完成", purchase.getOrderStatus());
    }

    @Test
    void testBuilderPattern() {
        PurchaseHistory purchase = PurchaseHistory.builder()
            .id(1L)
            .userId(100L)
            .orderNumber("ORD001")
            .productId(123L)
            .productName("iPhone 15")
            .category("电子产品")
            .brand("Apple")
            .quantity(1)
            .unitPrice(new BigDecimal("8999.00"))
            .totalPrice(new BigDecimal("8999.00"))
            .actualPrice(new BigDecimal("8499.00"))
            .paymentMethod("支付宝")
            .paymentStatus("已支付")
            .orderStatus("已完成")
            .build();
        
        assertEquals(1L, purchase.getId());
        assertEquals(100L, purchase.getUserId());
        assertEquals("ORD001", purchase.getOrderNumber());
        assertEquals("iPhone 15", purchase.getProductName());
        assertEquals(new BigDecimal("8999.00"), purchase.getUnitPrice());
        assertEquals(new BigDecimal("8499.00"), purchase.getActualPrice());
    }

    @Test
    void testCustomConstructorWithDefaultValues() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        PurchaseHistory purchase = new PurchaseHistory(
            100L, "ORD001", 123L, "iPhone 15", 1, 
            new BigDecimal("8999.00"), new BigDecimal("8999.00")
        );
        
        LocalDateTime afterCreation = LocalDateTime.now();
        
        assertEquals(100L, purchase.getUserId());
        assertEquals("ORD001", purchase.getOrderNumber());
        assertEquals(123L, purchase.getProductId());
        assertEquals("iPhone 15", purchase.getProductName());
        assertEquals(1, purchase.getQuantity());
        assertEquals(new BigDecimal("8999.00"), purchase.getUnitPrice());
        assertEquals(new BigDecimal("8999.00"), purchase.getTotalPrice());
        assertEquals(new BigDecimal("8999.00"), purchase.getActualPrice()); // 默认等于总价
        
        // 验证时间戳设置
        assertNotNull(purchase.getCreatedAt());
        assertNotNull(purchase.getUpdatedAt());
        assertNotNull(purchase.getPurchaseTime());
        assertTrue(purchase.getCreatedAt().isAfter(beforeCreation) || purchase.getCreatedAt().isEqual(beforeCreation));
        assertTrue(purchase.getCreatedAt().isBefore(afterCreation) || purchase.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testUpdateTimestamp() {
        LocalDateTime originalTime = LocalDateTime.now().minusMinutes(1);
        purchaseHistory.setUpdatedAt(originalTime);
        
        // 等待一小段时间确保时间戳不同
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        purchaseHistory.updateTimestamp();
        
        assertNotNull(purchaseHistory.getUpdatedAt());
        assertTrue(purchaseHistory.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void testSettersAndGetters() {
        purchaseHistory.setId(1L);
        purchaseHistory.setUserId(100L);
        purchaseHistory.setOrderNumber("ORD001");
        purchaseHistory.setProductId(123L);
        purchaseHistory.setProductName("iPhone 15");
        purchaseHistory.setCategory("电子产品");
        purchaseHistory.setBrand("Apple");
        purchaseHistory.setSku("IPH15-256");
        purchaseHistory.setQuantity(1);
        purchaseHistory.setUnitPrice(new BigDecimal("8999.00"));
        purchaseHistory.setTotalPrice(new BigDecimal("8999.00"));
        purchaseHistory.setDiscountAmount(new BigDecimal("500.00"));
        purchaseHistory.setActualPrice(new BigDecimal("8499.00"));
        purchaseHistory.setPaymentMethod("支付宝");
        purchaseHistory.setPaymentStatus("已支付");
        purchaseHistory.setOrderStatus("已完成");
        purchaseHistory.setDeliveryAddress("北京市朝阳区");
        purchaseHistory.setDeliveryMethod("快递");
        purchaseHistory.setCourierCompany("顺丰");
        purchaseHistory.setTrackingNumber("SF123");
        purchaseHistory.setCouponId("COUPON001");
        purchaseHistory.setCouponName("新用户券");
        purchaseHistory.setRating(5);
        purchaseHistory.setReview("很好");
        purchaseHistory.setChannel("官网");
        purchaseHistory.setSalesPersonId("SALES001");
        purchaseHistory.setRemarks("备注");
        purchaseHistory.setPurchaseTime(testTime);
        purchaseHistory.setPaymentTime(testTime);
        purchaseHistory.setShipmentTime(testTime);
        purchaseHistory.setCompletionTime(testTime);
        purchaseHistory.setCreatedAt(testTime);
        purchaseHistory.setUpdatedAt(testTime);
        
        assertEquals(1L, purchaseHistory.getId());
        assertEquals(100L, purchaseHistory.getUserId());
        assertEquals("ORD001", purchaseHistory.getOrderNumber());
        assertEquals(123L, purchaseHistory.getProductId());
        assertEquals("iPhone 15", purchaseHistory.getProductName());
        assertEquals("电子产品", purchaseHistory.getCategory());
        assertEquals("Apple", purchaseHistory.getBrand());
        assertEquals("IPH15-256", purchaseHistory.getSku());
        assertEquals(1, purchaseHistory.getQuantity());
        assertEquals(new BigDecimal("8999.00"), purchaseHistory.getUnitPrice());
        assertEquals(new BigDecimal("8999.00"), purchaseHistory.getTotalPrice());
        assertEquals(new BigDecimal("500.00"), purchaseHistory.getDiscountAmount());
        assertEquals(new BigDecimal("8499.00"), purchaseHistory.getActualPrice());
        assertEquals("支付宝", purchaseHistory.getPaymentMethod());
        assertEquals("已支付", purchaseHistory.getPaymentStatus());
        assertEquals("已完成", purchaseHistory.getOrderStatus());
        assertEquals("北京市朝阳区", purchaseHistory.getDeliveryAddress());
        assertEquals("快递", purchaseHistory.getDeliveryMethod());
        assertEquals("顺丰", purchaseHistory.getCourierCompany());
        assertEquals("SF123", purchaseHistory.getTrackingNumber());
        assertEquals("COUPON001", purchaseHistory.getCouponId());
        assertEquals("新用户券", purchaseHistory.getCouponName());
        assertEquals(5, purchaseHistory.getRating());
        assertEquals("很好", purchaseHistory.getReview());
        assertEquals("官网", purchaseHistory.getChannel());
        assertEquals("SALES001", purchaseHistory.getSalesPersonId());
        assertEquals("备注", purchaseHistory.getRemarks());
        assertEquals(testTime, purchaseHistory.getPurchaseTime());
        assertEquals(testTime, purchaseHistory.getPaymentTime());
        assertEquals(testTime, purchaseHistory.getShipmentTime());
        assertEquals(testTime, purchaseHistory.getCompletionTime());
        assertEquals(testTime, purchaseHistory.getCreatedAt());
        assertEquals(testTime, purchaseHistory.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        PurchaseHistory purchase1 = PurchaseHistory.builder()
            .id(1L)
            .userId(100L)
            .orderNumber("ORD001")
            .productName("iPhone 15")
            .build();
        
        PurchaseHistory purchase2 = PurchaseHistory.builder()
            .id(1L)
            .userId(100L)
            .orderNumber("ORD001")
            .productName("iPhone 15")
            .build();
        
        PurchaseHistory purchase3 = PurchaseHistory.builder()
            .id(2L)
            .userId(100L)
            .orderNumber("ORD002")
            .productName("iPhone 15")
            .build();
        
        assertEquals(purchase1, purchase2);
        assertEquals(purchase1.hashCode(), purchase2.hashCode());
        assertNotEquals(purchase1, purchase3);
        assertNotEquals(purchase1.hashCode(), purchase3.hashCode());
    }

    @Test
    void testToString() {
        purchaseHistory.setId(1L);
        purchaseHistory.setOrderNumber("ORD001");
        purchaseHistory.setProductName("iPhone 15");
        
        String toString = purchaseHistory.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("PurchaseHistory"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("orderNumber=ORD001"));
        assertTrue(toString.contains("productName=iPhone 15"));
    }
}