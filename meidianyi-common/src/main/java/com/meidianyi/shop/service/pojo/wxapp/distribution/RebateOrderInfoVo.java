package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-04-06
 */
@Data
public class RebateOrderInfoVo {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 下单时间
     */
    private Timestamp createTime;
    /**
     * 下单用户
     */
    private String username;
    /**
     * 返利商品总额
     */
    private BigDecimal canRebateMoney;
    /**
     * 返利佣金
     */
    private BigDecimal rebateMoney;
    /**
     * 返利状态
     */
    private Integer orderStatus;

}
