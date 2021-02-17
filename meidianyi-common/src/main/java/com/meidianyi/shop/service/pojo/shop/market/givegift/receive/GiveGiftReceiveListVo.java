package com.meidianyi.shop.service.pojo.shop.market.givegift.receive;

import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiftRecordGoodsVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/8/19 12:03
 */
@Data
public class GiveGiftReceiveListVo {
    Integer receiveuserId;
    String receiveusername;
    String receivemobile;
    Integer giveruserId;
    String giverusername;
    String givermobile;
    Integer productId;
    String mainOrderSn;
    Timestamp createTime;
    String orderSn;
    Byte status;
    String statusName;
    Byte orderStatus;
    String orderStatusName;
    Byte giftType;
    String goodsName;
    /**
     *  商品列表
     */
    private List<GiftRecordGoodsVo> giftGoodsList;
}
