package com.meidianyi.shop.service.pojo.wxapp.order.address;

import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsBaseCheckInfo;
import lombok.Data;

import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-26 14:46
 **/
@Data
public class OrderAddressParam {

    private String lat;

    private String lng;

    private List<StoreGoodsBaseCheckInfo> storeGoodsBaseCheckInfoList;

    private Byte deliveryType;

    private Integer addressId;
}
