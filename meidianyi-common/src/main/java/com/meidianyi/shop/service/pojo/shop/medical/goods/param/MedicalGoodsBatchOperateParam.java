package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年09月15日
 */
@Data
public class MedicalGoodsBatchOperateParam {
    private List<Integer> goodsIds;

    private Byte isOnSale;

    /**批量上架或下架his独有数据*/
    private Byte batchUpOrDownHisGoods;
    /**批量上架或下架药店独有数据*/
    private Byte batchUpOrDownStoreGoods;
    /**批量上架或下架双方匹配数据*/
    private Byte  batchUpOrDownBothInGoods;
}
