package com.meidianyi.shop.service.pojo.shop.member.order;

import java.sql.Timestamp;

import lombok.Data;

/**
* @author 黄壮壮
* @Date: 2019年10月11日
* @Description:
*/
@Data
public class UserCenterNumBean {
	private Integer orderAmount;
	private Integer count;
	private Timestamp addTime;
}
