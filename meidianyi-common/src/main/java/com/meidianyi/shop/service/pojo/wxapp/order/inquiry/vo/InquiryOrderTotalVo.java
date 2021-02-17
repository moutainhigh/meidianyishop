package com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/5
 **/
@Data
public class InquiryOrderTotalVo {
    /**
     * 咨询单数总数
     */
    private Integer amountTotal;
    /**
     * 咨询单次价格总数
     */
    private BigDecimal oncePriceTotal;
    /**
     * 咨询总金额总数
     */
    private BigDecimal amountPriceTotal;
}
