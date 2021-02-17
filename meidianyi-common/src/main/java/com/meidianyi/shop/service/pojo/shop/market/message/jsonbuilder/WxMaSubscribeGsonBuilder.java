package com.meidianyi.shop.service.pojo.shop.market.message.jsonbuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessage;

/**
 * @author zhaojianqiang
 */
public class WxMaSubscribeGsonBuilder {
	private static final GsonBuilder INSTANCE = new GsonBuilder();
	static {
		INSTANCE.disableHtmlEscaping();
	    INSTANCE.registerTypeAdapter(WxMaSubscribeMessage.class, new WxMaSubscribeMessageGsonAdapter());
	}

	public static Gson create() {
		return INSTANCE.create();
	}
}
