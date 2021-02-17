package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年07月08日
 */
@Data
public class GoodsColumnCheckExistParam {

    private ColumnCheckForEnum columnCheckFor;

    private Integer goodsId;
    private String goodsName;
    private String goodsSn;

    private Integer prdId;
    private String prdSn;
    private String prdCodes;

    public static enum ColumnCheckForEnum {
    	//商品信息重复判断
        E_GOODS,
        //商品SKU信息重复判断
        E_GOODS_SPEC_PRODUCTION;
    }

}
