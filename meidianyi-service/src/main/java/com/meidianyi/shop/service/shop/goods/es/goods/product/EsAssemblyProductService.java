package com.meidianyi.shop.service.shop.goods.es.goods.product;

import com.beust.jcommander.internal.Lists;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsProduct;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author luguangyao
 */
@Component
public class EsAssemblyProductService {
    public List<EsGoodsProductEntity> getEsProduct(List<EsGoods> esGoodsList){
        List<EsGoodsProductEntity> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(esGoodsList) ){
            return result;
        }
        for( EsGoods esGoods: esGoodsList ){
            if (CollectionUtils.isEmpty(esGoods.getPrds())){
                return Lists.newArrayList();
            }
            for(EsGoodsProduct product: esGoods.getPrds()){
                EsGoodsProductEntity entity = new EsGoodsProductEntity();
                BeanUtils.copyProperties(esGoods,entity);
                entity.setPrdId(product.getPrdId());
                entity.setPrdSn(product.getPrdSn());
                entity.setPrdDesc(product.getPrdDesc());
                entity.setPrdImg(product.getPrdImg());
                entity.setPrdNumber(product.getPrdNumber());
                entity.setPrdRealPrice(product.getPrdRealPrice());
                entity.setPrdLinePrice(product.getPrdLinePrice());
                entity.setPrdCodes(product.getPrdCodes());
                result.add(entity);
            }
        }
        return result;
    }
}
