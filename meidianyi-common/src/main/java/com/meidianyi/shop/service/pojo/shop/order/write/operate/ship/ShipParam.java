package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发货
 *
 * @author 王帅
 *
 */
@Getter
@Setter
public final class ShipParam extends OrderOperateQueryParam{

	@NotBlank(message = JsonResultMessage.MSG_ORDER_SHIPPING_SHIPPINGNO_NOT_NULL)
	private String shippingNo;
	@NotNull(message = JsonResultMessage.MSG_ORDER_SHIPPING_SHIPPINGID_NOT_NULL)
	private Byte shippingId;
	@NotNull(message = JsonResultMessage.MSG_ORDER_SHIPPING_SHIPGOODS_NOT_NULL)
	private ShipGoods[] shipGoods;
	/**
	 * 平台 0 admin 1store 3wxapp 4wxapp_store
	 */
	private Byte platform;
	private Integer shipAccountId;
	private Integer shipUserId;
	private String mobile;


	@Getter
	@Setter
    @AllArgsConstructor
    @NoArgsConstructor
	public static class ShipGoods {
		/** order_goods主键 */
		private Integer recId;
		private Integer sendNumber;
	}
}
