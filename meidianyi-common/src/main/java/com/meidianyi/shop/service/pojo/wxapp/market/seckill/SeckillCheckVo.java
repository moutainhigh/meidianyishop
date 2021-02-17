package com.meidianyi.shop.service.pojo.wxapp.market.seckill;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: 王兵兵
 * @create: 2020-03-11 17:24
 **/
@Setter
@Getter
public class SeckillCheckVo {

    /**
     * 0正常可用;1该活动不存在;2该活动已停用;3该活动未开始;4该活动已结束;5商品已抢光;6该用户已达到限购数量上限;
     *        7该秒杀为会员专属，该用户没有对应会员卡；8该规格无库存；9有待支付的秒杀订单
     */
    private Byte state = 0;

    /**
     * state == 6时，限购数量之内剩余的用户可购买数量。
     */
    private Integer diffNumber;
    /**
     * state == 6时，订单号
     */
    private String orderSn;
}
