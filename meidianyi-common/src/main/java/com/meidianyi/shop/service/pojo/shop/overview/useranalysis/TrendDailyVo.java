package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.Date;

/**
 * 客户概况及趋势
 * @author liangchen
 * @date 2019年7月18日
 */
@Data
public class TrendDailyVo {

	private Date refDate;
	private Integer loginData;
	private Integer userData;
	private Integer orderUserData;

}
