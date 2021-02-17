package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/***
 * 积分兑换用户导出
 * @author liangchen
 * @date 2020.03.18
 */
@ExcelSheet
@Data
public class IntegralUserExport {
    /** 用户id */
    @ExcelColumn(columnIndex = 0, columnName = JsonResultMessage.INTEGRAL_MALL_USER_ID)
    private Integer userId;
	/** 订单号 */
    @ExcelColumn(columnIndex = 1, columnName = JsonResultMessage.INTEGRAL_MALL_ORDER_SN)
	private String orderSn;
    /** 商品名称 */
    @ExcelColumn(columnIndex = 2, columnName = JsonResultMessage.INTEGRAL_MALL_GOODS_NAME)
    private String goodsName;
    /** 兑换现金 */
    @ExcelColumn(columnIndex = 3, columnName = JsonResultMessage.INTEGRAL_MALL_MONEY)
    private BigDecimal money;
    /** 兑换积分 */
    @ExcelColumn(columnIndex = 4, columnName = JsonResultMessage.INTEGRAL_MALL_SCORE)
    private Integer score;
    /** 用户昵称 */
    @ExcelColumn(columnIndex = 5, columnName = JsonResultMessage.INTEGRAL_MALL_USER_NAME)
    private String username;
    /** 手机号 */
    @ExcelColumn(columnIndex = 6, columnName = JsonResultMessage.INTEGRAL_MALL_MOBILE)
    private String mobile;
    /** 兑换商品数 */
    @ExcelColumn(columnIndex = 7, columnName = JsonResultMessage.INTEGRAL_MALL_EXCHANGE_GOODS_NUMBER)
    private Integer number;
    /** 兑换时间 */
    @ExcelColumn(columnIndex = 8, columnName = JsonResultMessage.INTEGRAL_MALL_EXCHANGE_TIME)
    private Timestamp createTime;
}
