package com.meidianyi.shop.service.pojo.shop.market.seckill;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-12 17:53
 **/
@ExcelSheet
@Data
public class SeckillOrderExportVo {
    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_ACT_NAME,columnIndex = 0)
    private String actName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_ORDER_SN,columnIndex = 1)
    private String orderSn;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_NAME,columnIndex = 2)
    private String goodsName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_PRICE,columnIndex = 3)
    private BigDecimal goodsPrice;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_CREATE_TIME,columnIndex = 4)
    private Timestamp createTime;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_USERNAME,columnIndex = 5)
    private String username;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_CONSIGNEE,columnIndex = 6)
    private String consignee;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_MONEY_PAID,columnIndex = 7)
    private BigDecimal moneyPaid;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_ORDER_STATUS,columnIndex = 8)
    private String orderStatus;
}
