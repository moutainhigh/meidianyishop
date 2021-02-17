package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;
/**
 * 
 * @author 黄壮壮
 * 用户卡有效时间入参参数
 */
@Data
public class EffectTimeParam {
	/** 有效时间戳 - 起始时间 */
	private Timestamp startTime;
	/** 有效时间戳 - 截至时间 */
	private Timestamp endTime;
	/** 过期类型 */
	private Byte expireType;
	/** 过期时间 */
	private Timestamp expireTime;
	/** user_card创建时间 */
	private Timestamp createTime;
}
