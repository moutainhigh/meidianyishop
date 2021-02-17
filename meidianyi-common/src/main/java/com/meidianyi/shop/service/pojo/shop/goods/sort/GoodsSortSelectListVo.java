package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Data;

/**
 * 商品分类下拉列表vo,不需要组织成树结构
 * @author 李晓冰
 * @date 2019年11月20日
 */
@Data
public class GoodsSortSelectListVo{
    private Integer sortId;
    private String sortName;
}
