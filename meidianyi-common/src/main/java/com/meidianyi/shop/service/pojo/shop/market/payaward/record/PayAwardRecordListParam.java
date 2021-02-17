package com.meidianyi.shop.service.pojo.shop.market.payaward.record;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 活动明细请求参数
 * @author 孔德成
 * @date 2019/10/31 16:04
 */
@Getter
@Setter
public class PayAwardRecordListParam {

    @NotNull
    private Integer id;
    /**
     * 领取时间
     */
    private Timestamp receiveTimeBegin ;
    private Timestamp receiveTimeEnd ;
    private String mobile;
    private String userName;
    /**
     * 礼物类型 0 无奖品 1普通优惠卷  2分裂优惠卷 3幸运大抽奖 4 余额 5 商品 6积分 7 自定义
     */
     private Integer awardType;

    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows ;
}
