package com.meidianyi.shop.service.foundation.es.handler;

import com.meidianyi.shop.service.foundation.es.EsAggregationName;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsLabelName;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;
import com.meidianyi.shop.service.pojo.shop.goods.es.Fact;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ElasticSearch Aggregation Handler
 * @author 卢光耀
 * @date 2019/11/12 4:18 下午
 *
*/
@Component
public class EsAggregationHandler {


    /**
     * 封装fact查询builder
     * @param factList 聚合参数{@link EsGoodsConstant.EsGoodsSearchFact}
     * @return List<AggregationBuilder>
     */
    public List<AggregationBuilder> assemblyAggregationBuilder(List<Fact> factList){
        return factList.stream().map(x-> AggregationBuilders.terms(x.getName())
            .field(x.getFieldName())
            //这里查询数量暂时设置为50，有需要再扩展
            .size(50))
            .collect(Collectors.toList());
    }

    /**
     * 商品规格nested
     * @param from 分页参数
     * @param size 分页参数
     * @return
     */
    public AggregationBuilder getGoodsProduct(int from,int size){
        return AggregationBuilders.nested(EsSearchName.PRDS,EsSearchName.PRDS).
            subAggregation(
                AggregationBuilders.
                    topHits(EsSearchName.Prd.PRD_ID).
                    from(from).
                    size(size)
            );
    }

    /**
     * assembly LabelAggregationBuilder by goodsId
     * @param groupSize 返回分组的数量
     * @param querySize 每一组返回数据的数量
     * @return {@link AggregationBuilder}
     */
    public AggregationBuilder assemblyLabelAggregationBuilderByGoodsId(Integer groupSize,Integer querySize){
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(EsLabelName.GOODS_ID)
            .field(EsLabelName.GOODS_ID)
            .size(groupSize);
        TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders
            .topHits(EsAggregationName.LABEL_NAME)
            .size(querySize)
            .fetchSource(EsAggregationName.LABEL_AGGREGATION_SOURCE,null)
            .sort(EsLabelName.LEVEL,SortOrder.DESC)
            .sort(EsLabelName.TYPE, SortOrder.ASC)
            .sort(EsLabelName.CREATE_TIME,SortOrder.DESC);
        return aggregationBuilder.subAggregation(topHitsAggregationBuilder);
    }
    /**
     * assembly LabelAggregationBuilder for count
     *
     * @return {@link AggregationBuilder}
     */
    public AggregationBuilder assemblyLabelAggregationBuilderByCount(){
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(EsLabelName.ID)
            .field(EsLabelName.ID);
        TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders
            .topHits(EsAggregationName.LABEL_NAME)
            .fetchSource(EsAggregationName.LABEL_AGGREGATION_SOURCE,null)
            .sort(EsLabelName.LEVEL,SortOrder.ASC)
            .sort(EsLabelName.CREATE_TIME,SortOrder.DESC)
            //去重
            .size(1);
        return aggregationBuilder.subAggregation(topHitsAggregationBuilder);
    }
    /**
     * assembly SortAggregationBuilder by sortId
     *
     * @return {@link AggregationBuilder}
     */
    public AggregationBuilder assemblySortAggregationBuilder(){
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(EsSearchName.SORT_ID)
            .field(EsSearchName.SORT_ID);
        TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders
            .topHits(EsAggregationName.SORT_NAME)
            .fetchSource(EsAggregationName.SORT_AGGREGATION_SOURCE,null);
        return aggregationBuilder.subAggregation(topHitsAggregationBuilder);
    }
}
