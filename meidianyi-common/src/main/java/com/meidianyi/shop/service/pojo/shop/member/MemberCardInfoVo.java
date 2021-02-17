package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Data;

import java.math.BigDecimal;
import java.security.Timestamp;

/**
 * 会员卡详情
 * @author 孔德成
 * @date 2020/4/13
 */
@Data
public class MemberCardInfoVo {
    private Integer    id;
    private String     cardName;
    private Byte       cardType;
    private Byte       bgType;
    private String     bgColor;
    private String     bgImg;
    private BigDecimal discount;
    private Integer    sorce;
    private String     buyScore;
    private Byte       expireType;
    private Timestamp  startTime;
    private Timestamp endTime;
    private Integer    receiveDay;
    private Byte       dateType;
    private Byte       activation;
    private String     receiveCode;
    private String     desc;
    private String     mobile;
    private Timestamp  createTime;
    private Timestamp  updateTime;
    private Byte       flag;
    private Integer    sendMoney;
    private String     chargeMoney;
    private Integer    useTime;
    private String     storeList;
    private Integer    count;
    private Byte       delFlag;
    private String     grade;
    private String     gradeCondition;
    private String     activationCfg;
    private Byte       examine;
    private String     discountGoodsId;
    private String     discountCatId;
    private String     discountSortId;
    private Byte       discountIsAll;
    private Byte       isPay;
    private Byte       payType;
    private BigDecimal payFee;
    private Byte       payOwnGood;
    private Byte       receiveAction;
    private Byte       isExchang;
    private Byte       storeUseSwitch;
    private String     exchangGoods;
    private Byte       exchangFreight;
    private Integer    exchangCount;
    private Integer    stock;
    private Integer    limit;
    private String     discountBrandId;
    private Byte       sendCouponSwitch;
    private Byte       sendCouponType;
    private String     sendCouponIds;
    private String     customRights;
    private Byte       freeshipLimit;
    private Integer    freeshipNum;
    private Byte       renewMemberCard;
    private Byte       renewType;
    private BigDecimal renewNum;
    private Integer    renewTime;
    private Byte       renewDateType;
    private Byte       cannotUseCoupon;
    private Byte       customRightsFlag;

}
