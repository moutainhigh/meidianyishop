package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-05-18 11:47
 **/
@Getter
@Setter
public class UserCardRenewListVo {
    /**
     * 续费单号
     */
    private String renewOrderSn;
    /**
     *
     */
    private Integer userId;
    /**
     * 会员卡名称
     */
    private String cardName;
    /**
     * 卡ID
     */
    private Integer cardId;
    /**
     * 会员昵称
     */
    private String username;
    /**
     * 会员手机号
     */
    private String mobile;
    /**
     * 续费时间
     */
    private Timestamp addTime;
    /**
     * 续费金额
     */
    private BigDecimal renewMoney;
    /**
     * 续费金额的单位
     * 0:现金 1：积分
     */
    private Byte renewType;
    /**
     * 续费时长
     */
    private Integer renewTime;
    /**
     * 续费时长的单位
     * 0:日，1:周 2: 月
     */
    private Byte renewDateType;
    /**
     * 当次续费后有效期
     */
    private Timestamp renewExpireTime;
}
