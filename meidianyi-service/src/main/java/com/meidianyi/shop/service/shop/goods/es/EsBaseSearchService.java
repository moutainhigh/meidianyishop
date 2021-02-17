package com.meidianyi.shop.service.shop.goods.es;

import com.beust.jcommander.internal.Lists;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.es.*;
import com.meidianyi.shop.service.foundation.es.handler.EsAggregationHandler;
import com.meidianyi.shop.service.foundation.es.handler.EsQueryBuilderHandler;
import com.meidianyi.shop.service.foundation.es.handler.EsRequestHandler;
import com.meidianyi.shop.service.foundation.es.handler.EsSearchSourceBuilderHandler;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.es.*;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.shop.goods.es.convert.EsConvertFactory;
import com.meidianyi.shop.service.shop.goods.es.convert.param.EsParamConvertInterface;
import com.meidianyi.shop.service.shop.goods.es.convert.param.GoodsPageConvertEsParam;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchFieldsConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName.*;

/**
 *  不可直接调用
 * @author 卢光耀
 * @date 2019/11/5 10:16 上午
 *
*/
@Service
public class EsBaseSearchService extends ShopBaseService {






    @Autowired
    private EsManager esManager;
    @Autowired
    private EsAggregationHandler esAggregationHandler;
    @Autowired
    private EsQueryBuilderHandler esQueryBuilderHandler;
    @Autowired
    private EsSearchSourceBuilderHandler esSearchSourceBuilderHandler;
    @Autowired
    private EsRequestHandler esRequestHandler;

    protected BoolQueryBuilder assemblySearchBuilder(List<FieldProperty> searchList, EsSearchSource searchSource, Boolean enableAnalyzer) {
        return esQueryBuilderHandler.assemblySearchBuilder(searchList,searchSource,enableAnalyzer);
    }

    protected List<AggregationBuilder> assemblyAggregationBuilder(List<Fact> factList){
        return esAggregationHandler.assemblyAggregationBuilder(factList);
    }

    protected boolean esState(){
        return esManager.esState();
    }
    /**
     * GoodsPageListParam to EsSearchParam
     * @param param 页面的传参
     * @return es的查询param
     */
    protected EsSearchParam goodsParamConvertEsGoodsParam(GoodsPageListParam param,Integer shopId){
        EsParamConvertInterface convert = EsConvertFactory.getParamConvert(GoodsPageConvertEsParam.class);
        return convert.convert(param,shopId);
    }

    /**
     * ElasticSearch assembly page
     * @return {@link Page}
     * @throws IOException 连接异常
     */
    Integer assemblyPage(SearchSourceBuilder sourceBuilder,String indexName) throws IOException {
        CountRequest countRequest = assemblyCountRequest(sourceBuilder,indexName);
        CountResponse countResponse = esManager.getDocumentCount(countRequest);
        return Long.valueOf(countResponse.getCount()).intValue();
    }


    /**
     * 调用esManager的searchResponse
     * @param searchRequest 查询请求
     * @return SearchResponse
     * @throws IOException 连接异常
     */
    protected SearchResponse search(SearchRequest searchRequest) throws IOException {
        return esManager.searchResponse(searchRequest);
    }
    /**
     * 封装fact查询builder
     * @param factList 聚合参数{@link EsGoodsConstant.EsGoodsSearchFact}
     * @return List<AggregationBuilder>
     */
    private List<AggregationBuilder> assemblyTermsAggregationBuilder(List<Fact> factList){
        return factList.stream().map(x-> AggregationBuilders.terms(x.getName())
            .field(x.getFieldName())
            //这里查询数量暂时设置为50，有需要再扩展
            .size(50))
            .collect(Collectors.toList());
    }
    /**
     * 封装SearchRequest
     * @param sourceBuilder es对应的查询
     * @param indexName 索引名称
     * @return SearchRequest
     */
    SearchRequest assemblySearchRequest(SearchSourceBuilder sourceBuilder, String indexName)  {
        return esRequestHandler.assemblySearchRequest(indexName,sourceBuilder);
    }
    /**
     * 封装CountRequest
     * @param sourceBuilder es对应的查询
     * @param indexName 索引名称
     * @return SearchRequest
     */
    protected CountRequest assemblyCountRequest(SearchSourceBuilder sourceBuilder, String indexName)  {
        return esRequestHandler.assemblyCountRequest(indexName,sourceBuilder);
    }

