package com.meidianyi.shop.service.pojo.shop.goods.pos;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年04月28日
 */
@Data
public class PosSyncProductMqParam {
    /**门店id*/
    private Integer storeId;
    /**待同步数据*/
    private List<PosSyncGoodsPrdParam> goodsPrdList;

    private Integer shopId;
    /**
     * 任务id
     */
    private Integer taskJobId;

}
