package com.meidianyi.shop.service.pojo.shop.market.lottery.prize;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/8/6 13:27
 */
@Data
public class LotteryPrize {
    private Integer   id;
    private Integer   lotteryId;
    private Integer   chanceNumerator;
    private Integer   chanceDenominator;
    private Byte      lotteryGrade;
    private String    lotteryDetail;
    private String    iconImgsImage;
    private String    iconImgs;
    private Byte      lotteryType;
    private Integer   lotteryNumber;
    private Integer   awardTimes;
    private Integer   integralScore;
    private Byte      couponId;
    private Byte      prdId;
    private Byte      prdKeepDays;
    private Byte      delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;

}
