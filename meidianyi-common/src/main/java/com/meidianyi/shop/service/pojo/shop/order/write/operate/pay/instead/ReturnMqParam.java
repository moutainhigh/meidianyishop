package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author 王帅
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReturnMqParam {
    private String orderSn;
    private BigDecimal money;
    private Integer retId;
    public Integer shopId;
    /**
     *任务id
     */
    private Integer taskJobId;

    public ReturnMqParam(String orderSn, BigDecimal money, Integer retId, Integer shopId) {
        this.orderSn = orderSn;
        this.money = money;
        this.retId = retId;
        this.shopId = shopId;
    }
}
