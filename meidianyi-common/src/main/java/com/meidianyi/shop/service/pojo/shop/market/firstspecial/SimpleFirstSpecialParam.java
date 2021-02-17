package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-08-16 18:45
 **/
@Data
public class SimpleFirstSpecialParam {
    /**
     * 活动的主键
     */
    @NotNull
    @Min(1)
    private Integer id;
}
