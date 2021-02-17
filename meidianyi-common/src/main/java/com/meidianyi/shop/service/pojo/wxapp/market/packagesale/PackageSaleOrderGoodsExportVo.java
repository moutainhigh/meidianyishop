package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-05-13 16:26
 **/
@ExcelSheet
@Data
public class PackageSaleOrderGoodsExportVo {
    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_NAME, columnIndex = 1)
    private String goodsName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.SECKILL_ORDER_LIST_GOODS_PRICE, columnIndex = 2)
    private BigDecimal goodsPrice;
}
