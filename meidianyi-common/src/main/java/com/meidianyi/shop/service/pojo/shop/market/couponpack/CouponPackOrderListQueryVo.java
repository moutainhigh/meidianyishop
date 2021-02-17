package com.meidianyi.shop.service.pojo.shop.market.couponpack;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-21 16:10
 **/
@Data
public class CouponPackOrderListQueryVo {

    /** 订单编号 */
    private String orderSn;

    /** 实际微信付款 */
    private BigDecimal moneyPaid;

    /** 使用余额支付数 */
    private BigDecimal useAccount;

    /** 使用积分支付数 */
    private BigDecimal useScore;

    /** 使用会员卡余额支付数 */
    private BigDecimal memberCardBalance;

    /** 用户ID */
    private Integer userId;

    /** 昵称 */
    private String username;

    /** 手机号 */
    private String mobile;

    /** 下单时间 */
    private Timestamp createTime;

    /** 订单状态 */
    private Byte orderStatus;
}
