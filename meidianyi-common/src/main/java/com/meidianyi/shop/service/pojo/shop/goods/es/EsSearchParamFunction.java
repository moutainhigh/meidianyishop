package com.meidianyi.shop.service.pojo.shop.goods.es;


import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;

/**
 * @author luguangyao
 */
@FunctionalInterface
public interface EsSearchParamFunction {

    /**
     * buildQueryBuilder
     *
     * @param searchList
     * @return
     */
    QueryBuilder buildQueryBuilder(List<FieldProperty> searchList);


}
