package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @author changle
 * @date 2020/7/21 2:19 下午
 */
@Data
public class RebateRankingParam {
    /**分销员ID*/
    public Integer userId;
    /**0:全网排名；1：当前等级排名*/
    public Integer nav;
}
