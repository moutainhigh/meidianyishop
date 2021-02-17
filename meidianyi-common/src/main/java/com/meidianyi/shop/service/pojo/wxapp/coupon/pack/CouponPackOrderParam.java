package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-03-02 15:43
 **/
@Setter
@Getter
public class CouponPackOrderParam {
    /**
     * 优惠券包主键
     */
    @NotNull
    private Integer packId;

    /**
     * 订单金额(积分支付时是0)
     */
    private BigDecimal orderAmount;
    /**
     * 使用积分
     */
    private Integer scoreDiscount = 0;
    /**
     * 使用余额
     */
    private BigDecimal accountDiscount = BigDecimal.ZERO;
    /**
     * 使用会员卡余额
     */
    private BigDecimal memberCardBalance = BigDecimal.ZERO;
    /**
     * 使用会员卡的卡号
     */
    private String cardNo;
    /**
     * 发票id
     */
    private Integer invoiceId = 0;
    /**
     * 发票内容
     */
    private String invoiceDetail;
}
