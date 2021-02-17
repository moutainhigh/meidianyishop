package com.meidianyi.shop.service.pojo.wxapp.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.foundation.util.I18N;
import com.meidianyi.shop.service.pojo.shop.config.BottomNavigatorConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 前端店铺配置拉取
 * 
 * @author lixinguo
 *
 */
@Data
public class WxAppConfigVo {

	@JsonProperty(value = "img_list")
	List<BottomNavigatorConfig> bottomNavigateMenuList;

	@JsonProperty(value = "show_poster")
	ShowPoster showPoster;
	
	/**
	 * 店铺状态，0 正常，1已过期  2已禁用  3未营业  4用户禁止登陆
	 */
	Byte status = 0;
	
	/**
	 * 是否显示店铺logo图片
	 */
	@JsonProperty(value = "show_logo")
	Byte showLogo = 0;

    /**
     * 店铺logo图片链接
     */
    @JsonProperty(value = "logo")
    String logo;
	
	/**
	 * 点击店铺logo图片后的跳转地址
	 */
	@JsonProperty(value = "logo_link")
	String logoLink;

	/**
	 * 设置
	 */
	Setting setting;

    /**
     * 用户类型
     */
    @JsonProperty(value = "user_type")
    Byte userType;
    /**
     * 医师ID 如果当前用户不是医师那么为空
     */
    @JsonProperty(value = "doctor_id")
    Integer doctorId;
    /**
     * 药师ID 如果当前用户不是药师那么为空
     */
    @JsonProperty(value = "pharmacist_id")
    Integer pharmacistId;
    /**
     * 店员account_id
     */
    @JsonProperty(value = "store_account_id")
    Integer storeAccountId;
	@Data
	public static final class ShowPoster{
		/**
		 * 是否显示下载海报
		 */
		@JsonProperty(value = "flag")
		Byte showFlag = 0;
		
		/**
		 * 按钮第一行文字，
		 */
		@I18N(propertiesFileName = "wxapp")
		@JsonProperty(value = "text_1")
		String textOne = "poster.text.download" ;
		
		/**
		 * 按钮第二行文字
		 */
		@I18N(propertiesFileName = "wxapp")
		@JsonProperty(value = "text_2")
		String textTwo = "poster.text.poster" ;
	}
	
	@Data
	@Builder
	public static class Setting{
		/**
		 * 是否隐藏底部logo,0 或者1
		 */
		@JsonProperty(value = "hid_bottom")
		Byte hideBottom;

		@JsonProperty(value = "shop_flag")
		Byte shopFlag;
		
		@JsonProperty(value = "shop_style")
		String[] shopStyle;
	}
}
