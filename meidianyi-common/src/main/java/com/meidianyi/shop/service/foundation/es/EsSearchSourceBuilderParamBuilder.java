package com.meidianyi.shop.service.foundation.es;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;

/**
 * @author luguangyao
 */
public final class EsSearchSourceBuilderParamBuilder {
    private EsSearchSourceBuilderParam esSearchSourceBuilderParam;

    private EsSearchSourceBuilderParamBuilder() {
        esSearchSourceBuilderParam = new EsSearchSourceBuilderParam();
    }

    public static EsSearchSourceBuilderParamBuilder builder() {
        return new EsSearchSourceBuilderParamBuilder();
    }

    public EsSearchSourceBuilderParamBuilder indexName(String indexName) {
        esSearchSourceBuilderParam.setIndexName(indexName);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder queryBuilder(QueryBuilder queryBuilder) {
        esSearchSourceBuilderParam.setQueryBuilder(queryBuilder);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder aggregations(List<AggregationBuilder> aggregations) {
        esSearchSourceBuilderParam.setAggregations(aggregations);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder includeSource(String[] includeSource) {
        esSearchSourceBuilderParam.setIncludeSource(includeSource);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder excludeSource(String[] excludeSource) {
        esSearchSourceBuilderParam.setExcludeSource(excludeSource);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder from(Integer from) {
        esSearchSourceBuilderParam.setFrom(from);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder size(Integer size) {
        esSearchSourceBuilderParam.setSize(size);
        return this;
    }

    public EsSearchSourceBuilderParamBuilder but() {
        return builder().indexName(esSearchSourceBuilderParam.getIndexName()).queryBuilder(esSearchSourceBuilderParam.getQueryBuilder()).aggregations(esSearchSourceBuilderParam.getAggregations()).includeSource(esSearchSourceBuilderParam.getIncludeSource()).excludeSource(esSearchSourceBuilderParam.getExcludeSource()).from(esSearchSourceBuilderParam.getFrom()).size(esSearchSourceBuilderParam.getSize());
    }

    public EsSearchSourceBuilderParam build() {
        return esSearchSourceBuilderParam;
    }
}
