package com.meidianyi.shop.common.foundation.excel;

import com.meidianyi.shop.common.foundation.excel.bean.ExcelColumnBean;
import com.meidianyi.shop.common.foundation.excel.bean.ExcelSheetBean;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelDataException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelHeaderException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalSheetPositionException;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李晓冰
 * @date 2019年07月18日
 */
public class ExcelReader extends AbstractExcelDisposer {

    private Workbook workbook;

    private IllegalExcelHandler illegalHandler;

    public ExcelReader(Workbook workbook, IllegalExcelHandler illegalHandler) {
        this(AbstractExcelDisposer.DEFAULT_LANGUAGE, workbook, illegalHandler);
    }

    public ExcelReader(String language, Workbook workbook, IllegalExcelHandler illegalHandler) {
        super(language);
        this.workbook = workbook;
        this.illegalHandler = illegalHandler;
    }

    /**
     * 根据model对象解析excel
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> readModelList(Class<T> clazz) {
        List<T> modelList = new ArrayList<>();

        ExcelSheetBean sheetBean = null;
        try {
            sheetBean = initSheet(clazz);

            buildColumnMapWithCheckWorkbook(sheetBean);
        } catch (IllegalSheetPositionException | IllegalExcelHeaderException e) {
            return modelList;
        }

        Sheet sheet = workbook.getSheetAt(sheetBean.sheetNum);
        for (int rowNum = sheetBean.beginDataNum; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row data = sheet.getRow(rowNum);

            if (data == null) {
                continue;
            }

            T it = null;
            try {
                it = converRowToObj(sheetBean.columnMap, data, clazz);
            } catch (Exception e) {
                continue;
            }

            modelList.add(it);
        }

        return modelList;
    }

    /**
     * 初始化excel列和Model字段的对应关系，同时检测excel的正确性
     *
     * @param sheetBean
     * @throws IllegalSheetPositionException
     * @throws IllegalExcelHeaderException
     * @throws IllegalExcelDataException
     */
    private void buildColumnMapWithCheckWorkbook(ExcelSheetBean sheetBean) throws IllegalSheetPositionException, IllegalExcelHeaderException {
        Sheet sheet = workbook.getSheetAt(sheetBean.sheetNum);

        if (sheet == null) {
            illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalSheetBinder());
            throw new IllegalSheetPositionException();
        }

        Row headRow = sheet.getRow(sheetBean.headLineNum);
        if (headRow == null) {
            illegalHandler.handleIllegalParse(IllegalExcelBinder.createHeadIsNullInfo());
            throw new IllegalExcelHeaderException();
        }

        short firstCellNum = headRow.getFirstCellNum();
        short lastCellNum = headRow.getLastCellNum();

        Map<String, ExcelColumnBean> columnMap = sheetBean.columnMap;

        //设置excel 列和类字段的对应关系，并判断是否有错误（最终是根据索引对应）
        for (Map.Entry<String, ExcelColumnBean> entry : columnMap.entrySet()) {
            ExcelColumnBean columnBean = entry.getValue();
            int columnIndex = columnBean.columnIndex;

            // 如果导入时想通过excle表头名称和实体类的columnName对应的话就不对索引进行判断
            if (!sheetBean.importBindByColumnName) {
                //设置了列索引，但是越界了
                boolean isWrongIndex = columnIndex != -1 && (columnIndex < firstCellNum || columnIndex >= lastCellNum);
                if (isWrongIndex) {
                    illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalHeadInfo(headRow));
                    throw new IllegalExcelHeaderException();
                }

                //设置了合法列索引
                if (columnIndex != -1) {
                    String cellValue = ExcelUtil.getCellStringValue(headRow.getCell(columnIndex));
                    columnBean.columnName = cellValue;
                    continue;
                }
            }


            //至此，model的该字段未设置sheet对应列的索引
            String columnName = columnBean.columnName;

            int i = firstCellNum;
            //根据设置的列名称查找对应索引
            for (; i < lastCellNum; i++) {
                Cell cell = headRow.getCell(i);
                String cellValue = ExcelUtil.getCellStringValue(cell);

                if (cellValue == null || !cellValue.trim().equals(columnName)) {
                    continue;
                } else {
                    columnBean.columnIndex = i;
                }
            }
            //类字段在excel head里没有对应索引
//            if (j == -1 || columnBean.columnIndex != j) {
//                illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalHeadInfo(headRow));
//                throw new IllegalExcelHeaderException();
//            }
        }
    }

    private <T> T converRowToObj(Map<String, ExcelColumnBean> headParamMap, Row row, Class<T> clazz) throws Exception {
        T instant = clazz.newInstance();

        for (Map.Entry<String, ExcelColumnBean> entry : headParamMap.entrySet()) {
            int columnIndex = entry.getValue().columnIndex;
            if (columnIndex == -1) {
                continue;
            }
            String filedName = entry.getKey();
            boolean notBeNull = entry.getValue().notNull;

            Cell cell = row.getCell(columnIndex);

            if (cell == null) {
                if (!notBeNull) {
                    continue;
                } else {
                    illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalDataInfo(row));
                    throw new IllegalExcelDataException();
                }
            }
            String cellValue = ExcelUtil.getCellStringValue(cell, workbook);

            if (StringUtils.isBlank(cellValue)) {
                if (!notBeNull) {
                    continue;
                } else {
                    illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalDataInfo(row));
                    throw new IllegalExcelDataException();
                }
            }


            try {
                ExcelUtil.setFieldValue(clazz.getDeclaredField(filedName), instant, cellValue);
            } catch (Exception e) {
                illegalHandler.handleIllegalParse(IllegalExcelBinder.createIllegalDataInfo(row));
                throw e;
            }

        }
        return instant;
    }
}
