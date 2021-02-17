package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;

/**
 * 接口查询返回的类
 * 
 * @author zhaojianqiang
 * @time 上午9:38:45
 */
@Data
public class MaPortraitResult {
	@SerializedName("ref_date")
	private String refDate;

	@SerializedName("visit_uv_new")
	private Portrait visitUvNew;

	@SerializedName("visit_uv")
	private Portrait visitUv;

	public static MaPortraitResult fromJson(String json) {
		return WxGsonBuilder.create().fromJson(json, MaPortraitResult.class);
	}

}
