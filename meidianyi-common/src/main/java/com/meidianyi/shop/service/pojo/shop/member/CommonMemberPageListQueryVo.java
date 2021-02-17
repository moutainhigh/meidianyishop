package com.meidianyi.shop.service.pojo.shop.member;

import lombok.Data;

/**
 *  通用会员列表弹窗的查询出参
 *  
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Data
public class CommonMemberPageListQueryVo {
	
	/**
	 * ID
	 */
	private Integer userId;

	/**
	 * 昵称
	 */
	private String username;

	/**
	 * 手机号
	 */
	private String mobile;
}
