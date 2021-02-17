package com.meidianyi.shop.service.pojo.shop.sms.recharge;

import lombok.Data;

import java.util.List;

/**
 * 充值记录
 * @author 孔德成
 * @date 2020/7/27 15:04
 */
@Data
public class SmsAccountRechargeListVo {

    private Integer total;
    private Integer code;
    private Integer page;
    private Integer rows;
    private Integer version;
    private String sid;

    private List<SmsRechargeRecordVo> data;
}
