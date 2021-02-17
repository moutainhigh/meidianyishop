package com.meidianyi.shop.service.pojo.shop.order.virtual;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-22 11:56
 **/
@Data
public class VirtualOrderPayInfo {

    private String orderSn;

    private Integer userId;

    private String payCode;

    /** 支付流水号 */
    private String paySn;

    private BigDecimal moneyPaid;

    private BigDecimal useAccount;

    private BigDecimal memberCardBalance;

    private Integer useScore;

    private BigDecimal orderAmount;

    private Timestamp payTime;

    private Byte returnFlag;

    private BigDecimal returnMoney;

    private BigDecimal returnAccount;

    private BigDecimal returnCardBalance;

    private Integer returnScore;

    private String cardNo;

    private Timestamp returnTime;

}
