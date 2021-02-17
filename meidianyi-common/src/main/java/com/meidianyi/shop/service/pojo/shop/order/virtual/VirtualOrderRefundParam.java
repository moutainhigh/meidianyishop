package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 虚拟商品退款
 *
 * @author 郑保乐
 */
@Data
public class VirtualOrderRefundParam {

    @NotNull
    private Integer orderId;

    private String orderSn;

    /** 余额 **/
    private BigDecimal account = BigDecimal.ZERO;
    /** 现金 **/
    private BigDecimal money = BigDecimal.ZERO;
    /** 会员卡余额 **/
    private BigDecimal memberCardBalance = BigDecimal.ZERO;
    /** 积分 **/
    private Integer score = 0;
}
