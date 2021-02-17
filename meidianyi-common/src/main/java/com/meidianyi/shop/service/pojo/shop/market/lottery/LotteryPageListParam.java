package com.meidianyi.shop.service.pojo.shop.market.lottery;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2019/8/5 11:48
 */
@Data
public class LotteryPageListParam {

    /**
     * 活动状态过滤 ：
     */
    @Max(5)
    @Min(0)
    private Byte state = (byte)0;

    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows ;
}
