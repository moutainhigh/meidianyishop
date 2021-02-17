package com.meidianyi.shop.service.pojo.wxapp.cart;

import com.meidianyi.shop.common.foundation.data.BaseConstant;

/**
 * @author 孔德成
 * @date 2019/11/13 10:32
 */
public class CartConstant {


    //************************************************选中状态 1选中 0 没选中
    /**
     * 购物车选中状态 1
     */
    public final static byte CART_IS_CHECKED = 1;
    /**
     * 购物车没选中 0
     */
    public final static byte CART_NO_CHECKED = 0;

    //****************************************商品状态 1 在售 2 下架 3 删除 4 售罄 5专享
    /**
     * 在售
     */
    public final static byte GOODS_STATUS_ON_SALE = 1;
    /**
     * 下架
     */
    public final static byte GOODS_STATUS_OFF_SALE = 2;
    /**
     * 删除
     */
    public final static byte GOODS_STATUS_DELETE = 3;
    /**
     * 售罄
     */
    public final static byte GOODS_STATUS_SOLD_OUT = 4;
    /**
     * 专享
     */
    public final static byte GOODS_STATUS_EXCLUSIVE = 5;
    /**
     * 失效
     */
    public final static byte GOODS_STATUS_DISABLED = 6;
    /**
     * 商品规格删除
     */
    public final static byte GOODS_STATUS_PRODUCT_DELETE = 7;
    /**
     * 商品没有规格
     */
    public final static byte GOODS_STATUS_PRODUCT_CHANGE = 8;
    /**商品库存不足*/
    public final static byte GOODS_STATUS_STOCK_SHORTAGE = 9;


    //******************************************活动状态 0 失效 1 生效
    /**
     * 失效
     */
    public final static byte ACTIVITY_STATUS_INVALID = 0;
    /**
     * 生效
     */
    public final static byte ACTIVITY_STATUS_VALID = 1;

    //***************************************购物车消息状态
    /**
     * 无
     */
    public final static byte CART_NOTICE_STATUS_COMMON = 0;
    /**
     * 警告
     */
    public final static byte CART_NOTICE_STATUS_WARNINGS = 1;

}
