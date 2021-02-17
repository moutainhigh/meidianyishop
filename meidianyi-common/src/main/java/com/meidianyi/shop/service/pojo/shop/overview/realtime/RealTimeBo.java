package com.meidianyi.shop.service.pojo.shop.overview.realtime;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 实时概况业务处理类
 * @author liangchen
 * @date 2020.05.28
 */
@Data
public class RealTimeBo {
    /** 订单数 */
    private Integer orderNum;
    /** 总金额 */
    private BigDecimal totalMoneyPaid;
}
