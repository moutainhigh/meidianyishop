package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 订单号查询拼图信息
 * @author zhaojianqiang
 * @time   上午10:00:08
 */
@Data
public class GroupDrawInfoByOsParam {
	@NotNull
	private String orderSn;
}
