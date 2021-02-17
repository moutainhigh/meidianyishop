package com.meidianyi.shop.service.pojo.wxapp.distribution.withdraw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author panjing
 * @date 2020/7/2 15:24
 */
@Data
public class SingleWithdrawRecordVo {
    /**
     * 提现金额
     */
    private BigDecimal withdrawCash;
    /**
     * 处理状态
     */
    private Byte status;
    /**
     * 审核时间
     */
    private Timestamp checkTime;
    /**
     * 驳回原因
     */
    private String refuseDesc;
    /**
     * 驳回时间
     */
    @JsonIgnore
    private Timestamp refuseTime;
    /**
     * 出账时间
     */
    @JsonIgnore
    private Timestamp billingTime;
    /**
     * 失败时间
     */
    @JsonIgnore
    private Timestamp failTime;
    /**
     * 备注时间
     */
    @JsonIgnore
    private Timestamp descTime;
    /**
     * 创建时间
     */
    @JsonIgnore
    private Timestamp createTime;
}