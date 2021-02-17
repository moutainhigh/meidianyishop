package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.*;

/**
 * @author 赵晓东
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStoreGoodsMqParam {
    /**商铺id*/
    public Integer shopId;
    /**队列任务id*/
    private Integer taskJobId;
    /**传输参数*/
    private StoreGoodsUpdateTimeParam param;

    public UpdateStoreGoodsMqParam(Integer shopId, StoreGoodsUpdateTimeParam param){
        this.shopId = shopId;
        this.param = param;
    }
}
