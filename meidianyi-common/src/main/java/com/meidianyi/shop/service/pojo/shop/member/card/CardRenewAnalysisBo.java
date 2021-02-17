package com.meidianyi.shop.service.pojo.shop.member.card;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author: 王兵兵
 * @create: 2020-05-18 14:36
 **/
@Getter
@Setter
public class CardRenewAnalysisBo {
    private Date createTime;
    private String renewOrderSn;
    private Integer userId;
    private BigDecimal moneyPaid;
    private BigDecimal useAccount;
    private BigDecimal memberCardRedunce;
}
