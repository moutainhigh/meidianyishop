package com.meidianyi.shop.service.pojo.shop.market.seckill.analysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2019-10-30 10:52
 **/
@Data
public class SeckillAnalysisTotalVo {
    /**活动实付总金额 */
    private BigDecimal totalPayment;
    /**活动优惠总金额 */
    private BigDecimal totalDiscount;
    /**总费效比  */
    private BigDecimal totalCostEffectivenessRatio;
    /**总付款订单数 */
    private Integer totalPaidOrderNumber;
    /**总付款商品件数 */
    private Integer totalPaidGoodsNumber;
    /**老成交用户总数 */
    private Integer totalOldUserNumber;
    /**新成交用户总数 */
    private Integer totalNewUserNumber;
}
