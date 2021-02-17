package com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年03月26日
 */
@Data
@ExcelSheet
public class GoodsVpuExcelImportFailModel {
    /**货品编号*/
    @ExcelColumn(columnIndex = 0, columnName = "vpu.import.goods.sn")
    private String goodsSn;

    /**商品名称*/
    @ExcelColumn(columnIndex = 1, columnName = "vpu.import.goods.name")
    private String goodsName;

    /**商家编码*/
    @ExcelColumn(columnIndex = 2, columnName = "vpu.import.goods.prd.sn")
    private String prdSn;

    /**对应sku，null表示默认sku*/
    @ExcelColumn(columnIndex = 3, columnName = "vpu.import.goods.prd.desc")
    private String prdDesc;

    /**错误消息*/
    @ExcelColumn(columnIndex = 4, columnName = "vpu.import.goods.error.msg")
    private String errorMsg;

    @ExcelIgnore
    private Byte errorCode;
}
