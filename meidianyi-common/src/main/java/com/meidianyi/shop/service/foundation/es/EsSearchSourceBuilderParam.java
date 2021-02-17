package com.meidianyi.shop.service.foundation.es;

import lombok.Builder;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;

/**
 * 封装ElasticSearch的SearchSourceBuilder所需参数
 * @author 卢光耀
 * @date 2019/11/12 2:08 下午
*/
@Data
public class EsSearchSourceBuilderParam {

    private String indexName;

    private QueryBuilder queryBuilder;

    private List<AggregationBuilder> aggregations;

    private String[] includeSource;

    private String[] excludeSource;

    private Integer from;

    private Integer size;

}
