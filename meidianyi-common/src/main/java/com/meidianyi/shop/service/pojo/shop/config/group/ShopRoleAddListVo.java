package com.meidianyi.shop.service.pojo.shop.config.group;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 店铺子账户管理-保存
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopRoleAddListVo {

	private Integer recId;
	private Integer accountId;
	private String accountName;
	private String mobile;
	private Integer roleId;
	private String roleName;
	private Byte isBind;
	/**
	 * 公众号openid
	 */
	private String officialOpenId;
	private String officialNickName;
	private String headImgUrl;
	
}
