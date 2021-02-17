package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import org.springframework.stereotype.Service;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
@Service
public class RebateConfigService extends BaseShopConfigService {
    public final static String K_REBATE= "rebate";

    /**
     * 获取返利配置
     * @return
     */
    public RebateConfig getRebateConfig(){
        RebateConfig config=this.getJsonObject(K_REBATE,RebateConfig.class);
        return config;
    }

    /**
     * 设置返利配置
     * @param rebateConfig
     * @return
     */
    public int setRebateConfig(RebateConfig rebateConfig){
        return this.setJsonObject(K_REBATE,rebateConfig);
    }
}
