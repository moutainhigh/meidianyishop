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
public class WxOpenMaSubScribeGetTemplateListResult extends WxOpenResult {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6154122698215849191L;


	/**
	 * 个人模板列表
	 */
	private List<WxOpenSubscribeTemplate> data;
	
	public static WxOpenMaSubScribeGetTemplateListResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMaSubScribeGetTemplateListResult>() {
		}.getType());
	}
	
	@Data
	public static class WxOpenSubscribeTemplate  implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1215798536793519713L;

		/**
		 * 添加至帐号下的模板 id，发送小程序订阅消息时所需
		 */
		private String priTmplId;
		
		/**
		 * 模版标题
		 */
		private String title;
		
		/**
		 * 模版内容
		 */
		private String content;
		
		/**
		 * 模板内容示例
		 */
		private String example;
		
		/**
		 * 关键词内容对应的示例，模版类型，2 为一次性订阅，3 为长期订阅
		 */
		private Integer type;
	}
}
