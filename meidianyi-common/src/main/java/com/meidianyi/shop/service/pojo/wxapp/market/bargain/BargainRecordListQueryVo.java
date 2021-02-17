package com.meidianyi.shop.service.pojo.wxapp.market.bargain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-10-24 11:48
 **/
@Data
public class BargainRecordListQueryVo {
    /**
     * 主键
     */
    private Integer id;
    private Integer bargainId;
    private Timestamp createTime;
    /**
     * 已砍金额
     */
    private BigDecimal bargainMoney;
    /**
     * 参与砍价人数
     */
    private Integer userNumber;


    private Integer prdId;
    private Integer goodsId;
    private String goodsName;
    private BigDecimal goodsPrice;
    private String goodsImg;
    private Integer goodsNumber;



    /**
     * 任意金额结算模式的结算金额底价
     */
    private BigDecimal floorPrice;

    /**
     *固定人数模式， 预期砍价最低金额
     */
    private BigDecimal expectationPrice;

    /**
     * 砍价类型0定人1任意价
     */
    private Byte bargainType;
    /**
     * 砍价库存
     */
    private Integer stock;
    /**
     * 砍价活动结束时间
     */
    private Timestamp endTime;

    /**
     * 订单状态（未结算时没有订单）
     */
    private Byte orderStatus;
    private String orderSn;
}
