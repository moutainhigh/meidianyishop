package com.meidianyi.shop.service.pojo.wxapp.login;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class WxAppLoginParam  extends WxAppCommonParam{
	
	String code;
	
	String avatar;
	
	String name;
	@JsonProperty(value = "path_query")
	PathQuery pathQuery;
	
	@JsonProperty(value = "system_verion")
	String systemVersion;
	
	/**
	 * 小程序启动时的参数
	 */
	@Data
	public static final class PathQuery{
		/**
		 * 启动小程序的路径
		 */
		String path;
		
		/**
		 * 启动小程序的场景值
		 */
		Integer scene;
		
		/**
		 * 启动小程序的 query 参数
		 */
		Map<String,String> query;
		
		/**
		 * shareTicket，详见获取更多转发信息
		 */
		String shareTicket;
		
		/**
		 * 来源信息。从另一个小程序、公众号或 App 进入小程序时返回。否则返回 {}。
		 */
		ReferrerInfo referrerInfo;
		//TODO
		String channel;
	}
	@Data
	public static final class ReferrerInfo{
		/**
		 * 来源小程序、公众号或 App 的 appId
		 */
		String appId;
		
		/**
		 * 来源小程序传过来的数据，scene=1037或1038时支持
		 */
		Object extraData;
	}
}
