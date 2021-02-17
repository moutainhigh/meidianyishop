package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Data;

import java.util.List;

/**
 * 商品推荐分类详情vo
 * @author 李晓冰
 * @date 2019年11月22日
 */
@Data
public class GoodsRecommendSortDetailVo {
    private Integer sortId;
    private String sortName;
    private Short first;
    private List<GoodsRecommendSortChild> children;

    @Data
    public static class GoodsRecommendSortChild{
        private Integer sortId;
        private Integer parentId;
        private String sortName;
        private String sortImg;
        private String sortImgUrl;
        private String imgLink;
    }
}
