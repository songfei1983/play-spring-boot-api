package fei.song.play_spring_boot_api.users.interfaces;

import fei.song.play_spring_boot_api.users.application.PurchaseHistoryService;
import fei.song.play_spring_boot_api.users.domain.PurchaseHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "购买历史", description = "用户购买记录管理")
public class PurchaseHistoryController {
    
    @Autowired
    private PurchaseHistoryService purchaseHistoryService;
    
    /**
     * 获取所有购买履历
     */
    @GetMapping
    @Operation(summary = "获取所有购买记录", description = "返回系统中所有用户购买记录的列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "根据ID获取购买记录", description = "根据购买记录ID获取特定购买记录信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "404", description = "购买记录不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<PurchaseHistory> getPurchaseById(
            @Parameter(description = "购买记录ID", required = true, example = "1")
            @PathVariable Long id) {
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
    @Operation(summary = "根据用户ID获取购买记录", description = "获取指定用户的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "根据订单号获取购买记录", description = "根据订单号获取特定购买记录信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "购买记录不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<PurchaseHistory> getPurchaseByOrderNumber(
            @Parameter(description = "订单号", required = true, example = "ORD123456")
            @PathVariable String orderNumber) {
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
    @Operation(summary = "根据商品ID获取购买记录列表", description = "获取指定商品的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByProductId(
            @Parameter(description = "商品ID", required = true, example = "PROD123")
            @PathVariable String productId) {
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
    @Operation(summary = "根据商品分类获取购买记录列表", description = "获取指定商品分类的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByCategory(
            @Parameter(description = "商品分类", required = true, example = "电子产品")
            @PathVariable String category) {
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
    @Operation(summary = "根据品牌获取购买记录列表", description = "获取指定品牌的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByBrand(
            @Parameter(description = "品牌名称", required = true, example = "Apple")
            @PathVariable String brand) {
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
    @Operation(summary = "根据支付状态获取购买记录列表", description = "获取指定支付状态的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPaymentStatus(
            @Parameter(description = "支付状态", required = true, example = "已支付")
            @PathVariable String paymentStatus) {
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
    @Operation(summary = "根据订单状态获取购买记录列表", description = "获取指定订单状态的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByOrderStatus(
            @Parameter(description = "订单状态", required = true, example = "已完成")
            @PathVariable String orderStatus) {
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
    @Operation(summary = "根据用户ID和订单状态获取购买记录列表", description = "获取指定用户和订单状态的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserIdAndOrderStatus(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "订单状态", required = true, example = "已完成")
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
    @Operation(summary = "根据时间范围获取购买记录列表", description = "获取指定时间范围内的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByTimeRange(
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-12-31T23:59:59")
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
    @Operation(summary = "根据用户ID和时间范围获取购买记录列表", description = "获取指定用户在指定时间范围内的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByUserIdAndTimeRange(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "开始时间", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间", required = true, example = "2024-12-31T23:59:59")
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
    @Operation(summary = "根据价格范围获取购买记录列表", description = "获取指定价格范围内的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPriceRange(
            @Parameter(description = "最低价格", required = true, example = "100.00")
            @RequestParam BigDecimal minPrice,
            @Parameter(description = "最高价格", required = true, example = "1000.00")
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
    @Operation(summary = "根据支付方式获取购买记录列表", description = "获取指定支付方式的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByPaymentMethod(
            @Parameter(description = "支付方式", required = true, example = "信用卡")
            @PathVariable String paymentMethod) {
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
    @Operation(summary = "根据购买渠道获取购买记录列表", description = "获取指定购买渠道的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getPurchasesByChannel(
            @Parameter(description = "购买渠道", required = true, example = "在线商店")
            @PathVariable String channel) {
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
    @Operation(summary = "获取用户最近的购买记录", description = "获取指定用户最近的购买记录列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户最近购买记录",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> getRecentPurchasesByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "返回记录数量限制", required = false, example = "10")
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
    @Operation(summary = "获取用户购买总金额", description = "获取指定用户的购买总金额")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户购买总金额",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserTotalAmount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "获取用户购买次数", description = "获取指定用户的购买次数统计")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户购买次数",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserPurchaseCount(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "获取用户最喜欢的品牌", description = "根据用户ID获取该用户购买最多的品牌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户最喜欢的品牌",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserFavoriteBrand(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "创建购买记录", description = "创建新的购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "购买记录创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "购买记录冲突"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<PurchaseHistory> createPurchase(
            @Parameter(description = "购买记录信息", required = true)
            @RequestBody PurchaseHistory purchase) {
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
    @Operation(summary = "批量创建购买记录", description = "批量创建多个购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "购买记录批量创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "购买记录冲突"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<List<PurchaseHistory>> createPurchases(
            @Parameter(description = "购买记录列表", required = true)
            @RequestBody List<PurchaseHistory> purchases) {
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
    @Operation(summary = "更新购买记录", description = "根据ID更新购买记录信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "购买记录更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseHistory.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "404", description = "购买记录不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<PurchaseHistory> updatePurchase(
            @Parameter(description = "购买记录ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "更新的购买记录信息", required = true)
            @RequestBody PurchaseHistory purchase) {
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
    @Operation(summary = "删除购买记录", description = "根据ID删除购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "购买记录删除成功",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "购买记录不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deletePurchase(
            @Parameter(description = "购买记录ID", required = true, example = "1")
            @PathVariable Long id) {
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
    @Operation(summary = "删除用户所有购买记录", description = "删除指定用户的所有购买记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户购买记录删除成功",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> deletePurchasesByUserId(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "获取购买记录总数", description = "获取系统中所有购买记录的总数")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录总数",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(summary = "获取用户购买统计", description = "获取指定用户的购买统计信息，包括总购买数、总金额、最喜欢的品牌等")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户购买统计",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Map<String, Object>> getUserPurchaseStats(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
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
    @Operation(summary = "获取购买记录统计信息", description = "获取系统中所有购买记录的统计信息，包括按分类、品牌、支付方式、订单状态的统计")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取购买记录统计信息",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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