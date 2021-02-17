package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 好友助力商品详情
 * @author liangchen
 * @date 2020.02.25
 */
@Data
public class GoodsInfo {
    /** 商品id */
    private Integer goodsId;
    /** 规格id */
    private Integer prdId;
    /** 规格描述 */
    private String prdDesc;
    /** 商品名称 */
    private String goodsName;
    /** 商品图片 */
    private String goodsImg;
    /** 商品价格 */
    private BigDecimal goodsPrice;
    /** 商品库存 */
    private Integer goodsStore;
    /** 市场价 */
    private BigDecimal marketPrice;
    /** 更新时间 */
    private Timestamp updateTime;
    /** 展示库存 */
    private Integer marketStore;
}
