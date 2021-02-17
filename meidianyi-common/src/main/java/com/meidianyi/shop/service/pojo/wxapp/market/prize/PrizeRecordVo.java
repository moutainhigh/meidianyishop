package com.meidianyi.shop.service.pojo.wxapp.market.prize;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsMpVo;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 我的奖品
 * @author 孔德成
 * @date 2020/1/3 10:03
 */
@Getter
@Setter
public class PrizeRecordVo {

    private Integer   id;
    private Integer   activityId;
    private Integer   activityType;
    private Integer   prdId;
    private String    orderSn;
    private Byte      prizeStatus;
    private Integer   expiredDay;
    private Timestamp expiredTime;
    private Timestamp createTime;
    /**
     * 商品状态 0正常 1商品失效
     */
    private Byte goodsStatus = BaseConstant.NO;

    private OrderGoodsMpVo orderGoodsMpVo;

}
