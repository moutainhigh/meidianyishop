package com.meidianyi.shop.service.pojo.shop.express;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 快递公司
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class ExpressVo {
    private Byte      shippingId;
    private String    shippingCode;
    private String    shippingName;
    private String    shippingDesc;
    private String    insure;
    private Byte      supportCod;
    private String    shippingPrint;
    private Byte      printModel;
    private Byte      shippingOrder;
}
