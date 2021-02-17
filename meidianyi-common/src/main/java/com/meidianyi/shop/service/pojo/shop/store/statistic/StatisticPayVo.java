package com.meidianyi.shop.service.pojo.shop.store.statistic;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenjie
 * @date 2020年08月27日
 */
@Data
public class StatisticPayVo {
    private BigDecimal totalMoneyPaid;
    private Integer userNum;
    private Integer orderNum;
}
