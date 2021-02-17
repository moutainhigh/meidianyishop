package com.meidianyi.shop.common.foundation.excel;

import org.apache.poi.ss.usermodel.*;

/**
 * @author 李晓冰
 * @date 2019年07月19日
 */
public class ExcelStyleFactory {

    private static final String DEFAULT_FONT_NAME = "宋体";
    private static final short DEFAULT_HEIGHT_IN_POINT = 20;
    private static final short DEFAULT_FONT_HEIGHT = 12 * DEFAULT_HEIGHT_IN_POINT;
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * 生成一般样式
     *
     * @param workbook
     * @return
     */
    public static CellStyle createCommonCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontName(DEFAULT_FONT_NAME);
        font.setFontHeightInPoints(DEFAULT_HEIGHT_IN_POINT);
        font.setFontHeight(DEFAULT_FONT_HEIGHT);
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * 数字样式
     *
     * @param workbook
     * @return
     */
    public static CellStyle createCommonNumberCellStyle(Workbook workbook) {
        CellStyle cellStyle = createCommonCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        return cellStyle;
    }

    /**
     * 生成一般文字样式
     *
     * @return
     */
    public static CellStyle createCommonTextCellStyle(Workbook workbook) {
        CellStyle cellStyle = createCommonCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }

    /**
     *  生成一般日期样式
     * @param workbook
     * @return
     */
    public static CellStyle createCommonDateCellStyle(Workbook workbook) {
        CellStyle cellStyle = createCommonCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DATE_FORMAT));
        return cellStyle;
    }

    /**
     * 生成excel头样式
     *
     * @param workbook
     * @return
     */
    public static CellStyle createCommonHeaderCellStyle(Workbook workbook) {
        CellStyle cellStyle = createCommonCellStyle(workbook);

        Font font = workbook.createFont();
        font.setFontName(DEFAULT_FONT_NAME);
        font.setFontHeightInPoints(DEFAULT_HEIGHT_IN_POINT);
        font.setFontHeight(DEFAULT_FONT_HEIGHT);
        font.setBold(true);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
