package com.meidianyi.shop.service.shop.member.excel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.excel.AbstractExcelDisposer;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.util.IDymicColNameI18n;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.userexp.UserExpVo;
/**
 * 
 * @author 黄壮壮
 *	动态列表国际化信息
 */
public class UserExpColNameI18n implements IDymicColNameI18n {
	private Map<String,String> map=null;
	
	@Override
	public String i18nName(String name,String language) {
		Map<String, String> i18nMap = getUserExpVoI18nMap();
		String message = i18nMap.get(name);
		return Util.translateMessage(language, message, AbstractExcelDisposer.LANGUAGE_TYPE_EXCEL);
	}
	
	/**
	 * 通过反射建立一个懒汉工厂返回UserExpVo对应的国际化信息字典
	 * @return UserExpVo对应的国际化信息字典
	 */
	public  Map<String,String> getUserExpVoI18nMap() {
		if(map!=null) {
			return map;
		}else {
			Map<String,String> userExpAttrMap = new HashMap<String,String>();
			Class<?> clazz = UserExpVo.class;
			Field[] fields = clazz.getDeclaredFields();
			for(Field field: fields) {
				JsonProperty jsonAnno = field.getAnnotation(JsonProperty.class);
				if(jsonAnno != null && !StringUtils.isBlank(jsonAnno.value())) {
					ExcelColumn excelColAnno = field.getAnnotation(ExcelColumn.class);
					if(excelColAnno != null) {
						userExpAttrMap.put(jsonAnno.value(), excelColAnno.columnName());
					}
				}
			}
			return userExpAttrMap;
		}
		
		
	}

}
