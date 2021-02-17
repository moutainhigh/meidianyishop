package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

/**
 * @Author 常乐
 * @Date 2020-04-07
 */
@Data
public class GoodsRebateConfigParam {
    /**分销赠送优惠券*/
    private String couponIds;
    /**分销改价后价格*/
    private Map<Integer,BigDecimal> rebatePrice;
    /**分销改价时间*/
    private Long rebateTime;
}
