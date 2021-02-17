package com.meidianyi.shop.service.pojo.wxapp.goods.goods.list;

import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import lombok.Data;

/**
 * 商品列表中样式展示控制类,店铺通用配置内进行控制
 * @author 李晓冰
 * @date 2020年03月11日
 */
@Data
public class GoodsShowStyleConfigBo {

    /**
     *是否显示划线价（市场价/销量/评论）开关，单选
     * 取值：0关闭，1显示市场价，2显示销量，3显示评价数
     */
    private Byte delMarket;

    /**
     * 购物车样式控制
     */
    private ShowCartConfig showCart;
}
