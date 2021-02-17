package com.meidianyi.shop.service.pojo.shop.config;

import lombok.Data;

/**
 * 微信好物圈功能设置
 * @author 李晓冰
 * @date 2019年08月06日
 */
@Data
public class WxShoppingListConfig {
    /**"0"未开启 "1"开启*/
    private String enabeldWxShoppingList;
    /** "1"订单详情页显示，"2"商品详情页显示，"1,2"前两者都设置，""空表示未设置*/
    private String wxShoppingRecommend;
}
