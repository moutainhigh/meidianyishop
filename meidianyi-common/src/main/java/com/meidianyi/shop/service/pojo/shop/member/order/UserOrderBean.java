package com.meidianyi.shop.service.pojo.shop.member.order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户订单数量与消费金额统计
 *
 * @author 黄壮壮
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderBean {
    /**
     * 最近下单时间
     */
    private Timestamp lastOrderTime;
    /**
     * 客单价
     */
    private BigDecimal unitPrice = BigDecimal.ZERO;
    /**
     * 累计消费金额
     */
    private BigDecimal totalMoneyPaid = BigDecimal.ZERO;
    /**
     * 累计消费订单数
     */
    private Integer orderNum = 0;
}
