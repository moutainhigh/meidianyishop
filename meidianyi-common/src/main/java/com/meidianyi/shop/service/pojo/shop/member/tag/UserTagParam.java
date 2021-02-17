package com.meidianyi.shop.service.pojo.shop.member.tag;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description: 为会员打标签-入参
*/
@Getter
@Setter
public class UserTagParam {
	/** 用户id */
	@NotNull
	private List<Integer> userIdList;
	/** 标签id列表  */
	private List<Integer> tagIdList;
}
