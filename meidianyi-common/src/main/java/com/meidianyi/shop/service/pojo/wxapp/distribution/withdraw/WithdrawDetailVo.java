package com.meidianyi.shop.service.pojo.wxapp.distribution.withdraw;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author changle
 * @date 2020/7/1 3:56 下午
 */
@Data
public class WithdrawDetailVo {
    /** 是否绑定手机号*/
    private Integer isBindMobile;
    private String realName;
    private Byte withdrawStatus;
    private Byte withdrawCash;
    /** 可提现金额*/
    private BigDecimal canWithdraw;
    private String withdrawSource;
    private String nickName;
    private Byte isPublicUser;


}
