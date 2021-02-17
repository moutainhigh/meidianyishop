package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.goods.brand.GoodsBrandConfig;
import org.springframework.stereotype.Service;

/**
 * @author 李晓冰
 * @date 2019年07月27日
 */
@Service
public class GoodsBrandConfigService extends BaseShopConfigService {
    /** 1显示 0不显示*/
    private final static String GOODS_BRAND_CONFIG = "goods_brand_config";


    /**
     * 配置商品品牌展示
     * @param config
     */
    public void setGoodsBrandConfig(GoodsBrandConfig config) {
        this.set(GOODS_BRAND_CONFIG, Util.toJson(config));
    }

    /**
     *
     * @return
     */
    public GoodsBrandConfig getGoodsBrandConfig() {
        GoodsBrandConfig brandConfig = new GoodsBrandConfig();
        String s = this.get(GOODS_BRAND_CONFIG);
        if (s != null) {
            brandConfig=  Util.parseJson(s,GoodsBrandConfig.class);
        }
       return brandConfig;
    }
}
