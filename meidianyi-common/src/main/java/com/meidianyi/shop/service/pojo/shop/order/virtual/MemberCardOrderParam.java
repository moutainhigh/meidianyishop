package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 会员卡订单查询入参
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class MemberCardOrderParam extends BasePageParam {
    private Integer userId;
    /** 下单用户信息（下单用户姓名或手机号）**/
    private String userInfo;
    /** 订单编号 **/
    private String orderSn;
    /**
     * 会员卡号
     **/
    private String cardNo;
    /**
     * 下单时间
     **/
    private Timestamp startTime;
    private Timestamp endTime;
    /**
     * 会员卡类型
     **/
    private Byte cardType;
    /**
     * 是否退款订单
     **/
    private Boolean refund;
    /**
     * 支付方式
     * wxpay 微信
     * balance 余额
     * score 积分
     */
    private String payCode;
}
