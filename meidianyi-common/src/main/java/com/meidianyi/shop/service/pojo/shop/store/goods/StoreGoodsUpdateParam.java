package com.meidianyi.shop.service.pojo.shop.store.goods;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-10-09 17:27
 **/
@Data
public class StoreGoodsUpdateParam {
    @NotNull
    private List<Integer> prdId;
    @NotNull
    private Integer storeId;
}
