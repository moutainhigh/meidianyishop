package com.meidianyi.shop.service.pojo.shop.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopSubAccountAddParam {
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_USERNAME_NOT_NULL)
	private String accountName;
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_MODILE_NOT_NULL)
	private String mobile;
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_PASSWD_NOT_NULL)
	@Pattern(regexp = "^[^\\u4e00-\\u9fa5]{6,16}$",message = JsonResultMessage.MSG_ACCOUNT_PASSWD_LENGTH_LIMIT)
	private String accountPwd;
}
