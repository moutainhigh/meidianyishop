package com.meidianyi.shop.service.pojo.shop.market.lottery;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/8/5 14:13
 */
@Data
public class LotteryPageListVo {

    public static final String JOIN_NUMBER ="joinNumber";
    public static final String AWAED_NUMBER ="awardNumber";


    private Integer   id;
    private String    lotteryName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte      status;

    /**
     * 参与人次
     */
    private Integer joinNumber;
    /**
     * 中奖人次
     */
    private Integer awardNumber;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;

}
