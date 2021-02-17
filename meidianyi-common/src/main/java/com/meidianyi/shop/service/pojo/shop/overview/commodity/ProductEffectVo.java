package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 2019/7/22
 * @description 商品效果
 */
@Data
public class ProductEffectVo {
    /** 商品id */
    private int goodsId;
    /** 商品名称 */
    private String goodsName;
    /** 商品图片 */
    private String goodsImg;
    /** 商品价格 */
    private BigDecimal shopPrice;
    /**  新成交客户数  */
    private  int newUserNumber;
    /**  老成交客户数  */
    private  int oldUserNumber;
    /**  浏览量  */
    private  int pv;
    /**  访客数  */
    private  int uv;
    /**  加购人数  */
    private  int cartUv;
    /**  付款人数  */
    private  int paidUv;
    /**  付款商品件数  */
    private  int paidGoodsNumber;
    /**  新成交客户数占比  */
    private BigDecimal newUserPercentage;
    /**  老成交客户数占比  */
    private BigDecimal oldUserPercentage;
    /**  商品转化率  */
    private BigDecimal uv2paidGoods;
    /**  推荐人数  */
    private int recommendUserNum;
    /**  收藏人数  */
    private int collectUserNum;
    /**
     * 分享次数
     */
    private int sharePv;
    /**
     * 分享人数
     */
    private int shareUv;
    /**
     * 销售额
     */
    private BigDecimal goodsSales;
}
