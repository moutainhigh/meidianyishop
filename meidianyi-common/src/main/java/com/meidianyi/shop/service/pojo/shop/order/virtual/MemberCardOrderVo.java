package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员卡订单列表出参
 *
 * @author 郑保乐
 */
@Data
public class MemberCardOrderVo {

    private Integer orderId;
    /** 订单编号 **/
    private String orderSn;
    /** 卡类型：0：普通，1：限次，2：等级 **/
    private Byte cardType;
    /** 卡号 **/
    private String cardNo;
    /** 卡名 **/
    private String cardName;
    /** 支付金额 **/
    private BigDecimal moneyPaid;
    /** 支付余额 **/
    private BigDecimal useAccount;
    /** 支付积分 **/
    private Integer useScore;
    /** 支付时间 **/
    private Timestamp payTime;
    /**是否能退款 1能退 0不能退*/
    private Byte canReturn;
    /** 购买方式：0：积分，1：现金 **/
    private Byte payType;
    /**
     * 用户名
     **/
    private String username;
    private Integer userId;
    /**
     * 手机号
     **/
    private String mobile;
    /** 退款状态 **/
    private Byte returnFlag;
    /** 退款时间 **/
    private Timestamp returnTime;

    /** 订单总金额 */
    private BigDecimal orderAmount;
    /** 币种 */
    private String currency;
}
