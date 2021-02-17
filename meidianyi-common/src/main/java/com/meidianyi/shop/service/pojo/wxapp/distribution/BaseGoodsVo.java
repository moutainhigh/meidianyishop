package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-04-06
 */
@Data
public class BaseGoodsVo {
    /**
     * 商品ID
     */
    private Integer goodsId;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片
     */
    private String goodsImg;
}
