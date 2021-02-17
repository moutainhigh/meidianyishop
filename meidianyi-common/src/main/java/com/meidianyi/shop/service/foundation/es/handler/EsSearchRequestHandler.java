package com.meidianyi.shop.service.foundation.es.handler;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;


/**
 * ElasticSearch SearchRequest Handler
 * @author 卢光耀
 * @date 2019/11/12 4:12 下午
 *
*/
@Component
public class EsSearchRequestHandler {
    /**
     * admin平台商品搜索
     * @param indexName 搜索条件
     * @param sourceBuilder  聚合条件
     * @return SearchResponse
     */
    public SearchRequest assemblySearchRequest(String indexName,SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        return searchRequest.source(sourceBuilder);
    }
}
