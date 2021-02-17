package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.service.pojo.shop.config.WxShoppingListConfig;
import org.springframework.stereotype.Service;

/**
 * @author 李晓冰
 * @date 2019年08月06日
 */
@Service
public class WxShoppingListConfigService extends BaseShopConfigService {

    final static String K_ENABLED_WX_SHOPPING_LIST = "enabled_wx_shopping_list";

    final static String K_WX_SHOPPING_RECOMMEND = "wx_shopping_recommend";

    final static String DISABLE_WX_SHOPPING_LIST="0";

    /**
     * @return 好物圈配置信息
     */
    public WxShoppingListConfig getShoppingListConfig() {
        WxShoppingListConfig wxShoppingListConfig = new WxShoppingListConfig();

        String v1 = get(K_ENABLED_WX_SHOPPING_LIST);
        String v2 = get(K_WX_SHOPPING_RECOMMEND);

        v1 = v1 == null ? "0" : v1;
        v2 = v2 == null ? "" : v2;
        wxShoppingListConfig.setEnabeldWxShoppingList(v1);
        wxShoppingListConfig.setWxShoppingRecommend(v2);

        return wxShoppingListConfig;
    }

    /**
     * @param wxShoppingListConfig 好物圈配置信息
     */
    public void setShoppingListConfig(WxShoppingListConfig wxShoppingListConfig){

        set(K_ENABLED_WX_SHOPPING_LIST,wxShoppingListConfig.getEnabeldWxShoppingList());

        if (DISABLE_WX_SHOPPING_LIST.equals(wxShoppingListConfig.getEnabeldWxShoppingList())) {
            set(K_WX_SHOPPING_RECOMMEND,"");
        } else {
            set(K_WX_SHOPPING_RECOMMEND,wxShoppingListConfig.getWxShoppingRecommend());
        }
    }
}
