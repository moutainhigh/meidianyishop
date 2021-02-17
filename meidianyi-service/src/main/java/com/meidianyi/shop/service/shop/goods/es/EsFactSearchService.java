package com.meidianyi.shop.service.shop.goods.es;

import com.google.common.collect.Lists;
import com.meidianyi.shop.service.foundation.es.EsSearchSourceBuilderParam;
import com.meidianyi.shop.service.foundation.es.EsSearchSourceBuilderParamBuilder;
import com.meidianyi.shop.service.foundation.jedis.data.SortDataHelper;
import com.meidianyi.shop.service.pojo.saas.category.SysCategorySelectTreeVo;
import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam;
import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortSelectTreeVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goodssort.GoodsSortCacheInfo;
import com.meidianyi.shop.service.saas.categroy.SysCatServiceHelper;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ES聚合查询对外服务
 * @author 卢光耀
 * @date 2019/11/5 10:15 上午
 *
*/
@Service
public class EsFactSearchService extends EsBaseSearchService{

    @Autowired
    SortDataHelper sortDataHelper;

    public GoodsFilterItemInitVo assemblyFactByAdminGoodsListInit(GoodsFilterItemInitParam initParam) throws Exception {
        GoodsPageListParam goodsPageListParam = new GoodsPageListParam();
        if (initParam.getIsSaleOut() != null) {
            goodsPageListParam.setIsSaleOut(initParam.getIsSaleOut());
        }
        if (initParam.getIsOnSale() != null) {
            goodsPageListParam.setIsOnSale(initParam.getIsOnSale());
        }
        goodsPageListParam.setIsOnSale(initParam.getIsOnSale());
        goodsPageListParam.setSelectType(initParam.getSelectType());

        EsSearchParam param = getFactSearchParamByAdminGoodsListInit(goodsPageListParam,getShopId());
        EsSearchSourceBuilderParam searchParam = EsSearchSourceBuilderParamBuilder.builder()
            .queryBuilder(assemblySearchBuilder(param.getSearchList(),param.getSearchSource(),param.getAnalyzerStatus()))
            .aggregations(assemblyAggregationBuilder(param.getFactList()))
            .build();
        //es查询
        SearchRequest searchRequest;
        if( initParam.getSelectType().equals(GoodsPageListParam.GOODS_PRD_LIST) ){
            searchRequest = assemblySearchRequest(
                assemblySearchSourceBuilder(searchParam)
                ,EsGoodsConstant.PRODUCT_ALIA_NAME
            );
        }else{
            searchRequest = assemblySearchRequest(
                assemblySearchSourceBuilder(searchParam)
                ,EsGoodsConstant.GOODS_ALIA_NAME
            );
        }
       return assemblyGoodsInitialVo(initParam,search(searchRequest));

    }
    private GoodsFilterItemInitVo assemblyGoodsInitialVo(GoodsFilterItemInitParam initParam,SearchResponse response){
        Aggregations aggregations = response.getAggregations();

        GoodsFilterItemInitVo vo = new GoodsFilterItemInitVo();
        if (Boolean.TRUE.equals(initParam.getNeedGoodsSort())) {
            vo.setGoodsSorts(assemblySortFactDataToVo(aggregations));
        }

        if (Boolean.TRUE.equals(initParam.getNeedSysCategory())) {
            vo.setGoodsCategories(assemblySysCatFactDataToVo(aggregations));
        }

        return vo;
    }
    /**
     * 封装sysCatFact数据
     * @param aggregations es聚合结果
     * @return List<SysCateVo>
     */
    private List<SysCategorySelectTreeVo> assemblySysCatFactDataToVo(Aggregations aggregations){
        Map<Integer,Integer> sysCatMap = assemblySysCatFactDataToMap(aggregations);
        List<Integer> sysCatIds = new ArrayList<>(sysCatMap.keySet());
        //TODO 暂时先走简化的缓存,后期对保证数据一致性做改造
        List<SysCatevo> result = SysCatServiceHelper.getSysCateVoByCatIds(sysCatIds);

        List<SysCategorySelectTreeVo> retList = new ArrayList<>(result.size());

        result.forEach(x->{
            SysCategorySelectTreeVo vo = new SysCategorySelectTreeVo();
            vo.setCatId(x.getCatId());
            vo.setCatName(x.getCatName());
            vo.setParentId(x.getParentId());
            vo.setHasChild(x.getHasChild());
            vo.setLevel(x.getLevel());
            vo.setGoodsSumNum(sysCatMap.get(x.getCatId()));
            retList.add(vo);
        });
        return retList;
    }
    /**
     * 从Aggregations中组装数据
     * @param aggregations  es聚合结果
     * @param factName 聚合参数{@link EsGoodsConstant.EsGoodsSearchFact}
     * @return key(factName对应的ID)/value(聚合组装的结果)
     */
    private Map<Integer,Integer> assemblyAggregationsMapData(Aggregations aggregations, String... factName){
        Map<Integer,Integer> resultMap = new HashMap<>();
        Map<String, Aggregation> aggregationsMap =aggregations.getAsMap();
        Arrays.stream(factName).forEach(x->{
            if( aggregationsMap.containsKey(x) ){
                Terms firstSysCat = aggregations.get(x);
                if( null != firstSysCat ){
                    firstSysCat.getBuckets().forEach(y->{
                        resultMap.put(Integer.parseInt(y.getKey().toString()),Math.toIntExact(y.getDocCount()));
                    });
                }
            }
        });
        return resultMap;
    }
    /**
     * 封装sortFact数据
     * @param aggregations es聚合结果
     * @return Map<sortId,numbers>
     */
    private Map<Integer,Integer> assemblySortFactDataToMap(Aggregations aggregations){
        return assemblyAggregationsMapData(aggregations
            , EsGoodsConstant.EsGoodsSearchFact.GOODS_SORT_FIRST_FACT
            , EsGoodsConstant.EsGoodsSearchFact.GOODS_SORT_SECOND_FACT);
    }