    /**
     * 封装SearchSourceBuilder(通常是封装用来getCount所需的SearchSourceBuilder)
     * @param param es对应的查询
     * @return {@link SearchSourceBuilder}
     */
    SearchSourceBuilder assemblySearchSourceBuilder(EsSearchSourceBuilderParam param){
        return esSearchSourceBuilderHandler.assemblySearchSourceBuilder(param);
    }

    /**
     * 从ElasticSearch中获取数据
     * @param param ElasticSearch搜索条件
     * @return {@link PageResult< EsGoods >}
     */
    protected PageResult<EsGoods> searchGoodsPageByParamForPage(EsSearchParam param) throws IOException {
        PageResult<EsGoods> result = new PageResult<>();
        SearchSourceBuilder sourceBuilder =  getSearchSourceBuilderAndPage(result,EsGoodsConstant.GOODS_ALIA_NAME,param);
        if( !CollectionUtils.isEmpty(param.getSorts()) ){
            for (int i = 0; i < param.getSorts().size(); i++) {
                Sort sort = param.getSorts().get(i);
                sourceBuilder.sort(sort.getSortName(),sort.getSortOrder());
            }
        }
        result.setDataList(searchEsGoods(assemblySearchRequest(sourceBuilder,EsGoodsConstant.GOODS_ALIA_NAME)));
        return result;
    }
    /**
     * 从ElasticSearch中获取数据
     * @param param ElasticSearch搜索条件
     * @return {@link PageResult< EsGoods >}
     */
    protected PageResult<EsGoodsProductEntity> searchGoodsProductPageByParam(EsSearchParam param) throws IOException {
        PageResult<EsGoodsProductEntity> result = new PageResult<>();
        SearchSourceBuilder sourceBuilder =  getSearchSourceBuilderAndPage(result,EsGoodsConstant.PRODUCT_ALIA_NAME,param);
        if( !CollectionUtils.isEmpty(param.getSorts()) ){
            for (int i = 0; i < param.getSorts().size(); i++) {
                Sort sort = param.getSorts().get(i);
                sourceBuilder.sort(sort.getSortName(),sort.getSortOrder());
            }
        }
        result.setDataList(searchEsGoodsProduct(assemblySearchRequest(sourceBuilder,EsGoodsConstant.PRODUCT_ALIA_NAME)));
        return result;
    }


    /**
     * 从ElasticSearch中获取数据
     * @param param ElasticSearch搜索条件
     * @return {@link PageResult< EsGoods >}
     */
    protected List<EsGoods> searchGoodsPageByParamForNotPage(EsSearchParam param) throws IOException {
        EsSearchSourceBuilderParam searchParam = assemblySourceBuilderParamNotPage(param);
        SearchSourceBuilder sourceBuilder = assemblySearchSourceBuilder(searchParam);

        if( !CollectionUtils.isEmpty(param.getSorts()) ){
            for (int i = 0; i < param.getSorts().size(); i++) {
                Sort sort = param.getSorts().get(i);

                sourceBuilder.sort(sort.getSortName(),sort.getSortOrder());
            }
        }
        return searchEsGoods(assemblySearchRequest(sourceBuilder,EsGoodsConstant.GOODS_ALIA_NAME));
    }

