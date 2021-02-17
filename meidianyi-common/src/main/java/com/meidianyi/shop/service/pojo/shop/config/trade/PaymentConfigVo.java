package com.meidianyi.shop.service.pojo.shop.config.trade;

import lombok.Data;

/**
 * @author liufei
 * @date 2019/7/11
 */
@Data
public class PaymentConfigVo {
    public String payCode;
    public String payName;
    public Byte enabled;
}
