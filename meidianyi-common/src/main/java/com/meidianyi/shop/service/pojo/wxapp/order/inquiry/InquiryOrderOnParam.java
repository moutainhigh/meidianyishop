package com.meidianyi.shop.service.pojo.wxapp.order.inquiry;

import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 */
@Data
public class InquiryOrderOnParam {
    private Integer orderId;
    @RedisLockField
    private String orderSn;
    private Byte orderStatus;
    private Integer sessionId;
    /**
     *退款金额
     */
    private BigDecimal refundMoney;
    /**
     * 退款原因
     */
    private String refundReason;
}
