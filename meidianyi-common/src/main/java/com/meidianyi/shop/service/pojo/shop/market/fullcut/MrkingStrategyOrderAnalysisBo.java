package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2020-05-12 09:55
 **/
@Getter
@Setter
public class MrkingStrategyOrderAnalysisBo {
    private Timestamp createTime;
    private Date date;
    private String orderSn;
    private Integer userId;
    private BigDecimal perDiscount;
    private BigDecimal discountedTotalPrice;
    private Integer goodsNumber;
}
