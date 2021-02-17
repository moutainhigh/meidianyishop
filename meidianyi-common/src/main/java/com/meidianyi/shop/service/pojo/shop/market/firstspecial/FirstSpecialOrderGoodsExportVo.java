package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-04-09 13:50
 **/
@ExcelSheet
@Data
public class FirstSpecialOrderGoodsExportVo {
    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.FIRST_SPECIAL_ORDER_LIST_GOODS_NAME,columnIndex = 1)
    private String goodsName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.FIRST_SPECIAL_ORDER_LIST_PRICE,columnIndex = 2)
    private BigDecimal price;
}
