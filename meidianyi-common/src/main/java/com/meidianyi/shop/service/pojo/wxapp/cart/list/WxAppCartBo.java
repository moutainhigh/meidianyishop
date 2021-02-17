package com.meidianyi.shop.service.pojo.wxapp.cart.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartConstant;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.CouponPackCartVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 购物车业务类
 * @author 孔德成
 * @date 2019/11/11 9:49
 */
@Builder
@Getter
@Setter
public class WxAppCartBo {

    /**
     * 总价
     */
    private BigDecimal totalPrice;
    /**
     * 满折满减折扣价格
     */
    private BigDecimal fullReductionPrice;
    /**
     * 商品总数量
     */
    private Integer totalGoodsNum;
    /**
     * 是否能支付
     */
    private Byte isCanPayment;
    /**
     * 是否全选
     */
    private Byte isAllCheck;
    /**
     * 商品列表
     */
    List<WxAppCartGoods> cartGoodsList;
    /**
     * 满折满减 k活动id
     */
    Map<Integer,List<WxAppCartGoods>> fullReductionGoodsMap;
    /**
     * 加价购 k活动id
     */
    Map<Integer,List<WxAppCartGoods>> purchasePriceGoodsMap;
    /**
     * 购物车 - 失效商品
     */
    private List<WxAppCartGoods> invalidCartList;
    /**
     * 消息状态 0正常 1有提示信息
     */
    @Builder.Default
    private Byte noticeStatus = CartConstant.CART_NOTICE_STATUS_COMMON;
    /**
     * 提示信息
     */
    private String notice ;
    /**
     * 可用的优惠券礼包
     */
    private CouponPackCartVo couponPackage;

    @JsonIgnore
    private Byte activityType;
    @JsonIgnore
    private Integer activityId;
    @JsonIgnore
    private Integer userId;
    @JsonIgnore
    private Boolean isNewUser;
    @JsonIgnore
    private List<Integer> productIdList;
    @JsonIgnore
    private List<Integer> goodsIdList;
    @JsonIgnore
    private Timestamp date;
    @JsonIgnore
    private Integer storeId;

}
