package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @Author 常乐
 * @Date 2020-05-20
 */
@Data
public class RebateOrderListVo {
    /**分销员昵称*/
    private String username;
    /**分销员头像*/
    private String userAvatar;
    /**分销员ID*/
    private Integer fanliUserId;
    /**返利金额*/
    private BigDecimal fanliMoney;
    /**返利时间*/
    private Timestamp finishedTime;
}
