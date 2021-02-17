package com.meidianyi.shop.service.shop.goods.es.convert.goods;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author luguangyao
 */
public class GoodsPageListVoForProductConverter {
    public static GoodsPageListVo convert(EsGoodsProductEntity esProduct) {
            GoodsPageListVo vo = new GoodsPageListVo();
            BeanUtils.copyProperties(esProduct,vo);
            vo.setGoodsName(esProduct.getGoodsName()+esProduct.getPrdDesc());
            if( null != esProduct.getMaxSpecPrdPrices() ){
                vo.setPrdMaxShopPrice(esProduct.getMaxSpecPrdPrices());
            }
            vo.setPrdPrice(esProduct.getPrdRealPrice());
            if( null != esProduct.getMinSpecPrdPrices() ){
                vo.setPrdMinShopPrice(esProduct.getMinSpecPrdPrices());
            }
            vo.setIsDefaultPrd(esProduct.getDefPrd());
            vo.setPrdSn(Optional.ofNullable(esProduct.getPrdSn()).orElse(""));
            vo.setPrdDesc(Optional.ofNullable(esProduct.getPrdDesc()).orElse(""));
            vo.setPrdId(esProduct.getPrdId());
            vo.setPrdImg(esProduct.getPrdImg());
            vo.setPrdPrice(Optional.ofNullable(esProduct.getPrdRealPrice()).orElse(BigDecimal.ZERO));
            vo.setPrdNumber(Optional.ofNullable(esProduct.getPrdNumber()).orElse(0));
            if(StringUtils.isNotBlank(esProduct.getCatName())){
                String[] catName = esProduct.getCatName().split(" ");
                vo.setCatName(catName[catName.length-1]);
            }
        return vo;
    }
}
