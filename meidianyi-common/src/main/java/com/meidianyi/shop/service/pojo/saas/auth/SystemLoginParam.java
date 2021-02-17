package com.meidianyi.shop.service.pojo.saas.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 新国
 *
 */
@Data
@NoArgsConstructor
public class SystemLoginParam {
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_USERNAME_NOT_NULL)
	public String username;
	
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_PASSWD_NOT_NULL)
	@Pattern(regexp = "^[^\\u4e00-\\u9fa5]{6,16}$",message = JsonResultMessage.MSG_ACCOUNT_PASSWD_LENGTH_LIMIT)
	public String password;
}
