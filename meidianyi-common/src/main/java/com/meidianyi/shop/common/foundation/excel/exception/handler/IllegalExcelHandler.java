package com.meidianyi.shop.common.foundation.excel.exception.handler;

/**
 * @author 李晓冰
 * @date 2019年07月19日
 */
public interface IllegalExcelHandler {

    /**
     *  excel解析错误回调方法
     * @param binder
     */
    void handleIllegalParse(IllegalExcelBinder binder);
}
