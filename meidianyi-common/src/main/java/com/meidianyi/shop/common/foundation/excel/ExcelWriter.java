package com.meidianyi.shop.common.foundation.excel;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelDynamicColumn;
import com.meidianyi.shop.common.foundation.excel.bean.ClassList;
import com.meidianyi.shop.common.foundation.excel.bean.ExcelColumnBean;
import com.meidianyi.shop.common.foundation.excel.bean.ExcelSheetBean;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelDataException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelHeaderException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalSheetPositionException;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2019年07月19日
 */
@Slf4j
public class ExcelWriter extends AbstractExcelDisposer {
    private Workbook workbook;
    private String sheetName;


    public ExcelWriter(Workbook workbook, String sheetName) {
        this(AbstractExcelDisposer.DEFAULT_LANGUAGE, workbook, sheetName);
    }

    public ExcelWriter(Workbook workbook) {
        this(AbstractExcelDisposer.DEFAULT_LANGUAGE, workbook);
    }

    public ExcelWriter(String language, Workbook workbook, String sheetName) {
        super(language);
        this.workbook = workbook;
        this.sheetName = sheetName;
    }

    public ExcelWriter(String language, Workbook workbook) {
        super(language);
        this.workbook = workbook;
    }

    /**
     * 根据clazz(sheet对应的modle类型)和指定的neededColumns生成对应excel sheet的关系类ExcelSheetBean
     *
     * @param clazz         sheet对应的model类型
     * @param neededColumns 指定需要的列，null表示不指定
     * @param <T>           clazz类型参数
     * @return ExcelSheetBean 映射了sheet的结构
     */
    private <T> ExcelSheetBean createSheetBean(Class<T> clazz, List<String> neededColumns) {
        ExcelSheetBean sheetBean = initSheet(clazz, neededColumns);

        // 导出指定excle列的时候处理多余的excel空白列
        if (neededColumns != null&&neededColumns.size()>0) {
            HashMap<String, ExcelColumnBean> columnMap = sheetBean.columnMap;
            List<ExcelColumnBean> neededColumnsBean = columnMap.values().stream().filter(c->c.columnIndex!=-1).sorted(Comparator.comparing(c->c.columnIndex)).collect(Collectors.toList());
            for (int i = 0; i < neededColumnsBean.size(); i++) {
                neededColumnsBean.get(i).columnIndex=i;
            }
        }
        return sheetBean;
    }

    /**
     * 根据ExcelSheetBean中指定的索引位子，在workbook中创建对应的sheet
     *
     * @param sheetBean 映射了sheet的结构
     * @return Sheet
     */
    private Sheet createTargetSheet(ExcelSheetBean sheetBean) {
        int sheetPosition = sheetBean.sheetNum;
        if (sheetPosition < 0) {
            sheetPosition = 0;
        }
        for (int i = 0; i < sheetPosition; i++) {
            workbook.createSheet();
        }

        Sheet sheet = null;
        if (sheetName != null) {
            sheet = workbook.createSheet(sheetName);
        } else {
            sheet = workbook.createSheet();
        }
        workbook.setActiveSheet(sheetPosition);

        return sheet;
    }

    @SuppressWarnings("all")
    private <T> void writeDataIntoSheet(ExcelSheetBean sheetBean, Sheet sheet, List<T> dataArray, Field dynamicField) {
        Map<Integer, CellStyle> styleMap = new HashMap<>(sheetBean.columnMap.size());
        Map<Integer, CellType> typeMap = new HashMap<>(sheetBean.columnMap.size());
        //设置单元格样式和类型缓存
        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean columnBean = entry.getValue();

            CellStyle cellStyle = ExcelUtil.getCellStyle(columnBean.fieldClazz, workbook);
            CellType cellType = ExcelUtil.convertJavaType2CellType(columnBean.fieldClazz);

            styleMap.put(columnBean.columnIndex, cellStyle);
            typeMap.put(columnBean.columnIndex, cellType);
        }

