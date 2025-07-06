package fei.song.play_spring_boot_api.users.application;

import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import fei.song.play_spring_boot_api.users.infrastructure.PurchaseHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseHistoryServiceTest {

    @Mock
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @InjectMocks
    private PurchaseHistoryService purchaseHistoryService;

    private PurchaseHistory testPurchase;
    private List<PurchaseHistory> testPurchases;

    @BeforeEach
    void setUp() {
        testPurchase = PurchaseHistory.builder()
                .id(1L)
                .userId(1L)
                .orderNumber("ORD20231201001")
                .productId(123L)
                .productName("iPhone 15 Pro")
                .category("电子产品")
                .brand("Apple")
                .sku("IPH15P-256-BLU")
                .quantity(1)
                .unitPrice(new BigDecimal("8999.00"))
                .totalPrice(new BigDecimal("8999.00"))
                .discountAmount(new BigDecimal("500.00"))
                .actualPrice(new BigDecimal("8499.00"))
                .paymentMethod("支付宝")
                .paymentStatus("已支付")
                .orderStatus("已确认")
                .deliveryAddress("北京市朝阳区xxx街道xxx号")
                .deliveryMethod("快递")
                .courierCompany("顺丰速运")
                .trackingNumber("SF1234567890")
                .couponId("COUPON123")
                .couponName("新用户专享券")
                .rating(5)
                .review("商品质量很好，物流很快")
                .channel("官网")
                .salesPersonId("SALES001")
                .remarks("客户要求加急配送")
                .purchaseTime(LocalDateTime.now())
                .paymentTime(LocalDateTime.now())
                .shipmentTime(LocalDateTime.now())
                .completionTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testPurchases = Arrays.asList(
                testPurchase,
                PurchaseHistory.builder()
                        .id(2L)
                        .userId(2L)
                        .orderNumber("ORD20231201002")
                        .productId(124L)
                        .productName("MacBook Pro")
                        .category("电子产品")
                        .brand("Apple")
                        .quantity(1)
                        .unitPrice(new BigDecimal("15999.00"))
                        .totalPrice(new BigDecimal("15999.00"))
                        .actualPrice(new BigDecimal("15999.00"))
                        .paymentMethod("微信支付")
                        .paymentStatus("已支付")
                        .orderStatus("配送中")
                        .channel("APP")
                        .purchaseTime(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    void getAllPurchases_ShouldReturnAllPurchases() {
        // Given
        when(purchaseHistoryRepository.findAll()).thenReturn(testPurchases);

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getAllPurchases();

        // Then
        assertEquals(2, result.size());
        verify(purchaseHistoryRepository).findAll();
    }

    @Test
    void getPurchaseById_WithValidId_ShouldReturnPurchase() {
        // Given
        when(purchaseHistoryRepository.findById(1L)).thenReturn(Optional.of(testPurchase));

        // When
        PurchaseHistory result = purchaseHistoryService.getPurchaseById(1L);

        // Then
        assertEquals(testPurchase, result);
        verify(purchaseHistoryRepository).findById(1L);
    }

    @Test
    void getPurchaseById_WithInvalidId_ShouldThrowException() {
        // Given
        when(purchaseHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseHistoryService.getPurchaseById(999L));
        assertEquals("购买履历不存在，ID: 999", exception.getMessage());
    }

    @Test
    void getPurchasesByUserId_WithValidUserId_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByUserId(1L)).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testPurchase, result.get(0));
        verify(purchaseHistoryRepository).findByUserId(1L);
    }

    @Test
    void getPurchasesByUserId_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByUserId(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getPurchaseByOrderNumber_WithValidOrderNumber_ShouldReturnPurchase() {
        // Given
        when(purchaseHistoryRepository.findByOrderNumber("ORD20231201001"))
                .thenReturn(Optional.of(testPurchase));

        // When
        PurchaseHistory result = purchaseHistoryService.getPurchaseByOrderNumber("ORD20231201001");

        // Then
        assertEquals(testPurchase, result);
        verify(purchaseHistoryRepository).findByOrderNumber("ORD20231201001");
    }

    @Test
    void getPurchaseByOrderNumber_WithInvalidOrderNumber_ShouldThrowException() {
        // Given
        when(purchaseHistoryRepository.findByOrderNumber("INVALID")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseHistoryService.getPurchaseByOrderNumber("INVALID"));
        assertEquals("购买履历不存在，订单号: INVALID", exception.getMessage());
    }

    @Test
    void getPurchaseByOrderNumber_WithNullOrderNumber_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchaseByOrderNumber(null));
        assertEquals("订单号不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByProductId_WithValidProductId_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByProductId(123L)).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByProductId("123");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByProductId(123L);
    }

    @Test
    void getPurchasesByProductId_WithNullProductId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByProductId(null));
        assertEquals("商品ID不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByCategory_WithValidCategory_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByCategory("电子产品")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByCategory("电子产品");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByCategory("电子产品");
    }

    @Test
    void getPurchasesByCategory_WithNullCategory_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByCategory(null));
        assertEquals("商品分类不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByBrand_WithValidBrand_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByBrand("Apple")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByBrand("Apple");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByBrand("Apple");
    }

    @Test
    void getPurchasesByBrand_WithNullBrand_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByBrand(null));
        assertEquals("品牌不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByPaymentStatus_WithValidStatus_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByPaymentStatus("已支付")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByPaymentStatus("已支付");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByPaymentStatus("已支付");
    }

    @Test
    void getPurchasesByPaymentStatus_WithInvalidStatus_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByPaymentStatus("无效状态"));
        assertEquals("支付状态只能是：待支付、已支付、支付失败、已退款", exception.getMessage());
    }

    @Test
    void getPurchasesByOrderStatus_WithValidStatus_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByOrderStatus("已确认")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByOrderStatus("已确认");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByOrderStatus("已确认");
    }

    @Test
    void getPurchasesByOrderStatus_WithInvalidStatus_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByOrderStatus("无效状态"));
        assertEquals("订单状态只能是：待确认、已确认、配送中、已送达、已取消、已退货", exception.getMessage());
    }

    @Test
    void getPurchasesByUserIdAndOrderStatus_WithValidParameters_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByUserIdAndOrderStatus(1L, "已确认"))
                .thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByUserIdAndOrderStatus(1L, "已确认");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByUserIdAndOrderStatus(1L, "已确认");
    }

    @Test
    void getPurchasesByUserIdAndOrderStatus_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByUserIdAndOrderStatus(null, "已确认"));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByTimeRange_WithValidRange_ShouldReturnPurchases() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(purchaseHistoryRepository.findByPurchaseTimeBetween(startTime, endTime))
                .thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByTimeRange(startTime, endTime);

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByPurchaseTimeBetween(startTime, endTime);
    }

    @Test
    void getPurchasesByTimeRange_WithNullStartTime_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByTimeRange(null, LocalDateTime.now()));
        assertEquals("时间范围不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByTimeRange_WithInvalidRange_ShouldThrowException() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByTimeRange(startTime, endTime));
        assertEquals("开始时间不能晚于结束时间", exception.getMessage());
    }

    @Test
    void getPurchasesByUserIdAndTimeRange_WithValidParameters_ShouldReturnPurchases() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(purchaseHistoryRepository.findByUserIdAndPurchaseTimeBetween(1L, startTime, endTime))
                .thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByUserIdAndTimeRange(1L, startTime, endTime);

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByUserIdAndPurchaseTimeBetween(1L, startTime, endTime);
    }

    @Test
    void getPurchasesByUserIdAndTimeRange_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByUserIdAndTimeRange(null, LocalDateTime.now().minusDays(1), LocalDateTime.now()));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByPriceRange_WithValidRange_ShouldReturnPurchases() {
        // Given
        BigDecimal minPrice = new BigDecimal("1000.00");
        BigDecimal maxPrice = new BigDecimal("10000.00");
        when(purchaseHistoryRepository.findByActualPriceBetween(minPrice, maxPrice))
                .thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByPriceRange(minPrice, maxPrice);

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByActualPriceBetween(minPrice, maxPrice);
    }

    @Test
    void getPurchasesByPriceRange_WithNullMinPrice_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByPriceRange(null, new BigDecimal("10000.00")));
        assertEquals("价格范围不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByPriceRange_WithNegativePrice_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByPriceRange(new BigDecimal("-100.00"), new BigDecimal("10000.00")));
        assertEquals("价格不能为负数", exception.getMessage());
    }

    @Test
    void getPurchasesByPriceRange_WithInvalidRange_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByPriceRange(new BigDecimal("10000.00"), new BigDecimal("1000.00")));
        assertEquals("最小价格不能大于最大价格", exception.getMessage());
    }

    @Test
    void getPurchasesByPaymentMethod_WithValidMethod_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByPaymentMethod("支付宝")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByPaymentMethod("支付宝");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByPaymentMethod("支付宝");
    }

    @Test
    void getPurchasesByPaymentMethod_WithNullMethod_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByPaymentMethod(null));
        assertEquals("支付方式不能为空", exception.getMessage());
    }

    @Test
    void getPurchasesByChannel_WithValidChannel_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findByChannel("官网")).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getPurchasesByChannel("官网");

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findByChannel("官网");
    }

    @Test
    void getPurchasesByChannel_WithNullChannel_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getPurchasesByChannel(null));
        assertEquals("购买渠道不能为空", exception.getMessage());
    }

    @Test
    void getRecentPurchasesByUserId_WithValidParameters_ShouldReturnPurchases() {
        // Given
        when(purchaseHistoryRepository.findRecentByUserId(1L, 5)).thenReturn(Arrays.asList(testPurchase));

        // When
        List<PurchaseHistory> result = purchaseHistoryService.getRecentPurchasesByUserId(1L, 5);

        // Then
        assertEquals(1, result.size());
        verify(purchaseHistoryRepository).findRecentByUserId(1L, 5);
    }

    @Test
    void getRecentPurchasesByUserId_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getRecentPurchasesByUserId(null, 5));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getRecentPurchasesByUserId_WithInvalidLimit_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getRecentPurchasesByUserId(1L, 0));
        assertEquals("限制数量必须大于0", exception.getMessage());
    }

    @Test
    void getUserTotalAmount_WithValidUserId_ShouldReturnAmount() {
        // Given
        BigDecimal totalAmount = new BigDecimal("25000.00");
        when(purchaseHistoryRepository.getTotalAmountByUserId(1L)).thenReturn(totalAmount);

        // When
        BigDecimal result = purchaseHistoryService.getUserTotalAmount(1L);

        // Then
        assertEquals(totalAmount, result);
        verify(purchaseHistoryRepository).getTotalAmountByUserId(1L);
    }

    @Test
    void getUserTotalAmount_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getUserTotalAmount(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getUserPurchaseCount_WithValidUserId_ShouldReturnCount() {
        // Given
        when(purchaseHistoryRepository.getPurchaseCountByUserId(1L)).thenReturn(5L);

        // When
        long result = purchaseHistoryService.getUserPurchaseCount(1L);

        // Then
        assertEquals(5L, result);
        verify(purchaseHistoryRepository).getPurchaseCountByUserId(1L);
    }

    @Test
    void getUserPurchaseCount_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getUserPurchaseCount(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getUserFavoriteBrand_WithValidUserId_ShouldReturnBrand() {
        // Given
        when(purchaseHistoryRepository.getFavoriteBrandByUserId(1L)).thenReturn(Optional.of("Apple"));

        // When
        String result = purchaseHistoryService.getUserFavoriteBrand(1L);

        // Then
        assertEquals("Apple", result);
        verify(purchaseHistoryRepository).getFavoriteBrandByUserId(1L);
    }

    @Test
    void getUserFavoriteBrand_WithNoFavoriteBrand_ShouldReturnNull() {
        // Given
        when(purchaseHistoryRepository.getFavoriteBrandByUserId(1L)).thenReturn(Optional.empty());

        // When
        String result = purchaseHistoryService.getUserFavoriteBrand(1L);

        // Then
        assertNull(result);
        verify(purchaseHistoryRepository).getFavoriteBrandByUserId(1L);
    }

    @Test
    void getUserFavoriteBrand_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getUserFavoriteBrand(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void createPurchase_WithValidPurchase_ShouldReturnSavedPurchase() {
        // Given
        PurchaseHistory newPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .build();
        when(purchaseHistoryRepository.existsByOrderNumber("ORD20231201003")).thenReturn(false);
        when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenReturn(newPurchase);

        // When
        PurchaseHistory result = purchaseHistoryService.createPurchase(newPurchase);

        // Then
        assertNotNull(result);
        assertNotNull(newPurchase.getPurchaseTime());
        assertNotNull(newPurchase.getTotalPrice());
        verify(purchaseHistoryRepository).save(newPurchase);
    }

    @Test
    void createPurchase_WithExistingOrderNumber_ShouldThrowException() {
        // Given
        PurchaseHistory newPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201001")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .build();
        when(purchaseHistoryRepository.existsByOrderNumber("ORD20231201001")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseHistoryService.createPurchase(newPurchase));
        assertEquals("订单号已存在: ORD20231201001", exception.getMessage());
    }

    @Test
    void createPurchase_WithNullPurchase_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(null));
        assertEquals("购买履历不能为空", exception.getMessage());
    }

    @Test
    void createPurchase_WithNullUserId_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void createPurchase_WithNullProductId_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("商品ID不能为空", exception.getMessage());
    }

    @Test
    void createPurchase_WithNullProductName_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("商品名称不能为空", exception.getMessage());
    }

    @Test
    void createPurchase_WithNegativeUnitPrice_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("-100.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("单价不能为空或负数", exception.getMessage());
    }

    @Test
    void createPurchase_WithInvalidQuantity_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(0)
                .unitPrice(new BigDecimal("6999.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("数量必须大于0", exception.getMessage());
    }

    @Test
    void createPurchase_WithNegativeDiscountAmount_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .discountAmount(new BigDecimal("-100.00"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("优惠金额不能为负数", exception.getMessage());
    }

    @Test
    void createPurchase_WithInvalidPaymentStatus_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .paymentStatus("无效状态")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("支付状态只能是：待支付、已支付、支付失败、已退款", exception.getMessage());
    }

    @Test
    void createPurchase_WithInvalidOrderStatus_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .orderStatus("无效状态")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("订单状态只能是：待确认、已确认、配送中、已送达、已取消、已退货", exception.getMessage());
    }

    @Test
    void createPurchase_WithFuturePurchaseTime_ShouldThrowException() {
        // Given
        PurchaseHistory invalidPurchase = PurchaseHistory.builder()
                .userId(1L)
                .orderNumber("ORD20231201003")
                .productId(125L)
                .productName("iPad Pro")
                .quantity(1)
                .unitPrice(new BigDecimal("6999.00"))
                .purchaseTime(LocalDateTime.now().plusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchase(invalidPurchase));
        assertEquals("购买时间不能是未来时间", exception.getMessage());
    }

    @Test
    void createPurchases_WithValidPurchases_ShouldReturnSavedPurchases() {
        // Given
        List<PurchaseHistory> newPurchases = Arrays.asList(
                PurchaseHistory.builder().userId(1L).orderNumber("ORD001").productId(1L).productName("Product1").quantity(1).unitPrice(new BigDecimal("100.00")).build(),
                PurchaseHistory.builder().userId(2L).orderNumber("ORD002").productId(2L).productName("Product2").quantity(2).unitPrice(new BigDecimal("200.00")).build()
        );
        when(purchaseHistoryRepository.existsByOrderNumber(anyString())).thenReturn(false);
        when(purchaseHistoryRepository.saveAll(anyList())).thenReturn(newPurchases);

        // When
        List<PurchaseHistory> result = purchaseHistoryService.createPurchases(newPurchases);

        // Then
        assertEquals(2, result.size());
        verify(purchaseHistoryRepository).saveAll(newPurchases);
    }

    @Test
    void createPurchases_WithNullList_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchases(null));
        assertEquals("购买履历列表不能为空", exception.getMessage());
    }

    @Test
    void createPurchases_WithEmptyList_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.createPurchases(new ArrayList<>()));
        assertEquals("购买履历列表不能为空", exception.getMessage());
    }

    @Test
    void updatePurchase_WithValidData_ShouldReturnUpdatedPurchase() {
        // Given
        when(purchaseHistoryRepository.existsById(1L)).thenReturn(true);
        when(purchaseHistoryRepository.save(any(PurchaseHistory.class))).thenReturn(testPurchase);

        // When
        PurchaseHistory result = purchaseHistoryService.updatePurchase(1L, testPurchase);

        // Then
        assertEquals(testPurchase, result);
        assertEquals(1L, testPurchase.getId());
        verify(purchaseHistoryRepository).existsById(1L);
        verify(purchaseHistoryRepository).save(testPurchase);
    }

    @Test
    void updatePurchase_WithNonExistentId_ShouldThrowException() {
        // Given
        when(purchaseHistoryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseHistoryService.updatePurchase(999L, testPurchase));
        assertEquals("购买履历不存在，ID: 999", exception.getMessage());
    }

    @Test
    void deletePurchase_WithValidId_ShouldReturnTrue() {
        // Given
        when(purchaseHistoryRepository.existsById(1L)).thenReturn(true);
        when(purchaseHistoryRepository.deleteById(1L)).thenReturn(true);

        // When
        boolean result = purchaseHistoryService.deletePurchase(1L);

        // Then
        assertTrue(result);
        verify(purchaseHistoryRepository).existsById(1L);
        verify(purchaseHistoryRepository).deleteById(1L);
    }

    @Test
    void deletePurchase_WithNonExistentId_ShouldThrowException() {
        // Given
        when(purchaseHistoryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> purchaseHistoryService.deletePurchase(999L));
        assertEquals("购买履历不存在，ID: 999", exception.getMessage());
    }

    @Test
    void deletePurchasesByUserId_WithValidUserId_ShouldReturnTrue() {
        // Given
        when(purchaseHistoryRepository.deleteByUserId(1L)).thenReturn(true);

        // When
        boolean result = purchaseHistoryService.deletePurchasesByUserId(1L);

        // Then
        assertTrue(result);
        verify(purchaseHistoryRepository).deleteByUserId(1L);
    }

    @Test
    void deletePurchasesByUserId_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.deletePurchasesByUserId(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }

    @Test
    void getPurchaseCount_ShouldReturnCount() {
        // Given
        when(purchaseHistoryRepository.count()).thenReturn(100L);

        // When
        long result = purchaseHistoryService.getPurchaseCount();

        // Then
        assertEquals(100L, result);
        verify(purchaseHistoryRepository).count();
    }

    @Test
    void getUserPurchaseStats_WithValidUserId_ShouldReturnStats() {
        // Given
        List<PurchaseHistory> userPurchases = Arrays.asList(
                PurchaseHistory.builder().category("电子产品").paymentMethod("支付宝").build(),
                PurchaseHistory.builder().category("服装").paymentMethod("微信支付").build(),
                PurchaseHistory.builder().category("电子产品").paymentMethod("支付宝").build()
        );
        when(purchaseHistoryRepository.findByUserId(1L)).thenReturn(userPurchases);
        when(purchaseHistoryRepository.getTotalAmountByUserId(1L)).thenReturn(new BigDecimal("25000.00"));
        when(purchaseHistoryRepository.getFavoriteBrandByUserId(1L)).thenReturn(Optional.of("Apple"));

        // When
        Map<String, Object> result = purchaseHistoryService.getUserPurchaseStats(1L);

        // Then
        assertEquals(3, result.get("totalPurchases"));
        assertEquals(new BigDecimal("25000.00"), result.get("totalAmount"));
        assertEquals("Apple", result.get("favoriteBrand"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> categoryStats = (Map<String, Long>) result.get("categoryStats");
        assertEquals(2L, categoryStats.get("电子产品"));
        assertEquals(1L, categoryStats.get("服装"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> paymentMethodStats = (Map<String, Long>) result.get("paymentMethodStats");
        assertEquals(2L, paymentMethodStats.get("支付宝"));
        assertEquals(1L, paymentMethodStats.get("微信支付"));
        
        verify(purchaseHistoryRepository).findByUserId(1L);
    }

    @Test
    void getUserPurchaseStats_WithNullUserId_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> purchaseHistoryService.getUserPurchaseStats(null));
        assertEquals("用户ID不能为空", exception.getMessage());
    }
}