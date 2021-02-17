package com.meidianyi.shop.service.shop.goods.goodsimport;

import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelBinder;
import com.meidianyi.shop.common.foundation.excel.exception.handler.IllegalExcelHandler;

/**
 * 微铺宝excel导入错误处理器
 * 该处理器会默认只处理sheet的位置错误，表头所在行位置错误，表中字段顺序错误的情况。
 * 对于具体的数据错误默认没有处理能力。
 * @author 李晓冰
 * @date 2020年03月19日
 */
public class GoodsExcelIllegalFormatterHandler implements IllegalExcelHandler {

    IllegalExcelBinder wrongBinderInfo;

    public IllegalExcelBinder getWrongBinderInfo() {
        return wrongBinderInfo;
    }

    @Override
    public void handleIllegalParse(IllegalExcelBinder binder) {
        this.wrongBinderInfo = binder;
    }
}
