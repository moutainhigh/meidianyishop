package com.meidianyi.shop.service.pojo.wxapp.coupon;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lixinguo
 */
@Getter
@Setter
public class ShopCollectInfo {

	/** 开关配置 0：关闭 1：开启 */
	@JsonProperty(value = "on_off")
	private Integer status;

	/** 开始时间 */
	@JsonProperty(value = "start_time")
	private Timestamp startTime;

	/** 结束时间 */
	@JsonProperty(value = "end_time")
	private Timestamp endTime;

	/** 图标 */
	@JsonProperty(value = "collect_logo")
	private Integer logo;

	/** 自定义图标路径 */
	@JsonProperty(value = "collect_logo_src")
	private String logoSrc;

	/** 积分 */
	@JsonProperty(value = "score")
	private Integer score;

	/** 优惠券id */
	@JsonProperty(value = "coupon_ids")
	private String couponIds;

	/**
	 * 是否用户已经收藏有礼 0无 1有
	 */
	@JsonProperty(value = "get_collect_gift")
	Byte getCollectGift = 0;

	/**
	 * 用户收藏有礼提示弹框时间
	 */
	@JsonProperty(value = "look_collect_time")
	Timestamp lookCollectTime;

	/**
	 * 店铺名称
	 */
	@JsonProperty(value = "shop_name")
	String shopName;

}
