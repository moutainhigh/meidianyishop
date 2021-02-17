package com.meidianyi.shop.service.wechat.api;

import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 开放平台  http请求接口
 * 
 * @author lixinguo
 *
 */
public interface WxOpenComponentHttpBase {
	
	public static final String COMPONENT_TOKEN_KEY = "component_access_token";
	public static final String ACCESS_TOKEN_KEY = "access_token";


	/**
	 * post请求
	 * @param url
	 * @param data
	 * @param accessTokenKey
	 * @return
	 * @throws WxErrorException
	 */
	String post(String url, String data,String accessTokenKey) throws WxErrorException;

	
	/**
	 * get请求
	 * @param url
	 * @param accessTokenKey
	 * @return
	 * @throws WxErrorException
	 */
	String get(String url,String accessTokenKey) throws WxErrorException;
}
