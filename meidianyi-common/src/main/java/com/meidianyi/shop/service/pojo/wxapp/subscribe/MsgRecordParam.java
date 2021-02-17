package com.meidianyi.shop.service.pojo.wxapp.subscribe;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息记录的入参
 * 
 * @author zhaojianqiang
 * @time 上午10:20:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgRecordParam {
	private Integer userId;

	/** MessageRecordType类中request_action 字段对应的 */
	private Byte requestAction;

	/** 关联其他表：如：外部请求requestid 公众号或小程序传templateNo */
	private String identityId;

	/** 模板平台：1： 小程序 2：公众号 */
	private Byte templatePlatform;

	private String page;

	/** 模板类型 7：商家自定义 */
	private Byte templateType;

	/** 模板消息关联id */
	private String linkIdentity;
	
	/** 模板内容 */
	private String templateContent;

	private String responseCode;

	private String responseMsg;
}
