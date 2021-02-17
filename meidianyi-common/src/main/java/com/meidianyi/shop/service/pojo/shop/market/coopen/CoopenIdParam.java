package com.meidianyi.shop.service.pojo.shop.market.coopen;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/11/22 17:27
 */
@Getter
@Setter
public class CoopenIdParam {
    @NotNull
    private Integer id;
}
