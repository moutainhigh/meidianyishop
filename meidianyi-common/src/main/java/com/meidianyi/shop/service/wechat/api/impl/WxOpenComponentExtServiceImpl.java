package com.meidianyi.shop.service.wechat.api.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.meidianyi.shop.service.wechat.api.WxOpenComponentExtService;
import com.meidianyi.shop.service.wechat.api.WxOpenComponentHttpBase;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenAuthorizerListResult;

import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import me.chanjar.weixin.open.api.WxOpenService;

/**
 * 用于实现原开放平台未实现的接口
 * 
 * @author lixinguo
 *
 */
public class WxOpenComponentExtServiceImpl implements WxOpenComponentHttpBase,WxOpenComponentExtService {

	public static final String METHOD_GET = "get";
	public static final String METHOD_POST = "post";
	
	static final Logger logger = LoggerFactory.getLogger(WxOpenComponentExtServiceImpl.class);

	protected WxOpenService openService;

	public WxOpenComponentExtServiceImpl(WxOpenService openService) {
		this.openService = openService;
	}
	
	/**
	 * 第三方平台可以使用接口拉取当前所有已授权的帐号基本信息。
	 * 
	 * @param offset
	 * @param count
	 * @return
	 * @throws WxErrorException
	 */
	@Override
    public WxOpenAuthorizerListResult getAuthorizerList(Integer offset, Integer count) throws WxErrorException {
		JsonObject param = new JsonObject();
		param.addProperty("component_appid", openService.getWxOpenConfigStorage().getComponentAppId());
		param.addProperty("offset", offset);
		param.addProperty("count", count);
		String json = post(GET_AUTHORIZER_LIST, param.toString(),COMPONENT_TOKEN_KEY);
		return WxOpenAuthorizerListResult.fromJson(json);
	}
	
	

	/**
	 * 请求WxOpenComponentServiceImpl私有方法
	 * 
	 * @param method         WxOpenComponentServiceImpl的私有方法get或者post
	 * @param uri
	 * @param data
	 * @param accessTokenKey
	 * @return
	 * @throws WxErrorException
	 */
	public String request(String method, String uri, String data, String accessTokenKey) throws WxErrorException {
		Class<?>[] getParams = { String.class, String.class };
		Class<?>[] postParams = { String.class, String.class, String.class };
		Method action;
		try {
			action = getComponentService().getClass().getDeclaredMethod(method,
					METHOD_POST.equals(method) ? postParams : getParams);
			action.setAccessible(true);
			return METHOD_POST.equals(method)
					? (String) action.invoke(getComponentService(), uri, data, accessTokenKey)
					: (String) action.invoke(getComponentService(), uri, accessTokenKey);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			if (e instanceof InvocationTargetException) {
				Throwable cause = ((InvocationTargetException) e).getCause();
				if (cause instanceof WxErrorException) {
					throw new WxErrorException(((WxErrorException) cause).getError(), cause);
				}
			}
			WxError error = WxError.builder().errorCode(-2).errorMsg(e.getMessage()).build();
			throw new WxErrorException(error, e);
		}
	}

	public WxOpenComponentService getComponentService() {
		return this.openService.getWxOpenComponentService();
	}

	@Override
	public String post(String url, String data, String accessTokenKey) throws WxErrorException {
		return request(METHOD_POST, url, data, accessTokenKey);
	}

	@Override
	public String get(String url, String accessTokenKey) throws WxErrorException {
		return request(METHOD_GET, url, null,accessTokenKey);
	}

}
