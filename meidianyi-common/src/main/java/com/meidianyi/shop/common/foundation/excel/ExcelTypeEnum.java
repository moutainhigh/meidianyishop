package com.meidianyi.shop.common.foundation.excel;

/**
 * @author 李晓冰
 * @date 2019年07月17日
 */
public enum ExcelTypeEnum {
    //Excel 97(-2007)
    XLS(".xls"),

    //Excel 2007 OOXML (.xlsx)
    XLSX(".xlsx");

    ExcelTypeEnum(String suffix){
        this.suffix=suffix;
    }

    String suffix;

    public String getSuffix(){
        return this.suffix;
    }
}
