package com.meidianyi.shop.service.pojo.shop.market.increasepurchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liufei
 * @date 2019/8/15
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionGoodsInfo {
    private Integer goodsId;
    private String goodsName;
    private String goodsImg;
    private Integer goodsNumber;

}
