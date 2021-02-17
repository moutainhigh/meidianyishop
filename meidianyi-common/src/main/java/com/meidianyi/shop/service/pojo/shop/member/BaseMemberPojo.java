package com.meidianyi.shop.service.pojo.shop.member;

import com.meidianyi.shop.common.foundation.util.Page;

import lombok.Data;

/**
 * 
 * 处理在会员列表中公共的方法和字段
 * @author 黄壮壮
 * 2019-07-10 10:47
 */
@Data
public class BaseMemberPojo {
	public Integer pageRows  = Page.DEFAULT_PAGE_ROWS;
	public Integer currentPage =  Page.DEFAULT_CURRENT_PAGE;
}
