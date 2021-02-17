package com.meidianyi.shop.service.pojo.shop.config.group;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新权限组的入参
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class ShopRoleGroupUpdateParam {
	/**
	 * 权限组名称
	 */
	private String roleName;
	/**
	 * 权限组列表
	 */
	private List<?> privilegeList;
	/**
	 * 功能权限组
	 */
	private List<?> privilegePass;
	/**
	 * 管理员登陆密码
	 */
	private String loginPass;
	/**
	 * 设置密码
	 */
	private String rolePass;
	
	/**
	 * roleId
	 */
	private Integer roleId;
}
