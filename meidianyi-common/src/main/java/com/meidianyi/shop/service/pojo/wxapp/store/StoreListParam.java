package com.meidianyi.shop.service.pojo.wxapp.store;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The type Store list param.
 *
 * @author liufei
 * @date 10 /15/19
 */
@Data
public class StoreListParam {
    /**
     * The Location.用户位置信息json
     * "{"latitude":39.95933,"longitude":116.29845,"speed":-1,"accuracy":65,"verticalAccuracy":65,"horizontalAccuracy":65,"errMsg":"getLocation:ok"}"
     */
    public Location location;
    /**
     * The Type.
     * type为0,普通入口
     * type为1,并且cardId不为空;表示入口为会员卡详情页
     * type为2 ,并且goodsId不为空表示入口为商品详情页自提/同城配送过来
     */
    public Byte type = 0;
    /**
     * The Scan stores.筛选支持扫码购的门店;1:是,0:否
     */
    @JsonProperty("scan_stores")
    @JsonAlias({"scan_stores", "scanStores"})
    public Byte scanStores;
    /**
     * The Goods id.商品id
     */
    @JsonProperty("goods_id")
    @JsonAlias({"goods_id", "goodsId"})
    public Integer goodsId;

    /**
     * The Card id.会员卡id
     */
    @JsonProperty("card_id")
    @JsonAlias({"card_id", "cardId"})
    public Integer cardId;

    /**
     * The Deliver type.0:门店自提,1同城配送
     */
    @JsonProperty("deliver_type")
    @JsonAlias({"deliver_type", "deliverType"})
    public Byte deliverType;

    /**
     * 是否开启位置授权，0未开启，1开启（默认未开启）
     */
    @JsonProperty("location_auth")
    @JsonAlias({"location_auth", "locationAuth"})
    public byte locationAuth = 0;

}
