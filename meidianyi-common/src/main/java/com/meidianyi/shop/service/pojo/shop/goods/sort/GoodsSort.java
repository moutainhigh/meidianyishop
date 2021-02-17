package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Data;

/**
 * 商品分类基类-admin端使用
 * @author 李晓冰
 * @date 2019年11月22日
 */
@Data
public class GoodsSort {
    protected Integer sortId;
    protected String sortName;
    protected Integer parentId;
    protected Short level;
    protected Short first;
    protected String sortImg;
    protected String imgLink;
}
