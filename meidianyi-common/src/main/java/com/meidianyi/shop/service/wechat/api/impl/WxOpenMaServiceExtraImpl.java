package com.meidianyi.shop.service.wechat.api.impl;

import com.meidianyi.shop.service.wechat.api.WxGetWeAnalysService;
import com.meidianyi.shop.service.wechat.api.WxMaLiveService;
import com.meidianyi.shop.service.wechat.api.WxOpenAccountService;
import com.meidianyi.shop.service.wechat.api.WxOpenMaLogisticsService;
import com.meidianyi.shop.service.wechat.api.WxOpenMaMallService;
import com.meidianyi.shop.service.wechat.api.WxOpenMaSubscribeService;
import com.meidianyi.shop.service.wechat.util.http.ApacheFormEncodedPostRequestExecutor;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenMaServiceImpl;

/**
 * 用于小程序未实现的接口
 *
 * @author lixinguo
 *
 */
public class WxOpenMaServiceExtraImpl implements  WxOpenAccountService, WxOpenMaLogisticsService,WxOpenMaMallService ,WxOpenMaSubscribeService,WxGetWeAnalysService,WxMaLiveService {

	protected WxOpenService openService;

    public WxOpenMaServiceExtraImpl(WxOpenService openService) {
		this.openService = openService;
	}

	@Override
	public String post(String appId, String url, String data) throws WxErrorException {
		WxOpenMaService maService = openService.getWxOpenComponentService().getWxMaServiceByAppid(appId);
		return maService.post(url, data);
	}

	@Override
	public String get(String appId, String url,String queryParam) throws WxErrorException {
		WxOpenMaService maService = openService.getWxOpenComponentService().getWxMaServiceByAppid(appId);
		return maService.get(url, queryParam);
	}

	@Override
	public String postForm(String appId, String url, String data) throws WxErrorException {
		WxOpenMaService maService = openService.getWxOpenComponentService().getWxMaServiceByAppid(appId);
		return maService.execute(new ApacheFormEncodedPostRequestExecutor((WxOpenMaServiceImpl)maService), url, data);
	}
}
