package com.meidianyi.shop.service.pojo.shop.market.lottery;

import com.meidianyi.shop.service.pojo.shop.market.lottery.prize.LotteryPrizeParam;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;


/**
 * @author 孔德成
 * @date 2019/8/5 11:00
 */
@Data
public class LotteryParam {

    private Integer   id;
    @NotBlank
    private String    lotteryName;
    @NotNull
    private Timestamp startTime;
    @NotNull
    private Timestamp endTime;
    @NotNull
    private String    lotteryExplain;
    /**
     * 免费抽奖次数 0不限制 -1不可免费抽奖
     */
    private Integer   freeChances;
    private Byte      canShare;
    private Integer   shareChances;
    private Byte      canUseScore;
    private Integer   scorePerChance;
    private Integer   scoreChances;
    /**
     * 未中奖奖励积分 0不赠送积分
     */
    private Integer   noAwardScore;
    private String    noAwardImage;
    private String    noAwardIcon;
    /**
     * 次数限制 0每人 1每人每天
     */
    private Byte chanceType;

    @Valid
    private List<LotteryPrizeParam> prizeList ;
}
