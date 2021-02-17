package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * RFM统计
 * @author liangchen
 * @date 2019.11.13
 */
@Data
public class RfmRowVo {
    /** 消费频次类型：1，2，3，4，5大于等于5次 */
    private Byte frequencyType;
    /** 用户数 */
    private Integer payUserNum;
    /** 占比 */
    private Double userRate;
    /** 累积支付金额 */
    private BigDecimal totalPaidMoney;
    /** 客单价 */
    private BigDecimal price;
}
