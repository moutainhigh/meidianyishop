package com.meidianyi.shop.service.pojo.saas.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 平台分类树形下拉框vo
 * @author 李晓冰
 * @date 2019年11月25日
 */
@Data
public class SysCategorySelectTreeVo {
    private Integer catId;
    private String catName;
    private Integer parentId;
    private Short level;
    private Integer hasChild;

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
