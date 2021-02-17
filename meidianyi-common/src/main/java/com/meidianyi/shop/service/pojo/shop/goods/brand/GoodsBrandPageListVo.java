package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 商品品牌分页vo
 * @author 李晓冰
 * @date 2019年11月13日
 */
@Setter
@Getter
public class GoodsBrandPageListVo extends GoodsBrandBase{
    private String classifyName;
    private Integer goodsNum;
    private Byte classifyFirst;
    private Timestamp createTime;
    /** 是否推荐分类 0否1是*/
    private Byte isRecommend;
}
