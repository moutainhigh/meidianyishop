package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年07月27日
 */
@Data
public class GoodsBrandConfig {
    /**
     * 展示全部品牌
     */
    public static final Integer SHOW_ALL_BRAND = 1;
    /**
     * 按品牌展示推荐品牌,0不显示推荐分类
     */
    public static final Integer SHOW_RECOMMEND_LIST = 2;
    /**
     * 按分类展示推荐品牌
     */
    public static final Integer SHOW_RECOMMEND_CLASSIFY = 3;

    private Integer showAllBrand = 0;

    private String recommendTitle = null;

    private Integer showRecommendBrandType = 0;
}
