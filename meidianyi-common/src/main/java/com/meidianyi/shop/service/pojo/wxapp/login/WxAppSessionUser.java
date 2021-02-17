package com.meidianyi.shop.service.pojo.wxapp.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Tolerate;

/**
 *
 * @author lixinguo
 *
 */
@Data
@ToString
@NoArgsConstructor
public class WxAppSessionUser {
	/**
	 * 是否开启地理位置授权
	 */
	@JsonProperty(value = "geographic_location")
	Byte geoLocation = 10;

	/**
	 * 是否隐藏底部导航
	 */
	@JsonProperty(value = "hid_bottom")
	Byte hideBottom = 0;

	/**
	 * 微信用户信息
	 */
	@JsonProperty(value = "res")
	WxUserInfo wxUser;

	/**
	 * 店铺标记
	 */
	@JsonProperty(value = "shop_flag")
	Byte shopFlag;


	@JsonProperty(value = "shop_id")
	Integer shopId;

	String token;

	@JsonProperty(value = "user_avatar")
	String userAvatar;

	@JsonProperty(value = "user_id")
	Integer userId;

	String username;
	/**  图片地址，小程序前端用 */
	String imageHost;

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
     * 门店account_id
     */
    @JsonProperty(value = "store_account_id")
    Integer storeAccountId;
	/**
	 * 用户登录session信息
	 * @author lixinguo
	 *
	 */
	@Data
	@Builder
	public static  class WxUserInfo{
		@JsonProperty(value = "openid")
		String openId;
		String mobile;
		String unionid;

	    @Tolerate
		public WxUserInfo() {
			super();
		}
	}
}
