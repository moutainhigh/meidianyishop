package com.meidianyi.shop.service.pojo.shop.market.collect;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

/**
 *	收藏有礼
 * @author liangchen
 * @date 2019年8月20日
 */
@Data
public class CollectGiftParam {
	
	/** 开关状态  0：关闭 1：开启*/
	@JsonProperty(value = "on_off")
	private Integer onOff = 0;
	/** 开始时间*/
	@JsonProperty(value = "start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private Timestamp startTime;
	/** 结束时间*/
	@JsonProperty(value = "end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private Timestamp endTime;
	/** 图标*/
	@JsonProperty(value = "collect_logo")
	private Integer collectLogo = 0;
	/** 自定义图标路径*/
	@JsonProperty(value = "collect_logo_src")
	private String collectLogoSrc;
	/** 积分*/
	@JsonProperty(value = "score")
	private Integer score = 0;
	/** 优惠券id*/
	@JsonProperty(value = "coupon_ids")
	private String couponIds;
    /**
     * 店铺名称 小程序端收藏有礼
     */
	private String shopName;
}
