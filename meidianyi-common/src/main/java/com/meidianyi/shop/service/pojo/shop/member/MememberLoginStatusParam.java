package com.meidianyi.shop.service.pojo.shop.member;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;



/**
 * 
 * @author 黄壮壮
 * @Date:  2019年8月13日
 * @Description: 会员用户批量设置禁止登陆-恢复登录-入参
 */
@Data
public class MememberLoginStatusParam {
	/**
	 * 	查询参数
	 */
	private MemberPageListParam searchCnt;
	
	
	/**
	 * 	全部用户
	 */
	public final static Integer ALL_USER_ID = -1;
	public enum LoginPermission{
		on,off
	}
	/** 
	  * 会员用户ID列表 [-1]表示更新全部 
     */
	@NotNull private List<Integer> userIdList;
	/** 
	 *	 用户登录权限
	 */
	@NotNull private LoginPermission permission;
}
