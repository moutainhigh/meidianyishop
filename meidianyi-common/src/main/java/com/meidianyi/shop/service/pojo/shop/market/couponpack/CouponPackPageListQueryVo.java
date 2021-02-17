package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 14:55
 **/
@Data
public class CouponPackPageListQueryVo {

    private Integer id;

    /** 活动名称 */
    private String actName;

    /** 礼包名称 */
    private String packName;

    /** 开始时间 */
    private Timestamp startTime;

    /** 结束时间 */
    private Timestamp endTime;

    /** 优惠券种类数 */
    private Integer voucherKindsNumber;

    /** 优惠券数量 */
    private Integer voucherNumber;

    /** 可发放礼包数 */
    private Integer totalAmount;

    /** 领取方式 0：现金购买，1：积分购买，2直接领取*/
    private Byte accessMode;

    /** 价格 */
    private BigDecimal accessCost;

    /** 已领取礼包数 */
    private Integer issueAmount;

    /** 开启状态1:开启，0:停用 */
    private Byte status;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;
}
