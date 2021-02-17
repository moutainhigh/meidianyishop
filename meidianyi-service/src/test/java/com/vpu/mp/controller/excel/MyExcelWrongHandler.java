package com.meidianyi.shop.controller.excel;

import org.apache.poi.ss.usermodel.Row;

import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年07月29日
 */
public class MyExcelWrongHandler implements IllegalExcelHandler {

    List<Row> rows=new ArrayList<>();
    @Override
    public void handleIllegalParse(IllegalExcelBinder binder) {
        switch (binder.getIllegalExcelEnum()) {
            case ILLEGEL_SHEET_POSITION:
                System.out.println("sheet位置错误");
                break;
            case ILLEGAL_SHEET_HEAD:
                System.out.println("sheet 头信息错误");
                break;
            case SHEET_HEAD_NULL:
                System.out.println("sheet 没头信息");
                break;
            case ILLEGAL_SHEET_DATA:
                System.out.println(binder.getUserDataRow().getRowNum()+"行，数据错误");
                rows.add(binder.getUserDataRow());
                break;
            default:
                break;
        }
    }
}
