package com.meidianyi.shop.service.pojo.shop.market.seckill;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.member.card.SimpleMemberCardVo;
import com.meidianyi.shop.service.pojo.shop.member.tag.TagVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-06 17:01
 **/
@Data
public class SeckillVo {
    /** 活动id*/
    private Integer skId;

    /** 活动名称*/
    private String name;

    /** 优先级*/
    private Byte first;

    /** 商品信息*/
    private List<SeckillGoods> goods;

    /** 开始时间*/
    private Timestamp startTime;

    /** 结束时间*/
    private Timestamp endTime;

    /** 每人限购数量*/
    private Short limitAmount;

    /** 秒杀当前库存*/
    private Integer stock;

    /** 初始销量*/
    private Integer baseSale;

    /** 规定的有效支付时间 单位：分钟*/
    private Short limitPaytime;

    /** 是否免运费： 1：免运费  0： 原先商品的运费*/
    private Byte freeFreight;

    /** 专属会员卡，卡ID字符串，逗号分隔；为空时代表该活动所有人都可以参与*/
    private List<SimpleMemberCardVo> memberCard;

    private String shareConfig;
    private PictorialShareConfigVo shopShareConfig;

    /**
     * 预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数
     */
    private Integer preTime;
    /**
     * 是否给参加活动的用户打标签，1是
     */
    private Byte activityTag;
    /**
     * 标签列表
     */
    private List<TagVo> tagList;

    @Setter
    @Getter
    public static class SeckillGoods{
        private Integer goodsId;
        private String goodsName;
        /**
         * 商品主图
         */
        private String goodsImg;
        /**
         * 商品库存
         */
        private Integer goodsNumber;

        /**
         * 商品价格
         */
        private BigDecimal shopPrice;
        /**
         * 单位
         */
        private String unit;
        /**
         * 销量
         */
        private Integer saleNum;
        /**
         * 秒杀商品规格价格设置实体
         */
        private List<SecKillProductVo> secKillProduct;
    }
}
