package com.meidianyi.shop.service.wechat.api;

import com.google.gson.JsonObject;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMiniLinkGetResult;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年8月23日 上午10:42:16
 */

public interface WxOpenMpampLinkService extends WxOpenMaMpHttpBase {
	/**
	 * 小程序管理权限集
	 */
	static final String GET_BIND_MINI_INFO = "https://api.weixin.qq.com/cgi-bin/wxopen/wxamplinkget";

	

	/**
	 * 获取公众号关联的小程序
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	default WxOpenMiniLinkGetResult getBindMiniInfo(String appId) throws WxErrorException {
		JsonObject param = new JsonObject();
		String json = post(appId,GET_BIND_MINI_INFO, param.toString());
		return WxOpenMiniLinkGetResult.fromJson(json);
	}

}
