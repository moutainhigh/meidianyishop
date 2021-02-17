package com.meidianyi.shop.service.shop.config;

import org.springframework.stereotype.Service;

/**
 * 服务承诺-开关全局设置
 * @author: 卢光耀
 * @date: 2019-07-09 15:17
 *
*/
@Service

public class PledgeConfigService extends BaseShopConfigService{
    /**
     * 服务承诺键值
     */
    final public static String K_PLEDGE = "pledge";

    /**
     * 服务承诺value:0关闭 1打开
     */
    final public static String V_PLEDGE_CLOSE = "0";

    final public static String V_PLEDGE_OPEN = "1";

    public String getPledgeConfig(){
        return this.get(K_PLEDGE,V_PLEDGE_CLOSE);
    }

    public int setPledgeConfig(String v){
        return this.set(K_PLEDGE,v);
    }

}
