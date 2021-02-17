package com.meidianyi.shop.service.pojo.shop.market.givegift.record;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 孔德成
 * @date 2019/8/19 11:11
 */
@Data
public class GiveGiftRecordListVo {
    /**
     * 订单号
     */
    String mainOrderSn;
    /**
     * 支付金额
     */
    BigDecimal orderAmount;
    /**
     * 下单时间
     */
    Timestamp createTime;
    /**
     * 支付时间
     */
    Timestamp payTime;
    /**
     * 订单状态
     */
    Byte orderStatus;

    /**
     * 礼单类型
     */
    Byte giftType;

    /**
     * 送人ID
     */
    Integer userId;
    /**
     * 名称
     */
    String username;
    /**
     * 电话
     */
    String mobile;
    /**
     * 收礼人数
     */
    Integer receiveNum;
    /**
     * 退款数量
     */
    Integer returnNum;
    /**
     * 商量列表
     */
    List<GiftRecordGoodsVo> giftGoodsList;
}
