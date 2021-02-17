package com.meidianyi.shop.service.pojo.shop.decoration;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 设置首页入参类
 * @author 常乐
 * 2019年10月9日
 */
@Data
public class PageIdParam {
	/**
	 * 页面id
	 */
	@NotNull
	private Integer pageId;
}
