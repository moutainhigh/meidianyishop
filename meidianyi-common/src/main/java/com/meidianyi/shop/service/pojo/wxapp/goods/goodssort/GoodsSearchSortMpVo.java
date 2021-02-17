package com.meidianyi.shop.service.pojo.wxapp.goods.goodssort;

import lombok.Data;

import java.util.List;

/**
 * 小程序-商品搜索页面-商家分类筛选内容
 * @author 李晓冰
 * @date 2019年12月09日
 */
public class GoodsSearchSortMpVo {
    /**商家分类id*/
    private Integer sortId;
    /**商家分类名称*/
    private String sortName;
    /**关联商品数量*/
    private Long goodsNum;

    private List<? extends GoodsSearchSortMpVo> children;

    public GoodsSearchSortMpVo(){}
    public GoodsSearchSortMpVo(Integer sortId,String sortName){
        this.sortId = sortId;
        this.sortName = sortName;
    }
    public GoodsSearchSortMpVo(Integer sortId,String sortName,Long goodsNum){
        this.sortId = sortId;
        this.sortName = sortName;
        this.goodsNum = goodsNum;
    }
    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Long getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Long goodsNum) {
        this.goodsNum = goodsNum;
    }

    public List<? extends GoodsSearchSortMpVo> getChildren() {
        return children;
    }

    public void setChildren(List<? extends GoodsSearchSortMpVo> children) {
        this.children = children;
    }
}
