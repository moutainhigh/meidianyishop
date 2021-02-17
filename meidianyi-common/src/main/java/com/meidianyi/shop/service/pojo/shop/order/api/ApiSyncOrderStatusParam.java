package com.meidianyi.shop.service.pojo.shop.order.api;

import lombok.Data;

/**
 * 药房同步订单状态请求参数
 * @author 李晓冰
 * @date 2020年09月03日
 */
@Data
public class ApiSyncOrderStatusParam {
    private String orderSn;
    private Byte orderStatus;
    private String deliverPersonName;
    private String deliverPersonPhone;
    private String deliverMemo;
}
