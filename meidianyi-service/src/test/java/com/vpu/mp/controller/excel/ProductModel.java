package com.meidianyi.shop.controller.excel;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

@ExcelSheet
@Data
public class ProductModel {
	// 订单号
	@ExcelColumn(columnName = "excel.ProductModel.orderId", columnIndex = 0)
	private String orderId;

	// 商品名称
	@ExcelColumn(columnName = "excel.ProductModel.productName", columnIndex = 1)
	private String productName;

	// 价格
	@ExcelColumn(columnName = "excel.ProductModel.price", columnIndex = 2)
	private String price;

	// 下单时间
	@ExcelColumn(columnName = "excel.ProductModel.downTime", columnIndex = 3)
	private Timestamp downTime;

	// 下单人信息
	@ExcelColumn(columnName = "excel.ProductModel.downerName", columnIndex = 4)
	private String downerName;

	// 收货人信息
	@ExcelColumn(columnName = "excel.ProductModel.uperName", columnIndex = 5)
	private String uperName;

	// 支付金额
	@ExcelColumn(columnName = "excel.ProductModel.allPrice", columnIndex = 6)
	private String allPrice;

	// 订单状态
	@ExcelColumn(columnName = "excel.ProductModel.status", columnIndex = 7)
	private String status;

}
