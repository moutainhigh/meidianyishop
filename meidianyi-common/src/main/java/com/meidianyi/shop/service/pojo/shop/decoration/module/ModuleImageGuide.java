package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleImageGuide extends ModuleBase {
	/**
	 * 导航样式：1圆形图片导航，2方形图片导航
	 */
	@JsonProperty(value = "nav_style")
	Byte navStyle = 0;
	
	/**
	 * 前景色值
	 */
	@JsonProperty(value = "font_color")
	String fontColor="rgba(255, 69, 0, 1)";
	
	/**
	 * 背景色值
	 */
	@JsonProperty(value = "bg_color")
	String bgColor="rgba(255, 255, 255, 1)";
	
	/**
	 * 导航链接组
	 */
	@JsonProperty(value = "nav_group")
	List<NavItem> navGroup = new ArrayList<>();

    @Getter
    @Setter
	public static class NavItem{
		
		/**
		 * 导航名称
		 */
		@JsonProperty(value = "nav_name")
		String navName;
		
		/**
		 * 导航链接
		 */
		@JsonProperty(value = "nav_link")
		String navLink;
		
		/**
		 * 图片地址
		 */
		@JsonProperty(value = "nav_src")
        String navSrc;
	}
}
