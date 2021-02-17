package com.meidianyi.shop.service.shop.goods.es;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.jedis.data.SortDataHelper;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.service.pojo.shop.goods.brand.GoodsBrandSelectListVo;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsGradePrd;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelAndCouple;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.pojo.shop.goods.sort.Sort;
import com.meidianyi.shop.service.pojo.shop.video.GoodsVideoBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goodssort.GoodsSortCacheInfo;
import com.meidianyi.shop.service.saas.categroy.SysCatServiceHelper;
import com.meidianyi.shop.service.shop.goods.*;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsGrade;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsProduct;
import com.meidianyi.shop.service.shop.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luguangyao
 */
@Service
@Slf4j
public class EsAssemblyDataService extends ShopBaseService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSpecProductService goodsSpecProductService;
    @Autowired
    private GoodsSortService goodsSortService;
    @Autowired
    private GoodsBrandService goodsBrandService;
    @Autowired
    private GoodsPriceService goodsPriceService;
    @Autowired
    private GoodsLabelService goodsLabelService;
    @Autowired
    private SortDataHelper sortDataHelper;
    @Autowired
    private ImageService imageService;



    private static final String[] VIP_FIELDS = {EsSearchName.V1,EsSearchName.V2,EsSearchName.V3
        ,EsSearchName.V4,EsSearchName.V5,EsSearchName.V6,EsSearchName.V7,EsSearchName.V8,EsSearchName.V9};


    public List<EsGoods> assemblyEsGoods(List<Integer> goodsIds, Integer shopId) {
        List<EsGoods> esGoodsList = new ArrayList<>(goodsIds.size());
        Map<Integer, GoodsRecord> goodsMap = goodsService.getIsSaleGoodsByIds(goodsIds);
        Map<Integer, Byte> goodsTypeMap = new HashMap<>(goodsMap.size());
        Map<Integer, Integer> goodsCatMap = new HashMap<>(goodsMap.size());
        Set<Integer> goodsSortIdSet = new HashSet<>(goodsMap.size());
        Set<Integer> goodsBrandIdSet = new HashSet<>(goodsMap.size());
        goodsMap.forEach((k, v) -> {
            goodsTypeMap.put(k, v.getGoodsType());
            if (null != v.getCatId()) {
                goodsCatMap.put(k, v.getCatId());
            }
            if (null != v.getSortId()) {
                goodsSortIdSet.add(v.getSortId());
            }
            if (null != v.getBrandId()) {
                goodsBrandIdSet.add(v.getBrandId());
            }
        });
        Map<Integer, List<GoodsGradePrd>> goodsGradePrdMap = goodsService.selectGoodsGradePrdByGoodsIds(goodsIds);
        Map<Integer, GoodsVideoBo> goodsVideoMap = goodsService.getGoodsVideo(goodsIds);
        Map<Integer,List<String>> imageUrlMap = goodsService.getGoodsImageList(goodsIds);
        Map<Integer, BigDecimal> goodsShowPriceMap = goodsPriceService.getShowPriceByIdAndType(goodsTypeMap);
        Map<Integer, Result<GoodsSpecProductRecord>> goodsProductMap = goodsSpecProductService.selectByGoodsIds(goodsIds);
        Map<Integer, List<SysCatevo>> goodsCatInfoMap = getCatInfoByGoodsIds(goodsCatMap);
        Map<Integer, GoodsSortCacheInfo> sortMap = sortDataHelper.getAllSortByIds(Lists.newArrayList(goodsSortIdSet));
        Map<Integer, GoodsBrandSelectListVo> brandMap = batchAssemblyBrandAndSale(goodsBrandIdSet);
        Map<Integer, Map<Byte, List<Integer>>> goodsLabelFilterMap = new HashMap<>(goodsIds.size());
        for (Integer goodsId : goodsIds) {
            if (!goodsMap.containsKey(goodsId)) {
                log.error("\n+批量建立索引--->商品【{}】未找到无法建立索引", goodsId);
                continue;
            }
            Map<Byte, List<Integer>> goodsLabelFilter = new HashMap<>();
            goodsLabelFilter.put(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode(), Collections.singletonList(goodsId));
            GoodsRecord goods = goodsMap.get(goodsId);
            EsGoods esGoods = assemblyEsGoods(goods, shopId);
            if (validationMap(goodsShowPriceMap, goodsId)) {
                esGoods.setShowPrice(goodsShowPriceMap.get(goodsId));
            } else {
                esGoods.setShowPrice(esGoods.getShopPrice());
            }
            assemblyVipPriceImp(esGoods, goodsGradePrdMap.get(goodsId));
            if( validationMap(imageUrlMap,goodsId) ){
                esGoods.setSecondaryGoodsImages(imageUrlMap.get(goodsId));
            }
            if( validationMap(goodsVideoMap,goodsId) ){
                esGoods.setVideoInfo(Util.toJson(goodsVideoMap.get(goodsId)));
            }

            if (validationMap(goodsProductMap, goodsId)) {
                buildProduct(goodsProductMap, goodsId, esGoods);
            }
            if (validationMap(goodsCatInfoMap, goodsId)) {
                List<SysCatevo> list = goodsCatInfoMap.get(goodsId);
                goodsLabelFilter.put(GoodsLabelCoupleTypeEnum.CATTYPE.getCode(),
                    list.stream().map(SysCatevo::getCatId).collect(Collectors.toList()));
                batchAssemblyCatInfoImp(esGoods, list);
            }
            if (validationMap(sortMap, esGoods.getSortId())) {
                List<GoodsSortCacheInfo> allSort = new ArrayList<>(3);
                getSort(esGoods.getSortId(), allSort, sortMap);
                goodsLabelFilter.put(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode(),
                    allSort.stream().map(GoodsSortCacheInfo::getSortId).collect(Collectors.toList()));
                assemblySortInfoImp(esGoods, allSort);
            }
            if (validationMap(brandMap, esGoods.getBrandId())) {
                esGoods.setBrandName(brandMap.get(esGoods.getBrandId()).getBrandName());
            }
            goodsLabelFilterMap.put(goodsId, goodsLabelFilter);
            esGoodsList.add(esGoods);
        }
        Map<Integer, List<Integer>> goodsLabelMap = assemblyGoodsLabel(goodsLabelFilterMap, goodsIds, goodsCatInfoMap, sortMap);
        esGoodsList.forEach(x -> {
            if (validationMap(goodsLabelMap, x.getGoodsId())) {
                x.setGoodsLabel(goodsLabelMap.get(x.getGoodsId()));
            }
        });
        return esGoodsList;
    }

    private void buildProduct(Map<Integer, Result<GoodsSpecProductRecord>> goodsProductMap, Integer goodsId, EsGoods esGoods) {
        List<GoodsSpecProductRecord> list = goodsProductMap.get(goodsId);
        list.sort(Comparator.comparing(GoodsSpecProductRecord::getPrdPrice));
        List<BigDecimal> specPrdPrices = Lists.newLinkedList();
        List<EsGoodsProduct> voList = Lists.newArrayList();
        StringBuilder prdSns = new StringBuilder();
        if( list.size() == 1 && StringUtils.isBlank(list.get(0).getPrdSpecs()) ){
            esGoods.setDefPrd(true);
        }else{
            esGoods.setDefPrd(false);
        }
        list.forEach(x -> {
            if( StringUtils.isNotBlank(x.getPrdImg()) ){
                String imgUlr = imageService.imageUrl(x.getPrdImg());
                x.setPrdImg(imgUlr);
            }
            voList.add(new EsGoodsProduct(x));
            specPrdPrices.add(x.getPrdPrice());
            prdSns.append(x.getPrdSn()).append(",");
        });
        esGoods.setPrdSns(prdSns.toString());
        int length = specPrdPrices.size();
        esGoods.setPrdJson(Util.toJson(voList));
        esGoods.setPrds(voList);
        esGoods.setMaxSpecPrdPrices(specPrdPrices.get(length - 1));
        esGoods.setMinSpecPrdPrices(specPrdPrices.get(0));
    }


    private Map<Integer, GoodsBrandSelectListVo> batchAssemblyBrandAndSale(Set<Integer> brandIds) {
        return goodsBrandService.listGoodsBrandNameByIds(new ArrayList<>(brandIds));
    }

    /**
     * 封装商家分类相关信息
     *
     * @param sortIds
     */
    private Map<Integer, Sort> batchAssemblySortInfo(Set<Integer> sortIds) {
        return goodsSortService.getParentSortsByChildId(new ArrayList<>(sortIds));
    }

    private void getSort(Integer sortId, List<GoodsSortCacheInfo> result, Map<Integer, GoodsSortCacheInfo> allSortMap) {
        if (!allSortMap.containsKey(sortId)) {
            return;
        }
        GoodsSortCacheInfo sort = allSortMap.get(sortId);
        result.add(sort);
        if (sort.getParentId() == 0) {
            return;
        }
        getSort(sort.getParentId(), result, allSortMap);
    }

    private EsGoods assemblyEsGoods(GoodsRecord goods, Integer shopId) {
        EsGoods esGoods = new EsGoods();
        BeanUtils.copyProperties(goods, esGoods);
        Optional<Integer> goodsSaleNumber = Optional.ofNullable(goods.getGoodsSaleNum());
        Optional<Integer> baseSaleNumber = Optional.ofNullable(goods.getBaseSale());
        esGoods.setFreightTemplateId(goods.getDeliverTemplateId());
        esGoods.setShopId(shopId);
        esGoods.setTotalSaleNumber(goodsSaleNumber.orElse(0)+baseSaleNumber.orElse(0));
        esGoods.setGoodsImg(imageService.imageUrl(goods.getGoodsImg()));
        esGoods.setSaleTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, goods.getSaleTime()));
        esGoods.setCreateTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, goods.getCreateTime()));
        esGoods.setAddEsDate(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, new Date()));
        esGoods.setUpdateDate(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, goods.getUpdateTime()));
        esGoods.setCreateTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, goods.getCreateTime()));
        esGoods.setSaleTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL, goods.getSaleTime()));
        return esGoods;
    }


    /**
     * 封装商家分类相关信息实现
     *
     * @param esGoods
     * @param list
     */
    private void assemblySortInfoImp(EsGoods esGoods, List<GoodsSortCacheInfo> list) {
        StringBuilder sortName = new StringBuilder();
        for (GoodsSortCacheInfo sort : list) {
            if (sort != null) {
                sortName.append(sort.getSortName()).append(" ");
                if (sort.getLevel() == 0) {
                    esGoods.setFirstSortId(sort.getSortId());
                } else if (sort.getLevel() == 1) {
                    esGoods.setSecondSortId(sort.getSortId());
                }
            }
            esGoods.setSortName(sortName.toString());
        }
    }

    private Map<Integer, List<SysCatevo>> getCatInfoByGoodsIds(Map<Integer, Integer> param) {
        Map<Integer, List<SysCatevo>> resultMap = new HashMap<>(param.size());
        param.forEach((k, v) -> {
            List<SysCatevo> list = SysCatServiceHelper.getSysCateVosByCatId(v);
            resultMap.put(k, list);
        });
        return resultMap;
    }


    private void batchAssemblyCatInfoImp(EsGoods esGoods, List<SysCatevo> list) {
        StringBuilder categoryName = new StringBuilder();
        list.forEach(x -> {
            categoryName.append(x.getCatName()).append(" ");
            if (x.getLevel() == 0) {
                esGoods.setFirstCatId(x.getCatId());
            } else {
                int level1 = 1;
                if (x.getLevel() == level1) {
                    esGoods.setSecondCatId(x.getCatId());
                } else {
                    int level2 = 2;
                    if (x.getLevel() == level2) {
                        esGoods.setThirdCatId(x.getCatId());
                    }
                }
            }
        });
        esGoods.setCatName(categoryName.toString());
    }

    private void batchAssemblyShowPrice(List<EsGoods> goodsList) {
        Map<Byte, List<Integer>> goodsTypeMap = new HashMap<>();
        goodsList.forEach(x -> {
            Byte type = x.getGoodsType();
            List<Integer> list;
            if (goodsTypeMap.containsKey(type)) {
                list = goodsTypeMap.get(type);
            } else {
                list = new ArrayList<>();
            }
            list.add(x.getGoodsId());
            goodsTypeMap.put(type, list);
        });

    }

    /**
     * 封装v会员等级价
     * 针对不同的goodsType有不同的封装方式
     *  (1).当goodsType是砍价、拼团、秒杀、定金膨胀时，会员等级价存的是商品对应的活动的最低价
     *  (2).当goodsType是限时降价时，活动价和会员价做对比，取最低价
     *  (3).其他的情况时有会员价取会员价，没有放活动价，会动价也没有放正常价
     * @param esGoods
     * @param goodsGradePrdList
     */
    private void assemblyVipPriceImp(EsGoods esGoods, List<GoodsGradePrd> goodsGradePrdList) {

        Map<String,List<GoodsGradePrd>> goodsGradeMap = Maps.newHashMap();
            if(CollectionUtils.isNotEmpty(goodsGradePrdList)){
                goodsGradeMap = goodsGradePrdList.stream()
                    .sorted(Comparator.comparing(GoodsGradePrd::getGradePrice))
                    .collect(Collectors.groupingBy(GoodsGradePrd::getGrade));
            }
        List<EsGoodsGrade> goodsGrades = Lists.newArrayList();
        for( String grade: VIP_FIELDS){
            boolean containsKey = goodsGradeMap.containsKey(grade);
            if( BaseConstant.ACTIVITY_TYPE_GROUP_BUY.equals(esGoods.getGoodsType()) ||
                BaseConstant.ACTIVITY_TYPE_BARGAIN.equals(esGoods.getGoodsType()) ||
                BaseConstant.ACTIVITY_TYPE_SEC_KILL.equals(esGoods.getGoodsType()) ||
                BaseConstant.ACTIVITY_TYPE_PRE_SALE.equals(esGoods.getGoodsType()) ){
                setGradePrice(grade,esGoods.getShowPrice(),esGoods);
            }else if( BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE.equals(esGoods.getGoodsType()) ){
                if( containsKey ){
                    setGradePrice(grade,goodsGradeMap.get(grade).get(0).getGradePrice().max(esGoods.getShowPrice()),esGoods);
                }else{
                    setGradePrice(grade,esGoods.getShowPrice(),esGoods);
                }
            }else {
                setGradePrice(grade,esGoods.getShowPrice(),esGoods);
            }
            if( containsKey ){
                List<GoodsGradePrd> gradePrds = goodsGradeMap.get(grade);
                for( GoodsGradePrd prd:gradePrds){
                    EsGoodsGrade goodsGrade = new EsGoodsGrade();
                    goodsGrade.setGrade(grade);
                    goodsGrade.setGradePrice(prd.getGradePrice());
                    goodsGrade.setPrdId(prd.getPrdId());
                    goodsGrades.add(goodsGrade);
                }
            }
        }
        if( !goodsGrades.isEmpty() ){
            esGoods.setGrades(goodsGrades);
        }
    }
    private void setGradePrice(String vipLevel,BigDecimal price,EsGoods esGoods){
        try {
            Field v = esGoods.getClass().getDeclaredField(vipLevel);
            v.setAccessible(true);
            v.set(esGoods,price);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("建立esGoods【id={}】索引时封装会员价失败", esGoods.getGoodsId());
            e.printStackTrace();
        }
    }

    private void outPutLog(Timestamp now, Integer goodsId, Byte goodsType) {
        log.error("{}商品【{}】是{}类型但没找到相关活动", now, goodsId, goodsType);
    }

    private void assemblyGoodsLabelMap(Map<Integer, List<GoodsLabelCoupleRecord>> sortForLabelMap) {

    }

    private Map<Integer, List<Integer>> assemblyGoodsLabel(Map<Integer, Map< Byte, List<Integer>>> goodsLabelFilterMap, List<Integer> goodsIds,
                                                           Map<Integer, List<SysCatevo>> categoryMap, Map<Integer, GoodsSortCacheInfo> sortMap) {
        Map<Integer, List<Integer>> resultMap = new HashMap<>();
        Set<Integer> categoryIdSet = new HashSet<>(categoryMap.size() * 3);
        categoryMap.values()
            .forEach(
                x ->
                    categoryIdSet.addAll(
                        x.stream().
                            map(SysCatevo::getCatId).
                            collect(Collectors.toList())
                    )
            );
        List<Integer> categoryIds = new ArrayList<>(categoryIdSet);
        List<Integer> sortIds = new ArrayList<>(sortMap.keySet());
        Map<Byte, List<GoodsLabelAndCouple>> typeRecord =
            goodsLabelService.getGoodsLabelByFilter(goodsIds, sortIds, categoryIds);

        Map<Integer, List<GoodsLabelAndCouple>> sortForLabelMap = new HashMap<>();
        Map<Integer, List<GoodsLabelAndCouple>> categoryForLabelMap = new HashMap<>();
        Map<Integer, List<GoodsLabelAndCouple>> goodsForLabelMap = new HashMap<>();
        List<GoodsLabelAndCouple> allGoodsForLabelList = new ArrayList<>();
        typeRecord.forEach((type, records) -> {
            if (type.equals(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode())) {
                goodsForLabelMap.putAll(records.stream().collect(Collectors.groupingBy(GoodsLabelAndCouple::getGtaId)));
            } else if (type.equals(GoodsLabelCoupleTypeEnum.CATTYPE.getCode())) {
                categoryForLabelMap.putAll(records.stream().collect(Collectors.groupingBy(GoodsLabelAndCouple::getGtaId)));
            } else if (type.equals(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode())) {
                sortForLabelMap.putAll(records.stream().collect(Collectors.groupingBy(GoodsLabelAndCouple::getGtaId)));
            } else if (type.equals(GoodsLabelCoupleTypeEnum.ALLTYPE.getCode())) {
                allGoodsForLabelList.addAll(records);
            }
        });

        goodsLabelFilterMap.forEach((goodsId, filterMap) -> {
            List<GoodsLabelAndCouple> list = new ArrayList<>();
            filterMap.forEach((type, ids) -> {
                if (type.equals(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode())) {

                    ids.forEach(x -> {
                        if (goodsForLabelMap.containsKey(x)) {
                            list.addAll(goodsForLabelMap.get(x));
                        }
                    });
                } else if (type.equals(GoodsLabelCoupleTypeEnum.CATTYPE.getCode())) {
                    ids.forEach(x -> {
                        if (categoryForLabelMap.containsKey(x)) {
                            list.addAll(categoryForLabelMap.get(x));
                        }
                    });
                } else if (type.equals(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode())) {
                    ids.forEach(x -> {
                        if (sortForLabelMap.containsKey(x)) {
                            list.addAll(sortForLabelMap.get(x));
                        }
                    });
                }
            });
            list.addAll(allGoodsForLabelList);
            int maxValidLabels = 5;
            if (list.size() > maxValidLabels) {
                //优先级降序
                Comparator<GoodsLabelAndCouple> byLevelDesc = Comparator.comparing(GoodsLabelAndCouple::getLevel).reversed();
                //创建时间降序
                Comparator<GoodsLabelAndCouple> byTimeDesc = Comparator.comparing(GoodsLabelAndCouple::getCreateTime).reversed();
                Comparator<GoodsLabelAndCouple> allComparator = byLevelDesc.thenComparing(byTimeDesc);
                resultMap.put(goodsId, list.stream().sorted(allComparator).limit(5).map(GoodsLabelAndCouple::getLabelId).collect(Collectors.toList()));
            } else {
                resultMap.put(goodsId, list.stream().map(GoodsLabelAndCouple::getLabelId).collect(Collectors.toList()));
            }
        });
        return resultMap;

    }

    private boolean validationMap(Map<Integer, ?> map, Integer goodsId) {
        return map != null && !map.isEmpty() && map.containsKey(goodsId);
    }
}
