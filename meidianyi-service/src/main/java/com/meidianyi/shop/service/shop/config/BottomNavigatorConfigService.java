package com.meidianyi.shop.service.shop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.config.BottomNavigatorConfig;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 新国
 */
@Service

public class BottomNavigatorConfigService extends BaseShopConfigService {

    @Autowired
    JedisManager jedisManager;
    @Autowired
    private DomainConfig domainConfig;

    /**
     * 导航键值
     */
    final public static String K_BOTTOM = "bottom";

    /**
     * 获取底部导航配置
     *
     * @return
	 */
	public List<BottomNavigatorConfig> getBottomNavigatorConfig() {
        List<BottomNavigatorConfig> res = this.getJsonObject(K_BOTTOM, new TypeReference<List<BottomNavigatorConfig>>() {
        });
        for (BottomNavigatorConfig c : res) {
            if (StringUtil.isNotBlank(c.getNormal())) {
                //兼容一下带域名和不带域名的图片路径
                c.setNormal(domainConfig.imageUrl(RegexUtil.getUri(c.getNormal())));
            }
            if (StringUtil.isNotBlank(c.getHover())) {
                c.setHover(domainConfig.imageUrl(RegexUtil.getUri(c.getHover())));
            }
        }
        return res;
    }

    /**
     * 设置底部导航配置
	 * 
	 * @param config
	 * @return
	 */
	public int setBottomNavigatorConfig(List<BottomNavigatorConfig> config) {
        for (BottomNavigatorConfig c : config) {
            if (StringUtil.isNotBlank(c.getNormal())) {
                c.setNormal(RegexUtil.getUri(c.getNormal()));
            }
            if (StringUtil.isNotBlank(c.getHover())) {
                c.setHover(RegexUtil.getUri(c.getHover()));
            }
        }

        int res = this.setJsonObject(K_BOTTOM, config);
        if (res > 0) {
            //缓存
            jedisManager.set(JedisKeyConstant.CONFIG_BOTTOM + getShopId(), Util.toJson(config), ShopCommonConfigCacheService.MAX_TIME_OUT);
        }
        return res;
    }

}
