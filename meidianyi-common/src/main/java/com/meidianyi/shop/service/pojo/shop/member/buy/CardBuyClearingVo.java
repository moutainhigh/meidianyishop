package com.meidianyi.shop.service.pojo.shop.member.buy;

import com.meidianyi.shop.service.pojo.shop.member.MemberCardInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.member.card.GeneralUserCardVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 会员卡下单结算
 *
 * @author 孔德成
 * @date 2020/4/10
 */
@Getter
@Setter
@ToString
public class CardBuyClearingVo {
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
     * 店铺的积分比例
     */
    private Integer scoreProportion;

    /**
     * 服务条款_展示开关
     */
    private Byte isShowServiceTerms;
    /**
     * 服务条款_首次是否默认勾选
     */
    private Byte serviceChoose;
    /**
     * 服务条款_服务条款名称
     */
    private String serviceName;
    /**
     * 服务条款_服务条款名称
     */
    private String serviceDocument;

    /**
     *会员卡信息
     */
    private CardInfo cardInfo;
    /**
     * 订单应付金额（或者积分数）
     */
    private BigDecimal orderAmount;
    /**
     * 积分支付数量
     */
    private Integer orderPayScore;
    /**
     * 订单应付金额
     */
    private BigDecimal moneyPaid;


    @Getter
    @Setter
    public static class CardInfo {
        private Integer id;
        private String cardName;
        /**
         * 0背景色  1图
         */
        private Byte bgType;
        private String bgColor;
        private String bgImg;
        /*0:普通会员卡，1:次卡,2:登记卡*/
        private Byte cardType;
        private Byte payType;
        /*金额或者积分*/
        private BigDecimal  payFee;
        /*有效期0:日，1:周 2: 月*/
        private Byte dateType;
        /*领取之日起n*/
        private Short receiveDay;
        /*开卡送积分*/
        private Integer sorce;
        /*使用须知*/
        private String desc;
        /*联系电弧*/
        private String mobile;
        /*开卡送钱*/
        private Integer sendMoney;
        /**
         * 0:固定日期 1：自领取之日起 2:不过期
         */
        private Byte expireType;
        /**
         * 0：不用激活，1：需要激活
         */
        private Byte activation;
        private String startTime;
        private String endTime;

    }

}
