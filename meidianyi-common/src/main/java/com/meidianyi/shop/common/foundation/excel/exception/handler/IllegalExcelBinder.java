package com.meidianyi.shop.common.foundation.excel.exception.handler;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author 李晓冰
 * @date 2019年07月19日
 */
public class IllegalExcelBinder {
     private IllegalExcelEnum illegalExcelEnum;

     private IllegalExcelBinder(){

     }

    /**
     * 导入数据Excel中的sheet位置错误
     * @return
     */
    public static IllegalExcelBinder createIllegalSheetBinder(){
        IllegalExcelBinder illegalExcelBinder = new IllegalExcelBinder();
        illegalExcelBinder.illegalExcelEnum=IllegalExcelEnum.ILLEGEL_SHEET_POSITION;
        return illegalExcelBinder;
    }

    /**
     * 对应sheet中，数据列头不在指定位置
     * @return
     */
    public static IllegalExcelBinder createHeadIsNullInfo() {
        IllegalExcelBinder illegalExcelBinder = new IllegalExcelBinder();
        illegalExcelBinder.illegalExcelEnum=IllegalExcelEnum.SHEET_HEAD_NULL;
        return illegalExcelBinder;
    }

    /**
     * sheet表的列头信息和实体类中设置的字段位置无法对应
     * @param userHeadRow
     * @return
     */
    public static IllegalExcelBinder createIllegalHeadInfo(Row userHeadRow){
        IllegalExcelBinder illegalExcelBinder = new IllegalExcelBinder();
        illegalExcelBinder.userDataRow=userHeadRow;
        illegalExcelBinder.illegalExcelEnum=IllegalExcelEnum.ILLEGAL_SHEET_HEAD;
        return illegalExcelBinder;
    }
    /**
     * sheet head信息错误
     */
    private Row userHeadRow;

    /**
     * 数据
     * @param userDataRow
     * @return
     */
    public static IllegalExcelBinder createIllegalDataInfo(Row userDataRow){
        IllegalExcelBinder illegalExcelBinder = new IllegalExcelBinder();
        illegalExcelBinder.userDataRow=userDataRow;
        illegalExcelBinder.illegalExcelEnum=IllegalExcelEnum.ILLEGAL_SHEET_DATA;
        return illegalExcelBinder;
    }
    /**
     * sheet 映射数据存在错误
     */
    private Row userDataRow;


    public IllegalExcelEnum getIllegalExcelEnum() {
        return illegalExcelEnum;
    }


    public Row getUserDataRow() {
        return userDataRow;
    }

}
