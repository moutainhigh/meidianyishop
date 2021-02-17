package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.Data;

/**
 * @author huangzhuangzhuang
 */
@Data
public class EffectTimeBean {
	/** 有效时间戳 - 起始时间 */
	private Timestamp startTime;
	/**	有效日期 - 起始日期 */
	private LocalDate startDate;
	/** 有效时间戳 - 截至时间 */
	private Timestamp endTime;
	/**	有效日期 - 截至日期 */
	private LocalDate endDate;
	/** 过期类型 */
	private Byte expireType;
}
