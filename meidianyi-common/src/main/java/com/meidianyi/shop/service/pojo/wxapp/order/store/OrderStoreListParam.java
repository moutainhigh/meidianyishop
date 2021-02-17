package com.meidianyi.shop.service.pojo.wxapp.order.store;

import com.meidianyi.shop.service.pojo.wxapp.order.base.BaseParam;
import lombok.Data;

/**
 * 门店订单列表
 * @author 孔德成
 * @date 2020/9/23 11:40
 */
@Data
public class OrderStoreListParam extends BaseParam {
    public Integer currentPage;
    public Integer pageRows;
    private Byte type;
    private String search;
}
