package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;

/***
 * 积分兑换订单导出
 * @author liangchen
 * @date 2020.03.17
 */
@ExcelSheet
@Data
public class IntegralOrderExport {
	
	/** 订单号 */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.INTEGRAL_MALL_ORDER_SN)
	private String orderSn;
	/** 积分兑换商品 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.INTEGRAL_MALL_GOODS)
	private String goods;
    /** 商品原价 */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.INTEGRAL_MALL_GOODS_PRICE)
    private BigDecimal goodsPrice;
	/** 商品数量 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.INTEGRAL_MALL_GOODS_NUMBER)
	private Integer number;
	/** 兑换现金 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.INTEGRAL_MALL_MONEY)
	private BigDecimal money;
	/** 兑换积分数 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.INTEGRAL_MALL_SCORE)
	private Integer score;
    /** 下单人信息 name+mobile */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.INTEGRAL_MALL_USER)
    private String user;
	/** 收货人信息 name+mobile */
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.INTEGRAL_MALL_RECEIVER)
	private String receiveUser;
	/** 订单状态 */
    @ExcelColumn(columnIndex = 8, columnName = JsonResultMessage.INTEGRAL_MALL_ORDER_STATUS)
	private String orderStatus;
}
