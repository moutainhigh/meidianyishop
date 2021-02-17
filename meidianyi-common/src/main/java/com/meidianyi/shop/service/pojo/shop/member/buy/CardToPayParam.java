package com.meidianyi.shop.service.pojo.shop.member.buy;

import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 会员卡购买
 * @author 孔德成
 * @date 2020/4/12
 */
@Getter
@Setter
@ToString
public class CardToPayParam {
    /** 用户id */
    private WxAppSessionUser user;
    /** ip地址 */
    private String clientIp;
    /**
     * 会员卡id
     */
    private Integer cardId;
    /**
     * 拥有的会员卡
     */
    private String cardNo;
    /**
     * 订单金额(积分支付时是0)
     */
    private BigDecimal orderAmount =BigDecimal.ZERO;
    /**
     * 真实支付金额
     */
    private BigDecimal moneyPaid =BigDecimal.ZERO;
    /**
     * 使用积分
     */
    private Integer scoreDiscount = 0;
    /**
     * 使用余额
     */
    private BigDecimal accountDiscount = BigDecimal.ZERO;
    /**
     * 使用会员卡余额
     */
    private BigDecimal memberCardBalance = BigDecimal.ZERO;
    /**
     * 发票id
     */
    private Integer invoiceId = 0;
    /**
     * 发票内容
     */
    private String invoiceDetail;



}
