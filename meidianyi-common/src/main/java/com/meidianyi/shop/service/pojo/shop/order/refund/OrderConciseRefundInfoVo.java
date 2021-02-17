package com.meidianyi.shop.service.pojo.shop.order.refund;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 订单详情页面展示退货款信息列表
 * @author 王帅
 *
 */
@Data
public class OrderConciseRefundInfoVo {
	private Integer retId;
	private Integer orderId;
	private String orderSn;
	private String returnOrderSn;
	private Byte returnType;
	private Byte refundStatus;
	private BigDecimal money;
	private List<OrderReturnGoodsVo> orderReturnGoodsVo;
	/**退货时的申请时间*/
	private Timestamp applyTime;
	/**只退款时为退款申请时间，退货又退款时为提交物流信息时间*/
	private Timestamp shippingOrRefundTime;
	/**成功时间*/
	private Timestamp refundSuccessTime;
	/**退运费*/
	private BigDecimal shippingFee;
}
