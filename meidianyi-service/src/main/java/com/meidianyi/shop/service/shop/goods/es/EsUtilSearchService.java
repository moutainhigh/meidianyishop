package com.meidianyi.shop.service.shop.goods.es;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.es.EsManager;
import com.meidianyi.shop.service.foundation.es.EsSearchSourceBuilderParam;
import com.meidianyi.shop.service.foundation.es.EsSearchSourceBuilderParamBuilder;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchParam;
import com.meidianyi.shop.service.pojo.shop.goods.es.FieldProperty;
import com.meidianyi.shop.service.pojo.shop.goods.es.Operator;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 卢光耀
 * @date 2019/11/8 5:15 下午
 *
*/
@Service
public class EsUtilSearchService extends EsBaseSearchService{

    private static final String[] GOODS_LABEL_SOURCE = {
        EsSearchName.GOODS_ID,
        EsSearchName.FIRST_CAT_ID,
        EsSearchName.SECOND_CAT_ID,
        EsSearchName.THIRD_CAT_ID,
        EsSearchName.FIRST_SORT_ID,
        EsSearchName.SECOND_SORT_ID
    };

    @Autowired
    private EsManager esManager;
    @Autowired
    private OrderGoodsService orderGoodsService;

    public Set<Integer> getZhiXiaoGoodsNumbers() throws IOException {
        Set<Integer> result = Sets.newHashSet();
        List<FieldProperty> propertyList = new ArrayList<>();
        propertyList.add(new FieldProperty(EsSearchName.SHOP_ID,getShopId()));
        propertyList.add(new FieldProperty(EsSearchName.UPDATE_TIME, DateUtils.getBeforeLocalFor(30), Operator.LT));
        BoolQueryBuilder searchBuilder = assemblySearchBuilder(propertyList, EsSearchSource.ADMIN,Boolean.FALSE);
        EsSearchSourceBuilderParam param = EsSearchSourceBuilderParamBuilder.builder()
            .queryBuilder(searchBuilder)
            .build();
        SearchSourceBuilder sourceBuilder = assemblySearchSourceBuilder(param);
        SearchRequest searchRequest = new SearchRequest(EsGoodsConstant.GOODS_ALIA_NAME);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = esManager.searchResponse(searchRequest);
        List<Integer> ids = Arrays.stream(searchResponse.getHits().getHits())
            .map(x->x.getSourceAsMap())
            .map(x->Integer.parseInt(x.get(EsSearchName.GOODS_ID).toString()))
            .collect(Collectors.toList());
        List<Integer> buydIds = orderGoodsService.getZhixiaoGoodsIds();
        for(Integer id: buydIds) {
            if (ids.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    /**
     * 据商品标签的平台分类/商家分类推出受影响的商品以及他的所有商家和平台分类的ID
     * @param param 平台分类/商家分类 ID
     * @return 商品以及他的所有商家和平台分类的ID
     * @throws IOException 连接异常
     */
    public List<Map<String,Object>> getGoodsIdsByParam(EsSearchParam param) throws IOException {

        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        param.getSearchList().forEach(x->{
            if( x.getValue() instanceof List ){
                bool.must(QueryBuilders.termsQuery(x.getSearchName(),(List)x.getValue()));
            }else{
                bool.must(QueryBuilders.termQuery(x.getSearchName(),x.getValue()));
            }
        });
        EsSearchSourceBuilderParam sourceBuilderParam = EsSearchSourceBuilderParamBuilder.builder()
            .queryBuilder(bool)
            .build();
        SearchSourceBuilder sourceBuilder = assemblySearchSourceBuilder(sourceBuilderParam);
        sourceBuilder.fetchSource(GOODS_LABEL_SOURCE,null);
        sourceBuilder.size(400);
        SearchRequest searchRequest = new SearchRequest(EsGoodsConstant.GOODS_ALIA_NAME);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = esManager.searchResponse(searchRequest);
        SearchHit[] hits =searchResponse.getHits().getHits();
        List<Map<String,Object>> resultMap = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            Map<String, Object> child = new HashMap<>(3);
            Map<String, Object> responseMap = hit.getSourceAsMap();
            child.put(EsSearchName.GOODS_ID, responseMap.get(EsSearchName.GOODS_ID));
            List<Integer> sortIds = Lists.newArrayList();
            List<Integer> categoryIds = Lists.newArrayList();

            checkAndSetMapValue(responseMap,EsSearchName.FIRST_CAT_ID,categoryIds);
            checkAndSetMapValue(responseMap,EsSearchName.SECOND_CAT_ID,categoryIds);
            checkAndSetMapValue(responseMap,EsSearchName.THIRD_CAT_ID,categoryIds);

            checkAndSetMapValue(responseMap,EsSearchName.FIRST_SORT_ID,sortIds);
            checkAndSetMapValue(responseMap,EsSearchName.SECOND_SORT_ID,sortIds);


            child.put(EsSearchName.FULL_CAT_ID, categoryIds);
            child.put(EsSearchName.FIRST_SORT_ID, sortIds);
            resultMap.add(child);
        }
        return resultMap;
    }
    private void checkAndSetMapValue(Map<String,Object> map ,String key,List<Integer> list){
        if( map.containsKey(key) && map.get(key)!= null && StringUtils.isNotBlank(map.get(key).toString()) ){
            list.add(Integer.valueOf(map.get(key).toString()));
        }
    }
    @Override
    public boolean esState(){
        return super.esState();

    }
}
