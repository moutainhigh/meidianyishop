package com.meidianyi.shop.service.pojo.shop.goods.goodsimport;

import lombok.Data;

/**
 * 商品导入数据模型基础类
 * @author 李晓冰
 * @date 2020年03月20日
 */
@Data
public class GoodsExcelImportBase {
    protected Integer goodsId;
    /**货品编号*/
    protected String goodsSn;
    protected String goodsName;
    protected Integer prdId;
    /**商家编码*/
    protected String prdSn;
    protected String prdDesc;
    /**商品条码*/
    protected String prdCodes;
}
