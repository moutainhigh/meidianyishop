package com.meidianyi.shop.service.pojo.wxapp.coupon;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author lixinguo
 */
@Data
public class SendCoupon {
	/**
	 * 送券类型：0不送券， 1活动送券   2抽奖送券，3开屏有礼送券
	 */
	@JsonProperty(value = "is_send_coupon")
	Byte isSendCoupon = 0;
	
	/**
	 * 标题，活动送券有效
	 */
	String title;
	
	/**
	 * 背景图片，活动送券有效
	 */
	@JsonProperty(value = "bg_img")
	String bgImg;
	
	/**
	 * 券列表，活动送券有效
	 */
	List<CanUseCoupon> canUseCouponList = new ArrayList<>();
	
	/**
	 * 抽奖Id，抽奖送券有效
	 */
	@JsonProperty(value = "lottery_id")
	Integer lotteryId;
	
	/**
	 * 活动Id，抽奖送券、开屏有礼有效
	 */
	@JsonProperty(value = "activity_id")
	Integer activityId;
	
	/**
	 * 自定义背景图片，开屏有礼有效
	 */
	@JsonProperty(value = "customize_img_path")
	String customizeImgPath;
	
	/**
	 *  自定义跳转链接，开屏有礼有效
	 */
	@JsonProperty(value = "customize_url")
	String customizeUrl;
	
	public static class CanUseCoupon{
		/**
		 * 券码
		 */
		@JsonProperty(value = "act_code")
		String actCode;
		
		/**
		 * 券面额
		 */
		String denomination;
		
		/**
		 * 券规则描述
		 */
		String rule;
	}
}
