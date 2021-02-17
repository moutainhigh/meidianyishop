package com.meidianyi.shop.service.shop.config;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author: 王兵兵
 * @create: 2019-08-19 09:43
 **/
@Service
public class FirstSpecialConfigService extends BaseShopConfigService {

    /**
     * 首单特惠，设置用户仅可购买活动商品的数量
     */
    final public static String K_FIRST_LIMIT_GOODS = "first_limit_goods";

    /**
     * 取首单特惠用户仅可购买活动商品的数量
     * @return
     */
    public Integer getFirstLimitGoods() {
        return this.get(K_FIRST_LIMIT_GOODS, Integer.class, 0);
    }

    /**
     * 设置首单特惠用户仅可购买活动商品的数量
     * @return
     */
    public int setFirstLimitGoods(int value) {
    	Assert.isTrue(value >= 0,"value need >=0");
        return this.set(K_FIRST_LIMIT_GOODS,  value, Integer.class);
    }
}
