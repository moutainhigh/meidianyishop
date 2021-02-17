package com.meidianyi.shop.service.pojo.wxapp.market.groupintegration;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 小程序组团瓜分积分入参
 * 
 * @author zhaojianqiang
 * @time 下午4:21:49
 */
@Data
public class GroupStartParam {
	/** 团信息id */
	@NotNull
	private Integer pinInteId;
	/** 拼团id */
	private Integer groupId;
	/** 邀请人（被谁邀请） */
	private Integer inviteUser;
}
