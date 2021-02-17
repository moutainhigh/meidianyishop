package com.meidianyi.shop.common.foundation.excel.bean;

import java.util.HashMap;

/**
 * @author 李晓冰
 * @date 2019年07月18日
 */
public class ExcelSheetBean {
    public int sheetNum;
    public int beginDataNum;
    public int headLineNum;
    public boolean importBindByColumnName;
    public HashMap<String, ExcelColumnBean> columnMap=new HashMap<>();


    @Override
    public String toString() {
        return "ModelListParam{" +
            "sheetNum=" + sheetNum +
            ", beginDataNum=" + beginDataNum +
            ", headLineNum=" + headLineNum +
            ", columnMap=" + columnMap +
            '}';
    }
}