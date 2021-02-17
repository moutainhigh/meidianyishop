package com.meidianyi.shop.service.pojo.saas.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 新国
 *
 */
@Data
@NoArgsConstructor
public class SystemTokenAuthInfo {
	public Integer systemUserId = 0;
	public String userName = "";
	public Integer subAccountId = 0;
	public String subUserName = "";
	public boolean subLogin = false;
	public String token = "";
}
