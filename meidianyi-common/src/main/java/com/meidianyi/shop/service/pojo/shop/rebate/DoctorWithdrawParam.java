package com.meidianyi.shop.service.pojo.shop.rebate;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Data
public class DoctorWithdrawParam {
    /**
     * 医师id
     */
    private Integer doctorId;
    /**
     * 提现类型
     */
    private Byte type;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 提现单号
     */
    private String orderSn;
    /**
     * 用户提现序号
     */
    private String     withdrawUserNum;
    /**
     * 流水号
     */
    private String     withdrawNum;
    /**
     * 提现金额
     */
    private BigDecimal withdrawCash;
    /**
     * 可提现金额
     */
    private BigDecimal withdraw;
    /**
     * 备注
     */
    private String     desc;
    /**
     * 驳回原因
     */
    private String     refuseDesc;
    /**
     * 申请时提现配置
     */
    private String     withdrawSource;
    /**
     * 真实姓名
     */
    private String     realName;
    private String clientIp;
}
