package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * pos 同步数据
 * @author 孔德成
 * @date 2020/5/19
 */
@Getter
@Setter
@ToString
public class ThirdPosSyncParam {
    @NotNull
    private Integer id;
    /**
     *  sync_store 同步商店 2 sync_goods 同步商品
     */
    @NotNull
    private String action;
}
