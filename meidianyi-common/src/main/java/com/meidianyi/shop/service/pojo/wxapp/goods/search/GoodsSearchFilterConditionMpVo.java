package com.meidianyi.shop.service.pojo.wxapp.goods.search;

import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpPinYinVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goodssort.GoodsSearchSortMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.label.GoodsLabelMpVo;
import lombok.Data;

import java.util.List;

/**
 * 小程序商品搜索界面-可使用搜索条件内容
 * @author 李晓冰
 * @date 2019年12月09日
 */
@Data
public class GoodsSearchFilterConditionMpVo {

    /**商家分类筛选条件*/
    private List<GoodsSearchSortMpVo> sorts;

    /**商家分类筛选条件默认展示项*/
    private List<GoodsSearchSortMpVo> showSorts;

    /**商家品牌筛选条件*/
    private List<GoodsBrandMpPinYinVo> goodsBrands;

    /**商品活动筛选条件*/
    private List<Integer> activityTypes;

    private List<GoodsLabelMpVo> goodsLabels;
}
