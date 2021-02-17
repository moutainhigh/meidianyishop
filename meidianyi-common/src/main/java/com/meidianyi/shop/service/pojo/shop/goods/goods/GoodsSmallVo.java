package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.math.BigDecimal;

/**
* @author 黄壮壮
* @Date: 2019年10月29日
* @Description: 商品部分信息
*/
@Data
public class GoodsSmallVo {
	private Integer goodsId;
	private String goodsName;
	/**
     * 商品货号
     */
    private String goodsSn;
    /**
     * 在售状态1在售,0下架
     */
    private Byte isOnSale;
    /**
     * 商品库存，该字段是通过商品规格计算而来
     */
    private Integer goodsNumber;
    /**
     * 0 未删除； 1： 删除
     */
    private Byte delFlag;
    
    /**
     * 商品主图
     */
    private String goodsImg;
    /**
     * 商品价格，商品规格中的最低价格，（对于默认规格和自定义规格计算方式是一样的）
     */
    private BigDecimal shopPrice;
    /**
     * 市场价格
     */
    private BigDecimal marketPrice;
    private Byte goodsType;
    /**
     * 是否会员专享
     */
    private Byte isCardExclusive;

    /** 单位 */
    private String unit;
    /**
     * 1默认规格，0自定义规格（多规格）
     */
    private Byte isDefaultProduct;
    /**
     * 限制兑换次数
     */
	private Integer limitExhangNum;
}
