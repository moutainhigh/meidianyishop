package com.meidianyi.shop.service.pojo.wxapp.market.enterpolitely;

import lombok.Data;

import javax.validation.constraints.Positive;

/**
 * @author liufei
 * @date 12/25/19
 */
@Data
public class EnterPolitelyParam {
    @Positive
    private Integer userId;
}