        int beginDataIndex = sheetBean.beginDataNum;
        //将model数据转换为对应的row
        for (int i = 0; i < dataArray.size(); i++) {
            int rowIndex = beginDataIndex + i;
            Row row = sheet.createRow(rowIndex);
            for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
                ExcelColumnBean columnBean = entry.getValue();
                String fieldName = entry.getKey();
                Cell cell = row.createCell(columnBean.columnIndex);
                cell.setCellType(typeMap.get(columnBean.columnIndex));
                cell.setCellStyle(styleMap.get(columnBean.columnIndex));

                Object value = null;
                try {
                    if (columnBean.isDynamicColumn) {
                        value = ExcelUtil.getFieldValue(fieldName,dataArray.get(i),dynamicField);
                    } else {
                        value = ExcelUtil.getFieldValue(fieldName, dataArray.get(i));
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ExcelUtil.setCellValue(cell, value);
            }
        }
        //设置列宽自适应
        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean columnBean = entry.getValue();
            sheet.autoSizeColumn(columnBean.columnIndex);
        }
    }

    /**
     * 获取动态字段的列头和类型的对应关系
     *
     * @param obj   待导出的数据对象
     * @param field 动态字段
     * @return key 列头名称，value 该列的类型
     */
    private Map<String, Class> getDynamicColumnMap(Object obj, Field field) throws Exception {
        field.setAccessible(true);
        Object val = field.get(obj);
        if (!(val instanceof Map)) {
            throw new Exception("动态字段类型错误，仅支持Map类型");
        }
        Map map = (Map) val;
        Map<String, Class> returnMap = new LinkedHashMap<>(map.size());
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            if(entry.getValue()==null) {
                returnMap.put(entry.getKey().toString(), String.class);
            }else {
                returnMap.put(entry.getKey().toString(), entry.getValue().getClass());
            }
        }
        return returnMap;
    }



    public <T> void writeModelListWithDynamicColumn(List<T> dataArray, Class<T> clazz) {
        if (dataArray.size() == 0) {
            // 如果传入的是空数据，则按照无动态列进行处理
            writeModelList(dataArray, clazz, null);
            return;
        }

        ExcelSheetBean sheetBean = createSheetBean(clazz, null);
        Sheet sheet = createTargetSheet(sheetBean);

        Field dynamicField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelDynamicColumn.class)) {
                dynamicField = field;
                break;
            }
        }
        if (dynamicField == null) {
            // 如果没有设置动态字段，则按照无动态列进行处理
            writeModelList(dataArray, clazz, null);
            return;
        }
        // 获取动态字段的字段名称和字段值
        T val = dataArray.get(0);
        Map<String, Class> dynamicColumnMap = null;
        try {
            dynamicColumnMap = getDynamicColumnMap(val, dynamicField);
        } catch (Exception e) {
            log.debug("excel导出动态列头-动态内容字段读取异常：" + e.getMessage());
        }
        // 将动态列表字段加入到sheetBean
        appendDynamicColumns(sheetBean, dynamicColumnMap);
        // 创建excel模板（将列头写入到excel内）
        createExcelTemplate(clazz, sheetBean, sheet);

        writeDataIntoSheet(sheetBean,sheet,dataArray,dynamicField);
    }



    public <T> void writeModelList(List<T> dataArray, Class<T> clazz) {
        writeModelList(dataArray, clazz, null);
    }

    /**
     * 将数据写入到excel中
     *
     * @param dataArray
     * @param clazz
     * @param <T>
     * @throws IllegalExcelHeaderException
     * @throws IllegalSheetPositionException
     * @throws IllegalExcelDataException
     */
    public <T> void writeModelList(List<T> dataArray, Class<T> clazz, List<String> neededColumns) {

        ExcelSheetBean sheetBean = createSheetBean(clazz, neededColumns);

        Sheet sheet = createTargetSheet(sheetBean);

        createExcelTemplate(clazz, sheetBean, sheet);

        writeDataIntoSheet(sheetBean,sheet,dataArray,null);
    }


    private void createExcelTemplate(Class<?> clazz, ExcelSheetBean sheetBean, Sheet sheet) {

        int headLineNum = sheetBean.headLineNum;

        int maxColumnIndex = -1;
        // 寻找最大的位置下标
        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean value = entry.getValue();
            if (maxColumnIndex < value.columnIndex) {
                maxColumnIndex = value.columnIndex;
            }
        }
        // 将未填写位置下标的依次追加到末尾
        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean value = entry.getValue();
            if (value.columnIndex == -1) {
                value.columnIndex = ++maxColumnIndex;
            }
        }

        Row row = sheet.createRow(headLineNum);
        CellStyle cellStyle = ExcelStyleFactory.createCommonHeaderCellStyle(workbook);

        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean value = entry.getValue();
            Cell cell = row.createCell(value.columnIndex);

            cell.setCellType(ExcelUtil.convertJavaType2CellType(String.class));

            ExcelUtil.setCellValue(cell, value.columnName);

            cell.setCellStyle(cellStyle);

            sheet.autoSizeColumn(value.columnIndex);
        }
    }

    /**
     * 对外创建excel模板
     *
     * @param clazz
     */
    public void createExcelTemplate(Class<?> clazz) {
        ExcelSheetBean sheetBean = initSheet(clazz);

        Sheet sheet = createTargetSheet(sheetBean);

        createExcelTemplate(clazz, sheetBean, sheet);
    }


    /**
     * 可操作合并单元格  测试类见ExcelTest.excelExportDataByMergedRegion()
     *
     * @param <T>
     * @param dataArray
     * @param cList
     * @throws Exception
     */
    public <T> void writeModelListByRegion(List<T> dataArray, ClassList cList) throws Exception {
        Class<?> clazz = cList.getUpClazz();
        Class<?> innerClazz = cList.getInnerClazz();
        ExcelSheetBean sheetBean = initSheet(clazz);
        ExcelSheetBean initSheet = initSheet(innerClazz);
        Sheet sheet = createTargetSheet(sheetBean);
        createExcelTemplate(clazz, sheetBean, sheet);
        Map<Integer, CellStyle> styleMap = new HashMap<>(sheetBean.columnMap.size());
        Map<Integer, CellType> typeMap = new HashMap<>(sheetBean.columnMap.size());

        // 设置单元格样式和类型缓存
        String key = processCellStyleAndTypeCache(sheetBean, initSheet, styleMap, typeMap);

        int beginDataIndex = sheetBean.beginDataNum;
        // 将model数据转换为对应的row
        for (int i = 0; i < dataArray.size(); i++) {
            // 合并的行对应行数的统计，要顺序，所以用LinkedList
            List<Integer> rowList = new LinkedList<Integer>();
            // 合并的列对应的列数 单个的时候元素会重
            List<Integer> columnList = new LinkedList<Integer>();
            // 没有进行合并的列的列数
            Set<Integer> cNo = new HashSet<Integer>();
            // 合并的列的数量
            Set<Integer> innerRow = new HashSet<Integer>();
            int rowIndex = beginDataIndex + i;
            Row row = sheet.createRow(rowIndex);
            for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
                ExcelColumnBean columnBean = entry.getValue();
                String fieldName = entry.getKey();
                Object value = null;
                Cell cell = row.createCell(columnBean.columnIndex);
                cell.setCellType(typeMap.get(columnBean.columnIndex));
                cell.setCellStyle(styleMap.get(columnBean.columnIndex));
                try {
                    value = ExcelUtil.getFieldValue(fieldName, dataArray.get(i));
                    int tempRowIndex = rowIndex;
                    Row temoRow = row;
                    if (fieldName.equals(key)) {
                        rowList.add(tempRowIndex);
                        for (Map.Entry<String, ExcelColumnBean> entrySet : initSheet.columnMap.entrySet()) {
                            tempRowIndex = rowIndex;
                            ExcelColumnBean innerColumnBean = entrySet.getValue();
                            String innerFieldName = entrySet.getKey();
                            for (Object o : (List<?>) value) {
                                temoRow = sheet.getRow(tempRowIndex);
                                if (temoRow == null) {
                                    beginDataIndex++;
                                    temoRow = sheet.createRow(tempRowIndex);
                                }
                                Object value1 = ExcelUtil.getFieldValue(innerFieldName, o);
                                Cell cell1 = temoRow.createCell(innerColumnBean.columnIndex);
                                cell1.setCellType(typeMap.get(innerColumnBean.columnIndex));
                                cell1.setCellStyle(styleMap.get(innerColumnBean.columnIndex));
                                cNo.add(innerColumnBean.columnIndex);
                                innerRow.add(tempRowIndex);
                                System.out.println(innerColumnBean);
                                System.out.println(
                                    fieldName + "-行：" + tempRowIndex + " 列：" + innerColumnBean.columnIndex + "   " + value1);
                                ExcelUtil.setCellValue(cell1, value1);
                                tempRowIndex = tempRowIndex + 1;
                            }
                        }
                        rowList.add(tempRowIndex - 1);
                    } else {
                        System.out.println(fieldName + "---行：" + rowIndex + " 列：" + columnBean.columnIndex + "   " + value);
                        columnList.add(columnBean.columnIndex);
                        ExcelUtil.setCellValue(cell, value);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            // 因为翻译时候有站位，要去掉不是合并单元格的列的列数
            processDropColumns(sheetBean, sheet, rowList, columnList, cNo, innerRow);
            System.out.println();
        }
    }

    private void processDropColumns(ExcelSheetBean sheetBean, Sheet sheet, List<Integer> rowList, List<Integer> columnList, Set<Integer> cNo, Set<Integer> innerRow) {
        List<Integer> cnList = new ArrayList<Integer>(cNo);
        columnList.removeAll(cnList);
        if (innerRow.size() > 1) {
            // List中的类如果是一行的就不要合并
            processMergeRegion(sheet, rowList, columnList);
            for (Integer coum : cNo) {
                sheet.autoSizeColumn(coum);
            }
        } else {
            for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
                ExcelColumnBean columnBean = entry.getValue();
                sheet.autoSizeColumn(columnBean.columnIndex);
            }
        }
    }

    private void processMergeRegion(Sheet sheet, List<Integer> rowList, List<Integer> columnList) {
        for (int e = 0; e < columnList.size(); e++) {
            // 合并日期占两行(4个参数，分别为起始行，结束行，起始列，结束列)
            CellRangeAddress region = new CellRangeAddress(rowList.get(0), rowList.get(1), columnList.get(e),
                columnList.get(e));
            sheet.addMergedRegion(region);
            // 考虑是否合并单元格，
            sheet.autoSizeColumn(columnList.get(e), true);
            // 格式化后设置边框
            setBorderStyle(BorderStyle.THIN, region, sheet);

        }
    }

    private String processCellStyleAndTypeCache(ExcelSheetBean sheetBean, ExcelSheetBean initSheet, Map<Integer, CellStyle> styleMap, Map<Integer, CellType> typeMap) {
        String key = null;
        for (Map.Entry<String, ExcelColumnBean> entry : sheetBean.columnMap.entrySet()) {
            ExcelColumnBean columnBean = entry.getValue();
            if (List.class.equals(columnBean.fieldClazz)) {
                key = entry.getKey();
                for (Map.Entry<String, ExcelColumnBean> entrySet : initSheet.columnMap.entrySet()) {
                    ExcelColumnBean innerColumnBean = entrySet.getValue();
                    CellStyle cellStyle = ExcelUtil.getCellStyle(innerColumnBean.fieldClazz, workbook);
                    CellType cellType = ExcelUtil.convertJavaType2CellType(innerColumnBean.fieldClazz);

                    styleMap.put(innerColumnBean.columnIndex, cellStyle);
                    typeMap.put(innerColumnBean.columnIndex, cellType);

                }
            } else {

                CellStyle cellStyle = ExcelUtil.getCellStyle(columnBean.fieldClazz, workbook);
                CellType cellType = ExcelUtil.convertJavaType2CellType(columnBean.fieldClazz);
                styleMap.put(columnBean.columnIndex, cellStyle);
                typeMap.put(columnBean.columnIndex, cellType);
            }
        }
        return key;
    }


    /**
     * 合并单元格以后加边框
     *
     * @param border
     * @param region
     * @param sheet
     */
    public void setBorderStyle(BorderStyle border, CellRangeAddress region, Sheet sheet) {
        RegionUtil.setBorderBottom(border, region, sheet);//下边框
        RegionUtil.setBorderLeft(border, region, sheet);     //左边框
        RegionUtil.setBorderRight(border, region, sheet);    //右边框
        RegionUtil.setBorderTop(border, region, sheet);      //上边框
    }


}
