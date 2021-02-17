package com.meidianyi.shop.common.foundation.excel.util;
/**
 * excel动态列名国际化
 * @author 黄壮壮
 *
 */
public interface IDymicColNameI18n {
	/**
	 * 获取动态列的列名国际化
	 * @param name 国际化的字段
	 * @param language 语言类型
	 * @return 
	 */
	public String i18nName(String name,String language);
}
