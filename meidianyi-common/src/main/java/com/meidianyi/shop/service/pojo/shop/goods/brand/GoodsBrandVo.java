package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年11月13日
 */
@Setter
@Getter
public class GoodsBrandVo extends GoodsBrandBase{
    private String eName;
    private String fullUrlLogo;
    private Integer classifyId;
    private Byte isRecommend;
    private List<Integer> goodsIds;
}
