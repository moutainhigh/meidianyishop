package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;

import lombok.Data;

/**
 * 发货
 * 
 * @author 王帅
 *
 */
@Data
public class ShipVo {
	@JsonIgnore
	private String orderSn;
	@JsonIgnore
	private String mainOrderSn;
	/**收件人姓名*/
	private String consignee;
	/**下单人手机号*/
	private String mobile;
	/**完整收货地址*/
	private String completeAddress;
	private List<OrderGoodsVo> orderGoodsVo;
}
