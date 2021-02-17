package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.meidianyi.shop.service.pojo.saas.category.SysCategorySelectTreeVo;
import com.meidianyi.shop.service.pojo.shop.goods.brand.GoodsBrandSelectListVo;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelSelectListVo;
import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortSelectTreeVo;
import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年11月22日
 */
@Data
public class GoodsFilterItemInitVo {
    private List<GoodsSortSelectTreeVo> goodsSorts;

    private List<GoodsLabelSelectListVo> goodsLabels;

    private List<GoodsBrandSelectListVo> goodsBrands;

    private List<SysCategorySelectTreeVo> goodsCategories;
}
