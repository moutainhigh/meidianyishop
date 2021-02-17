package com.meidianyi.shop.service.pojo.shop.member.account;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 会员卡续费
 * @author liangchen
 * @date 2020.04.07
 */
@Data
public class RenewValidCardList {
    /** 用户id */
    private Integer userId;
    /** 会员卡id */
    private Integer cardId;
    /** 0:正常 1:删除 */
    private Byte flag;
    /** 会员卡编号 */
    private String cardNo;
    /** 过期时间 */
    private Timestamp expireTime;
    /** 1:默认会员卡 */
    private Byte isDefault;
    /** 卡余额 */
    private BigDecimal money;
    /** 卡剩余次数 */
    private Integer surplus;
    /** 激活时间 */
    private Timestamp activationTime;
    /** 卡剩余兑换次数 */
    private Integer exchangSurplus;
    /** -1：不包邮，0:不限制，1：持卡有效期内，2：年，3：季，4：月，5：周，6：日 */
    private Byte freeLimit;
    /** 周期内包邮次数 */
    private Integer freeNum;
    /** 卡名 */
    private String cardName;
    /** 0:普通会员卡，1:次卡,2:登记卡 */
    private Byte cardType;
    /** 折扣 */
    private BigDecimal discount;
    /** 0:背景色，1:背景图 */
    private Byte bgType;
    /** 背景色 */
    private String bgColor;
    /** 背景图 */
    private String bgImg;
    /** 购物送积分策略json数据 */
    private String buyScore;
    /** 结束日期 */
    private Timestamp endTime;
    /** 领取之日起n */
    private Integer receiveDay;
    /** 0:日，1:周 2: 月 */
    private Byte dateType;
    /** 可用门店 */
    private String storeList;
    /** 0：不用激活，1：需要激活 */
    private Byte activation;
    /** 0:固定日期 1：自领取之日起 2:不过期 */
    private Byte expireType;
    /** 0:未过期 1：已过期 */
    private Byte expire;
    /** 开始日期 */
    private Timestamp startDate;
    /** 结束日期 */
    private Timestamp endDate;
    /** 开始日期 */
    private Timestamp createTime;
}
