package com.meidianyi.shop.service.saas.shop;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;

import lombok.extern.slf4j.Slf4j;
/**
 * 项目初始化时候读配置文件到redis
 * @author zhaojianqiang
 * @date 2020年5月20日下午1:58:42
 */
@Slf4j
@Component
public class ShopAuthInfoService implements InitializingBean {
    @Autowired
    private JedisManager jedis;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("读取配置文件存的redis");
		String menuJson = Util.loadResource(AuthConstant.FILE_MENUJSON);
		String authorityJson = Util.loadResource(AuthConstant.FILE_AUTHORITYJSON);
		String versionJson = Util.loadResource(AuthConstant.FILE_VERSIONJSON);
		jedis.set(AuthConstant.KEY_MENU, menuJson);
		jedis.set(AuthConstant.KEY_AUTHORITY, authorityJson);
		jedis.set(AuthConstant.KEY_VERSION, versionJson);
		log.info("读取配置文件存的redis结束");
	}

}
