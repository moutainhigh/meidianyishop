package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 分销员分组列表入参
 * @author 常乐
 * 2019年7月24日
 */
@Data
public class DistributorGroupListParam {
	private Integer id;
    /**
     * 分组名称
     */
	private String groupName;
    /**
     * 是否支持用户选择 1：支持；0：不支持
     */
	private Byte canSelect;
	private Integer currentPage ;
	private Integer pageRows;
}
