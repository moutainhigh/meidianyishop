package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 积分兑换活动弹窗
 * @author liangchen
 * @date 2020.01.15
 */
@Data
public class PopListVo {
    /** 活动id */
    private Integer integralGoodsId;
    /** 商品id */
    private Integer goodsId;
    /** 商品名称 */
    private String goodsName;
    /** 商品图片 */
    private String goodsImg;
    /** 商品价格 */
    private BigDecimal prdPrice;
    /** 活动库存 */
    private Short stockSum;
    /** 活动价格 */
    private BigDecimal money;
    /** 活动积分 */
    private Integer score;
    /** 开始时间 */
    private Timestamp startTime;
    /** 结束时间 */
    private Timestamp endTime;
    /** 是否在售 */
    private Byte isOnSale;
    /** 是否删除 */
    private Byte isDelete;
}
