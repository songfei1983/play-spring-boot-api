package fei.song.play_spring_boot_api.users.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import fei.song.play_spring_boot_api.users.application.PurchaseHistoryService;
import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseHistoryController.class)
class PurchaseHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseHistoryService purchaseHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private PurchaseHistory testPurchase;
    private List<PurchaseHistory> testPurchases;

    @BeforeEach
    void setUp() {
        testPurchase = new PurchaseHistory();
        testPurchase.setId(1L);
        testPurchase.setUserId(1L);
        testPurchase.setOrderNumber("ORD001");
        testPurchase.setProductId(1L);
        testPurchase.setProductName("Test Product");
        testPurchase.setCategory("Electronics");
        testPurchase.setBrand("TestBrand");
        testPurchase.setUnitPrice(new BigDecimal("99.99"));
        testPurchase.setQuantity(2);
        testPurchase.setPaymentMethod("CREDIT_CARD");
        testPurchase.setPaymentStatus("PAID");
        testPurchase.setOrderStatus("DELIVERED");
        testPurchase.setChannel("ONLINE");
        testPurchase.setCreatedAt(LocalDateTime.now());
        testPurchase.setUpdatedAt(LocalDateTime.now());

        PurchaseHistory purchase2 = new PurchaseHistory();
        purchase2.setId(2L);
        purchase2.setUserId(2L);
        purchase2.setOrderNumber("ORD002");
        purchase2.setProductId(2L);
        purchase2.setProductName("Test Product 2");
        purchase2.setCategory("Clothing");
        purchase2.setBrand("TestBrand2");
        purchase2.setUnitPrice(new BigDecimal("49.99"));
        purchase2.setQuantity(1);
        purchase2.setPaymentMethod("PAYPAL");
        purchase2.setPaymentStatus("PAID");
        purchase2.setOrderStatus("SHIPPED");
        purchase2.setChannel("MOBILE");
        purchase2.setCreatedAt(LocalDateTime.now());
        purchase2.setUpdatedAt(LocalDateTime.now());

        testPurchases = Arrays.asList(testPurchase, purchase2);
    }

    @Test
    void getAllPurchases_ShouldReturnPurchaseList() throws Exception {
        // Given
        when(purchaseHistoryService.getAllPurchases()).thenReturn(testPurchases);

        // When & Then
        mockMvc.perform(get("/api/users/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD001"))
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[1].orderNumber").value("ORD002"));

        verify(purchaseHistoryService, times(1)).getAllPurchases();
    }

    @Test
    void getPurchaseById_ShouldReturnPurchase() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseById(1L)).thenReturn(testPurchase);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD001"))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.unitPrice").value(99.99));

        verify(purchaseHistoryService, times(1)).getPurchaseById(1L);
    }

    @Test
    void getPurchaseById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseById(999L)).thenThrow(new RuntimeException("Purchase not found"));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/999"))
                .andExpect(status().isNotFound());

        verify(purchaseHistoryService, times(1)).getPurchaseById(999L);
    }

    @Test
    void getPurchasesByUserId_ShouldReturnUserPurchases() throws Exception {
        // Given
        List<PurchaseHistory> userPurchases = Arrays.asList(testPurchase);
        when(purchaseHistoryService.getPurchasesByUserId(1L)).thenReturn(userPurchases);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));

        verify(purchaseHistoryService, times(1)).getPurchasesByUserId(1L);
    }

    @Test
    void getPurchaseByOrderNumber_ShouldReturnPurchase() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseByOrderNumber("ORD001")).thenReturn(testPurchase);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/order/ORD001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderNumber").value("ORD001"));

        verify(purchaseHistoryService, times(1)).getPurchaseByOrderNumber("ORD001");
    }

    @Test
    void getPurchasesByProductId_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByProductId("PROD001")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/product/PROD001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(1L));

        verify(purchaseHistoryService, times(1)).getPurchasesByProductId("PROD001");
    }

    @Test
    void getPurchasesByCategory_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByCategory("Electronics")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(purchaseHistoryService, times(1)).getPurchasesByCategory("Electronics");
    }

    @Test
    void getPurchasesByBrand_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByBrand("TestBrand")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/brand/TestBrand"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("TestBrand"));

        verify(purchaseHistoryService, times(1)).getPurchasesByBrand("TestBrand");
    }

    @Test
    void getPurchasesByPaymentStatus_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByPaymentStatus("PAID")).thenReturn(testPurchases);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/payment-status/PAID"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(purchaseHistoryService, times(1)).getPurchasesByPaymentStatus("PAID");
    }

    @Test
    void getPurchasesByOrderStatus_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByOrderStatus("DELIVERED")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/order-status/DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderStatus").value("DELIVERED"));

        verify(purchaseHistoryService, times(1)).getPurchasesByOrderStatus("DELIVERED");
    }

    @Test
    void getPurchasesByUserIdAndOrderStatus_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByUserIdAndOrderStatus(1L, "DELIVERED"))
                .thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/order-status/DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(purchaseHistoryService, times(1)).getPurchasesByUserIdAndOrderStatus(1L, "DELIVERED");
    }

    @Test
    void getPurchasesByTimeRange_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        when(purchaseHistoryService.getPurchasesByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testPurchases);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/time-range")
                        .param("startTime", startTime.toString())
                        .param("endTime", endTime.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(purchaseHistoryService, times(1)).getPurchasesByTimeRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getPurchasesByPriceRange_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        when(purchaseHistoryService.getPurchasesByPriceRange(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/price-range")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "150.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(purchaseHistoryService, times(1)).getPurchasesByPriceRange(any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void getPurchasesByPaymentMethod_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByPaymentMethod("CREDIT_CARD")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/payment-method/CREDIT_CARD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"));

        verify(purchaseHistoryService, times(1)).getPurchasesByPaymentMethod("CREDIT_CARD");
    }

    @Test
    void getPurchasesByChannel_ShouldReturnFilteredPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByChannel("ONLINE")).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/channel/ONLINE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].channel").value("ONLINE"));

        verify(purchaseHistoryService, times(1)).getPurchasesByChannel("ONLINE");
    }

    @Test
    void getRecentPurchasesByUserId_ShouldReturnRecentPurchases() throws Exception {
        // Given
        when(purchaseHistoryService.getRecentPurchasesByUserId(1L, 5)).thenReturn(Arrays.asList(testPurchase));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/recent")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(purchaseHistoryService, times(1)).getRecentPurchasesByUserId(1L, 5);
    }

    @Test
    void getUserTotalAmount_ShouldReturnTotalAmountResponse() throws Exception {
        // Given
        BigDecimal totalAmount = new BigDecimal("199.98");
        when(purchaseHistoryService.getUserTotalAmount(1L)).thenReturn(totalAmount);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/total-amount"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(199.98))
                .andExpect(jsonPath("$.message").value("用户购买总金额"));

        verify(purchaseHistoryService, times(1)).getUserTotalAmount(1L);
    }

    @Test
    void getUserPurchaseCount_ShouldReturnCountResponse() throws Exception {
        // Given
        when(purchaseHistoryService.getUserPurchaseCount(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.message").value("用户购买次数"));

        verify(purchaseHistoryService, times(1)).getUserPurchaseCount(1L);
    }

    @Test
    void getUserFavoriteBrand_ShouldReturnFavoriteBrandResponse() throws Exception {
        // Given
        when(purchaseHistoryService.getUserFavoriteBrand(1L)).thenReturn("TestBrand");

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/favorite-brand"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.favoriteBrand").value("TestBrand"))
                .andExpect(jsonPath("$.message").value("用户最喜欢的品牌"));

        verify(purchaseHistoryService, times(1)).getUserFavoriteBrand(1L);
    }

    @Test
    void createPurchase_ShouldReturnCreatedPurchase() throws Exception {
        // Given
        PurchaseHistory newPurchase = new PurchaseHistory();
        newPurchase.setUserId(3L);
        newPurchase.setOrderNumber("ORD003");
        newPurchase.setProductId(3L);
        newPurchase.setProductName("New Product");
        newPurchase.setCategory("Books");
        newPurchase.setBrand("NewBrand");
        newPurchase.setUnitPrice(new BigDecimal("29.99"));
        newPurchase.setQuantity(1);
        newPurchase.setPaymentMethod("DEBIT_CARD");
        newPurchase.setPaymentStatus("PENDING");
        newPurchase.setOrderStatus("PROCESSING");
        newPurchase.setChannel("MOBILE");

        PurchaseHistory createdPurchase = new PurchaseHistory();
        createdPurchase.setId(3L);
        createdPurchase.setUserId(3L);
        createdPurchase.setOrderNumber("ORD003");
        createdPurchase.setProductId(3L);
        createdPurchase.setProductName("New Product");
        createdPurchase.setCategory("Books");
        createdPurchase.setBrand("NewBrand");
        createdPurchase.setUnitPrice(new BigDecimal("29.99"));
        createdPurchase.setQuantity(1);
        createdPurchase.setPaymentMethod("DEBIT_CARD");
        createdPurchase.setPaymentStatus("PENDING");
        createdPurchase.setOrderStatus("PROCESSING");
        createdPurchase.setChannel("MOBILE");
        createdPurchase.setCreatedAt(LocalDateTime.now());
        createdPurchase.setUpdatedAt(LocalDateTime.now());

        when(purchaseHistoryService.createPurchase(any(PurchaseHistory.class))).thenReturn(createdPurchase);

        // When & Then
        mockMvc.perform(post("/api/users/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPurchase)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.orderNumber").value("ORD003"))
                .andExpect(jsonPath("$.productName").value("New Product"));

        verify(purchaseHistoryService, times(1)).createPurchase(any(PurchaseHistory.class));
    }

    @Test
    void createPurchases_ShouldReturnCreatedPurchases() throws Exception {
        // Given
        List<PurchaseHistory> newPurchases = Arrays.asList(testPurchase);
        when(purchaseHistoryService.createPurchases(anyList())).thenReturn(newPurchases);

        // When & Then
        mockMvc.perform(post("/api/users/purchases/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPurchases)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(purchaseHistoryService, times(1)).createPurchases(anyList());
    }

    @Test
    void updatePurchase_ShouldReturnUpdatedPurchase() throws Exception {
        // Given
        PurchaseHistory updatedPurchase = new PurchaseHistory();
        updatedPurchase.setId(1L);
        updatedPurchase.setUserId(1L);
        updatedPurchase.setOrderNumber("ORD001");
        updatedPurchase.setProductId(1L);
        updatedPurchase.setProductName("Updated Product");
        updatedPurchase.setCategory("Electronics");
        updatedPurchase.setBrand("TestBrand");
        updatedPurchase.setUnitPrice(new BigDecimal("109.99"));
        updatedPurchase.setQuantity(3);
        updatedPurchase.setPaymentMethod("CREDIT_CARD");
        updatedPurchase.setPaymentStatus("PAID");
        updatedPurchase.setOrderStatus("DELIVERED");
        updatedPurchase.setChannel("ONLINE");
        updatedPurchase.setCreatedAt(testPurchase.getCreatedAt());
        updatedPurchase.setUpdatedAt(LocalDateTime.now());

        when(purchaseHistoryService.updatePurchase(eq(1L), any(PurchaseHistory.class))).thenReturn(updatedPurchase);

        // When & Then
        mockMvc.perform(put("/api/users/purchases/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPurchase)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Updated Product"))
                .andExpect(jsonPath("$.unitPrice").value(109.99))
                .andExpect(jsonPath("$.quantity").value(3));

        verify(purchaseHistoryService, times(1)).updatePurchase(eq(1L), any(PurchaseHistory.class));
    }

    @Test
    void deletePurchase_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(purchaseHistoryService.deletePurchase(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/purchases/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("购买履历删除成功"));

        verify(purchaseHistoryService, times(1)).deletePurchase(1L);
    }

    @Test
    void deletePurchasesByUserId_ShouldReturnSuccessResponse() throws Exception {
        // Given
        when(purchaseHistoryService.deletePurchasesByUserId(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/purchases/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户购买履历删除成功"));

        verify(purchaseHistoryService, times(1)).deletePurchasesByUserId(1L);
    }

    @Test
    void getPurchaseCount_ShouldReturnCountResponse() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseCount()).thenReturn(100L);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.message").value("购买履历总数"));

        verify(purchaseHistoryService, times(1)).getPurchaseCount();
    }

    @Test
    void getUserPurchaseStats_ShouldReturnStatsResponse() throws Exception {
        // Given
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalPurchases", 10);
        userStats.put("totalAmount", new BigDecimal("999.90"));
        when(purchaseHistoryService.getUserPurchaseStats(1L)).thenReturn(userStats);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/1/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalPurchases").value(10));

        verify(purchaseHistoryService, times(1)).getUserPurchaseStats(1L);
    }

    @Test
    void getPurchaseStats_ShouldReturnOverallStatsResponse() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseCount()).thenReturn(2L);
        when(purchaseHistoryService.getAllPurchases()).thenReturn(testPurchases);

        // When & Then
        mockMvc.perform(get("/api/users/purchases/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.categoryStats").exists())
                .andExpect(jsonPath("$.brandStats").exists())
                .andExpect(jsonPath("$.paymentMethodStats").exists())
                .andExpect(jsonPath("$.orderStatusStats").exists());

        verify(purchaseHistoryService, times(1)).getPurchaseCount();
        verify(purchaseHistoryService, times(1)).getAllPurchases();
    }

    // Error handling tests
    @Test
    void getAllPurchases_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(purchaseHistoryService.getAllPurchases()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/users/purchases"))
                .andExpect(status().isInternalServerError());

        verify(purchaseHistoryService, times(1)).getAllPurchases();
    }

    @Test
    void getPurchasesByUserId_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchasesByUserId(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPurchaseByOrderNumber_WhenNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(purchaseHistoryService.getPurchaseByOrderNumber("INVALID"))
                .thenThrow(new RuntimeException("Order not found"));

        // When & Then
        mockMvc.perform(get("/api/users/purchases/order/INVALID"))
                .andExpect(status().isNotFound());

        verify(purchaseHistoryService, times(1)).getPurchaseByOrderNumber("INVALID");
    }

    @Test
    void createPurchase_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        when(purchaseHistoryService.createPurchase(any(PurchaseHistory.class)))
                .thenThrow(new IllegalArgumentException("Invalid purchase data"));

        // When & Then
        mockMvc.perform(post("/api/users/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PurchaseHistory())))
                .andExpect(status().isBadRequest());

        verify(purchaseHistoryService, times(1)).createPurchase(any(PurchaseHistory.class));
    }

    @Test
    void createPurchase_WhenPurchaseAlreadyExists_ShouldReturnConflict() throws Exception {
        // Given
        when(purchaseHistoryService.createPurchase(any(PurchaseHistory.class)))
                .thenThrow(new RuntimeException("Purchase already exists"));

        // When & Then
        mockMvc.perform(post("/api/users/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPurchase)))
                .andExpect(status().isConflict());

        verify(purchaseHistoryService, times(1)).createPurchase(any(PurchaseHistory.class));
    }

    @Test
    void updatePurchase_WhenPurchaseNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(purchaseHistoryService.updatePurchase(eq(999L), any(PurchaseHistory.class)))
                .thenThrow(new RuntimeException("Purchase not found"));

        // When & Then
        mockMvc.perform(put("/api/users/purchases/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPurchase)))
                .andExpect(status().isNotFound());

        verify(purchaseHistoryService, times(1)).updatePurchase(eq(999L), any(PurchaseHistory.class));
    }

    @Test
    void deletePurchase_WhenPurchaseNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(purchaseHistoryService.deletePurchase(999L)).thenThrow(new RuntimeException("Purchase not found"));

        // When & Then
        mockMvc.perform(delete("/api/users/purchases/999"))
                .andExpect(status().isNotFound());

        verify(purchaseHistoryService, times(1)).deletePurchase(999L);
    }
}