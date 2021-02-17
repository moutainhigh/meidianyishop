package com.meidianyi.shop.service.pojo.shop.member;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 	 通用会员列表弹窗的查询入参
 * 	不同场景下需要不同参数，根据需要添加成员变量，同时在MemberService下的buildCommonPageListQueryOptions方法中实现过滤
 * 
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Data
public class CommonMemberPageListQueryParam {
	
	 /**
     * 	分页信息
     */
    private Integer currentPage = Page.DEFAULT_CURRENT_PAGE;
    private Integer pageRows = Page.DEFAULT_PAGE_ROWS;
	
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
	
	/**
	 * 门店ID
	 * 查询 非 指定门店核销员的会员
	 * 用于向指定门店添加核销员，提前过滤该门店核销员
	 */
	private Integer storeId;
	
}
