package com.meidianyi.shop.service.wechat.bean.open;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import me.chanjar.weixin.open.util.json.WxOpenGsonBuilder;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年8月23日 上午10:45:44
 */
@Getter
@Setter
public class WxOpenMiniLinkGetResult extends WxOpenResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 19592307483513662L;
	
	@SerializedName("wxopens")
	private WxOpenMiniLinkGetInner wxopens;

	
	public static WxOpenMiniLinkGetResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMiniLinkGetResult>() {
		}.getType());
	}
}
