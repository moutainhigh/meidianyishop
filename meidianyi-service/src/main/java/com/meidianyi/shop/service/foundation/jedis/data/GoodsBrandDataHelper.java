package com.meidianyi.shop.service.foundation.jedis.data;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.util.ChineseToPinYinUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpPinYinVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpVo;
import com.meidianyi.shop.service.shop.goods.GoodsBrandService;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luguangyao
 */
@Service
public class GoodsBrandDataHelper extends ShopBaseService implements DataHelperInterface<GoodsBrandMpVo>{

    @Autowired
    GoodsBrandService goodsBrandService;
    @Autowired
    JedisManager jedisManager;
    @Autowired
    ImageService imageService;



    @Override
    public List<GoodsBrandMpVo> get(List<Integer> ids) {
        Objects.requireNonNull(ids);
        List<GoodsBrandMpVo> results = Lists.newArrayList();
        if( ids.isEmpty() ){
            return results;
        }

        String[] field = ids.parallelStream().map(x->x.toString()).toArray(String[]::new);
        List<String> resultStrs = jedisManager.batchGetHash(getKey(),field);
        results = resultStrs.stream().
            map(x-> Util.parseJson(x,GoodsBrandMpVo.class)).
            collect(Collectors.toList());
        if ( !results.contains(null) ){
            return results;
        }
        results.removeIf(Objects::isNull);
        List<Integer> queryedIds = results.parallelStream().map(GoodsBrandMpVo::getId).collect(Collectors.toList());
        List<Integer> needIds;
        if( queryedIds.isEmpty() ){
            needIds = ids;
        }else{
            needIds =  ids.parallelStream().filter(x->!queryedIds.contains(x)).collect(Collectors.toList());
        }
        List<GoodsBrandMpVo> queryVos = goodsBrandService.getGoodsBrandVoById(needIds);
        if( queryVos.isEmpty() ){
            return results;
        }
        batchUpdate(queryVos);
        results.addAll(queryVos);
        return results;
    }

    @Override
    public void update(GoodsBrandMpVo goodsBrandMpVo) {
        jedisManager.addToHash(getKey(),goodsBrandMpVo.getId().toString(),Util.toJson(goodsBrandMpVo),TIME_OUT);

    }

    @Override
    public void update(List<Integer> id) {
        List<GoodsBrandMpVo> vos = goodsBrandService.getGoodsBrandVoById(id);
        updateLogoPath(vos);
        if( vos.isEmpty() ){
            return ;
        }
        batchUpdate(vos);
    }

    private void updateLogoPath(List<GoodsBrandMpVo> vos){
        for( GoodsBrandMpVo vo :vos ){
            String url = vo.getLogo();
            if(StringUtils.isNotBlank(url)){
                vo.setLogo(imageService.imageUrl(url));
            }
        }
    }

    @Override
    public void batchUpdate(List<GoodsBrandMpVo> values) {
        updateLogoPath(values);
        Map<String,String> value = values.stream().collect(Collectors.toMap(c->c.getId().toString(), Util::toJson));
        jedisManager.addToHash(getKey(),value,TIME_OUT);
    }

    @Override
    public void delete(Integer id) {
        jedisManager.delHash(getKey(),id.toString());
    }

    @Override
    public String getKey() {
        return  JedisKeyConstant.GOODS_BRAND + getShopId();
    }

    public List<GoodsBrandMpPinYinVo> getAndGroup(List<Integer> ids) {
        List<GoodsBrandMpVo> vos = get(ids);
        if( vos.isEmpty() ){
            return null;
        }
        TreeMap<String,List<GoodsBrandMpVo>> treeMap = Maps.newTreeMap();
        for (GoodsBrandMpVo vo : vos) {
            String c = ChineseToPinYinUtil.getStartAlphabet(vo.getBrandName());
            treeMap.computeIfAbsent(c, k -> Lists.newArrayList()).add(vo);
        }
        LinkedList<GoodsBrandMpPinYinVo> retList = treeMap.entrySet().
            stream().
            map(x->new GoodsBrandMpPinYinVo(x.getKey(),x.getValue())).
            collect(Collectors.toCollection(LinkedList::new));

        if (retList.peek() != null&&ChineseToPinYinUtil.OTHER_CHARACTER.equals(retList.peek().getCharacter())) {
            GoodsBrandMpPinYinVo t = retList.removeFirst();
            retList.addLast(t);
        }
        return retList;

    }
}
