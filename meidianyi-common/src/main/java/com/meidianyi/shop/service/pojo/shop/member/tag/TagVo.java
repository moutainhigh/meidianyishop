package com.meidianyi.shop.service.pojo.shop.member.tag;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
/**
* @author 黄壮壮
* @Date: 2019年9月3日
* @Description:
*/
@Getter
@Setter
public class TagVo {
	@JsonProperty("id")
	private Integer tagId;
	@JsonProperty("value")
	private String tagName;
}
