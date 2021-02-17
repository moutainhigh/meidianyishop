package com.meidianyi.shop.controller.excel;

import java.math.BigDecimal;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;


@ExcelSheet
@Data
public class OrderGoods {

	
	// 商品名称
	@ExcelColumn(columnName = "excel.ProductModel.productName", columnIndex = 1)
	private String goodsName;
	

	// 价格
	@ExcelColumn(columnName = "excel.ProductModel.price", columnIndex = 2)
	private BigDecimal goodsPrice;
	
}
