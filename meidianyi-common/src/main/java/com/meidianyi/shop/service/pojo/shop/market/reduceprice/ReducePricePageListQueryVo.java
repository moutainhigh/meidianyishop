package com.meidianyi.shop.service.pojo.shop.market.reduceprice;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author: 王兵兵
 * @create: 2019-08-14 11:25
 **/
@Data
public class ReducePricePageListQueryVo {

    /** 活动的主键 */
    private Integer id;

    /** 活动名称 */
    private String name;

    /** 商品数量 */
    private Integer goodsAmount;

    /** 优先级 */
    private Byte first;

    /** 开始时间 */
    private Timestamp startTime;

    /** 结束时间 */
    private Timestamp endTime;

    /** 活动状态：0停用，1启用 */
    private Byte status;

    /** 付款订单数 */
    private Integer orderAmount;

    /** 付款用户数 */
    private Integer userAmount;

    /** 付款总金额 */
    private BigDecimal paymentTotalAmount;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;
}
