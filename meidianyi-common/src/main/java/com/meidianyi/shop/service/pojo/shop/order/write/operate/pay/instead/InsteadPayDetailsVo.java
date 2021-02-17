package com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 王帅
 * 代付明细
 */
@Getter
@Setter
@ToString
public class InsteadPayDetailsVo {
    private String subOrderSn;
    private String mainOrderSn;
    private Integer userId;
    private String username;
    private Byte orderStatus;
    private BigDecimal moneyPaid;
    private BigDecimal refundMoney;
    private String message;
    private String userAvatar;
    private String paySn;
    private Timestamp payTime;
    private Timestamp refundTime;

}
