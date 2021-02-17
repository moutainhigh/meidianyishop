package com.meidianyi.shop.service.pojo.shop.member.card;

import java.util.Objects;

import com.meidianyi.shop.common.foundation.util.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author zhaojianqiang
 * @time 下午2:19:43
 */
@Getter
@AllArgsConstructor
public enum CardNoImportTemplate {
	/** 领取码不能为空 */
	CARDNO_NULL("1", "excel.error.cardNo.null"),
	/** 领取码仅限数字和字母 */
	CARDNO_ERROR("2", "excel.error.cardNo.error"),
	/** 领取码已存在 */
	CARDNO_EXIST("3", "excel.error.cardNo.exist"),
	/** 领取码长度应小于15位，展示内容已做裁剪 */
	CARDNO_LIMIT("4", "excel.error.cardNo.limit"),
	/** 密码不能为空 */
	CARDPWD_NULL("5", "excel.error.cardPwd.null"),
	/** 密码仅限数字和字母 */
	CARDPWD_ERROR("6", "excel.error.cardPwd.error"),
	/** 密码长度应小于20位，展示内容已做裁剪 */
	CARDPWD_LIMIT("7", "excel.error.cardPwd.limit");
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
		CardNoImportTemplate[] values = CardNoImportTemplate.values();
		for (CardNoImportTemplate userImportTemplate : values) {
			if (Objects.equals(userImportTemplate.getCode(), code)) {
				return Util.translateMessage(lang, userImportTemplate.getMessage(), "", "excel");
			}
		}
		return null;
	}
}
