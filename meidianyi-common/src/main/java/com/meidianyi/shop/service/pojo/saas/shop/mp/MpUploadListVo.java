package com.meidianyi.shop.service.pojo.saas.shop.mp;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年9月9日 下午4:16:42
 */
@Data
public class MpUploadListVo {
	private Integer recId;
	private Integer shopId;
	private Integer processId;
	private String jobName;
	private String className;
	private String parameters;
	private Byte state;
	private String failReason;
	private Byte onlyRunOne;
	private Short progress;
	private String progressInfo;
	private Timestamp endTime;
	private Integer jobCode;
	private String jobMessage;
	private String jobResult;
	private String memo;
	private Timestamp created;

}
