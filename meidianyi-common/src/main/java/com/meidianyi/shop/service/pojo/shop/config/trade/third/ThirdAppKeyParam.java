package com.meidianyi.shop.service.pojo.shop.config.trade.third;

import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2020/5/18
 */
@Getter
@Setter
@ToString
public class ThirdAppKeyParam {
    @NotNull
    private Integer id;
    @NotNull
    private Byte action;
    @NotNull
    private String appKey;
    private String appSecret;

}
