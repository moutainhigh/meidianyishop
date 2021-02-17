package com.meidianyi.shop.service.pojo.shop.distribution;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 分销员分组列表出参
 * @author 常乐
 * 2019年7月24日
 */
@Data
public class DistributorGroupListVo {
	private Integer id;
	private String groupName;
	private Integer isDefault;
	private Integer delFlag;
	private Integer distributorAmount;
	private Timestamp createTime;
	private Byte canSelect;
	
	 /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
}
