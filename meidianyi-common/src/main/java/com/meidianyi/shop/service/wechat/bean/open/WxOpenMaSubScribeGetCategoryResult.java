package com.meidianyi.shop.service.wechat.bean.open;

import java.io.Serializable;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import lombok.Data;
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
public class WxOpenMaSubScribeGetCategoryResult extends WxOpenResult {

	private static final long serialVersionUID = 19592307483513662L;

	/**
	 * 类目列表
	 */
	private List<WxOpenSubscribeCategory> data;

	public static WxOpenMaSubScribeGetCategoryResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMaSubScribeGetCategoryResult>() {
		}.getType());
	}

	/**
	 * 类目信息
	 */
	@Data
	public static class WxOpenSubscribeCategory implements Serializable {
		private static final long serialVersionUID = -258682090703218173L;
		
		/**
		 * 类目id，查询公共库模版时需要
		 */
		private Integer id;
		
		/**
		 * 类目的中文名
		 */
		private String name;
	}
}
