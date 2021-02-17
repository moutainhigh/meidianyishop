package com.meidianyi.shop.service.pojo.wxapp.goods.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 查询商品列表
 * @author 孔德成
 * @date 2020/1/9 16:34
 */
@Getter
@Setter
public class GoodsSearchParam extends BasePageParam {

    /**用户id，controller 层获取*/
    private Integer userId;

    /**搜索输入的关键字*/
    private String goodsName;
    /**
     * GOODS_AREA_TYPE_SECTION
     * 0范围 1全部
     */
    private Byte goodsAreaType;
    /**
     * 商品id
     */
    private List<Integer> goodsIds;
    /**商家分类id*/
    private List<Integer> sortIds;

    /**平台id集合*/
    private List<Integer> catIds;
    /**
     * 是否展示已售罄商品
     */
    private Boolean showSoldOut;
}
