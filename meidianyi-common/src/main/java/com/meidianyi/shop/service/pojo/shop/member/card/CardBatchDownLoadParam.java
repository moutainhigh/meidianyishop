package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Data;

/**
 * 下载记录
 * 
 * @author zhaojianqiang
 * @time 下午4:17:10
 */
@Data
public class CardBatchDownLoadParam {
	/** 批次Id */
	private Integer batchId;
	/** 是否是带密码的 */
	private Boolean isPwd = false;
}
