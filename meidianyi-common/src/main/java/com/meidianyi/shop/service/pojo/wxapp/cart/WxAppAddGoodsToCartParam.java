package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/10/16 14:22
 */
@Getter
@Setter
public class WxAppAddGoodsToCartParam {
    /**
     * 修改
     */
    public static final Byte CART_GOODS_NUM_TYPE_GENERAL=1;
    /**
     * 累加
     */
    public static final Byte CART_GOODS_NUM_TYPE_ADD=2;

    private Integer userId;
    private Integer cartId;
    /**
     * 数量
     */
    @NotNull
    @Min(1)
    private Integer goodsNumber;
    /**
     * 修改数量类型  默认1 直接修改数量, 2添加数量
     */
    private Byte type= CART_GOODS_NUM_TYPE_GENERAL;
    /**
     * 规格id
     */
    @NotNull
    private Integer prdId;
    /**
     * 活动类型
     */
    private Byte activityType;
    /**
     * 活动id
     */
    private Integer activityId;
    /** 直播的roomId*/
    private Integer roomId;

}
