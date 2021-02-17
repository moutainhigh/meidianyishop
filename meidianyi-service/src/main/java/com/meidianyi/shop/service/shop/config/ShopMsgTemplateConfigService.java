package com.meidianyi.shop.service.shop.config;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.pojo.shop.config.ShopMsgTempConfig;

/**
 * 
 * @author 新国
 *
 */
@Service
public class ShopMsgTemplateConfigService extends BaseShopConfigService {
	
	final public static String K_MESSAGE_STYLE = "message_library";
	
	/**
	 * 获取店铺风格配置
	 * 
	 * @return
	 */
	public ShopMsgTempConfig getShopTempConfig() {
		return this.getJsonObject(K_MESSAGE_STYLE, ShopMsgTempConfig.class);
	}

	/**
	 * 设置店铺风格配置
	 * 
	 * @param  config
	 * @return
	 */
	public int setShopTempConfig(ShopMsgTempConfig config) {
		return this.setJsonObject(K_MESSAGE_STYLE, config);
	}
	
	
	public int  setShopTempConfig(String config) {
		return this.set(K_MESSAGE_STYLE, config);
	}
}
