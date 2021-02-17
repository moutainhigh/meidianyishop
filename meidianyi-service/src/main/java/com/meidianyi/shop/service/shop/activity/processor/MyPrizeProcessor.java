package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.PrizeRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.FpRewardContent;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.FriendPromoteSelectParam;
import com.meidianyi.shop.service.pojo.shop.market.friendpromote.FriendPromoteSelectVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.shop.market.friendpromote.FriendPromoteService;
import com.meidianyi.shop.service.shop.market.payaward.PayAwardRecordService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_GIFT;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_MY_PRIZE;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_PAY_AWARD;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.YES;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_EVALUATION;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_FRIEND_POWER;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_LOTTERY;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_SOURCE_PAY_AWARD;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_STATUS_EXPIRE;
import static com.meidianyi.shop.service.pojo.wxapp.market.prize.PrizeConstant.PRIZE_STATUS_RECEIVED;

/**
 * 我的奖品
 *
 * @author 孔德成
 * @date 2020/1/6 10:38
 */
@Component
public class MyPrizeProcessor extends ShopBaseService implements Processor, CreateOrderProcessor {

    @Autowired
    private PrizeRecordService prizeRecordService;
    @Autowired private FriendPromoteService friendPromoteService;
    @Autowired
    private PayAwardRecordService payAwardRecordService;
    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return ACTIVITY_TYPE_MY_PRIZE;
    }

    /**
     * 我的奖品初始化
     *  金额
     * @param param 参数
     * @throws MpException
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        logger().info("我的奖品>>>>{}", param.getGoods().get(0).getGoodsInfo().getGoodsName());
        String memberCardNo = param.getMemberCardNo();
        String couponSn = param.getCouponSn();
        //不使用优惠券和会员卡
        param.setMemberCardNo(StringUtils.EMPTY);
        param.setCouponSn(StringUtils.EMPTY);
        param.setIsFreeShippingAct(YES);
        //下架商品可以购买
        param.getGoods().forEach(goods -> {
            goods.getGoodsInfo().setIsOnSale(BaseConstant.YES);
        });
        PrizeRecordRecord prizeRecord = prizeRecordService.getById(param.getActivityId());
        if (prizeRecord.getPrizeStatus().equals(PRIZE_STATUS_RECEIVED)) {
            logger().info("奖品已经领取过了");
            throw new MpException(JsonResultCode.MY_PRIZE_ACTIVITY_RECEIVED, null);
        }
        if (prizeRecord.getPrizeStatus().equals(PRIZE_STATUS_EXPIRE)||prizeRecord.getExpiredTime().compareTo(DateUtils.getLocalDateTime())<=0) {
            logger().info("奖品过期了");
            throw new MpException(JsonResultCode.MY_PRIZE_ACTIVITY_EXPIRED, null);
        }

        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            goods.setProductPrice(BigDecimal.ZERO);
            goods.setGoodsPriceAction(ACTIVITY_TYPE_MY_PRIZE);
            if (prizeRecord.getActivityType().equals(PRIZE_SOURCE_FRIEND_POWER)){
                FriendPromoteSelectParam selectParam = new FriendPromoteSelectParam();
                selectParam.setId(prizeRecord.getActivityId());
                FriendPromoteSelectVo actRecord = friendPromoteService.selectOne(selectParam);
                //如果是折扣商品
                if (NumberUtils.BYTE_ONE.equals(actRecord.getRewardType())){
                    //如果可叠加优惠券、会员卡
                    if (NumberUtils.BYTE_ONE.equals(actRecord.getUseDiscount())){
                        param.setMemberCardNo(memberCardNo);
                        param.setCouponSn(couponSn);
                    }
                    //如果不可抵扣积分
                    if (NumberUtils.BYTE_ZERO.equals(actRecord.getUseScore())){
                        if(param.getPaymentList() != null) {
                            param.getPaymentList().remove(OrderConstant.PAY_CODE_SCORE_PAY);
                        }
                    }
                }
                FpRewardContent rewardContent = Util.json2Object(actRecord.getRewardContent(),FpRewardContent.class,false);
                if (rewardContent!=null){
                    goods.setProductPrice(rewardContent.getMarketPrice());
                }
            }
            switch (prizeRecord.getActivityType()){
                case PRIZE_SOURCE_PAY_AWARD:
                    logger().info("支付有礼");
//                collect.add(ACTIVITY_TYPE_PAY_AWARD);
                    break;
                case PRIZE_SOURCE_FRIEND_POWER:
                    logger().info("好友助力");
//                collect.add(ACTIVITY_TYPE_PROMOTE_ORDER);
                    break;
                case PRIZE_SOURCE_LOTTERY:
                    logger().info("大抽奖");
//                collect.add(ACTIVITY_TYPE_LOTTERY_PRESENT);
                    break;
                case PRIZE_SOURCE_EVALUATION:
                    logger().info("测评");
//                collect.add(ACTIVITY_TYPE_GIFT);
                    break;
                default:
            }
        }
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        logger().info("增加奖品来源的活动类型");
        List<Byte> collect = Arrays.stream(OrderInfoService.orderTypeToArray(order.getGoodsType())).map(Byte::valueOf).collect(Collectors.toList());
        PrizeRecordRecord prizeRecord = prizeRecordService.getById(param.getActivityId());
        switch (prizeRecord.getActivityType()){
            case PRIZE_SOURCE_PAY_AWARD:
                logger().info("支付有礼");
                collect.add(ACTIVITY_TYPE_PAY_AWARD);
                break;
            case PRIZE_SOURCE_FRIEND_POWER:
                logger().info("好友助力");
                collect.add(ACTIVITY_TYPE_PROMOTE_ORDER);
                break;
            case PRIZE_SOURCE_LOTTERY:
                logger().info("大抽奖");
                collect.add(ACTIVITY_TYPE_LOTTERY_PRESENT);
                break;
            case PRIZE_SOURCE_EVALUATION:
                logger().info("测评");
                collect.add(ACTIVITY_TYPE_GIFT);
                break;
            default:
        }
        order.setGoodsType(OrderInfoService.addGoodsTypeToInsert(order.getGoodsType(), collect));
    }

    /**
     * 领取奖品成功
     * @throws MpException 无
     */
    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        logger().info("奖品已成功领取");
        int i = prizeRecordService.updateReceivedPrize(param.getActivityId(), order.getOrderSn());
        PrizeRecordRecord prizeRecord = prizeRecordService.getById(param.getActivityId());
        if (prizeRecord.getActivityType().equals(PRIZE_SOURCE_PAY_AWARD)){
            payAwardRecordService.updataStatut(prizeRecord.getRecordId(),(byte)1);
        }
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) {

    }
}
