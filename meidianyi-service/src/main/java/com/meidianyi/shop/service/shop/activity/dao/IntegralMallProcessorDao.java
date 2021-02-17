package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.IntegralMallDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.IntegralMallProductRecord;
import com.meidianyi.shop.db.shop.tables.records.IntegralMallRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.market.integralconvert.IntegralConvertProductVo;
import com.meidianyi.shop.service.pojo.shop.market.integralconvert.IntegralConvertSelectParam;
import com.meidianyi.shop.service.pojo.shop.market.integralconvert.IntegralConvertSelectVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.integral.IntegralMallMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.integral.IntegralMallPrdMpVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.market.integralconvert.IntegralConvertService;
import com.meidianyi.shop.service.shop.member.ScoreCfgService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.IntegralMallDefine.INTEGRAL_MALL_DEFINE;
import static com.meidianyi.shop.db.shop.tables.IntegralMallProduct.INTEGRAL_MALL_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.IntegralMallRecord.INTEGRAL_MALL_RECORD;

/**
 * @author 李晓冰
 * @date 2020年03月03日
 */
@Service
public class IntegralMallProcessorDao extends IntegralConvertService {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ScoreCfgService scoreCfgService;

    /**
     * 获取积分兑换商品活动信息
     * @param activityId 活动id
     * @param userId 用户id
     * @return IntegralMallMpVo
     */
    public IntegralMallMpVo getIntegralMallInfoForDetail(Integer activityId, Integer userId) {
        IntegralMallMpVo vo = new IntegralMallMpVo();
        vo.setActivityId(activityId);
        vo.setActivityType(BaseConstant.ACTIVITY_TYPE_INTEGRAL);
        vo.setActState(BaseConstant.ACTIVITY_STATUS_CAN_USE);

        IntegralMallDefineRecord integralMallRecord = getRecordByIdDao(activityId);
        int userJoinNum = getUserJoinTime(activityId, userId);
        Timestamp now = DateUtils.getLocalDateTime();
        if (integralMallRecord == null) {
            logger().debug("小程序-商品详情-积分兑换商品信息-活动已删除");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_HAS);
            return vo;
        } else if (BaseConstant.ACTIVITY_STATUS_DISABLE.equals(integralMallRecord.getStatus())) {
            logger().debug("小程序-商品详情-积分兑换商品信息-已停用");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_DISABLE);
        } else if (now.compareTo(integralMallRecord.getStartTime()) < 0) {
            logger().debug("小程序-商品详情-积分兑换商品信息-活动未开始");
            vo.setStartTime((integralMallRecord.getStartTime().getTime() - now.getTime()) / 1000);
            vo.setEndTime((integralMallRecord.getEndTime().getTime() - now.getTime()) / 1000);
            vo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_START);
        } else if (now.compareTo(integralMallRecord.getEndTime()) > 0) {
            logger().debug("小程序-商品详情-积分兑换商品信息-活动已结束");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_END);
        } else {
            vo.setEndTime((integralMallRecord.getEndTime().getTime() - now.getTime()) / 1000);
            if (userJoinNum > integralMallRecord.getMaxExchangeNum()&&integralMallRecord.getMaxExchangeNum()!=0) {
                logger().debug("小程序-商品详情-积分兑换商品信息-用户参与达到上限");
                vo.setActState(BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT);
            }
        }
        if (integralMallRecord.getMaxExchangeNum() == 0) {
            vo.setMaxExchangeNum(-1);
        } else {
            vo.setMaxExchangeNum(integralMallRecord.getMaxExchangeNum()-userJoinNum);
        }

        vo.setUserScore(scoreService.getUserScore(userId));
        vo.setRedeemNum(getUserJoinTime(activityId, null));

        List<IntegralMallProductRecord> integralPrdInfo = getIntegralPrdInfo(activityId);
        List<IntegralMallPrdMpVo> prds = integralPrdInfo.stream().map(x -> x.into(IntegralMallPrdMpVo.class)).collect(Collectors.toList());
        vo.setIntegralMallPrdMpVos(prds);
        return vo;
    }

    /**
     * 根据活动id获取活动信息
     * @param activityId 活动id
     * @return IntegralMallDefineRecord
     */
    private IntegralMallDefineRecord getRecordByIdDao(Integer activityId) {
        return db().selectFrom(INTEGRAL_MALL_DEFINE)
            .where(INTEGRAL_MALL_DEFINE.ID.eq(activityId).and(INTEGRAL_MALL_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchAny();
    }

    /**
     * 获取用户已兑换某一活动的商品总数量
     * @param activityId 活动id
     * @param userId 用户id 当id为null表示查询所有已参与的情况
     * @return 参与数量
     */
    private int getUserJoinTime(Integer activityId, Integer userId) {
        Condition condition = INTEGRAL_MALL_RECORD.INTEGRAL_MALL_DEFINE_ID.eq(activityId);
        if (userId!=null){
            condition =condition.and(INTEGRAL_MALL_RECORD.USER_ID.eq(userId));
        }
        Integer sum = db().select(DSL.sum(INTEGRAL_MALL_RECORD.NUMBER)).from(INTEGRAL_MALL_RECORD).where(condition).fetchOneInto(Integer.class);
        return sum== null ? 0 : sum;
    }

    /**
     * 获取活动对应的商品规格信息
     * @param activityId 活动id
     * @return List<IntegralMallProductRecord>
     */
    private List<IntegralMallProductRecord> getIntegralPrdInfo(Integer activityId){
      return  db().selectFrom(INTEGRAL_MALL_PRODUCT).where(INTEGRAL_MALL_PRODUCT.INTEGRAL_MALL_DEFINE_ID.eq(activityId))
            .fetchInto(IntegralMallProductRecord.class);
    }

    /**
     * 活动详情
     * @param activityId
     * @return
     */
    public IntegralConvertSelectVo getDetail(Integer activityId) {
        return selectOne(new IntegralConvertSelectParam(activityId));

    }

    public void orderCheck(OrderBeforeParam param, IntegralConvertSelectVo activityInfo) throws MpException {
        if (activityInfo == null ||
            param.getDate().after(activityInfo.getEndTime()) ||
            DelFlag.DISABLE_VALUE.equals(activityInfo.getDelFlag()) ||
            BaseConstant.ACTIVITY_STATUS_DISABLE.equals(activityInfo.getStatus()) ||
            activityInfo.getGoodsId() == null ||
            CollectionUtils.isEmpty(activityInfo.getProductVo())) {
            logger().error("积分兑换活动停用");
            throw new MpException(JsonResultCode.CODE_ORDER_ACTIVITY_DISABLE);
        }
        //此次购买数量
        int currentNum = 0;
        //
        Map<Integer, IntegralConvertProductVo> productMap = activityInfo.getProductVo().stream().collect(Collectors.toMap(IntegralConvertProductVo::getPrdId, Function.identity()));
        for (OrderBeforeParam.Goods goods : param.getGoods()) {

            IntegralConvertProductVo actPrd = productMap.get(goods.getProductId());
            //累加
            currentNum += goods.getGoodsNumber();
            if(actPrd == null) {
                logger().error("积分兑换规格不存在");
                throw new MpException(JsonResultCode.CODE_ORDER_GOODS_NOT_EXIST);
            }
            if(scoreService.getUserScore(param.getWxUserInfo().getUserId()) < actPrd.getScore()) {
                logger().error("当前用户积分不足，无法兑换");
                throw new MpException(JsonResultCode.CODE_MEMBER_SCORE_NOT_ENOUGH);
            }
            if(actPrd.getStock().intValue() < goods.getGoodsNumber()) {
                logger().error("积分兑换活动库存不足");
                throw new MpException(JsonResultCode.CODE_ORDER_ACTIVITY_GOODS_OUT_OF_STOCK);
            }
        }
        if(activityInfo.getMaxExchangeNum() != 0) {
            if(currentNum > activityInfo.getMaxExchangeNum() || currentNum + getUserJoinTime(param.getWxUserInfo().getUserId(), activityInfo.getId()) > activityInfo.getMaxExchangeNum()) {
                logger().error("积分兑换活动超出兑换次数");
                throw new MpException(JsonResultCode.CODE_ORDER_ACTIVITY_NUMBER_LIMIT);
            }
        }
    }

    public void orderInit(OrderBeforeParam param, IntegralConvertSelectVo activityInfo) throws MpException {
        //不使用优惠券
        param.setCouponSn(StringUtils.EMPTY);
        param.setMemberCardNo(StringUtils.EMPTY);
        //免运费
        param.setIsFreeShippingAct(OrderConstant.YES);
        //禁止好友代付
        param.getInsteadPayCfg().setStatus(false);
        //禁用货到付款、积分支付
        if(param.getPaymentList() != null) {
            param.getPaymentList().remove(OrderConstant.PAY_CODE_SCORE_PAY);
            param.getPaymentList().remove(OrderConstant.PAY_CODE_COD);
        }
        //初始化输入积分
        param.setScoreDiscount(0);
        //计算价格
        Map<Integer, IntegralConvertProductVo> productMap = activityInfo.getProductVo().stream().collect(Collectors.toMap(IntegralConvertProductVo::getPrdId, Function.identity()));
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            IntegralConvertProductVo prd = productMap.get(goods.getProductId());
            //实际价格 = 兑换价格 + 积分(积分换算成对应金额)
            goods.setProductPrice(
                BigDecimalUtil.add(
                    prd.getMoney(),
                    BigDecimalUtil.divide(new BigDecimal(prd.getScore()), new BigDecimal(scoreCfgService.getScoreProportion()))));
            goods.setGoodsScore(prd.getScore());
            //后台设置此次需要的积分
            param.setScoreDiscount(param.getScoreDiscount() + goods.getGoodsScore() * goods.getGoodsNumber());
        }
        //积分兑换跳过下架校验
        param.getGoods().forEach(goods -> {
            goods.getGoodsInfo().setIsOnSale(BaseConstant.YES);
        });
    }

    /**
     * 库存更新
     * @param activityId 活动id
     * @param updateParam k:prdId;v:num
     */
    public void updateStockAndSales(Integer activityId, Map<Integer, Integer> updateParam) throws MpException {
        logger().info("积分兑换更新活动库存start");
        Map<Integer, IntegralMallProductRecord> prds = db().selectFrom(imp).where(imp.INTEGRAL_MALL_DEFINE_ID.eq(activityId).and(imp.PRODUCT_ID.in(updateParam.keySet()))).fetchMap(imp.PRODUCT_ID);
        List<IntegralMallProductRecord> executeParam = new ArrayList<>(prds.size());
        for (Map.Entry<Integer, Integer> entry : updateParam.entrySet()) {
            IntegralMallProductRecord prd = prds.get(entry.getKey());
            if(prd == null) {
                if(entry.getValue() < 0) {
                    continue;
                }else {
                    logger().error("updateStockAndSales.积分兑换下单,商品不存在，id:{}", entry.getKey());
                    throw new MpException(JsonResultCode.CODE_ORDER_GOODS_NOT_EXIST, entry.getKey().toString());
                }
            }
            if (entry.getValue() > 0 && prd.getStock() < entry.getValue()) {
                logger().error("updateStockAndSales.积分兑换下单,库存不足，id:{}", entry.getKey());
                throw new MpException(JsonResultCode.CODE_ORDER_GOODS_OUT_OF_STOCK, null);
            }
            logger().info("{}积分兑换下单更新活动库存,prdId:{},num:{}", entry.getValue() > 0 ? "下单" : "退款", entry.getKey(), entry.getValue());
            prd.setStock((short) (prd.getStock() - entry.getValue()));
            executeParam.add(prd);
        }
        db().batchUpdate(executeParam).execute();
        logger().info("积分兑换更新活动库存end");
    }

    /**
     * 积分兑换增加兑换记录
     * @param order 订单
     * @param updateParam k:prdId;v:OrderGoodsBo
     */
    public void addRecords(OrderInfoRecord order, Map<Integer, OrderGoodsBo> updateParam) {
        logger().info("积分兑换增加记录start");
        List<IntegralMallRecordRecord> executeParam = new ArrayList<>(updateParam.size());
        for (Map.Entry<Integer, OrderGoodsBo> entry : updateParam.entrySet()) {
            IntegralMallRecordRecord record = db().newRecord(imr);
            record.setOrderSn(order.getOrderSn());
            record.setIntegralMallDefineId(order.getActivityId());
            record.setProductId(entry.getKey());
            //商品id
            record.setGoodsId(entry.getValue().getGoodsId());
            //积分
            record.setScore(entry.getValue().getGoodsScore());
            //金额
            record.setMoney(entry.getValue().getGoodsPrice());
            record.setNumber(entry.getValue().getGoodsNumber().shortValue());
            record.setUserId(order.getUserId());
            executeParam.add(record);
        }
        db().batchInsert(executeParam).execute();
        logger().info("积分兑换增加记录end");
    }

    /**
     * 删除记录
     * @param activityId 活动id
     * @param updateParam prdId
     */
    public void removeRecords(Integer activityId, Set<Integer> updateParam) {
        logger().info("积分兑换删除记录start");
        if(activityId == null || CollectionUtils.isEmpty(updateParam)) {
            return;
        }
        int execute = db().update(imr).set(imr.DEL_FLAG, DelFlag.DISABLE_VALUE).where(imr.INTEGRAL_MALL_DEFINE_ID.eq(activityId).and(imr.PRODUCT_ID.in(updateParam))).execute();
        logger().info("积分兑换删除记录end,受影响行数：{}", execute);
    }
}
