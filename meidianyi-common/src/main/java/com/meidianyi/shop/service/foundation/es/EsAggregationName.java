package com.meidianyi.shop.service.foundation.es;

import com.meidianyi.shop.service.pojo.shop.goods.es.EsLabelName;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;

/**
 * @author luguangyao
 */
public interface EsAggregationName {

    String LABEL_NAME = "label_info";
    /**
     * default goods label aggregation return data
     */
    String[] LABEL_AGGREGATION_SOURCE = {EsLabelName.NAME,EsLabelName.ID,EsLabelName.LIST_PATTERN,EsLabelName.TYPE};



    String SORT_NAME = "sort_info";
    /**
     * default goods sort aggregation return data
     */
    String[] SORT_AGGREGATION_SOURCE = {EsSearchName.SORT_NAME,EsSearchName.FIRST_SORT_ID,EsSearchName.SECOND_SORT_ID};



}
