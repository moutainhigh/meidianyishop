package com.meidianyi.shop.service.pojo.shop.store.service.order;

import lombok.*;

/**
 * @author: 王兵兵
 * @create: 2020-02-04 12:05
 **/
@Setter
@Getter
public class OrderCloseQueenParam {
    private Integer shopId;
    private String orderSn;

    /**
     *任务id
     */
    private Integer taskJobId;
}
