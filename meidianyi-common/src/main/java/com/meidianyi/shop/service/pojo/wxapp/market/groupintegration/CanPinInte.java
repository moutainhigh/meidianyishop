package com.meidianyi.shop.service.pojo.wxapp.market.groupintegration;

import lombok.Data;

/**
 * 小程序组团瓜分积分出参一部分
 * 
 * @author zhaojianqiang
 * @time 下午3:14:13
 */
@Data
public class CanPinInte {
	/** 剩余时间 */
	private Long remainingTime;
	private Byte status=0;
	private String msg;
	/** 1:是新人；0：不是新人 */
	private Byte isNew;
}
