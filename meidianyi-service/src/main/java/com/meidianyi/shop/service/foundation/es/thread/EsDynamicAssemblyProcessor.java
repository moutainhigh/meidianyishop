package com.meidianyi.shop.service.foundation.es.thread;

import com.google.common.collect.Lists;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.shop.ShopApplication;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author luguangyao
 */
@Component
public class EsDynamicAssemblyProcessor extends MainBaseService {


    public List<EsGoods> assemblyData(ShopApplication shop, List<Integer> goodsIds, Integer shopId){
        Map<Integer, GoodsRecord> goodsMap = shop.goods.getGoodsByIds(goodsIds);
        List<EsGoods> result = Lists.newArrayList();
        for( Map.Entry<Integer,GoodsRecord> entry : goodsMap.entrySet() ){
            EsGoods goods = new EsGoods();
            goods.setGoodsId(entry.getKey());
            goods.setShopId(shopId);
            dynamicAssemblyData(goods);
            result.add(goods);
        }
        return result;
    }

    /**
     *  动态设置es需要建索引的字段
     * @param goods 商品
     */
    private void dynamicAssemblyData(EsGoods goods){
    }

}
