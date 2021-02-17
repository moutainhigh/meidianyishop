package com.meidianyi.shop.service.shop.config;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * 赠品配置
 * @author 王帅
 */
@Service
public class GiftConfigService extends BaseShopConfigService{
    /**
     * 赠品叠加：当买家满足多个活动的赠品条件。
     * 1：只赠送其中优先级最高的活动赠品  0：赠送满足赠品条件的所有赠品
     */
    final public static String K_GIFT_CONDITION = "gift_condition";

    public Byte getCfg() {
        return get(K_GIFT_CONDITION, Byte.class, NumberUtils.BYTE_ZERO);
    }

    public void setCfg(Byte value) {
        set(K_GIFT_CONDITION,  value, Byte.class);
    }
}
