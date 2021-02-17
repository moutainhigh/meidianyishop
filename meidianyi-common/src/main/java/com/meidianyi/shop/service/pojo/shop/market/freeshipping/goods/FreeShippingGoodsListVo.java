package com.meidianyi.shop.service.pojo.shop.market.freeshipping.goods;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 包邮商品
 * @author 孔德成
 * @date 2020/1/7 15:14
 */
@Getter
@Setter
public class FreeShippingGoodsListVo {
    /**
     * 标题
     */
    private String title;

    /**
     * 商品列表
     */
    private List<GoodsListMpVo> goodsList;

    /**
     * 购物车商品
     */
    private List<GoodsListMpVo> cartGoodsList;


}
