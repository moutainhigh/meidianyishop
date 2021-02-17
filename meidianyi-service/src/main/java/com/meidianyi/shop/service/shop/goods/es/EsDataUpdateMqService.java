package com.meidianyi.shop.service.shop.goods.es;


import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.jedis.data.label.MqEsGoodsLabel;
import com.meidianyi.shop.service.foundation.mq.RabbitmqSendService;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsGoodsMqParam;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSortService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.label.EsGoodsLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ElasticSearch External Service
 * @author luguangyao
 * @date 2019/10/23
 *
*/
@Service
public class EsDataUpdateMqService extends ShopBaseService {

    @Autowired
    private RabbitmqSendService rabbitmqSendService;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsSortService goodsSortService;




    /**
     *  update EsGoods data by goods id
     * @param goodsIds goodsIdList
     * @param shopId shop id
     */
    public void addEsGoodsIndex(List<Integer> goodsIds,Integer shopId,DBOperating operating){
        EsGoodsMqParam param = new EsGoodsMqParam();
        param.setIdList(goodsIds);
        param.setOperate(operating);
        param.setShopId(shopId);
        rabbitmqSendService.sendMessage(RabbitConfig.EXCHANGE_ES,RabbitConfig.BINDING_ES_GOODS_KEY,
            Util.toJson(param),param.getClass().getName());

    }
    /**
     *  update EsGoods data by brand id
     * @param brandId goods brand id
     * @param shopId shop id
     */
    public void updateEsGoodsIndexByBrandId(Integer brandId,Integer shopId){
        List<Integer> goodsIds = goodsService.getGoodsIdByBrandId(brandId);
        if( goodsIds.isEmpty() ){
            return ;
        }
        addEsGoodsIndex(goodsIds,shopId,DBOperating.UPDATE);
    }
    /**
     *  update EsGoods data by brand id
     * @param brandIds goods brand ids
     * @param shopId shop id
     */
    public void updateEsGoodsIndexByBrandId(List<Integer> brandIds,Integer shopId){
        List<Integer> goodsIds = goodsService.getGoodsIdByBrandId(brandIds);
        if( goodsIds.isEmpty() ){
            return ;
        }
        addEsGoodsIndex(goodsIds,shopId,DBOperating.UPDATE);
    }
    /**
     *  update EsGoods data by sortId id
     * @param sortId goods sortId id
     * @param shopId shop id
     */
    public void updateEsGoodsIndexBySortId(Integer sortId,Integer shopId){
        List<Integer> sortIds = goodsSortService.getChildSortIdsBySortId(sortId);
        sortIds.add(sortId);
        List<Integer> goodsIds = goodsService.getGoodsIdBySortId(sortIds);
        if( goodsIds.isEmpty() ){
            return ;
        }
        addEsGoodsIndex(goodsIds,shopId,DBOperating.UPDATE);
    }
    /**
     * update {@link EsGoodsLabel} data by labelId/goodsId
     * @param labelIds labelIds
     * @param shopId shop id
     * @param goodsIds goodsIds
     */
    public void updateGoodsLabelByLabelId(@NotNull Integer shopId, DBOperating operating,
                                          List<Integer> goodsIds, List<Integer> labelIds){
        MqEsGoodsLabel param = new MqEsGoodsLabel();
        if(goodsIds != null && !goodsIds.isEmpty()){
            param.setGoodsIds(goodsIds);
        }
        if( labelIds != null && !labelIds.isEmpty() ){
            param.setLabelIds(labelIds);
        }
        param.setOperating(operating);
        param.setShopId(shopId);
        rabbitmqSendService.sendMessage(RabbitConfig.EXCHANGE_ES,RabbitConfig.BINDING_EXCHANGE_ES_GOODS_LABEL_KEY,
            Util.toJson(param),param.getClass().getName());

    }

}
