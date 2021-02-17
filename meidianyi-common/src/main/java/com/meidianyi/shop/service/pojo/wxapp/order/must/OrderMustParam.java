package com.meidianyi.shop.service.pojo.wxapp.order.must;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 下单必填信息
 * @author 王帅
 */
@Getter
@Setter
public class OrderMustParam {
    @JsonIgnore
    private String orderSn;
    private String orderRealName;
    private String orderCid;
    private String consigneeRealName;
    private String consigneeCid;
    private String custom;
    private String customTitle;
}
