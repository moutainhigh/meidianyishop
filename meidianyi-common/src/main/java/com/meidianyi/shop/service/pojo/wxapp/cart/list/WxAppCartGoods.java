package com.meidianyi.shop.service.pojo.wxapp.cart.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant.GOODS_STATUS_ON_SALE;

/**
 * @author 孔德成
 * @date 2019/10/16 11;49
 */
@Data
public class WxAppCartGoods {

    //***** 购物车 *************
    /**
     * 购物车;id
     */
    private Integer cartId;
    /**
     * 商品现在价格
     */
    private BigDecimal goodsPrice;
    /**
     * 最终价格
     */
    private BigDecimal prdPrice;
    /**
     * 规格的市场价
     */
    private BigDecimal prdMarketPrice;
    /**
     * 添加购物车时价格
     */
    private BigDecimal originalPrice;
    /**
     * 数量
     */
    private Integer cartNumber;
    /**
     * 是否选中
     */
    private Byte isChecked;
    /**
     * 商品状态 1 在售 2 下架 3 删除 4 售罄
     */
    private Byte goodsStatus =GOODS_STATUS_ON_SALE;
    /**
     * 购买状态 0不可以购买 1可以购买
     */
    private Byte buyStatus =BaseConstant.YES;
    /**
     * 价格状态 0 不确定 1确定
     */
    private Byte priceStatus =BaseConstant.NO;
    /**
     *  指定的活动类型，21满折满减
     */
    private Byte type;
    /**
     * 扩展字段: 如：换购挡位ID
     */
    private Integer extendId;

    /**
     * 最终价格的取价来源活动，0普通商品，2分销改价，6限时降价，18首单特惠，23会员专享
     */
    private Byte priceActivityType = BaseConstant.ACTIVITY_TYPE_GENERAL;
    /**
     * 活动数量限制
     */
    private Integer activityLimitMinNum;
    /**
     * 活动数量限制
     */
    private Integer activityLimitMaxNum;
    /**
     * 超限购买设置标记，1禁止超限购买，0超限全部恢复原价
     * 部分活动会设置
     */
    private Byte activityLimitType =1;
    /**
     * 限制活动类型
     */
    private Byte limitActivityType  =BaseConstant.ACTIVITY_TYPE_GENERAL;

    private Integer storeId;
    private Integer userId;
    /**
     * 商品id
     */
    private Integer goodsId;
    private String goodsSn;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品
     */
    private String goodsImg;
    /**
     * 商品规格数据
     */
    private String prdImg;
    private String prdDesc;
    private Integer productId;
    private String prdSn;
    /**
     * 商品库存
     */
    private Integer prdNumber;
    /**
     * 商品最少限购数量
     */
    private Integer limitBuyNum;
    /**
     * 商品最大限购数量
     */
    private Integer limitMaxNum;


    /**
     * 活动id
     */
    private Integer activityId;
    /**
     * 活动类型
     */
    private Byte activityType;
    //***** 商品属性 *************
    /**
     * 商品
     */
    @JsonIgnore
    GoodsRecord goodsRecord;
    /**
     * 规格
     */
    @JsonIgnore
    GoodsSpecProductRecord productRecord;
    /**
     * 活动列表
     */
    private List<CartActivityInfo> cartActivityInfos = new ArrayList<>();

    public CartActivityInfo getActivity(Byte activityType) {
        if (cartActivityInfos==null||cartActivityInfos.size()==0){
            return null;
        }
        return cartActivityInfos.stream().filter(cartActivityInfo -> cartActivityInfo.getActivityType().equals(activityType)).findFirst().orElse(null);
    }


}
