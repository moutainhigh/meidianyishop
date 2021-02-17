package com.meidianyi.shop.common.foundation.excel.exception.handler;

/**
 * @author 李晓冰
 * @date 2019年07月19日
 */
public enum IllegalExcelEnum {
    /**
     * sheet位置错误
     */
    ILLEGEL_SHEET_POSITION,
    /**
     * sheet列头错误
     */
    ILLEGAL_SHEET_HEAD,
    /**
     * sheet不存在列头
     */
    SHEET_HEAD_NULL,
    /**
     * sheet数据行存在错误
     */
    ILLEGAL_SHEET_DATA;
}
