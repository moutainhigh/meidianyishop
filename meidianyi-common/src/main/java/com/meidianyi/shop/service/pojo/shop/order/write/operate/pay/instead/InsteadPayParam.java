package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 代付订单支付
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class InsteadPayParam extends OrderOperateQueryParam {
    private BigDecimal moneyPaid;
    private String username;
    private String message;
    private String clientIp;
}
