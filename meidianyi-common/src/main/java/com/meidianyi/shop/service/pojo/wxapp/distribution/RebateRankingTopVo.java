package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 常乐
 * @Date 2020-05-20
 */
@Data
public class RebateRankingTopVo {
    /**分销员ID*/
    private Integer userId;
    /**分销员昵称*/
    private String username;
    /**分销员头像*/
    private String userAvatar;
    /**获得总返利金额*/
    private BigDecimal finalMoney;
}
