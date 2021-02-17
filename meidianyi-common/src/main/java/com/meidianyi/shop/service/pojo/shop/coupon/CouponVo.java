package com.meidianyi.shop.service.pojo.shop.coupon;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/8/13 13:53
 */
@Data
public class CouponVo {
    private Integer    id;
    private String     actCode;
    private String     actName;
    private Timestamp startTime;
    private Timestamp  endTime;
    private BigDecimal denomination;
    private Integer    totalAmount;
    private Byte       type;
    private Integer    surplus;
    private Byte       useConsumeRestrict;
    private BigDecimal    leastConsume;
    private String     useExplain;
    private Byte       enabled;
    private Byte       isRandom;
    private Short      receivePerPerson;
    private Byte       suitGoods;
    private Byte       togetherUsed;
    private Byte       permitShare;
    private Byte       remindOwner;
    private Short      giveoutAmount;
    private Short      giveoutPerson;
    private Short      receiveAmount;
    private Short      receivePerson;
    private Short      usedAmount;
    private String     aliasCode;
    private String     validationCode;
    private String     recommendGoodsId;
    private String     recommendCatId;
    private String     recommendSortId;
    private Integer    validity;
    private Byte       delFlag;
    private Byte       action;
    private String     identityId;
    private String     recommendProductId;
    private Byte       useScore;
    private Integer    scoreNumber;
    private String     cardId;
}
