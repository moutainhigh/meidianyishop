package com.meidianyi.shop.service.pojo.shop.member.userimp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class SetNoticeJson {
	/** 通知说明 */
	private String explain;
	/** 积分 */
	private String score;
	/** 优惠券Id */
	@JsonProperty(value = "mrking_voucher_id")
	private String mrkingVoucherId;

}
