package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * RFM统计
 * @author liangchen
 * @date 2019.11.13
 */
@Data
public class RfmTableVo {
    /** 消费频次类型：1，2，3，4，5大于等于5次 */
    private Byte frequencyType;
    /** 总成交金额 */
    private BigDecimal totalPaidMoney;
    /** 下单人数（已付款订单人数） */
    private Integer payUserNum;
    /** 订单数量（已付款订单数） */
    private Integer orderNum;
}