    /**
     * 封装sysCatFact数据
     * @param aggregations es聚合结果
     * @return Map<sysCatId,numbers>
     */
    private Map<Integer,Integer> assemblySysCatFactDataToMap(Aggregations aggregations){
        return assemblyAggregationsMapData(aggregations
            , EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_FIRST_FACT
            , EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_SECOND_FACT
            , EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_THIRD_FACT);

    }


    /**
     * 封装sortFact数据
     * @param aggregations es聚合结果
     * @return List<SysCateVo>
     */
    private List<GoodsSortSelectTreeVo> assemblySortFactDataToVo(Aggregations aggregations){
        Map<Integer,Integer> sortMap = assemblySortFactDataToMap(aggregations);
        List<Integer> sortIds = new ArrayList<>(sortMap.keySet());
        //TODO 暂时先走数据库,后期做改造走缓存
        Map<Short,List<GoodsSortCacheInfo>> resultMap = sortDataHelper.getAndGroup(sortIds);
        List<GoodsSortSelectTreeVo> retList = Lists.newArrayList();
        for(Map.Entry<Short,List<GoodsSortCacheInfo>> entry: resultMap.entrySet()){
            if(!CollectionUtils.isEmpty(entry.getValue())){
                entry.getValue().forEach(x->{
                    GoodsSortSelectTreeVo vo =new GoodsSortSelectTreeVo();
                    vo.setSortId(x.getSortId());
                    vo.setSortName(x.getSortName());
                    vo.setParentId(x.getParentId());
                    vo.setLevel(x.getLevel().byteValue());
                    vo.setHasChild(x.getParentId().equals(0)?(byte)1:(byte)0);
                    vo.setGoodsSumNum(sortMap.getOrDefault(x.getSortId(),0));
                    retList.add(vo);
                });
            }
        }
        return retList;
    }
    private EsSearchParam getFactSearchParamByAdminGoodsListInit(GoodsPageListParam param, Integer shopId){
        updateGoodsPageListParamForEs(param);
        return goodsParamConvertEsGoodsParam(param,shopId);

    }
    private void updateGoodsPageListParamForEs(GoodsPageListParam param){
        //封装需要fact的字段
        param.setIsFactQuery(Boolean.TRUE);
        param.setFactNameList(Arrays.asList(
            EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_FIRST_FACT,
            EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_SECOND_FACT,
            EsGoodsConstant.EsGoodsSearchFact.GOODS_CATEGORY_THIRD_FACT,
            EsGoodsConstant.EsGoodsSearchFact.GOODS_SORT_FIRST_FACT,
            EsGoodsConstant.EsGoodsSearchFact.GOODS_SORT_SECOND_FACT)
        );
    }
}