    /**
     * 【分页】封装es查询条件（SearchSourceBuilder）
     * @param result 分页信息
     * @param indexName 索引别名
     * @param param 查询参数
     * @return es查询条件（SearchSourceBuilder）
     * @throws IOException 封装分页信息错误
     */
    private SearchSourceBuilder getSearchSourceBuilderAndPage(PageResult<?> result, String indexName, EsSearchParam param) throws IOException {
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        if( param == null ){
            return sourceBuilder;
        }
        QueryBuilder queryBuilder = assemblySearchBuilder(param.getSearchList(),param.getSearchSource(),param.getAnalyzerStatus());
        sourceBuilder.query(queryBuilder);
        Integer totalRow =  assemblyPage(sourceBuilder,indexName);
        Page page = Page.getPage(totalRow,param.getCurrentPage(),param.getPageRows());
        result.setPage(page);
        Integer size = page.getPageRows();
        Integer from = (page.getCurrentPage()-1)*size;
        if( from > page.getTotalRows() ){
            from = page.getTotalRows();
        }
        sourceBuilder.from( from ).size( size );
        if( param.getFactList() != null ){
            assemblyAggregationBuilder(param.getFactList()).forEach(sourceBuilder::aggregation);
        }
        if( param.getIncludes() != null){
            sourceBuilder.fetchSource(param.getIncludes(), null);
        }
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field field ;
        if ( param.getAnalyzerStatus() ){
            field = new HighlightBuilder.Field(GOODS_NAME+".sing");
        }else{
            field = new HighlightBuilder.Field(GOODS_NAME);
        }

        field.preTags("<font color='red'>");
        field.postTags("</font>");
        highlightBuilder.field(field);
        sourceBuilder.highlighter(highlightBuilder);

        return sourceBuilder;
    }

