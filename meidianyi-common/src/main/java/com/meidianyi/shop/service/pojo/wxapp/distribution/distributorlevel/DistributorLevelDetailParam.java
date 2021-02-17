package com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel;

import lombok.Data;

/**
 * @author changle
 * @date 2020/8/17 2:47 下午
 */
@Data
public class DistributorLevelDetailParam {
    /**分销等级ID*/
    private Byte levelId;
    /**分销员ID*/
    private Integer userId;
}
