package com.meidianyi.shop.service.foundation.es.pojo.goods;

/**
 * Es常量类
 * @author 卢光耀
 * @date 2019/10/10 10:11 上午
 *
*/
public class EsGoodsConstant {
    /**
     * es商品索引名称前缀
     */
    public static final String GOODS_INDEX_NAME_PREFIX = "es_goods_";
    /**
     * es商品标签索引名称前缀
     */
    public static final String LABEL_INDEX_NAME_PREFIX = "es_goods_label_";
    /**
     * es商品规格索引名称前缀
     */
    public static final String PRODUCT_INDEX_NAME_PREFIX = "es_goods_product_";

    /**
     * es商品索引别名
     */
    public static final String GOODS_ALIA_NAME = "vpu_goods";
    /**
     * es商品标签索引别名
     */
    public static final String LABEL_ALIA_NAME = "vpu_label";
    /**
     * es商品规格索引别名
     */
    public static final String PRODUCT_ALIA_NAME = "vpu_product";


    /**通用页*/
    public static final Byte GENERAL_PAGE = 0;
    /**小程序-商品详情页*/
    public static final Byte GOODS_DETAIL_PAGE = 1;
    /**小程序-商品列表页*/
    public static final Byte GOODS_LIST_PAGE = 2;
    /**小程序-商品筛选页*/
    public static final Byte GOODS_SEARCH_PAGE = 3;
    /**Admin-商品列表页*/
    public static final Byte ADMIN_GOODS_LIST_PAGE = 4;

    public static class EsGoodsSearchFact{
        public static final String GOODS_BRAND_FACT = "brand_id";

        public static final String GOODS_CATEGORY_FIRST_FACT = "first_cat_id";
        public static final String GOODS_CATEGORY_SECOND_FACT = "second_cat_id";
        public static final String GOODS_CATEGORY_THIRD_FACT = "third_cat_id";


        public static final String GOODS_SORT_FIRST_FACT = "first_sort_id";
        public static final String GOODS_SORT_SECOND_FACT = "second_sort_id";

        public static final String GOODS_LABEL_FACT = "goods_label";
    }

    public static class EsGoodsShowPriceReducePeriodAction{
        public static final Byte EVERY_DAY = 1;

        public static final Byte EVERY_MONTH = 2;

        public static final Byte EVERY_WEEK = 3;



    }
}
