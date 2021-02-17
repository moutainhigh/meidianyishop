package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

/**
 * 分销推荐商品入参 - 自定义模式
 * @author panjing
 * @date 2020/7/14 8:58
 */
@Data
public class DistributionRecommendGoodsParam {
    /**
     * 推荐商品id集合，以,分隔：1,2,3,4
     */
    private String recommendGoodsId;
    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
}
