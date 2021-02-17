package com.meidianyi.shop.service.pojo.shop.goods.goodsimport;

/**
 * 商品导入时，数据格式错误类型
 * @author 李晓冰
 * @date 2020年03月20日
 */
public enum GoodsImportDataIIllegalEnum {
    /**货品编号为null*/
    GOODS_SN_NULL((byte)1,"goods.sn.is.null"),
    /**商品名称为null*/
    GOODS_NAME_NULL((byte)2,"goods.name.is.null"),
    /**商家编码为null*/
    GOODS_PRD_SN_NULL((byte)3,"goods.prd.sn.is.null");


    private byte errorCode;
    private String errorMsg;

    GoodsImportDataIIllegalEnum(byte errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public byte getErrorCode() {
        return errorCode;
    }
}
