package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import com.meidianyi.shop.service.pojo.shop.config.ShopShareConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-11-12 11:35
 **/
@Getter
@Setter
public class SeckillMpVo extends GoodsActivityDetailMp {
    /** 当前前秒杀剩余库存(各规格库存之和) */
    private Integer stock;

    /** 每人限购数量 */
    private Short limitAmount;

    /** 规定的有效支付时间(分钟数) */
    private Short limitPaytime;

    /** 活动销量（已加入初始销量） */
    private Integer saleNum;

    /** 专属会员卡ID串，逗号隔开 */
    private String cardId;

    /** 分享配置 */
    private ShopShareConfig shareConfig;

    /** 秒杀规格 */
    private List<SecKillPrdMpVo> actProducts;
}
