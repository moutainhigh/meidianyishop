package com.meidianyi.shop.service.pojo.shop.goods.label;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年09月18日
 */
@Data
public class GoodsLabelsMapParam {
    private Integer goodsId;
    private List<Integer> labelIds;
}
