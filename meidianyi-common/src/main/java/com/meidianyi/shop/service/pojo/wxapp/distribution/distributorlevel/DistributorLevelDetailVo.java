package com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author changle
 * @date 2020/8/17 2:47 下午
 */
@Data
public class DistributorLevelDetailVo {
    /**分销员昵称*/
    private String username;
    /**分销员头像*/
    private String userAvator;
    /**当前等级*/
    private String levelName;
    /**下一等级名称*/
    private String nextLevelName;
    /**是否是最高等级 0:否；1：是*/
    private Byte isHigtLevel;
    /**累计邀请用户数*/
    private Integer nextNumber;
    /**累计推广金*/
    private BigDecimal rebateMoney;
    /**累计推广金与消费金额*/
    private BigDecimal rebateAndPayMoney;
    /**下一等级累计邀请用户数*/
    private Integer toNextNumber;
    /**下一等级累计推广金*/
    private BigDecimal toRebateMoney;
    /**下一等级累计推广金与消费金额*/
    private BigDecimal toRebateAndPayMoney;
}
