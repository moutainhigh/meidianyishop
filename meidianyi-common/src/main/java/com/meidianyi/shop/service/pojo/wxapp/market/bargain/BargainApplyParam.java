package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-12-25 18:05
 **/
@Getter
@Setter
public class BargainApplyParam {
    @NotNull
    private Integer goodsId;
    @NotNull
    private Integer bargainId;
    @NotNull
    private Integer prdId;
}
