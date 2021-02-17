package com.meidianyi.shop.service.pojo.wxapp.market.gift;

import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: 王兵兵
 * @create: 2020-03-26 13:57
 **/
@Setter
@Getter
public class ShowCartConfigVo {
    /**
     * 商品搜索页以及推荐商品列表中会显示购买按钮
     *  0：关，1：开
     */
    public Byte showCart = 0;

    /**
     * 购买按钮类型
     *  0，1，2，3  四种类型
     */
    public Integer cartType = 0;

    public ShowCartConfigVo(ShowCartConfig showCartConfig){
        this.showCart = showCartConfig.getShowCart();
        this.cartType = showCartConfig.getCartType();
    }
}
