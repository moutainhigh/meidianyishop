package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 商品标签分页数据
 * @author 李晓冰
 * @date 2019年11月27日
 */
@Getter
@Setter
public class GoodsLabelPageListVo extends GoodsLabelBase{
    private Short listPattern;
    private Integer goodsNum;
    private Timestamp updateTime;
}
