package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 小程序拼团抽奖参团详情出参
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDrawReturn {
	private JsonResultCode code;
	private GroupDrawInfoReturnVo vo;
	/** 一些情况下跳转的地址 比如已经参加活动，让跳到详细信息*/
	private String url;
}
