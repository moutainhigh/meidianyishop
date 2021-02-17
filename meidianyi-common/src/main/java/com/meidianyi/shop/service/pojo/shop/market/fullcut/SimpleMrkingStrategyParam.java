package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: 王兵兵
 * @create: 2019-08-12 16:10
 **/
@Data
public class SimpleMrkingStrategyParam {

    /** 活动主键 */
    @NotNull
    private Integer id;
}
