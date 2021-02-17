package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

/**
 * @author liufei
 * @date 2019/8/20
 */
@Data
public class ShareRewardAddParam {
    /**
     * 活动id
     */
    @PositiveOrZero(groups = UpdateGroup.class)
    private Integer id;
    /**
     * 活动名称
     */
    @NotNull
    private String name;
    /**
     * 活动起始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp startTime;
    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp endTime;
    /**
     * 是否永久有效
     */
    @NotNull
    private Byte isForever;
    /**
     * 活动优先级
     */
    @NotNull
    private Integer priority;
    /**
     * 触发条件：1.分享全部商品，2.分享指定商品，3.分享访问量较少商品
     */
    @NotNull
    private Byte condition;
    /**
     * 触发条件为2时：分享指定商品id列表，逗号分隔符
     */
    private String goodsIds;
    /**
     * 触发条件为3时：被分享商品访问量条件
     */
    private Integer goodsPv;
    /**
     * 是否仅邀请未访问过店铺的用户有效
     */
    private Byte visitFirst;

    /**
     * 一级分享规则
     */
    private ShareRule firstRule;
    private String firstLevelRule;
    /**
     * 二级分享规则
     */
    private ShareRule secondRule;
    private String secondLevelRule;
    /**
     * 三级分享规则
     */
    private ShareRule thirdRule;
    private String thirdLevelRule;

    /**
     * 一级规则奖励奖品剩余数量
     */
    private Integer firstAwardNum;
    /**
     * 二级规则奖励奖品剩余数量
     */
    private Integer secondAwardNum;
    /**
     * 三级规则奖励奖品剩余数量
     */
    private Integer thirdAwardNum;
}
