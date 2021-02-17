package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

/**
 * 筛选商品相关元素初始化类，元素可包含 标签，品牌，商家分类，平台分类
 * @author 李晓冰
 * @date 2019年11月22日
 */
@Data
public class GoodsFilterItemInitParam {
    /**是否需要查询商家，平台分类关联的商品数量（只会展示有数量的商品）*/
    private Boolean needGoodsNum;
    /**
     * 是否在售 1 在售（上架），0下架（仓库中）
     * 商品数量为0时属于售罄状态，但是依然可以为（上架状态）
     */
    private Byte isOnSale;
    /**
     * 1表示查询已售罄
     * 0表示未售罄
     */
    private Byte isSaleOut;
    /**查询类型：1 以商品为统计对象，2以商品规格为统计对象*/
    private Byte selectType;

    /**是否需要标签*/
    private Boolean needGoodsLabel;
    /**是否需要品牌*/
    private Boolean needGoodsBrand;
    /**是否需要商家分类*/
    private Boolean needGoodsSort;
    /**是否需要平台分类*/
    private Boolean needSysCategory;

    private Boolean isHasGoEs;
    /** 可以直接走ES*/
    public boolean canGoEs(){
        boolean catOrSort  = Boolean.TRUE.equals(needGoodsSort) || Boolean.TRUE.equals(needSysCategory);
        if (Boolean.TRUE.equals(needGoodsNum) && catOrSort) {
            return true;
        } else {
            return false;
        }
    }
}
