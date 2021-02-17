package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 文件上传
 * 
 * @author zhaojianqiang
 * @time 下午4:34:06
 */
@Data
public class UiGetListParam {
	/** 批次Id */
	private Integer batchId;
	private Timestamp startTime;
	private Timestamp endTime;
	/** -每页总数 */
	public Integer pageRows;
	/** -当前页 */
	public Integer currentPage;
}