    /**
     * 根据搜索请求返回List<EsGoods>
     * @param searchRequest
     * @return
     * @throws IOException
     */
    private List<EsGoods> searchEsGoods(SearchRequest searchRequest) throws IOException {
        SearchResponse searchResponse = search(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<EsGoods> data = new LinkedList<>();
        for( SearchHit hit:hits){
            EsGoods esGoods = Util.parseJson(hit.getSourceAsString(),EsGoods.class, EsUtil.ES_FILED_SERIALIZER);
            esGoods.setGoodsTitleName(esGoods.getGoodsName());
            if( !hit.getHighlightFields().isEmpty() ){
                if( hit.getHighlightFields().containsKey(GOODS_NAME) ){
                    esGoods.setGoodsName(hit.getHighlightFields().get(GOODS_NAME).fragments()[0].toString());
                }else if(hit.getHighlightFields().containsKey(GOODS_NAME+".sing")){
                    esGoods.setGoodsName(hit.getHighlightFields().get(GOODS_NAME+".sing").fragments()[0].toString());
                }
            }

            data.add(esGoods);
        }
        return data;
    }
    /**
     * 根据搜索请求返回List<EsGoods>
     * @param searchRequest
     * @return
     * @throws IOException
     */
    private List<EsGoodsProductEntity> searchEsGoodsProduct(SearchRequest searchRequest) throws IOException {
        SearchResponse searchResponse = search(searchRequest);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<EsGoodsProductEntity> data = new LinkedList<>();
        for( SearchHit hit:hits){
            EsGoodsProductEntity esGoods = Util.parseJson(hit.getSourceAsString(),EsGoodsProductEntity.class, EsUtil.ES_FILED_SERIALIZER);
            esGoods.setGoodsTitleName(esGoods.getGoodsName());
            if( !hit.getHighlightFields().isEmpty() ){
                if( hit.getHighlightFields().containsKey(GOODS_NAME) ){
                    esGoods.setGoodsName(hit.getHighlightFields().get(GOODS_NAME).fragments()[0].toString());
                }else if(hit.getHighlightFields().containsKey(GOODS_NAME+".sing")){
                    esGoods.setGoodsName(hit.getHighlightFields().get(GOODS_NAME+".sing").fragments()[0].toString());
                }
            }

            data.add(esGoods);
        }
        return data;
    }
    /**
     * 封装处理ElasticSearch的查询配置条件(不分页)
     * @param param 存储大部分的查询配置
     * @return ElasticSearch的查询配置条件
     */
    EsSearchSourceBuilderParam assemblySourceBuilderParamNotPage(EsSearchParam param) {
        EsSearchSourceBuilderParamBuilder builder = EsSearchSourceBuilderParamBuilder.builder();
        if( param.getSearchList() != null && !param.getSearchList().isEmpty()){
            builder.queryBuilder(assemblySearchBuilder(param.getSearchList(),param.getSearchSource(),param.getAnalyzerStatus()));
        }
        if( param.getFactList() != null && !param.getFactList().isEmpty() ){
            builder.aggregations(assemblyAggregationBuilder(param.getFactList()));
        }
        if( param.getIncludes() != null ){
            builder.includeSource(param.getIncludes());
        }
        return builder.build();
    }
    /**
     * 封装处理ElasticSearch的查询配置条件(分页)
     * @param param 存储大部分的查询配置
     * @return ElasticSearch的查询配置条件
     */
    EsSearchSourceBuilderParam assemblySourceBuilderParamPage(EsSearchParam param,Page esPage) {
        EsSearchSourceBuilderParam resultParam = assemblySourceBuilderParamNotPage(param);
        return assemblyEsSearchSourceBuilderParamForPage(resultParam,esPage);
    }

    /**
     * 处理ElasticSearch的查询配置的分页条件
     * @param resultParam ElasticSearch的查询配置
     * @param esPage 分页参数
     * @return  {@link EsSearchSourceBuilderParam}
     */
    private EsSearchSourceBuilderParam assemblyEsSearchSourceBuilderParamForPage(EsSearchSourceBuilderParam resultParam,Page esPage){
        Integer size = esPage.getPageRows();
        Integer from = (esPage.getCurrentPage()-1)*size;
        if( from > esPage.getTotalRows() ){
            from = esPage.getTotalRows();
        }
        resultParam.setFrom(from);
        resultParam.setSize(size);
        return resultParam;
    }

    /**
     * 根据ElasticSearch的_id查询对应的数据
     * @param goodsId 商品Id
     * @param shopId 店铺Id
     * @return 单条商品的信息
     * @throws IOException ElasticSearch连接异常
     */
    EsGoods getEsGoodsById(Integer goodsId,Integer shopId) throws IOException {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("_id",shopId.toString()+goodsId);
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
            .query(queryBuilder)
            .fetchSource(EsSearchFieldsConstant.GOODS_SEARCH_STR,null);
        List<EsGoods> list = searchEsGoods(assemblySearchRequest(sourceBuilder,EsGoodsConstant.GOODS_ALIA_NAME));
        return list.size()>0?list.get(0):null;
    }

    /**
     * 获取agg条件（针对商品标签，根据商品id）
     *
     * @return AggregationBuilder
     */
    protected AggregationBuilder assemblyLabelAggregationBuilderByGoodsId(Integer groupSize,Integer querySize ){
        return esAggregationHandler.assemblyLabelAggregationBuilderByGoodsId(groupSize,querySize);
    }
    /**
     * 获取agg条件（针对商品，根据商家分类）
     *
     * @return AggregationBuilder
     */
    protected AggregationBuilder assemblySortAggregationBuilder(){
        return esAggregationHandler.assemblySortAggregationBuilder();
    }
    /**
     * 获取agg条件（针对商品标签，根据标签id）
     *
     * @return AggregationBuilder
     */
    protected AggregationBuilder assemblyLabelAggregationBuilderByCount(){
        return esAggregationHandler.assemblyLabelAggregationBuilderByCount();
    }


    public void getGoodsProductIdByParam(EsSearchParam param){
        EsSearchSourceBuilderParam searchParam = assemblySourceBuilderParamNotPage(param);
        searchParam.setAggregations(
            Lists.newArrayList(
                esAggregationHandler.getGoodsProduct((param.getCurrentPage()-1)*param.getPageRows(),param.getCurrentPage()*param.getPageRows())
            )
        );
        searchParam.setIncludeSource(new String[]{});
        SearchSourceBuilder sourceBuilder = assemblySearchSourceBuilder(searchParam);
        if( !CollectionUtils.isEmpty(param.getSorts()) ){
            for (int i = 0; i < param.getSorts().size(); i++) {
                Sort sort = param.getSorts().get(i);

                sourceBuilder.sort(sort.getSortName(),sort.getSortOrder());
            }
        }
        try {
            SearchResponse response =esManager.searchResponse(assemblySearchRequest(sourceBuilder,EsGoodsConstant.GOODS_ALIA_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
