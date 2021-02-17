package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author: 王兵兵
 * @create: 2020-05-19 14:45
 **/
@Getter
@Setter
public class CardChargeAnalysisBo {
    private Date createTime;
    private String orderSn;
    private Integer userId;
    private BigDecimal charge;
    private BigDecimal returnMoney;
}
