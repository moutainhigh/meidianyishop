package com.meidianyi.shop.service.wechat.api;

import com.google.gson.JsonObject;
import com.meidianyi.shop.service.wechat.bean.open.WxMaLiveInfoResult;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 小程序直播
 * @author zhaojianqiang
 * 2020年4月2日下午4:39:37
 */
public interface WxMaLiveService extends WxOpenMaMpHttpBase {
	/** 获取直播房间列表*/
	static final String GET_LIVE_INFO = "http://api.weixin.qq.com/wxa/business/getliveinfo";


    /**
     * getLiveInfo
     *
     * @param appId
     * @param start
     * @param limit
     * @return
     * @throws WxErrorException
     */
	default WxMaLiveInfoResult getLiveInfo(String appId,Integer start,Integer limit) throws WxErrorException {
		JsonObject param = new JsonObject();
		param.addProperty("start", start);
		param.addProperty("limit", limit);
		String json = post(appId,GET_LIVE_INFO, param.toString());
		return WxMaLiveInfoResult.fromJson(json);
	}
}
