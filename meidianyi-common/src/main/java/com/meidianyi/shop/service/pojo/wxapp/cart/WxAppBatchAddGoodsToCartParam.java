package com.meidianyi.shop.service.pojo.wxapp.cart;

import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年07月23日
 */
@Data
public class WxAppBatchAddGoodsToCartParam {
    private List<WxAppAddGoodsToCartParam> wxAppAddGoodsToCartParams;
}
