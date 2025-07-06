package fei.song.play_spring_boot_api.users.interfaces;

import fei.song.play_spring_boot_api.users.application.PurchaseHistoryService;
import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/purchases")
@CrossOrigin(origins = "*")
public class PurchaseHistoryController {
    
    @Autowired
    private PurchaseHistoryService purchaseHistoryService;
    
    /**
     * 获取所有购买履历
     */
    @GetMapping
    public ResponseEntity<List<PurchaseHistory>> getAllPurchases() {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getAllPurchases();
            return ResponseEntity.ok(purchases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据ID获取购买履历
     */
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseHistory> getPurchaseById(@PathVariable Long id) {
        try {
            PurchaseHistory purchase = purchaseHistoryService.getPurchaseById(id);
            return ResponseEntity.ok(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID获取购买履历列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserId(@PathVariable Long userId) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByUserId(userId);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据订单号获取购买履历
     */
    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<PurchaseHistory> getPurchaseByOrderNumber(@PathVariable String orderNumber) {
        try {
            PurchaseHistory purchase = purchaseHistoryService.getPurchaseByOrderNumber(orderNumber);
            return ResponseEntity.ok(purchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据商品ID获取购买履历列表
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByProductId(@PathVariable String productId) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByProductId(productId);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据商品分类获取购买履历列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByCategory(@PathVariable String category) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByCategory(category);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据品牌获取购买履历列表
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByBrand(@PathVariable String brand) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByBrand(brand);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据支付状态获取购买履历列表
     */
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPaymentStatus(@PathVariable String paymentStatus) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByPaymentStatus(paymentStatus);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据订单状态获取购买履历列表
     */
    @GetMapping("/order-status/{orderStatus}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByOrderStatus(@PathVariable String orderStatus) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByOrderStatus(orderStatus);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID和订单状态获取购买履历列表
     */
    @GetMapping("/user/{userId}/order-status/{orderStatus}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserIdAndOrderStatus(
            @PathVariable Long userId, 
            @PathVariable String orderStatus) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByUserIdAndOrderStatus(userId, orderStatus);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据时间范围获取购买履历列表
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByTimeRange(startTime, endTime);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据用户ID和时间范围获取购买履历列表
     */
    @GetMapping("/user/{userId}/time-range")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserIdAndTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByUserIdAndTimeRange(userId, startTime, endTime);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据价格范围获取购买履历列表
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据支付方式获取购买履历列表
     */
    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPaymentMethod(@PathVariable String paymentMethod) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByPaymentMethod(paymentMethod);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 根据购买渠道获取购买履历列表
     */
    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByChannel(@PathVariable String channel) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getPurchasesByChannel(channel);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户最近的购买履历
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<PurchaseHistory>> getRecentPurchasesByUserId(
            @PathVariable Long userId, 
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<PurchaseHistory> purchases = purchaseHistoryService.getRecentPurchasesByUserId(userId, limit);
            return ResponseEntity.ok(purchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户购买总金额
     */
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<Map<String, Object>> getUserTotalAmount(@PathVariable Long userId) {
        try {
            BigDecimal totalAmount = purchaseHistoryService.getUserTotalAmount(userId);
            Map<String, Object> response = Map.of(
                "userId", userId,
                "totalAmount", totalAmount,
                "message", "用户购买总金额"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户购买次数
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> getUserPurchaseCount(@PathVariable Long userId) {
        try {
            long count = purchaseHistoryService.getUserPurchaseCount(userId);
            Map<String, Object> response = Map.of(
                "userId", userId,
                "count", count,
                "message", "用户购买次数"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户最喜欢的品牌
     */
    @GetMapping("/user/{userId}/favorite-brand")
    public ResponseEntity<Map<String, Object>> getUserFavoriteBrand(@PathVariable Long userId) {
        try {
            String favoriteBrand = purchaseHistoryService.getUserFavoriteBrand(userId);
            Map<String, Object> response = Map.of(
                "userId", userId,
                "favoriteBrand", favoriteBrand != null ? favoriteBrand : "暂无数据",
                "message", "用户最喜欢的品牌"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 创建购买履历
     */
    @PostMapping
    public ResponseEntity<PurchaseHistory> createPurchase(@RequestBody PurchaseHistory purchase) {
        try {
            PurchaseHistory createdPurchase = purchaseHistoryService.createPurchase(purchase);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 批量创建购买履历
     */
    @PostMapping("/batch")
    public ResponseEntity<List<PurchaseHistory>> createPurchases(@RequestBody List<PurchaseHistory> purchases) {
        try {
            List<PurchaseHistory> createdPurchases = purchaseHistoryService.createPurchases(purchases);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPurchases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 更新购买履历
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseHistory> updatePurchase(@PathVariable Long id, @RequestBody PurchaseHistory purchase) {
        try {
            PurchaseHistory updatedPurchase = purchaseHistoryService.updatePurchase(id, purchase);
            return ResponseEntity.ok(updatedPurchase);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除购买履历
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePurchase(@PathVariable Long id) {
        try {
            boolean deleted = purchaseHistoryService.deletePurchase(id);
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "购买履历删除成功" : "购买履历删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 删除用户的所有购买履历
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> deletePurchasesByUserId(@PathVariable Long userId) {
        try {
            boolean deleted = purchaseHistoryService.deletePurchasesByUserId(userId);
            Map<String, Object> response = Map.of(
                "success", deleted,
                "message", deleted ? "用户购买履历删除成功" : "用户购买履历删除失败"
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取购买履历总数
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getPurchaseCount() {
        try {
            long count = purchaseHistoryService.getPurchaseCount();
            Map<String, Object> response = Map.of(
                "count", count,
                "message", "购买履历总数"
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取用户购买统计
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserPurchaseStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = purchaseHistoryService.getUserPurchaseStats(userId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 获取购买履历统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPurchaseStats() {
        try {
            long totalCount = purchaseHistoryService.getPurchaseCount();
            List<PurchaseHistory> allPurchases = purchaseHistoryService.getAllPurchases();
            
            // 按分类统计
            Map<String, Long> categoryStats = allPurchases.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    purchase -> purchase.getCategory() != null ? purchase.getCategory() : "未分类",
                    java.util.stream.Collectors.counting()
                ));
            
            // 按品牌统计
            Map<String, Long> brandStats = allPurchases.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    purchase -> purchase.getBrand() != null ? purchase.getBrand() : "未知品牌",
                    java.util.stream.Collectors.counting()
                ));
            
            // 按支付方式统计
            Map<String, Long> paymentMethodStats = allPurchases.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    purchase -> purchase.getPaymentMethod() != null ? purchase.getPaymentMethod() : "未知",
                    java.util.stream.Collectors.counting()
                ));
            
            // 按订单状态统计
            Map<String, Long> orderStatusStats = allPurchases.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    purchase -> purchase.getOrderStatus() != null ? purchase.getOrderStatus() : "未知",
                    java.util.stream.Collectors.counting()
                ));
            
            Map<String, Object> stats = Map.of(
                "totalCount", totalCount,
                "categoryStats", categoryStats,
                "brandStats", brandStats,
                "paymentMethodStats", paymentMethodStats,
                "orderStatusStats", orderStatusStats
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}