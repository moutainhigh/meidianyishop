package com.meidianyi.shop.service.pojo.shop.member.data;

import com.meidianyi.shop.service.foundation.util.I18N;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月17日
* @Description: 行业信息出参
*/
@Data
public class IndustryVo {
	private Integer value;
	
	@I18N(propertiesFileName = "member")
	private String label;
	public IndustryVo(Integer value,String label) {
		this.value = value;
		this.label = label;
	}
}
