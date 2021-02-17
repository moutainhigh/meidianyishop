package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Data;

/**
 * 商品推荐分类配置信息修改param
 * @author 李晓冰
 * @date 2019年10月08日
 */
@Data
public class GoodsRecommendSortConfig {
    /** 是否开启推荐分类 0关闭 1开启*/
    public static final Integer SHOW_RECOMMEND_SORT = 1;

    private Integer recommendSortStatus = 0;
    /** 推荐分类全路径 */
    private String recommendSortImg;
    /** 推荐分类相对路径*/
    private String recommendSortImgPath;
    private String recommendImgLink;
}
