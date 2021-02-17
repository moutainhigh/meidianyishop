package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 赵晓东
 */
@Data
public class StoreGoodsUpdateTimeParam {
    /**更新类型 0-普通更新 1-全部更新*/
    @NotNull
    private Byte updateTye;
    @NotNull
    private Integer storeId;
    /**起始时间*/
    private Timestamp updateBegin;
    /**结束时间*/
    private Timestamp updateEnd;
}
