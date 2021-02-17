package com.meidianyi.shop.controller.excel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;
import lombok.NoArgsConstructor;

@ExcelSheet
@Data
@NoArgsConstructor
public class OrderListInfo {

	// 订单号
	@ExcelColumn(columnName = "excel.ProductModel.orderId", columnIndex = 0)
	private String orderSn;

	// 商品名称
	@ExcelColumn(columnName = "excel.ProductModel.productName", columnIndex = 1)
	private List<OrderGoods> goods;

	// 价格
	@ExcelColumn(columnName = "excel.ProductModel.price", columnIndex = 2)
	private BigDecimal goodsPrice;
	/** 下单时间 */
	// 下单时间
	@ExcelColumn(columnName = "excel.ProductModel.downTime", columnIndex = 3)
	private Timestamp createTime;

	// 下单人信息
	@ExcelColumn(columnName = "excel.ProductModel.downerName", columnIndex = 4)
	private String userName;

	// 收货人信息
	@ExcelColumn(columnName = "excel.ProductModel.uperName", columnIndex = 5)
	private String consignee;

	/** 支付金额 */
	// 支付金额
	@ExcelColumn(columnName = "excel.ProductModel.allPrice", columnIndex = 6)
	private String moneyPaid;
	// 订单状态
	@ExcelColumn(columnName = "excel.ProductModel.status", columnIndex = 7)
	private String orderStatus;

}
