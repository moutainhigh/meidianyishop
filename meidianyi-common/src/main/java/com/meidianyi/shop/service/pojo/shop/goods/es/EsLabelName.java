package com.meidianyi.shop.service.pojo.shop.goods.es;

/**
 * ElasticSearch中商品标签索引和项目中字段的对应关系
 * @author 卢光耀
 * @date 2019/11/22 11:28 上午
 *
*/
public interface EsLabelName {

    String ID = "id" ;
    String GOODS_ID ="goods_id";
    String SHOP_ID = "shop_id" ;
    String TYPE = "type" ;
    String TYPE_ID = "type_id" ;
    String CREATE_TIME = "create_time" ;
    String LEVEL = "level" ;
    String DETAIL_SHOW = "detail_show" ;
    String SEARCH_SHOW = "search_show" ;
    String LIST_SHOW = "list_show" ;
    String NAME = "name";
    String LIST_PATTERN="list_pattern";

}
