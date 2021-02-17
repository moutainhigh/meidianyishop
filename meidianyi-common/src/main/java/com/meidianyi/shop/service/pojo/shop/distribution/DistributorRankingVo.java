package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-03-13
 */
@Data
public class DistributorRankingVo {
    /**
     * 头像
     */
    private String userImg;
    /**
     * 分销员名称
     */
    private String distributorName;
    /**
     * 总返利
     */
    private Double totalRebate;
}
