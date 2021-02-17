package com.meidianyi.shop.service.foundation.jedis.data.label;

import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;

import java.util.List;

/**
 * RabbitMQ es goods label param entity
 * @author luguangyao
 * @date 2019/11/21
 *
*/
public class MqEsGoodsLabel {


    private Integer shopId;


    private List<Integer> goodsIds;

    private List<Integer> labelIds;

    private DBOperating operating;

    public DBOperating getOperating() {
        return operating;
    }

    public void setOperating(DBOperating operating) {
        this.operating = operating;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public List<Integer> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(List<Integer> goodsIds) {
        this.goodsIds = goodsIds;
    }

    public List<Integer> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(List<Integer> labelIds) {
        this.labelIds = labelIds;
    }



}
