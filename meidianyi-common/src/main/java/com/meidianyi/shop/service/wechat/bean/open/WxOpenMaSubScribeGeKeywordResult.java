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
public class WxOpenMaSubScribeGeKeywordResult extends WxOpenResult {
	
private static final long serialVersionUID = 19592307423513661L;
	
	/**
	 * 模版标题列表总数
	 */
	private Integer count = 0;
	
	/**
	 * 关键词列表
	 */
	private List<WxOpenSubscribeKeryword> data;
	
	public static WxOpenMaSubScribeGeKeywordResult fromJson(String json) {
		return WxOpenGsonBuilder.create().fromJson(json, new TypeToken<WxOpenMaSubScribeGeKeywordResult>() {
		}.getType());
	}
	
	@Data
	public static class WxOpenSubscribeKeryword  implements Serializable{
		private static final long serialVersionUID = -258682091703218171L;
		/**
		 * 关键词 id，选用模板时需要
		 */
		private Integer kid;
		
		/**
		 * 关键词内容
		 */
		private String name;
		
		/**
		 * 关键词内容对应的示例
		 */
		private String example;
		
		/**
		 * 参数类型
		 */
		private String rule;
		
	}
}
