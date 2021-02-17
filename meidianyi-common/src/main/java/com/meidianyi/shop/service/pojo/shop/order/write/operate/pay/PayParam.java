package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 发货
 * 
 * @author 王帅
 *
 */
@Getter
@Setter
public final class PayParam extends OrderOperateQueryParam{
    @JsonIgnore
    private String clientIp;
}
