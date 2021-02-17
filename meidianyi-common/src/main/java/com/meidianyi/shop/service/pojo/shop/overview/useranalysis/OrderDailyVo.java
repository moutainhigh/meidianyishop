package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成交用户分析
 * @author liangchen
 * @date 2019年7月19日
 */
@Data
public class OrderDailyVo {
    /** 日期 */
    private Date refDate;
    /** 客户数 */
    private Integer orderUserData;

    /** 客单价 */
    private BigDecimal unitPrice;

    /** 付款金额 */
    private BigDecimal paidMoney;

    /** 转化率 */
    private Double transRate;

    /** 访问人数 */
    private Integer loginData;
}
