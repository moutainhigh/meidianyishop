package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-03-27 17:42
 **/
@Getter
@Setter
public class BargainGoodsPriceBo {

    /** 活动ID */
    private Integer    id;

    private Integer goodsId;

    private Integer first;
    private Timestamp createTime;

    private Byte bargainType;

    private BigDecimal expectationPrice;
    private BigDecimal floorPrice;
}
