package com.meidianyi.shop.service.pojo.shop.member.score;

import java.sql.Timestamp;

import lombok.Data;
/**
 * @author huangzhuangzhuang
 */
@Data
public class ScorePageInfo {
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
	/** 备注模板id */
	private Integer remarkId;
	/** 备注模板数据 */
	private String remarkData;
}
