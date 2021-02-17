package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelDynamicColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author liufei
 * @date 2/18/2020
 */
@ExcelSheet
@Data
public class GoodsEffectExportVo {
    @ExcelColumn(columnName = JsonResultMessage.GOODS_RANKING_GOODS_NAME, columnIndex = 0)
    private String goodsName;

    @ExcelDynamicColumn
    private LinkedHashMap<String, Object> dynamicValue;
}
