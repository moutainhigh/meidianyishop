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
public class WxOpenMaSubScribeGetTemplateTitleResult extends WxOpenResult {
	private static final long serialVersionUID = 12592307483513610L;
	
	/**
	 * 模版标题列表总数
	 */
	private Integer count = 0;
	
	/**
	 * 模板标题列表
	 */
	private List<WxOpenSubscribeTemplateTitle> data;
	
	public static WxOpenMaSubScribeGetTemplateTitleResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMaSubScribeGetTemplateTitleResult>() {
		}.getType());
	}
	
	@Data
	public static class WxOpenSubscribeTemplateTitle  implements Serializable{
		private static final long serialVersionUID = -228682090703218171L;
		
		/**
		 * 模版标题 id
		 */
		private Integer tid;
		
		/**
		 * 模版标题
		 */
		private String title;
		
		/**
		 * 关键词内容对应的示例，模版类型，2 为一次性订阅，3 为长期订阅
		 */
		private Integer type;
		
		/**
		 * 参数类型 模版所属类目 id
		 */
		private Integer categoryId;
		
	}
}
