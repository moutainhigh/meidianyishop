package com.meidianyi.shop.service.pojo.shop.market.lottery;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/8/5 15:08
 */
@Getter
@Setter
public class LotteryByIdParam {

    @NotNull
    private Integer id;

}
