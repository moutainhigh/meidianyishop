package com.meidianyi.shop.service.pojo.wxapp.coupon.pack;

import com.meidianyi.shop.service.pojo.wxapp.member.card.GeneralUserCardVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-02-28 16:28
 **/
@Getter
@Setter
public class CouponPackOrderBeforeVo {
    /**
     * 用户可用余额
     */
   private BigDecimal account;

    /**
     * 用户可用积分
     */
   private Integer score;

    /**
     * 店铺发票开关
     */
   private Byte invoiceSwitch;

    /**
     * 店铺LOGO
     */
   private String shopLogo;
    /**
     * 店铺头像
     */
    private String shopAvatar;

    /**
     * 活动配置内容
     */
    private CouponPackActBaseVo packInfo;
    /**
     * 活动下属优惠券
     */
   private List<CouponPackVoucherVo> orderGoods;

    /**
     * 订单应付金额（或者积分数）
     */
   private BigDecimal orderAmount;

    /**
     * 订单应付现金数
     */
   private BigDecimal moneyPaid;

    /**
     * 店铺的积分比例
     */
   private Integer scoreProportion;

    /**
     * 可用的会员卡
     */
    private List<GeneralUserCardVo> memberCardList;
    /**
     * 手动或默认选择的会员卡
     */
    private GeneralUserCardVo memberCardInfo;
    /**
     * 手动或默认选择的会员卡卡号
     */
    private String memberCardNo;

    /**服务条款_展示开关*/
    private Byte isShowServiceTerms;
    /**服务条款_首次是否默认勾选*/
    private Byte serviceChoose;
    /**服务条款_服务条款名称*/
    private String serviceName;
    /**服务条款_服务条款名称*/
    private String serviceDocument;

    /**
     * 支付配置项
     * 会员卡余额支付
     */
    private Byte cardFirst;
    /**
     * 支付配置项
     * 余额支付
     */
    private Byte balanceFirst;
    /**
     * 支付配置项
     * 积分支付
     */
    private Byte scoreFirst;
}
