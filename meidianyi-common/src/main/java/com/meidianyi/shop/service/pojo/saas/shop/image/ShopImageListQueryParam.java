package com.meidianyi.shop.service.pojo.saas.shop.image;

/**
 * @author 孔德成
 * @date 2019/7/17 10:03
 */
public class ShopImageListQueryParam {

    /**
     * 页码
     */
    public Integer page;
    /**
     * 分组id
     */
    public Integer imgCatId;
    /**
     * 关键词
     */
    public String keywords;
    /**
     * 每页显示的记录数
     */
    public Integer pageRows;
    /**
     * 更多需求 1.开启 0关闭
     */
    public Integer searchNeed;
    /**
     * 宽度要求 （更多）
     */
    public Integer needImgWidth;
    /**
     * 高度需求（更多）
     */
    public Integer needImgHeight;
    /**
     * 排序
     */
    public Integer uploadSortId;
}
