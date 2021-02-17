package com.meidianyi.shop.service.pojo.wxapp.order.term;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务条款
 * @author 王帅
 */
@Getter
@Setter
public class OrderTerm {
    private Byte serviceTerms;
    private String serviceName;
    private Byte serviceChoose;

    public void init(Byte serviceTerms, String serviceName, Byte serviceChoose){
        this.serviceTerms = serviceTerms;
        this.serviceName = serviceName;
        this.serviceChoose = serviceChoose;
    }
}
