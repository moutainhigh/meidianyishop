package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *
 * @author 孔德成
 * @date 2019/11/15 9:56
 */
@Data
public class AddressParam {
    /**
     * 微信地址
     */
    @NotNull
    public WxAddress wxAddress;
    /**
     * 商品id
     */
    private Integer goodsId;
    private Integer userId;
    private String lang;

}
