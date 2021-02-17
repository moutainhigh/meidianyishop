package com.meidianyi.shop.service.pojo.shop.market.seckill;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-06 13:54
 **/
@Data
public class SeckillAddParam {
    /** 活动名称*/
    @NotNull
    private String name;

    /** 商品id，字符串，逗号分隔*/
    @NotNull
    private String goodsId;

    /** 开始时间*/
    @NotNull
    private Timestamp startTime;

    /** 结束时间*/
    @NotNull
    private Timestamp endTime;

    /** 总库存（各规格秒杀库存之和）*/
    @NotNull
    private Integer stock;

    /** 每人限购数量*/
    @NotNull
    @Min(0)
    private Short limitAmount;

    /** 规定的有效支付时间 单位：分钟*/
    @NotNull
    @Min(0)
    private Short limitPaytime;

    /** 秒杀商品规格价格设置实体*/
    @NotNull
    @Size(min=1)
    private SeckillProductAddParam[] secKillProduct;

    /** 是否免运费： 1：免运费  0： 原先商品的运费*/
    @NotNull
    @Min(0)
    @Max(1)
    private Byte freeFreight;

    /**活动初始销量*/
    private Integer baseSale;

    /** 专属会员卡，卡ID字符串，逗号分隔；为空时代表该活动所有人都可以参与*/
    private String cardId;

    /**优先级*/
    @NotNull
    private Byte first;

    /** 分享设置*/
    private PictorialShareConfig shareConfig;

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
