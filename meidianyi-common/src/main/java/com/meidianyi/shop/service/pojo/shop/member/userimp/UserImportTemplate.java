package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.util.Objects;

import com.meidianyi.shop.common.foundation.util.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kangyaxin
 */
@Getter
@AllArgsConstructor
public enum UserImportTemplate {
	MOBILE_NULL("31", "excel.error.mobile.null"),
	MOBILE_ERROR("32", "excel.error.mobile.error"),
	MOBILE_EXIST("33", "excel.error.mobile.exist"),
	NAME_LIMIT("34", "excel.error.name.limit"),
	INVITEUSER_ERROR("35", "excel.error.inviteuser.error"),
	INVITEUSER_NO("36", "excel.error.inviteuser.no"),
	SCORE_NULL("37", "excel.error.score.null"),
	SCORE_ERROR("38", "excel.error.score.error"),
	SEX_ERROR("39", "excel.error.sex.error"),
	BIRTHDAY_ERROR("40", "excel.error.birthday.error"),
	ADDRESS_ERROR("41", "excel.error.address.error"),
	PROVINCE_ERROR("42", "excel.error.province.error"),
	CITY_ERROR("43", "excel.error.city.error"),
	DISTRICT_ERROR("44", "excel.error.district.error"),
	ID_ERROR("45", "excel.error.id.error"),
	INCOME_ERROR("46", "excel.error.income.error"),
	MARRIAGE_ERROR("47", "excel.error.marriage.error"),
	EDUCATION_ERROR("48", "excel.error.education.error"),
	INDUSTRY_ERROR("49", "excel.error.industry.error");
	/**
	 * 得到返回码
	 */
	public String code;

	/**
	 * 返回信息
	 */
	private String message;

	/** 根据code Id获取枚举类 */

	public static String getNameByCode(String code, String lang) {
		UserImportTemplate[] values = UserImportTemplate.values();
		for (UserImportTemplate userImportTemplate : values) {
			if (Objects.equals(userImportTemplate.getCode(), code)) {
				return Util.translateMessage(lang, userImportTemplate.getMessage(), "", "excel");
			}
		}
		return null;
	}
}
