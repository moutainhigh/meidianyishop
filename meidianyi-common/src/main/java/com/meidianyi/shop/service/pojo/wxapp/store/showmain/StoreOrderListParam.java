package com.meidianyi.shop.service.pojo.wxapp.store.showmain;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/9/17
 **/
@Data
public class StoreOrderListParam extends BasePageParam {
    /**
     * 0待处理 1已完成
     */
    private Byte status;
    /**
     * 订单状态
     */
    private List<Byte> orderStatusList;
    private Integer storeId;
    /**
     * 门店账户id
     */
    private Integer storeAccountId;
}
