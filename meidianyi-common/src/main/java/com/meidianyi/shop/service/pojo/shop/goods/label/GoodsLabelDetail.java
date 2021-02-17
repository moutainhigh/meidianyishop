package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商品标签详细信息类，包含直接关联的商品，平台，商家分类id信息
 * @author 李晓冰
 * @date 2019年11月27日
 */
@Getter
@Setter
public class GoodsLabelDetail extends GoodsLabelBase{
    public static final Byte NONE_GOODS = 1;
    protected Short listPattern;
    protected Byte isAll;
    /**是否不指定任何商品*/
    protected Byte isNone;

    protected List<Integer> goodsIds;
    protected List<Integer> sortIds;
    protected List<Integer> catIds;
}
