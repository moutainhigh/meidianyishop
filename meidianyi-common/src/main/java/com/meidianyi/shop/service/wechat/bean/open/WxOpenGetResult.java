package com.meidianyi.shop.service.wechat.bean.open;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class WxOpenGetResult extends WxOpenResult {

	private static final long serialVersionUID = -5311105253080299829L;

	@SerializedName("open_appid")
	private String openAppid;

	public static WxOpenGetResult fromJson(String json) {
		return WxGsonBuilder.create().fromJson(json, WxOpenGetResult.class);
	}
}
