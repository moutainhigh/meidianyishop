package com.meidianyi.shop.service.pojo.shop.member;

import com.meidianyi.shop.common.foundation.util.Util;

/**
* @author 黄壮壮
* @Date: 2019年9月2日
* @Description: 微信来源名称国际化
*/
public enum SourceNameEnum {
	
	/** 后台 */
	SRC_BACK_STAGE(0,"member.source.back_stage"),
	/** 搜索进入 */
	SEARCH(-1,"member.source.search"),
	/** 分享进入 */
	SHARE(-2,"member.source.share"),
	/** 扫码进入 */
	SCAN_QRCODE(-3,"member.source.scan_qrcode"),
	/** 未获取 */
	NOT_GET(-4,"member.source.notget");
	

	
	/** 数字代号 */
	private Integer code;
	/**来源名称*/
	private String name;
	private final static String PROPERTY_FILE = "member";
	private SourceNameEnum(Integer code,String name) {
		this.code = code;
		this.name = name;
	} 
	
	public Integer getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * 获取来源
	 * @param code	来源代号
	 * @param lang	语言
	 * @return String 国际化后的来源信息 || null 未匹配
	 */
	public static String getI18NameByCode(Integer code,String lang) {
		for(SourceNameEnum e: SourceNameEnum.values()) {
			if(e.getCode().equals(code)) {
				return Util.translateMessage(lang,e.getName(), PROPERTY_FILE);
			}
		}
		return null;
	}
	
	
	
	
}
