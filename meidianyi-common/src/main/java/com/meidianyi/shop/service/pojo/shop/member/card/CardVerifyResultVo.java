package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月30日
* @Description: 会员卡审核结果
*/
@Data
public class CardVerifyResultVo {
	private Integer id;
	private Byte status;
	private Timestamp refuseTime;
	private Timestamp passTime;
	private String refuseDesc;
}
