package com.meidianyi.shop.service.pojo.shop.distribution;

import java.util.List;

import lombok.Data;

/**
 * 分组中添加分销员入参
 * @author 常乐
 * 2019年9月18日
 */
@Data
public class AddDistributorToGroupParam {
	private int groupId;
	public List<Integer> userIds;
}
