package com.meidianyi.shop.service.foundation.jedis.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goodssort.GoodsSortCacheInfo;
import com.meidianyi.shop.service.shop.goods.GoodsSortService;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author luguangyao
 */
@Component
public class SortDataHelper extends ShopBaseService implements DataHelperInterface<GoodsSortCacheInfo> {

    @Autowired
    GoodsSortService goodsSortServices;
    @Autowired
    JedisManager jedisManager;
    @Autowired
    ImageService imageService;



    @Override
    public List<GoodsSortCacheInfo> get(List<Integer> ids) {
        Objects.requireNonNull(ids);
        List<GoodsSortCacheInfo> results = Lists.newArrayList();
        if( ids.isEmpty() ){
            return results;
        }

        String[] field = ids.parallelStream().map(x->x.toString()).toArray(String[]::new);
        List<String> resultStrs = jedisManager.batchGetHash(getKey(),field);
        results = resultStrs.stream().
            map(x-> Util.parseJson(x,GoodsSortCacheInfo.class)).
            collect(Collectors.toList());
        if ( !results.contains(null) ){
            return results;
        }
        results.removeIf(Objects::isNull);
        List<Integer> queryedIds = results.parallelStream().map(GoodsSortCacheInfo::getSortId).collect(Collectors.toList());
        List<Integer> needIds;
        if( queryedIds.isEmpty() ){
            needIds = ids;
        }else{
            needIds =  ids.parallelStream().filter(x->!queryedIds.contains(x)).collect(Collectors.toList());
        }

        List<GoodsSortCacheInfo> queryVos = goodsSortServices.getGoodsSortCacheInfoById(needIds);
        if( queryVos.isEmpty() ){
            return results;
        }
        batchUpdate(queryVos);
        results.addAll(queryVos);
        return results;
    }

    @Override
    public void update(GoodsSortCacheInfo goodsSortCacheInfo) {
        jedisManager.addToHash(getKey(),goodsSortCacheInfo.getSortId().toString(),Util.toJson(goodsSortCacheInfo),TIME_OUT);
    }

    @Override
    public void update(List<Integer> ids) {
        List<GoodsSortCacheInfo> infos = goodsSortServices.getGoodsSortCacheInfoById(ids);
        if( infos.isEmpty() ){
            return ;
        }
        batchUpdate(infos);
    }

    @Override
    public void batchUpdate(List<GoodsSortCacheInfo> values) {
        Map<String,String> value = values.stream().collect(Collectors.toMap(c->c.getSortId().toString(), Util::toJson));
        jedisManager.addToHash(getKey(),value,TIME_OUT);
    }

    @Override
    public void delete(Integer id) {
        jedisManager.delHash(getKey(),id.toString());
    }

    @Override
    public String getKey() {
        return JedisKeyConstant.GOODS_SORT + getShopId();
    }


    private Map<Short,List<GoodsSortCacheInfo>> getGoodsSortMapGroupByLevel(List<GoodsSortCacheInfo> list){
        return list.stream().collect(Collectors.groupingBy(GoodsSortCacheInfo::getLevel));
    }

    /**
     * 根据第一次查询的结果，二次查询一级分类
     * @param sortLevelMap 第一次查询的结果
     * @return 一级分类信息
     */
    private List<GoodsSortCacheInfo> getParentSortInfoByGoodsSortLevelMap(Map<Short,List<GoodsSortCacheInfo>> sortLevelMap){
        Set<Integer> parentIds = Sets.newHashSet();
        if( sortLevelMap.containsKey(GoodsConstant.ROOT_LEVEL) ){
            parentIds.addAll(getSortIds(sortLevelMap.get(GoodsConstant.ROOT_LEVEL)));
        }
        if( sortLevelMap.containsKey(GoodsConstant.SECOND_LEVEL) ){
            parentIds.addAll(getParentIds(sortLevelMap.get(GoodsConstant.SECOND_LEVEL)));
        }
        return get(Lists.newArrayList(parentIds));
    }
    private List<Integer> getSortIds(List<GoodsSortCacheInfo> sorts){
        return sorts.stream().map(GoodsSortCacheInfo::getSortId).collect(Collectors.toList());
    }
    private List<Integer> getParentIds(List<GoodsSortCacheInfo> childSorts){
        return childSorts.stream().map(GoodsSortCacheInfo::getParentId).collect(Collectors.toList());
    }

    public Map<Short,List<GoodsSortCacheInfo>> getAndGroup(List<Integer> ids){
        Map<Short,List<GoodsSortCacheInfo>> results = Maps.newHashMap();
        List<GoodsSortCacheInfo> infos = get(ids);
        if( infos.isEmpty() ){
            return results;
        }
        Map<Short,List<GoodsSortCacheInfo>> levelMap = getGoodsSortMapGroupByLevel(infos);
        results.put(GoodsConstant.SECOND_LEVEL,levelMap.get(GoodsConstant.SECOND_LEVEL));
        results.put(GoodsConstant.ROOT_LEVEL,getParentSortInfoByGoodsSortLevelMap(levelMap));
        return results;
    }



    public Map<Integer,GoodsSortCacheInfo> getAllSortByIds(List<Integer> sortIds){
        List<GoodsSortCacheInfo> allSorts = Lists.newArrayList();
        getAndGroup(sortIds).values().stream().filter(x-> !CollectionUtils.isEmpty(x)).forEach(allSorts::addAll);
        return allSorts.stream().collect(Collectors.toMap(GoodsSortCacheInfo::getSortId, Function.identity()));
    }

}
