package com.meidianyi.shop.service.pojo.shop.order.goods.param;

import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 */
@Data
public class SyncHisMedicalOrderRequestParam {
    private Byte status;
    private String orderSn;
    private List<SyncMedicalOrderGoodsItem> goodsItemList;
}
