package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class PrivilegeAndPassParam {
	/**
	 * 需要权限的
	 */
	private List<?> privilegeLlist;
	/**
	 * 需要密码的
	 */
	private List<?> passList;

	
}
