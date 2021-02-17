package com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel;

import lombok.Data;

/**
 * @author changle
 * @date 2020/8/17 9:42 上午
 */
@Data
public class DistributorLevelRecordParam {
    /**分销员ID*/
    private Integer userId;
    /**当前页*/
    private Integer currentPage = 0;
    /**每页展示条数*/
    private Integer pageRows = 20;
}
