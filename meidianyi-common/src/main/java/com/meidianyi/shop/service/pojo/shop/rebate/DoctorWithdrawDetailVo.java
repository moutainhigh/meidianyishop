package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.foundation.util.PageResult;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/9/8
 **/
@Data
public class DoctorWithdrawDetailVo {
    private Integer doctorId;
    private String doctorName;
    private String userName;
    private String mobile;
    private String realName;
    private Timestamp createTime;
    private String orderSn;
    private Byte type;
    /**
     * 用户提现序号
     */
    private String withdrawUserNum;
    /**
     * 流水号
     */
    private String withdrawNum;
    /**
     * 提现金额
     */
    private BigDecimal withdrawCash;
    private Timestamp checkTime;
    private Byte status;
    private String refuseDesc;
    private String desc;
    /**
     * 可提现金额
     */
    private BigDecimal withdraw;

    private Timestamp updateTime;
    private PageResult<DoctorWithdrawVo> withdrawList;
}
