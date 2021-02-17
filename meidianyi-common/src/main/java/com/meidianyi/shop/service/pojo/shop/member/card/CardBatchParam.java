package com.meidianyi.shop.service.pojo.shop.member.card;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年9月30日
* @Description: 会员卡领取批次
*/
@Data
public class CardBatchParam {
	/**   领取方式： 领取码 1，  卡号+密码 2 */
	private Byte receiveAction;
	/** 领取码获得方式 1：自动生成 2：导入  */
	private Byte action;
	/**   批次名称 */
	private String batchName;
	/**   领取码位数 或 卡号位数 */
	private Byte codeSize;
	/**   发放数量 */
	private Integer number;
	/**   领取码前缀  或 卡号前缀 */
	private String codePrefix;
	/**   密码位数 */
	private Byte cardPwdSize;
	
	/**   批次id */
	private Integer batchId;
	/**   分组id */
	private Integer groupId;
	
	private MultipartFile file;
	/** 已经生成的批次的batchId */
	private Integer[] batchIdStr;
}
