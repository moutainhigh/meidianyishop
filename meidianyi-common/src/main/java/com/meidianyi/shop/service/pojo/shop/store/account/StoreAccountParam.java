package com.meidianyi.shop.service.pojo.shop.store.account;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import lombok.Data;

/**
 * 创建的入参
 *
 * @author zhaojianqiang
 * @time 下午3:36:35
 */
@Data
public class StoreAccountParam {
	@NotNull
	@Pattern(regexp = "^1[3|7|8]\\d{9}$|^19[8-9]\\d{8}$|^166\\d{8}|^15[0-3|5-9]\\d{8}|^14[5|7]\\d{8}$",message = JsonResultMessage.MSG_ACCOUNT_MOBILE_LENGTH_LIMIT)
	private String mobile;
	@NotNull
	@Pattern(regexp = "^[^\\u4e00-\\u9fa5]{1,20}$",message = JsonResultMessage.MSG_ACCOUNT_USERNAME_LENGTH_LIMIT)
	private String accountName;
	@NotNull
	private Byte accountType;
	@NotNull
	@Pattern(regexp = "^[^\\u4e00-\\u9fa5]{6,16}$",message = JsonResultMessage.MSG_ACCOUNT_PASSWD_LENGTH_LIMIT)
	private String accountPasswd;
	@NotNull
	private Integer[] storeList;
	private String wxNickName = "";
	private Integer userId=0;
}
