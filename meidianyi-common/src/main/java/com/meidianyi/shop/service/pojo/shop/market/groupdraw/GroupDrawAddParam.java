package com.meidianyi.shop.service.pojo.shop.market.groupdraw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 拼团抽奖 - 添加活动入参
 *
 * @author 郑保乐
 */
@Data
public class GroupDrawAddParam {

    /** 活动名称 **/
    @NotEmpty
    private String name;
    /** 活动开始时间 **/
    @NotNull
    private Timestamp startTime;
    /** 活动结束时间 **/
    @NotNull
    private Timestamp endTime;
    /** 奖池最少人数 **/
    @NotNull
    private Short minJoinNum;
    /** 商品金额 **/
    @NotNull
    private BigDecimal payMoney;
    /** 最大参团数量 **/
    @NotNull
    private Short joinLimit;
    /** 最大开团数量 **/
    @NotNull
    private Short openLimit;
    /** 最小成团人数 **/
    @NotNull
    private Short limitAmount;
    /** 最小展示人数 **/
    @NotNull
    private Short toNumShow;
    /** 鼓励奖优惠券 **/
    private List<Integer> rewardCouponIds;
    /** 商品 id **/
    @NotEmpty
    private List<Integer> goodsIds;
    /** 活动说明 */
    private GroupDrawActCopywriting actCopywriting;
    @JsonIgnore
    private String activityCopywriting;
    @JsonIgnore
    private String goodsId;
    @JsonIgnore
    private String rewardCouponId;
}
