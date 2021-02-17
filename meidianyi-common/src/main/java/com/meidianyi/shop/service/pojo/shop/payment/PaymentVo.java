package com.meidianyi.shop.service.pojo.shop.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class PaymentVo {
    private Byte      id;
    private Integer   shopId;
    private String    payName;
    private String    payCode;
    private String    payFee;
    private String    payDesc;
    private Byte      enabled;
    private Byte      isCod;
    private Byte      isOnlinePay;
}
