package com.meidianyi.shop.service.pojo.wxapp.store.showmain;

import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/9/16
 **/
@Data
public class StoreMonthStatisticVo {
    /**
     * 待处理数量
     */
    private Integer waitHandleNum;
    /**
     * 配送中数量
     */
    private Integer deliveryNum;
    /**
     * 已完成数量
     */
    private Integer finishedNum;
    /**
     * 售后中数量
     */
    private Integer saleAfterNum;
}
