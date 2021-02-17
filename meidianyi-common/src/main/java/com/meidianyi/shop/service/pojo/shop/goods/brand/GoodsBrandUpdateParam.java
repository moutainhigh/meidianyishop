package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年11月13日
 */
@Data
public class GoodsBrandUpdateParam {
    private Integer id;
    private String brandName;
    private String logo;
    private Byte first;
    private String eName;
    private Integer classifyId;
    private Byte isRecommend;
    private List<Integer> goodsIds;
    private List<Integer> oldGoodsIds;
}
