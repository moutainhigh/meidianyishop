package com.meidianyi.shop.service.shop.order.ship;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量发货
 * @author 王帅
 */
@Slf4j
public class BatchShipExcelWrongHandler implements IllegalExcelHandler {
    List<Row> rows = new ArrayList<>();

    @Override
    public void handleIllegalParse(IllegalExcelBinder binder) {
        switch (binder.getIllegalExcelEnum()) {
            case ILLEGEL_SHEET_POSITION:
                log.info("sheet位置错误");
                break;
            case ILLEGAL_SHEET_HEAD:
                log.info("sheet 头信息错误");
                break;
            case SHEET_HEAD_NULL:
                log.info("sheet 没头信息");
                break;
            case ILLEGAL_SHEET_DATA:
                log.info(binder.getUserDataRow().getRowNum() + "行，数据错误");
                rows.add(binder.getUserDataRow());
                break;
            default:
                break;
        }
    }
}
