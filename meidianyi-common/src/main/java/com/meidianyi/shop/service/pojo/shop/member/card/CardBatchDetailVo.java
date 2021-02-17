package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年11月20日
* @Description: 
*/
@Data
public class CardBatchDetailVo {
	private Integer id;
	private Integer cardId;
	private Byte action;
	private String name;
	private Byte codeSize;
	private Byte cardSize;
	private Byte cardPwdSize;
	private Integer number;
	private String codePrefix;
	private String cardPrefix;
	private Timestamp createTime;
}
