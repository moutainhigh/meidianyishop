package com.meidianyi.shop.service.pojo.shop.market.seckill;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-06 14:29
 **/
@Data
public class SeckillUpdateParam {
    /**
     * 活动的主键
     */
    @NotNull
    @Min(1)
    private Integer skId;

    /**
     * 状态：1可用，0停用
     */
    @Min(0)
    @Max(1)
    private Byte status;

    /**
     * 开始时间
     */
    private Timestamp startTime;

    /**
     * 结束时间
     */
    private Timestamp endTime;

    /**
     * 每人限购数量
     */
    @Min(0)
    private Short limitAmount;

    /**
     * 规定的有效支付时间 单位：分钟
     */
    @Min(0)
    private Short limitPaytime;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 专属会员卡，卡ID字符串，逗号分隔；为空时代表该活动所有人都可以参与
     */
    private String cardId;

    /**
     * 分享设置
     */
    private PictorialShareConfig shareConfig;

    /**
     * 秒杀商品规格价格设置实体
     */
    private List<SeckillProductAddParam> secKillProduct;

    /**
     * 优先级
     */
    private Byte first;

    /**
     * 活动初始销量
     */
    private Integer baseSale;

    /**
     * 是否免运费： 1：免运费  0： 原先商品的运费
     */
    @Min(0)
    @Max(1)
    private Byte freeFreight;

    /**
     * 预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数
     */
    private Integer preTime;

    /**
     * 是否给参加活动的用户打标签，1是
     */
    private Byte activityTag;
    /**
     * 参加活动打标签id列表
     */
    private List<Integer> activityTagId;
}
