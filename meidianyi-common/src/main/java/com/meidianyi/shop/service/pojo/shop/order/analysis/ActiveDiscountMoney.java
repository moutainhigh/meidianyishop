package com.meidianyi.shop.service.pojo.shop.order.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 拼团报表 计算销售额bo类
 * @author 孔德成
 * @date 2019/8/5 10:07
 */
@Data
public class ActiveDiscountMoney {

    public static final String DISCOUNT_AMOUNT ="discountAmount";

    public static final String CREATE_TIME ="createTime";
    /**
     * 销售金额
     */
    public static final String ORDER_AMOUNT ="orderAmount";
    public static final String MONEY_PAID ="moneyPaid";
    public static final String USE_ACCOUNT ="useAccount";
    public static final String SHIPPING_FEE ="shippingFee";
    /**
     * 订单数量
     */
    public static final String ORDER_NUMBER ="orderNumber";
    /**
     * 处方订单数量
     */
    public static final String PRESCRIPTION_ORDER_NUMBER ="prescriptionOrderNum";
    /**
     * 处方订单金额
     */
    public static final String PRESCRIPTION_ORDER_AMOUNT ="prescriptionOrderAmount";
    /**
     * 客单价
     */
    public static final String ORDER_AVG ="orderAvg";
    /**
     * 药品销售金额
     */
    public static final String ORDER_MEDICAL_AMOUNT ="orderMedicalAmount";
    /**
     * 药品订单数量
     */
    public static final String ORDER_MEDICAL_NUMBER ="orderMedicalNumber";
    /**
     * 退款数量
     */
    public static final String RETURN_NUMBER ="returnNumber";
    /**
     * 退款金额
     */
    public static final String RETURN_AMOUNT ="returnAmount";
    public static final String PAYMENT_AMOUNT ="paymentAmount";
    public static final String PAID_ORDER_NUMBER ="paidOrderNumber";
    public static final String PAID_GOODS_NUMBER ="paidGoodsNumber";

    /**
     * 时间 %Y-%m-%d
      */
    private Timestamp createTime;
    /**
     *  活动优惠总金额
      */
    private BigDecimal discountAmount;
    /**
     *  活动实付总金额
     */
    private BigDecimal paymentAmount;

    /** 付款订单数  */
    private Integer paidOrderNumber;
    /**
     *  商品数量
     */
    private Integer paidGoodsNumber;

}
