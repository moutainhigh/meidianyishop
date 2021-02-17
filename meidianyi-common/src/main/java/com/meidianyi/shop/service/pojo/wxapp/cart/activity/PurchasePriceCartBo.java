package com.meidianyi.shop.service.pojo.wxapp.cart.activity;

import com.meidianyi.shop.service.pojo.wxapp.cart.list.CartActivityInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车里的满折满减活动
 * @author 孔德成
 * @date 2020/3/4
 */
@Getter
@Setter
public class PurchasePriceCartBo {

    private Integer purchaseId;
    private Integer purchaseRuleId;
    /**
     * 商品id
     */
    private List<Integer> goodsId =new ArrayList<>();
    /**
     * 商品规格id
     */
    private List<Integer> productId=new ArrayList<>();
    /**
     * 购物车id
     */
    private List<Integer> cartId=new ArrayList<>();
    /**
     * 数量
     */
    private List<Integer> num=new ArrayList<>();
    /**
     * 钱
     */
    private List<BigDecimal> money=new ArrayList<>();
    /**
     * 加价购
     */
    private CartActivityInfo.PurchasePrice purchasePrice;
}
