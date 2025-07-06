package fei.song.play_spring_boot_api.users.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户购买履历实体")
public class PurchaseHistory {
    @Schema(description = "购买记录ID", example = "1")
    private Long id;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "订单号", example = "ORD20231201001")
    private String orderNumber;
    
    @Schema(description = "商品ID", example = "123")
    private Long productId;
    
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String productName;
    
    @Schema(description = "商品分类", example = "电子产品")
    private String category;
    
    @Schema(description = "商品品牌", example = "Apple")
    private String brand;
    
    @Schema(description = "商品SKU", example = "IPH15P-256-BLU")
    private String sku;
    
    @Schema(description = "购买数量", example = "1")
    private Integer quantity;
    
    @Schema(description = "单价", example = "8999.00")
    private BigDecimal unitPrice;
    
    @Schema(description = "总价", example = "8999.00")
    private BigDecimal totalPrice;
    
    @Schema(description = "折扣金额", example = "500.00")
    private BigDecimal discountAmount;
    
    @Schema(description = "实付金额", example = "8499.00")
    private BigDecimal actualPrice;
    
    @Schema(description = "支付方式", example = "支付宝", allowableValues = {"支付宝", "微信支付", "银行卡", "现金", "其他"})
    private String paymentMethod;
    
    @Schema(description = "支付状态", example = "已支付", allowableValues = {"待支付", "已支付", "已退款", "部分退款"})
    private String paymentStatus;
    
    @Schema(description = "订单状态", example = "已完成", allowableValues = {"待确认", "已确认", "配送中", "已完成", "已取消", "已退货"})
    private String orderStatus;
    
    @Schema(description = "配送地址", example = "北京市朝阳区xxx街道xxx号")
    private String deliveryAddress;
    
    @Schema(description = "配送方式", example = "快递", allowableValues = {"快递", "自提", "同城配送"})
    private String deliveryMethod;
    
    @Schema(description = "快递公司", example = "顺丰速运")
    private String courierCompany;
    
    @Schema(description = "快递单号", example = "SF1234567890")
    private String trackingNumber;
    
    @Schema(description = "优惠券ID", example = "COUPON123")
    private String couponId;
    
    @Schema(description = "优惠券名称", example = "新用户专享券")
    private String couponName;
    
    @Schema(description = "评价分数", example = "5")
    private Integer rating;
    
    @Schema(description = "评价内容", example = "商品质量很好，物流很快")
    private String review;
    
    @Schema(description = "购买渠道", example = "官网", allowableValues = {"官网", "APP", "小程序", "线下门店", "第三方平台"})
    private String channel;
    
    @Schema(description = "销售员ID", example = "SALES001")
    private String salesPersonId;
    
    @Schema(description = "备注", example = "客户要求加急配送")
    private String remarks;
    
    @Schema(description = "购买时间")
    private LocalDateTime purchaseTime;
    
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;
    
    @Schema(description = "发货时间")
    private LocalDateTime shipmentTime;
    
    @Schema(description = "完成时间")
    private LocalDateTime completionTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    // 自定义构造函数，用于设置默认时间戳
    public PurchaseHistory(Long userId, String orderNumber, Long productId, String productName,
                          Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.actualPrice = totalPrice; // 默认实付金额等于总价
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.purchaseTime = LocalDateTime.now();
    }
    
    // 更新时间戳的自定义方法
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}