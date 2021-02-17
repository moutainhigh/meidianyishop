package com.meidianyi.shop.service.pojo.shop.sms.recharge;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2020/7/27 15:06
 */
@Data
public class SmsRechargeRecordVo {

    private String rechargeTime;
    private Integer rechargeType;
    private String payNo;
    private BigDecimal price;
    private String remark;
    private Integer smsNum;

}
