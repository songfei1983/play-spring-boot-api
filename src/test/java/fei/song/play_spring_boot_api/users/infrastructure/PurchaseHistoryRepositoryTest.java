package fei.song.play_spring_boot_api.users.infrastructure;

import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseHistoryRepositoryTest {
    
    private PurchaseHistoryRepository repository;
    private PurchaseHistory testPurchase;
    
    @BeforeEach
    void setUp() {
        repository = new PurchaseHistoryRepository();
        testPurchase = new PurchaseHistory(1L, "ORD20231201001", 123L, "iPhone 15 Pro", 1, 
                new BigDecimal("8999.00"), new BigDecimal("8999.00"));
        testPurchase.setActualPrice(new BigDecimal("8499.00"));
        testPurchase.setPaymentStatus("已支付");
        testPurchase.setOrderStatus("已完成");
        testPurchase.setBrand("Apple");
    }
    
    @Test
    void testInitializeData() {
        // When
        List<PurchaseHistory> purchases = repository.findAll();
        
        // Then
        assertEquals(4, purchases.size());
        assertTrue(purchases.stream().anyMatch(p -> "iPhone 15 Pro".equals(p.getProductName())));
        assertTrue(purchases.stream().anyMatch(p -> "MacBook Pro 14".equals(p.getProductName())));
        assertTrue(purchases.stream().anyMatch(p -> "AirPods Pro 2".equals(p.getProductName())));
        assertTrue(purchases.stream().anyMatch(p -> "iPad Air".equals(p.getProductName())));
    }
    
    @Test
    void testSave_NewPurchase() {
        // Given
        PurchaseHistory newPurchase = new PurchaseHistory(2L, "ORD999", 999L, "iPad Pro", 1, new BigDecimal("6999.00"), new BigDecimal("6999.00"));
        
        // When
        PurchaseHistory savedPurchase = repository.save(newPurchase);
        
        // Then
        assertNotNull(savedPurchase.getId());
        assertEquals("iPad Pro", savedPurchase.getProductName());
        assertEquals(new BigDecimal("6999.00"), savedPurchase.getUnitPrice());
        
        // Verify it's in the repository
        Optional<PurchaseHistory> foundPurchase = repository.findById(savedPurchase.getId());
        assertTrue(foundPurchase.isPresent());
        assertEquals(savedPurchase, foundPurchase.get());
    }
    
    @Test
    void testSave_ExistingPurchase() {
        // Given
        PurchaseHistory savedPurchase = repository.save(testPurchase);
        Long originalId = savedPurchase.getId();
        savedPurchase.setQuantity(2);
        
        // When
        PurchaseHistory updatedPurchase = repository.save(savedPurchase);
        
        // Then
        assertEquals(originalId, updatedPurchase.getId());
        assertEquals(2, updatedPurchase.getQuantity());
        
        // Verify it's updated in the repository
        Optional<PurchaseHistory> foundPurchase = repository.findById(originalId);
        assertTrue(foundPurchase.isPresent());
        assertEquals(2, foundPurchase.get().getQuantity());
    }
    
    @Test
    void testFindById_ExistingPurchase() {
        // Given
        PurchaseHistory savedPurchase = repository.save(testPurchase);
        
        // When
        Optional<PurchaseHistory> foundPurchase = repository.findById(savedPurchase.getId());
        
        // Then
        assertTrue(foundPurchase.isPresent());
        assertEquals(savedPurchase, foundPurchase.get());
    }
    
    @Test
    void testFindById_NonExistingPurchase() {
        // When
        Optional<PurchaseHistory> foundPurchase = repository.findById(999L);
        
        // Then
        assertFalse(foundPurchase.isPresent());
    }
    
    @Test
    void testFindByUserId() {
        // Given
        PurchaseHistory purchase1 = new PurchaseHistory(1L, "ORD001", 101L, "Product1", 1, new BigDecimal("100.00"), new BigDecimal("100.00"));
        PurchaseHistory purchase2 = new PurchaseHistory(1L, "ORD002", 102L, "Product2", 1, new BigDecimal("200.00"), new BigDecimal("200.00"));
        PurchaseHistory purchase3 = new PurchaseHistory(2L, "ORD005", 105L, "Product3", 1, new BigDecimal("300.00"), new BigDecimal("300.00"));
        
        repository.save(purchase1);
        repository.save(purchase2);
        repository.save(purchase3);
        
        // When
        List<PurchaseHistory> user1Purchases = repository.findByUserId(1L);
        List<PurchaseHistory> user2Purchases = repository.findByUserId(2L);
        
        // Then
        assertTrue(user1Purchases.size() >= 2);
        assertTrue(user2Purchases.size() >= 1);
    }
    
    @Test
    void testFindByBrand() {
        // Given
        repository.save(testPurchase);
        
        // When
        List<PurchaseHistory> purchases = repository.findByBrand("Apple");
        
        // Then
        assertTrue(purchases.size() >= 1);
        assertTrue(purchases.stream().anyMatch(p -> "Apple".equals(p.getBrand())));
    }
    
    @Test
    void testFindByPaymentStatus() {
        // Given
        repository.save(testPurchase);
        
        // When
        List<PurchaseHistory> purchases = repository.findByPaymentStatus("已支付");
        
        // Then
        assertTrue(purchases.size() >= 1);
        assertTrue(purchases.stream().anyMatch(p -> "已支付".equals(p.getPaymentStatus())));
    }
    
    @Test
    void testFindByActualPriceBetween() {
        // Given
        repository.save(testPurchase);
        
        // When
        List<PurchaseHistory> purchases = repository.findByActualPriceBetween(new BigDecimal("8000.00"), new BigDecimal("9000.00"));
        
        // Then
        assertTrue(purchases.size() >= 1);
        assertTrue(purchases.stream().anyMatch(p -> "iPhone 15 Pro".equals(p.getProductName())));
    }
    
    @Test
    void testFindByPurchaseTimeBetween() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime oneHourLater = now.plusHours(1);
        
        repository.save(testPurchase);
        
        // When
        List<PurchaseHistory> purchases = repository.findByPurchaseTimeBetween(oneHourAgo, oneHourLater);
        
        // Then
        assertTrue(purchases.size() >= 1);
    }
    
    @Test
    void testFindRecentByUserId() {
        // Given
        PurchaseHistory purchase1 = new PurchaseHistory(1L, "ORD006", 106L, "Product1", 1, new BigDecimal("100.00"), new BigDecimal("100.00"));
        PurchaseHistory purchase2 = new PurchaseHistory(1L, "ORD007", 107L, "Product2", 1, new BigDecimal("200.00"), new BigDecimal("200.00"));
        
        repository.save(purchase1);
        repository.save(purchase2);
        
        // When
        List<PurchaseHistory> recentPurchases = repository.findRecentByUserId(1L, 2);
        
        // Then
        assertTrue(recentPurchases.size() >= 2);
    }
    
    @Test
    void testSaveAll() {
        // Given
        PurchaseHistory purchase1 = new PurchaseHistory(1L, "ORD003", 103L, "Product1", 1, new BigDecimal("100.00"), new BigDecimal("100.00"));
        PurchaseHistory purchase2 = new PurchaseHistory(2L, "ORD004", 104L, "Product2", 1, new BigDecimal("200.00"), new BigDecimal("200.00"));
        List<PurchaseHistory> purchasesToSave = Arrays.asList(purchase1, purchase2);
        
        // When
        List<PurchaseHistory> savedPurchases = repository.saveAll(purchasesToSave);
        
        // Then
        assertEquals(2, savedPurchases.size());
        savedPurchases.forEach(purchase -> {
            assertNotNull(purchase.getId());
            assertNotNull(purchase.getCreatedAt());
        });
    }
    
    @Test
    void testDeleteById_ExistingPurchase() {
        // Given
        PurchaseHistory savedPurchase = repository.save(testPurchase);
        Long purchaseId = savedPurchase.getId();
        
        // When
        boolean deleted = repository.deleteById(purchaseId);
        
        // Then
        assertTrue(deleted);
        assertFalse(repository.existsById(purchaseId));
        assertFalse(repository.findById(purchaseId).isPresent());
    }
    
    @Test
    void testDeleteById_NonExistingPurchase() {
        // When
        boolean deleted = repository.deleteById(999L);
        
        // Then
        assertFalse(deleted);
    }
    
    @Test
    void testDeleteByUserId() {
        // Given
        repository.save(testPurchase);
        long initialCount = repository.count();
        
        // When
        boolean deleted = repository.deleteByUserId(1L);
        
        // Then
        assertTrue(deleted);
        assertTrue(repository.count() < initialCount);
        assertEquals(0, repository.findByUserId(1L).size());
    }
    
    @Test
    void testExistsById() {
        // Given
        PurchaseHistory savedPurchase = repository.save(testPurchase);
        
        // When & Then
        assertTrue(repository.existsById(savedPurchase.getId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    void testCount() {
        // Given
        long initialCount = repository.count();
        repository.save(testPurchase);
        
        // When
        long newCount = repository.count();
        
        // Then
        assertEquals(initialCount + 1, newCount);
    }
    
    @Test
    void testGetPurchaseCountByUserId() {
        // Given
        repository.save(testPurchase);
        
        // When
        long count = repository.getPurchaseCountByUserId(1L);
        
        // Then
        assertTrue(count >= 1);
    }
    
    @Test
    void testGetTotalAmountByUserId() {
        // Given
        repository.save(testPurchase);
        
        // When
        BigDecimal totalAmount = repository.getTotalAmountByUserId(1L);
        
        // Then
        assertTrue(totalAmount.compareTo(BigDecimal.ZERO) > 0);
    }
    
    @Test
    void testGetFavoriteBrandByUserId() {
        // Given
        repository.save(testPurchase);
        
        // When
        Optional<String> favoriteBrand = repository.getFavoriteBrandByUserId(1L);
        
        // Then
        assertNotNull(favoriteBrand);
    }
}