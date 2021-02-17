package com.meidianyi.shop.common.foundation.excel;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelDynamicColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import com.meidianyi.shop.common.foundation.excel.bean.ExcelColumnBean;
import com.meidianyi.shop.common.foundation.excel.bean.ExcelSheetBean;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelDataException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalExcelHeaderException;
import com.meidianyi.shop.common.foundation.excel.exception.IllegalSheetPositionException;
import com.meidianyi.shop.common.foundation.excel.exception.NotExcelModelException;
import com.meidianyi.shop.common.foundation.excel.util.IDymicColNameI18n;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.excel.annotation.*;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 李晓冰
 * @date 2019年07月20日
 */
public abstract class AbstractExcelDisposer {

    public static final String LANGUAGE_TYPE_EXCEL="excel";

    public static final String DEFAULT_LANGUAGE="zh_CN";

    public String language;
    /**
     * 动态列国际化
     */
    protected IDymicColNameI18n colI18n;

    public AbstractExcelDisposer() {
    }

    public AbstractExcelDisposer(String language) {
        this.language = StringUtils.isBlank(language) ?AbstractExcelDisposer.DEFAULT_LANGUAGE: language;
    }

    protected ExcelSheetBean initSheet(Class<?> clazz){
        return initSheet(clazz,null);
    }
    /**
     *  初始化ExcelSheetBean，映射model类和excel
     * @param clazz
     * @return
     * @throws IllegalSheetPositionException
     * @throws IllegalExcelHeaderException
     * @throws IllegalExcelDataException
     */
    protected ExcelSheetBean initSheet(Class<?> clazz, List<String> neededColumns){
        if (!clazz.isAnnotationPresent(ExcelSheet.class)) {
            throw new NotExcelModelException();
        }

        // 读取ExcelSheet注解内容，创建并初始化ExcelSheetBean类
        ExcelSheet sheetAnnotation = clazz.getAnnotation(ExcelSheet.class);
        ExcelSheetBean sheetBean = new ExcelSheetBean();
        sheetBean.sheetNum = sheetAnnotation.sheetNum();
        sheetBean.beginDataNum = sheetAnnotation.beginDataNum();
        sheetBean.headLineNum = sheetAnnotation.headLineNum();
        sheetBean.importBindByColumnName = sheetAnnotation.importBindByColumnName();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 字段需要忽略则跳过
            if (field.isAnnotationPresent(ExcelIgnore.class)) {
                continue;
            }
            // 不处理动态字段
            if (field.isAnnotationPresent(ExcelDynamicColumn.class)) {
                continue;
            }
            // 指定了需要的字段，而当前字段不在指定的字段中则跳过
            if (neededColumns != null && !neededColumns.contains(field.getName())) {
                continue;
            }

            ExcelColumnBean columnBean = new ExcelColumnBean();

            if (field.isAnnotationPresent(ExcelColumnNotNull.class)) {
                columnBean.notNull = true;
            }

            String filedName = field.getName();

            columnBean.columnName = filedName;

            if (field.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn columnAnnotation = field.getAnnotation(ExcelColumn.class);

                if (columnAnnotation.args().length > 0) {
                    columnBean.args=columnAnnotation.args();
                }

                if (columnAnnotation.columnName().length() > 0) {
                    columnBean.columnName = columnAnnotation.columnName();
                }

                columnBean.columnName =Util.translateMessage(language, columnBean.columnName, LANGUAGE_TYPE_EXCEL, (Object[])columnBean.args);

                if (columnAnnotation.columnIndex() != -1) {
                    columnBean.columnIndex = columnAnnotation.columnIndex();
                }

                columnBean.fieldClazz=field.getType();

            }

            sheetBean.columnMap.put(filedName, columnBean);
        }

        return sheetBean;
    }

    /**
     * 当需要添加动态列表示，ExcelSheetBean追加对应的ExcelColumnBean
     * @param sheetBean excel 中的sheet映射类
     * @param dynamicColumns 动态列的信息，key列名，value值的类型
     */
    protected void appendDynamicColumns(ExcelSheetBean sheetBean, Map<String,Class> dynamicColumns){
        // 寻找动态字段的开始位置
        int maxColumnIndex = -1;
        Set<Map.Entry<String, ExcelColumnBean>> entries = sheetBean.columnMap.entrySet();
        for (Map.Entry<String, ExcelColumnBean> entry : entries) {
            if (maxColumnIndex < entry.getValue().columnIndex) {
                maxColumnIndex = entry.getValue().columnIndex;
            }
        }
        maxColumnIndex++;
        // 将动态字段映射成对应的值
        for (Map.Entry<String, Class> stringClassEntry : dynamicColumns.entrySet()) {
            ExcelColumnBean columnBean = new ExcelColumnBean();
            columnBean.columnIndex = maxColumnIndex++;
            // 处理动态列的国际化
            if(colI18n!=null) {
                columnBean.columnName = colI18n.i18nName(stringClassEntry.getKey(),language);
            }else {
                columnBean.columnName = stringClassEntry.getKey();
            }

            columnBean.fieldClazz = stringClassEntry.getValue();
            columnBean.isDynamicColumn = true;
            sheetBean.columnMap.put(stringClassEntry.getKey(),columnBean);
        }
    }
    /**
     * 处理model类型属性字段是Map的情况，将其视为动态添加表头，
     * Map的key作为表头，
     */
    private void processMapClazzField(){

    }

    /**
     * 设置动态列的国际化
     * @param colI18n
     */
    public void setColI18n(IDymicColNameI18n colI18n) {
        this.colI18n = colI18n;
    }

}
