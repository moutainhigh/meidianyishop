package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author liufei
 * @date 12/3/19
 */
@Data
public class OrderSnParam {
    @NotBlank
    private String orderSn;
    private String clientIp;
}
