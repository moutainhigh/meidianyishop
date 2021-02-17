package com.meidianyi.shop.service.shop.goods.es.convert.goods;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;

/**
 * es
 * @author 卢光耀
 * @date 2019/11/12 11:01 上午
 *
*/
public class GoodsPageListVoConverter implements EsGoodsConvertInterface<GoodsPageListVo> {

    private final static ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public GoodsPageListVo convert(EsGoods esGoods) {
        GoodsPageListVo vo = new GoodsPageListVo();
        BeanUtils.copyProperties(esGoods,vo);
        if( null != esGoods.getMaxSpecPrdPrices() ){
            vo.setPrdMaxShopPrice(esGoods.getMaxSpecPrdPrices());
        }
        if( null != esGoods.getMinSpecPrdPrices() ){
            vo.setPrdMinShopPrice(esGoods.getMinSpecPrdPrices());
        }
        vo.setIsDefaultPrd(esGoods.getDefPrd());

        if( esGoods.getDefPrd()!= null && esGoods.getDefPrd() && !esGoods.getPrds().isEmpty() ){

            vo.setPrdId(CollectionUtils.isNotEmpty(esGoods.getPrds())?esGoods.getPrds().get(0).getPrdId():0);
        }
        if(StringUtils.isNotBlank(esGoods.getCatName())){
            String[] catName = esGoods.getCatName().split(" ");
            vo.setCatName(catName[catName.length-1]);
        }


        return vo;
    }
    private List<GoodsPrdMpVo> strToGoodsPrdMpVos(String jsonStr){
        try {
            return MAPPER.readValue(jsonStr,new TypeReference<List<GoodsPrdMpVo>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
