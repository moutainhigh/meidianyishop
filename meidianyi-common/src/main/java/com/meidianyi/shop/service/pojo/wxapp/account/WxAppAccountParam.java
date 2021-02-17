package com.meidianyi.shop.service.pojo.wxapp.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppCommonParam;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年10月9日 下午3:42:51
 */
@Data
public class WxAppAccountParam extends WxAppCommonParam {

	private String username;
	
	@JsonProperty(value = "user_avatar")
	private String userAvatar;
	
	@JsonProperty(value = "encrypted_data")
	private String encryptedData;
	
	private String iv;

}
