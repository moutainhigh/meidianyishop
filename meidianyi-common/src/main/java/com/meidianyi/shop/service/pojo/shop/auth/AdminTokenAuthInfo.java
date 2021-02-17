package com.meidianyi.shop.service.pojo.shop.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author 新国
 *
 */
@Data
@NoArgsConstructor
public class AdminTokenAuthInfo {
	public Integer sysId = 0;
	public String userName = "";
	public String mobile = "";
	public Integer subAccountId = 0;
	public String subUserName = "";
	public boolean subLogin = false;
	public Integer loginShopId = 0;
	public boolean shopLogin = false;
	public String token = "";
	public String accountName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String currency;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String shopLanguage;
}
