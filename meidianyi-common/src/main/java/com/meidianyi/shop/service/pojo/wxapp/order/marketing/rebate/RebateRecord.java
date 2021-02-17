package com.meidianyi.shop.service.pojo.wxapp.order.marketing.rebate;

import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 下单submit计算返利结果例时存储
 * @author 王帅
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class RebateRecord {
    private DistributionStrategyParam strategy;
    private Integer userId;
    private Byte rebateLevel;
    private BigDecimal ratio;
}
