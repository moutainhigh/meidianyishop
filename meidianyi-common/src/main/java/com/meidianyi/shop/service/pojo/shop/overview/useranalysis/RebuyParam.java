package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.Date;

/**
 * 客户复购趋势
 * @author liangchen
 * @date 2019年7月22日
 */
@Data
public class RebuyParam {
    /** 7天 */
    private Byte type = 7;
    /** 当前周数 */
	private Integer weekNum;
	/** 传入的日期-周日 */
    private String sunday;
	/** 第一个自然周 */
	private Date firstDate;
    /** 第二个自然周 */
	private Date secondDate;
    /** 第三个自然周 */
	private Date thirdDate;
	/** 第四个自然周 */
	private Date fourthDate;
}
