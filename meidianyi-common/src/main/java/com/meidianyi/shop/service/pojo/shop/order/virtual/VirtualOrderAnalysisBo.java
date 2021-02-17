package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author: 王兵兵
 * @create: 2020-05-15 17:17
 **/
@Getter
@Setter
public class VirtualOrderAnalysisBo {
    private Date createTime;
    private String orderSn;
    private Integer userId;
    private BigDecimal orderAmount;
    private BigDecimal returnMoney;
    private BigDecimal returnAccount;
    private BigDecimal returnCardBalance;
}
