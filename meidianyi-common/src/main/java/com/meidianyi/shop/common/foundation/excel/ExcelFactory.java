package com.meidianyi.shop.common.foundation.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * @author 李晓冰
 * @date 2019年07月17日
 */
public class ExcelFactory {

    /**
     * 默认产生.xlsx版本
     * 用于创建excel
     * @return
     */
    public static Workbook createWorkbook() {
        return createWorkbook(ExcelTypeEnum.XLSX);
    }

    public static Workbook createWorkbook(ExcelTypeEnum excelType) {
        Workbook workbook = null;
        if (ExcelTypeEnum.XLS.equals(excelType)) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        return workbook;
    }

    /**
     * 用于读取excel
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Workbook createWorkbook(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }

        InputStream inputStream = new FileInputStream(file);

        Workbook workbook = null;
        if (filePath.endsWith(ExcelTypeEnum.XLS.getSuffix())) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }


    public static Workbook createWorkbook(InputStream in,ExcelTypeEnum excelType) throws IOException {
        Workbook workbook = null;
        if (ExcelTypeEnum.XLS.equals(excelType)) {
            workbook = new HSSFWorkbook(in);
        } else {
            workbook = new XSSFWorkbook(in);
        }
        return workbook;
    }
}
