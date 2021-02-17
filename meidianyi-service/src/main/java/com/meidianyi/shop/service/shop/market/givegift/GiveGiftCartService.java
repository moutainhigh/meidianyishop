package com.meidianyi.shop.service.shop.market.givegift;

import com.meidianyi.shop.service.shop.order.info.AdminMarketOrderInfoService;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiftRecordGoodsVo;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiveGiftRecordListParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.record.GiveGiftRecordListVo;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;

/**
 *
 *  礼物车
 * @author 孔德成
 * @date 2019/8/16 18:40
 */
@Service
public class GiveGiftCartService extends ShopBaseService {

    @Autowired
    private AdminMarketOrderInfoService orderInfo;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private GiveGiftReceiveService giftReceive;

    /**
     *  根据活动ID查询 送礼数量--已经交易的礼物车数量
     * @param activityId 活动id
     * @return 返回活动的数量
     */
    public Integer getSendGiftOrderCt(Integer activityId) {
         return orderInfo.getSendGiftOrderCt(activityId);
    }

    /**
     * 送礼明细列表
     * @param param  GiveGiftRecordListParam
     * @return PageResult<GiveGiftRecordListVo>
     */
    public PageResult<GiveGiftRecordListVo> giveGiftRecordList(GiveGiftRecordListParam param) {
        SelectJoinStep<? extends Record> selectJoinStep = orderInfo.giveGiftRecordList(param);
        PageResult<GiveGiftRecordListVo> pageResult = getPageResult(selectJoinStep, param.getCurrentPage(), param.getPageRows(), GiveGiftRecordListVo.class);
        pageResult.dataList.forEach(giftCart->{
            Result<? extends Record> records = orderGoods.getGoodsInfoByOrderSn(giftCart.getMainOrderSn());
            Integer receiveNum = giftReceive.getReceiveNumByoOrderSn(giftCart.getMainOrderSn());
            Integer returnNum = orderInfo.getReturnNumByMainOrderSn(giftCart.getMainOrderSn());
            giftCart.setReturnNum(returnNum);
            giftCart.setReceiveNum(receiveNum);
            giftCart.setGiftGoodsList(records.into(GiftRecordGoodsVo.class));
        });
        return pageResult;
    }



}
