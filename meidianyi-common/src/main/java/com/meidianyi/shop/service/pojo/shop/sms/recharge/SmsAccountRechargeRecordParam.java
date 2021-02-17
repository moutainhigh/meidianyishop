package com.meidianyi.shop.service.pojo.shop.sms.recharge;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.util.Page;
import lombok.Data;

/**
 * 短信充值记录
 * @author 孔德成
 * @date 2020/7/27 14:54
 */
@Data
public class SmsAccountRechargeRecordParam {

    /**
     * 账户id
     */
    private String sid;
    /**
     * 版本毕传
     */
    private Integer version=4;
    /**
     * 时间不传 默认三个月
     */
    @JsonProperty(value = "start_time")
    private String startCreateTime;
    @JsonProperty(value = "end_time")
    private String endCreateTime;

    private Integer page = Page.DEFAULT_CURRENT_PAGE;
    private Integer rows = Page.DEFAULT_PAGE_ROWS;


}
