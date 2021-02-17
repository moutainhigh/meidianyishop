package com.meidianyi.shop.service.pojo.shop.member.data;

import lombok.Data;
/**
 * 
 * @author 黄壮壮
 *	婚姻状况出参
 */
@Data
public class MarriageData {
	private Integer value;
	private String label;
	public MarriageData(Integer value,String label) {
		this.value = value;
		this.label = label;
	}
}
