package com.meidianyi.shop.service.shop.goods.es.convert.goods;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.video.GoodsVideoBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序商品详情页数据(ElasticSearch -> GoodsDetailMpBo)转换器
 * @author 卢光耀
 * @date 2019/11/18 3:42 下午
 *
*/
public class GoodsDetailBoConverter implements EsGoodsConvertInterface<GoodsDetailMpBo> {

    private final static ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public GoodsDetailMpBo convert(EsGoods esGoods) {
        GoodsDetailMpBo bo = new GoodsDetailMpBo();
        copyValues(bo,esGoods);
        // vip level price
        assemblyGrdPrice(esGoods,bo);

        bo.setBaseSale(esGoods.getAddSaleNum());
        bo.setIsOnSale(esGoods.getIsOnSale());
        bo.setCatId(esGoods.getCatId());
        bo.setSortId(esGoods.getSortId());
        bo.setUnit(esGoods.getUnit());
        if( StringUtils.isNotBlank(esGoods.getPrdJson()) ){
            bo.setProducts(strToGoodsPrdMpVos(esGoods.getPrdJson()));
        }

        List<String> secondaryGoodsImages = esGoods.getSecondaryGoodsImages();
        if( secondaryGoodsImages != null && !secondaryGoodsImages.isEmpty() ){
//            secondaryGoodsImages.add(0,esGoods.getGoodsImg());
            bo.setGoodsImgs(secondaryGoodsImages);
        }
        bo.setGoodsImg(esGoods.getGoodsImg());
        if( StringUtils.isNotBlank(esGoods.getVideoInfo()) ){
            GoodsVideoBo node = Util.parseJson(esGoods.getVideoInfo(),GoodsVideoBo.class);
            if (node != null) {
                bo.setGoodsVideoId(node.getId());
                bo.setGoodsVideoImg(node.getImgUrl());
                Double size = node.getSize()*1.0/1024/1024;
                bo.setGoodsVideoSize(BigDecimalUtil.setDoubleScale(size,2,true));
                bo.setGoodsVideo(node.getUrl());
                bo.setVideoWidth(node.getWidth());
                bo.setVideoHeight(node.getHeight());
            }
        }
        bo.setDeliverTemplateId(esGoods.getFreightTemplateId());
        bo.setLimitBuyNum(esGoods.getLimitBuyNum());
        bo.setLimitMaxNum(esGoods.getLimitMaxNum());
        bo.setIsExclusive(esGoods.getIsCardExclusive());
        bo.setIsPageUp(esGoods.getIsPageUp());
        bo.setGoodsDesc(esGoods.getGoodsDesc());
        bo.setGoodsAd(esGoods.getGoodsAd());
        bo.setBrandId(esGoods.getBrandId());
        bo.setBrandName(esGoods.getBrandName());
        bo.setGoodsWeight(esGoods.getGoodsWeight());
        bo.setGoodsNumber(esGoods.getGoodsNumber());
        bo.setCanRebate(esGoods.getCanRebate());
        bo.setGoodsPageId(esGoods.getGoodsPageId());
        bo.setRoomId(esGoods.getRoomId());
//        o.getAsString()

        return bo;
    }

    private void assemblyGrdPrice(EsGoods esGoods,GoodsDetailMpBo bo){
        List<GoodsDetailMpBo.GradePrd> gradePrdList = Lists.newArrayList();
        String vipGeneral = "v";
        int max = 10;
        for (int i = 1; i < max; i++) {
            try {
                String vipLevel = vipGeneral + i;
                Field f = EsGoods.class.getDeclaredField(vipLevel);
                f.setAccessible(true);
                //prd:price;prd:price;
                if( f.get(esGoods) != null ){
                    String priceStr = f.get(esGoods).toString();
                    String[] prdPrices = priceStr.split(";");
                    for (String prdPrice : prdPrices) {
                        String[] prdAndPrice = prdPrice.split(":");
                        GoodsDetailMpBo.GradePrd gradePrd = new GoodsDetailMpBo.GradePrd();
//                        gradePrd.setGrade(vipLevel);
                        gradePrd.setPrdId(Integer.valueOf(prdAndPrice[0]));
                        gradePrd.setGradePrice(BigDecimal.valueOf(Double.parseDouble(prdAndPrice[1])));
                        gradePrdList.add(gradePrd);
                    }
                    if( !gradePrdList.isEmpty() ){
                        bo.setGradeCardPrice(gradePrdList);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

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
