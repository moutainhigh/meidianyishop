package com.meidianyi.shop.service.wechat.api.impl;

import com.meidianyi.shop.service.wechat.api.WxOpenAccountService;
import com.meidianyi.shop.service.wechat.api.WxOpenMaMpHttpBase;
import com.meidianyi.shop.service.wechat.api.WxOpenMpampLinkService;
import com.meidianyi.shop.service.wechat.util.http.ApacheFormEncodedPostRequestExecutor;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenMpServiceImpl;

/**
 * 用于公众号未实现的接口
 * 
 * @author lixinguo
 *
 */
public class WxOpenMpServiceExtraImpl implements WxOpenMaMpHttpBase,WxOpenAccountService,WxOpenMpampLinkService{

	protected WxOpenService openService;
	
	public WxOpenMpServiceExtraImpl(WxOpenService openService) {
		this.openService = openService;
	}

	@Override
	public String post(String appId, String url, String data) throws WxErrorException {
		WxMpService mpService = openService.getWxOpenComponentService().getWxMpServiceByAppid(appId);
		return mpService.post(url, data);
	}

	@Override
	public String get(String appId, String url,String queryParam) throws WxErrorException {
		WxMpService mpService = openService.getWxOpenComponentService().getWxMpServiceByAppid(appId);
		return mpService.get(url, queryParam);
	}

	@Override
	public String postForm(String appId, String url, String data) throws WxErrorException {
		WxMpService mpService = openService.getWxOpenComponentService().getWxMpServiceByAppid(appId);
		return mpService.execute(new ApacheFormEncodedPostRequestExecutor((WxOpenMpServiceImpl)mpService), url, data);
	}

}
