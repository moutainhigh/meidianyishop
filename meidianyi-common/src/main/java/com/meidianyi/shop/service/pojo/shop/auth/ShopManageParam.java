package com.meidianyi.shop.service.pojo.shop.auth;

import javax.validation.constraints.NotBlank;

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
public class ShopManageParam {

	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_SHOPAVATAR_NOT_NULL)
	public String shopAvatar;
	@NotBlank(message = JsonResultMessage.MSG_ACCOUNT_ACCOUNTNAME_NOT_NULL)
	public String accountName;
	public Integer sysId;
	public String userName;
	public String mobile;

}
