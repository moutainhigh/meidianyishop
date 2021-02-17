package com.meidianyi.shop.service.pojo.shop.order.refund;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
/**
 * 
 * @author wangshuai
 */
@Getter
@Setter
public class OrderReturnGoodsVo extends OrderGoodsVo{
    private Integer id;
	private Integer retId;
	/**0代表退货申请被拒绝，1代表正在退货中，2代表退货成功'*/
	private Byte success;
	@JsonIgnore
	private BigDecimal returnMoney;
}
