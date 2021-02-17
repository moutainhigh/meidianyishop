package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.util.List;

import lombok.Data;

/**
 * 订单里显示拼团抽奖详情的
 * 
 * @author zhaojianqiang
 * @time 下午4:21:51
 */
@Data
public class GroupDrawInfoByOrderVo {
	private GroupDrawInfoByOsVo pinGroup;
	private GroupDrawInfoVo pinGroupInfo;
	private List<GroupDrawList> pinUserGroup;
}
