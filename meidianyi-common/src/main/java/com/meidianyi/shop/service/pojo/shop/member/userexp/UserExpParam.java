package com.meidianyi.shop.service.pojo.shop.member.userexp;

import java.util.List;

import lombok.Data;

/**
 * 	用户导出参数
 * @author 黄壮壮
 *
 */
@Data
public class UserExpParam {
	
	/**
	 * 导出条数开始数目
	 */
	private Integer startNum;
	
	/**
	 * 导出条数结束数目
	 */
	private Integer endNum;
	/**
	 * 	自定义导出数据选项
	 */
	private List<String> columns;
}
