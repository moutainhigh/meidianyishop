package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 地址商品邮费
 * @author 孔德成
 * @date 2020/6/9 16:20
 */
@Getter
@Setter
@ToString
public class  AddressGoodsShippingParam {

    /**
     * 地址id
     */
    @NotNull
    private Integer addressId;
    /**
     * 商品id
     */
    @NotNull
    private Integer goodsId;
    /**
     * 模板id
     */
    @NotNull
    private Integer deliverTemplateId;
    @NotNull
    private Integer goodsNum;
    /**
     *商品价格
     */
    @NotNull
    private BigDecimal goodsPrice;
    /**
     * 重量
     */
    @NotNull
    private BigDecimal goodsWeight;


    private Integer userId;
}
