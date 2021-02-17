package com.meidianyi.shop.service.pojo.shop.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: 王兵兵
 * @create: 2020-05-06 15:00
 **/
@Getter
@Setter
public class ChargeRenewAddParam {
    /**
     * 申请类型 0升级 1续费
     */
    private Byte applyType;
    /**
     * 点击模块
     */
    private String applyMod;
}
