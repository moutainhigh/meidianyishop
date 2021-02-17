package com.meidianyi.shop.service.pojo.shop.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopManageVo {

	public String shopAvatar;
	public String accountName;
	public Integer sysId;
	public String userName;
	public String mobile;
	public String shopAvatarPath;

}
