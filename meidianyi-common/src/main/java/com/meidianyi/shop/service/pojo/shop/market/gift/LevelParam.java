package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 优先级入参
 *
 * @author 郑保乐
 */
@Data
public class LevelParam {

    /** 活动id **/
    @NotNull
    private Integer id;
    /** 优先级 **/
    @NotNull
    private Short level;
}
