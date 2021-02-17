package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: 王兵兵
 * @create: 2020-05-11 13:51
 **/
@ExcelSheet
@Data
public class MrkingStrategyOrderGoodsExportVo {
    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_GOODS_NAME, columnIndex = 1)
    private String goodsName;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_GOODS_PRICE, columnIndex = 2)
    private BigDecimal goodsPrice;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_PER_DISCOUNT, columnIndex = 3)
    private BigDecimal perDiscount;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_DISCOUNTED_GOODS_PRICE, columnIndex = 4)
    private BigDecimal discountedGoodsPrice;

    /**
     *
     */
    @ExcelColumn(columnName = JsonResultMessage.MRKING_STRATEGY_ORDER_LIST_GOODS_NUMBER, columnIndex = 5)
    private Integer goodsNumber;
}
