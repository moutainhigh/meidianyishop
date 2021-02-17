package com.meidianyi.shop.service.pojo.shop.member.userexp;

import java.util.Map;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelDynamicColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * 
 * @author 黄壮壮
 *	用户 excel导出数据模型
 */
@Data
@ExcelSheet
public class UserExcelModel {
	/**   TODO 国际化 */
	@ExcelColumn(columnIndex=0,columnName="ID")
	private Integer userId;
	
	/**
	 *	 用户导出的动态选项
	 */
	@ExcelDynamicColumn
	private Map<String,Object> other;
}
