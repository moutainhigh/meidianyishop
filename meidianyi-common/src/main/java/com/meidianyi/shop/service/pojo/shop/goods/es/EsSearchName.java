package com.meidianyi.shop.service.pojo.shop.goods.es;

import java.math.BigDecimal;

/**
 * es和goods 字段映射关系
 * @author 卢光耀
 * @date 2019/11/5 5:37 下午
 *
*/

public interface EsSearchName {
    /**
     * 空
     */
    String NULL = "null" ;
    /**
     * 商品名称
     */
    String GOODS_NAME = "goods_name" ;
    String GOODS_ID = "goods_id" ;
    String SHOP_ID = "shop_id" ;
    String GOODS_AD = "goods_ad" ;
    String GOODS_DESC = "goods_descs" ;
    String GOODS_SN = "goods_sn" ;
    String CAT_ID = "cat_id" ;
    String GOODS_IMG = "goods_img" ;
    String UNIT = "unit" ;
    String SORT_ID = "sort_id" ;
    String BRAND_ID = "brand_id" ;
    String MARKET_PRICE = "market_price" ;
    String LIMIT_BUY_NUM = "limit_buy_num" ;
    String LIMIT_MAX_NUM = "limit_max_num" ;
    String ADD_SALE_NUM = "add_sale_num" ;
    String GOODS_WEIGHT = "goods_weight" ;
    String IS_CARD_EXCLUSIVE = "is_card_exclusive" ;
    String IS_ON_SALE = "is_on_sale" ;
    String SALE_TYPE = "sale_type" ;
    String IS_PAGE_UP = "is_page_up" ;
    String GOODS_PAGE_ID = "goods_page_id" ;
    String GOODS_NUMBER = "goods_number" ;
    String SHOP_PRICE = "shop_price" ;
    String GOODS_TYPE = "goods_type" ;
    String GOODS_SALE_NUM = "goods_sale_num" ;
    String GOODS_COLLECT_NUM = "goods_collect_num" ;
    String SUB_ACCOUNT_ID = "sub_account_id" ;
    String STATE = "state" ;
    String COST_PRICE = "cost_price" ;
    String SOURCE = "source" ;
    String IS_CONTROL_PRICE = "is_control_price" ;
    String PV = "pv" ;
    String COMMENT_NUM = "comment_num" ;
    String BASE_SALE = "base_sale" ;
    String V1 = "v1" ;
    String V2 = "v2" ;
    String V3 = "v3" ;
    String V4 = "v4" ;
    String V5 = "v5" ;
    String V6 = "v6" ;
    String V7 = "v7" ;
    String V8 = "v8" ;
    String V9 = "v9" ;
    String SHOW_PRICE = "show_price" ;
    String PRD_SNS = "prd_sns" ;
    String CAT_NAME = "cat_name" ;
    String FIRST_CAT_ID = "first_cat_id" ;
    String SECOND_CAT_ID = "second_cat_id" ;
    String THIRD_CAT_ID = "third_cat_id" ;
    String FULL_CAT_ID = "full_cat_id" ;
    String SORT_NAME = "sort_name" ;
    String FIRST_SORT_ID = "first_sort_id" ;
    String SECOND_SORT_ID = "second_sort_id" ;
    String FULL_SORT_ID = "full_sort_id" ;
    String BRAND_NAME = "brand_name" ;
    String GOODS_LABEL = "goods_label" ;
    String MAX_SPEC_PRD_PRICE = "max_spec_prd_price" ;
    String MIN_SPEC_PRD_PRICE = "min_spec_prd_price" ;
    String UPDATE_TIME = "update_time" ;
    String ADD_ES_TIME = "add_es_time";
    String FREIGHT_TEMPLATE_ID = "freight_template_id";


    String PRD_JSON = "prd_json";
    String VIDEO_INFO_JSON = "video_info_json";
    String DEFAULT_PRD = "default_prd";
    String SECONDARY_GOODS_IMAGES = "secondary_goods_images";
    String CAN_REBATE = "can_rebate";
    String CREATE_TIME = "create_time";
    String SALE_TIME = "sale_time";
    String TOTAL_SALE_NUMBER = "total_sale_number";
    String ROOM_ID = "room_id";
    String PRDS = "prds";
    String GRADES = "grades";

     interface Prd {
        String PRD_ID = "prd_id";

        String PRD_NUMBER = "prd_number";

        String PRD_WEIGHT = "prd_weight";

        String PRD_REAL_PRICE = "prd_real_price";

        String PRD_LINE_PRICE = "prd_line_price";

        String PRD_SPECS = "prd_specs";

        String PRD_DESC = "prd_desc";

        String PRD_IMG = "prd_img";

        String PRD_CODES = "prd_codes";

         String PRD_SN = "prd_sn";

    }
    interface Grade {
        String PRD_ID = "prd_id";
        String GRADE_PRICE = "grade_price";
        String GRADE = "grade";
    }






    /**单纯搜索使用，es中无此字段*/
    String KEY_WORDS = "key_words";




}
