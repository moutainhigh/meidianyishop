package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

/**
* @author 黄壮壮
* @Date: 2019年8月13日
* @Description:
*/
@Getter
@Setter
public class ScorePageListVo {
	/** 用户名称 */
	private String username;
	/** 手机号码 */
	private String mobile;
	/** 订单号 */
	private String orderSn;
	/** 添加时间 */
	private Timestamp createTime;
	/** 过期时间 */
	private Timestamp expireTime;
	/** 积分变化 */
	private Integer score;
	/** 备注 */
	private String remark;
}
