package com.meidianyi.shop.service.pojo.shop.goods.sort;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 商品分类树形下拉框vo
 * @author 李晓冰
 * @date 2019年11月22日
 */
@Data
public class GoodsSortSelectTreeVo {
    protected Integer sortId;
    protected String sortName;
    protected Integer parentId;
    private Byte hasChild;
    private Byte level;
    /**当前分类所包含的商品数量*/
    @JsonIgnore
    private Integer goodsNum = 0;
    /**当前分类所及所有子分类包含的商品数量*/
    private Integer goodsSumNum = 0;

    @JsonIgnore
    /**查询带商品数量的分类时排序使用*/
    private Byte first;
    @JsonIgnore
    /**同上*/
    private Timestamp createTime;
}
