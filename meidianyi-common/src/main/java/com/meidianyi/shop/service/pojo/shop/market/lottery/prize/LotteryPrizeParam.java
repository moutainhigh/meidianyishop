package com.meidianyi.shop.service.pojo.shop.market.lottery.prize;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2019/8/7 17:41
 */
@Data
public class LotteryPrizeParam {

    private Integer id;
    private Integer lotteryId;
    @NotNull
    private Integer chanceNumerator;
    @NotNull
    private Integer chanceDenominator;
    private Byte lotteryGrade;
    private Byte lotteryType;
    private Integer lotteryNumber;
    private Integer awardTimes;
    private Integer integralScore;
    private BigDecimal account;
    private Integer couponId;
    private Integer prdId;
    private Short prdKeepDays;
    private String lotteryDetail;
    private String iconImgsImage;
    private String iconImgs;
}
