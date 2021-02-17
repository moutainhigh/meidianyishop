package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Data;

/**
 * 商品标签基类
 * @author 李晓冰
 * @date 2019年11月27日
 */
@Data
public class GoodsLabelBase {
    protected Integer id;
    protected String name;
    protected Byte goodsDetail;
    protected Byte goodsList;
    protected Byte goodsSelect;
    protected Short level;
    protected Byte isChronic;
}
