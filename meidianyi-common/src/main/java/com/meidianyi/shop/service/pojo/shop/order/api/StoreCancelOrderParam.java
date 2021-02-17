package com.meidianyi.shop.service.pojo.shop.order.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李晓冰
 * @date 2020年09月03日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreCancelOrderParam {
    private String shopSn;
    private String orderSn;
}
