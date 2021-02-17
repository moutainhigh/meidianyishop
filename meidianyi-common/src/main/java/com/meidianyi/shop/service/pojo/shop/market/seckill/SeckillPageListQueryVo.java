package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年8月5日
 */
@Data
public class SeckillPageListQueryVo {
    /**主键*/
    private Integer skId;

    /** 活动名称 */
    private String name;

    private String goodsId;
    /** 商品种类数 */
    private Integer goods;
    private Byte first;
    /**
     * 商品库存
     */
    private Integer goodsNumber;
    /**
     * 商品价格
     */
    private BigDecimal shopPrice;
    private String goodsImg;
    /**
     * 是否在售，1在售，0下架
     */
    private Byte isOnSale;

    private Timestamp startTime;
    private Timestamp endTime;

    /**
     * 最小的一个秒杀价格
     */
    private BigDecimal secPrice;
    /**
     * 活动库存
     */
    private Integer stock;
    /**
     * 停用启用的状态：1可用，0停用
     */
    private Byte status;

    /**商品交易数量*/
    private Integer saleNum;

    /**初始销量*/
    private Integer baseSale;

    /**单用户最大购买数量*/
    private Short limitAmount;

    /**
     * 当前活动状态：1进行中，2未开始，3已过期，4已停用
     */
    private Byte currentState;
}
