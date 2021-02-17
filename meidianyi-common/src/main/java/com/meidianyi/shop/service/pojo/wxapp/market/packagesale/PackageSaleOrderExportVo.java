package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-05-13 16:20
 **/
@ExcelSheet
@Data
public class PackageSaleOrderExportVo {

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_ORDER_SN, columnIndex = 0)
    private String orderSn;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_NAME, columnIndex = 1)
    private List<PackageSaleOrderGoodsExportVo> goods;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_PRICE, columnIndex = 2)
    private BigDecimal goodsPrice;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_CREATE_TIME, columnIndex = 3)
    private Timestamp createTime;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_USERNAME, columnIndex = 4)
    private String username;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_CONSIGNEE, columnIndex = 5)
    private String consignee;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_MONEY_PAID, columnIndex = 6)
    private BigDecimal moneyPaid;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_ORDER_STATUS, columnIndex = 7)
    private String orderStatus;
}
