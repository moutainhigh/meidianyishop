package com.meidianyi.shop.service.wechat.bean.open;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import me.chanjar.weixin.open.util.json.WxOpenGsonBuilder;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class WxOpenMaSubscribeAddTemplateResult extends WxOpenResult {

	private static final long serialVersionUID = 19592307483513662L;
	
	@SerializedName("priTmplId")
	private String priTmplId;

	
	public static WxOpenMaSubscribeAddTemplateResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMaSubscribeAddTemplateResult>() {
		}.getType());
	}
}
