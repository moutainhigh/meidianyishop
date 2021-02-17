package com.meidianyi.shop.service.pojo.shop.market.lottery.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/5 15:28
 */
@Data
public class LotteryRecordPageListParam {
    /**
     * 分页信息
     */
    private Integer currentPage;
    private Integer pageRows;


    /**
     * 抽奖活动id
     */
    @NotNull
    private Integer lotteryId;
    /**
     * 用户电话
     */
    private String mobile;
    /**
     * 用户姓名
     */
    private String username;
    /**
     * 中奖等级  0未中奖，1一等奖，2二等奖，3三等奖，4四等奖
     */
    private Byte lotteryGrade;
    /**
     * 抽奖来源 1.登录  2.支付有礼
     */
    private Byte lotterySource;
    /**
     * 抽奖来源id
     */
    @JsonIgnore
    private Integer lotteryActId;
    /**
     * 抽奖机会来源 0免费 1分享 2积分
     */
    private Byte chanceSource;


}
