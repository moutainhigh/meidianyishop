package com.meidianyi.shop.service.shop.order.action;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.main.table.PlatformTotalRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsPlatformRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.dao.main.platform.PlatformTotalRebateDao;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.rebate.DoctorTotalRebateDao;
import com.meidianyi.shop.dao.shop.rebate.OrderGoodsPlatformRebateDao;
import com.meidianyi.shop.dao.shop.rebate.PrescriptionRebateDao;
import com.meidianyi.shop.db.shop.tables.OrderGoods;
import com.meidianyi.shop.db.shop.tables.records.FanliGoodsStatisticsRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRebateRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.member.card.ScoreJson;
import com.meidianyi.shop.service.pojo.shop.member.data.AccountData;
import com.meidianyi.shop.service.pojo.shop.member.data.ScoreData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.OrderGoodsPlatformRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateParam;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.shop.distribution.FanliGoodsStatisticsService;
import com.meidianyi.shop.service.shop.distribution.MpDistributionGoodsService;
import com.meidianyi.shop.service.shop.distribution.OrderGoodsRebateService;
import com.meidianyi.shop.service.shop.distribution.UserFanliStatisticsService;
import com.meidianyi.shop.service.shop.distribution.UserTotalFanliService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.member.ScoreCfgService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.Comparators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.UserScoreSet.USER_SCORE_SET;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.CONDITION_ONE;
import static com.meidianyi.shop.service.pojo.shop.market.increasepurchase.PurchaseConstant.CONDITION_ZERO;
import static com.meidianyi.shop.service.shop.member.ScoreCfgService.BUY;
import static com.meidianyi.shop.service.shop.member.ScoreCfgService.BUY_EACH;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author 王帅 
 */
@Component
public class FinishService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam,OrderOperateQueryParam> {
    @Autowired
    private OrderInfoService orderInfo;

    @Autowired
    public OrderGoodsService orderGoods;

    @Autowired
    OrderActionService orderAction;

    @Autowired
    public RecordAdminActionService record;

    @Autowired
    public ReturnOrderService returnOrder;

    @Autowired
    private UserCardService userCard;

    @Autowired
    public ScoreService scoreService;

    @Autowired
    private RecordTradeService recordMemberTrade;

    @Autowired
    public ScoreCfgService scoreCfgService;

    @Autowired
    private OrderGoodsRebateService orderGoodsRebate;

    @Autowired
    private FanliGoodsStatisticsService fanliGoodsStatistics;

    @Autowired
    private UserFanliStatisticsService userFanliStatistics;

    @Autowired
    private UserTotalFanliService userTotalFanli;

    @Autowired
    private UserService user;
    @Autowired
    private MpDistributionGoodsService mpDistributionGoods;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private PrescriptionRebateDao prescriptionRebateDao;
    @Autowired
    private DoctorTotalRebateDao doctorTotalRebateDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private OrderOperateSendMessage sendMessage;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private PlatformTotalRebateDao platformTotalRebateDao;
    @Autowired
    private OrderGoodsPlatformRebateDao orderGoodsPlatformRebateDao;

    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.FINISH;
    }

    @Override
    public Object query(OrderOperateQueryParam param) throws MpException {
        return null;
    }

    @Override
    public ExecuteResult execute(OrderOperateQueryParam param) {
        OrderInfoRecord orderRecord = orderInfo.getRecord(param.getOrderId());
        OrderInfoVo order = orderRecord.into(OrderInfoVo.class);

        //查询订单订单是否存在退款中订单
        Map<Integer, Integer> returningCount = returnOrder.getOrderCount(new Integer[]{order.getOrderId()}, OrderConstant.REFUND_STATUS_AUDITING, OrderConstant.REFUND_STATUS_AUDIT_PASS, OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);

        if (!OrderOperationJudgment.mpIsFinish(order, returningCount.get(order.getOrderId()))) {
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_FINISH_OPERATION_NOT_SUPPORTED, null);
        }

        transaction(() -> {
            //分销返利
            BigDecimal total = finishRebate(orderRecord, param.getIsMp());
            //update order
            orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_FINISHED);
            //订单完成赠送积分
            orderSendScore(order);
            //完成返利设置
            orderInfo.setOrderRebateInfo(orderRecord, total);
            //医师返利
            setDoctorRebate(orderRecord);
            //平台返利
            setPlatformRebate(orderRecord);
            //新增收支
        });
        //分销升级
        //action操作
        orderAction.addRecord(order, param, OrderConstant.ORDER_RECEIVED, "完成订单");
        //TODO 操作记录 b2c_record_admin_action  需要测试记录
        this.record.insertRecord(Arrays.asList(new Integer[]{RecordContentTemplate.ORDER_FINISH.code}), new String[]{param.getOrderSn()});
        return null;
    }

    /**
     * 医师返利
     * @param orderRecord
     */
    public void setDoctorRebate(OrderInfoRecord orderRecord){
        List<String> preCodeList=orderGoodsDao.getPrescriptionCodeListByOrderSn(orderRecord.getOrderSn());
        preCodeList=preCodeList.stream().distinct().collect(Collectors.toList());

        for(String preCode:preCodeList){
            PrescriptionVo prescriptionVo=prescriptionDao.getDoByPrescriptionNo(preCode);
            if(prescriptionVo==null||!PrescriptionConstant.SETTLEMENT_WAIT.equals(prescriptionVo.getSettlementFlag())){
                continue;
            }
            List<PrescriptionItemDo> itemList=prescriptionItemDao.listOrderGoodsByPrescriptionCode(preCode);
            itemList.forEach(item -> {
                OrderGoodsDo orderGoodsDo=orderGoodsDao.getByPrescriptionDetailCode(item.getPrescriptionDetailCode());
                //实际返利数量
                int rebateNumber = orderGoodsDo.getGoodsNumber() - orderGoodsDo.getReturnNumber();
                //实际返利金额
                item.setRealRebateMoney(
                    BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
                        BigDecimalUtil.BigDecimalPlus.create(item.getTotalRebateMoney(), BigDecimalUtil.Operator.multiply),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(rebateNumber), BigDecimalUtil.Operator.divide),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(orderGoodsDo.getGoodsNumber())))
                );
                //平台实际返利金额
                item.setPlatformRealRebateMoney(
                    BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
                        BigDecimalUtil.BigDecimalPlus.create(item.getPlatformRebateMoney(), BigDecimalUtil.Operator.multiply),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(rebateNumber), BigDecimalUtil.Operator.divide),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(orderGoodsDo.getGoodsNumber())))
                );
                prescriptionItemDao.updatePrescriptionItem(item);
            });
            BigDecimal realRebateTotalMoney=itemList.stream().map(PrescriptionItemDo::getRealRebateMoney).reduce(BIGDECIMAL_ZERO,BigDecimal::add);
            BigDecimal platformRealRebateTotalMoney=itemList.stream().map(PrescriptionItemDo::getPlatformRealRebateMoney).reduce(BIGDECIMAL_ZERO,BigDecimal::add);
            //更新实际返利金额
            prescriptionRebateDao.updateRealRebateMoney(preCode,realRebateTotalMoney,platformRealRebateTotalMoney);

            //更改处方返利状态
            prescriptionRebateDao.updateStatus(preCode, PrescriptionRebateConstant.REBATED,null);
            prescriptionDao.updateSettlementFlag(preCode, PrescriptionConstant.SETTLEMENT_FINISH);
            //获取医师id
            DoctorOneParam doctor=doctorService.getDoctorByCode(prescriptionVo.getDoctorCode());
            if(doctor!=null){
                //获取处方返利金额，统计医师返利金额
                PrescriptionRebateParam rebate= prescriptionRebateDao.getRebateByPrescriptionCode(preCode);
                doctorTotalRebateDao.updateDoctorTotalRebate(doctor.getId(),rebate.getRealRebateMoney());
            }
            //统计平台返利
            PlatformTotalRebateDo platformTotalRebateDo=new PlatformTotalRebateDo();
            platformTotalRebateDo.setShopId(getShopId());
            platformTotalRebateDo.setTotalMoney(platformRealRebateTotalMoney);
            platformTotalRebateDo.setFinalMoney(platformRealRebateTotalMoney);
            platformTotalRebateDao.savePlatFormTotalRebate(platformTotalRebateDo);
        }
    }

    /**
     * 平台返利信息
     * @param orderRecord
     */
    public void setPlatformRebate(OrderInfoRecord orderRecord){
        List<OrderGoodsRecord> goodsRecordList = orderGoods.getByOrderId(orderRecord.getOrderId()).into(OrderGoodsRecord.class);
        for(OrderGoodsRecord orderGoodsRecord:goodsRecordList){
            if(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT.equals(orderGoodsRecord.getMedicalAuditType())){
                //实际返利数量
                int rebateNumber = orderGoodsRecord.getGoodsNumber() - orderGoodsRecord.getReturnNumber();
                OrderGoodsPlatformRebateDo platformRebateDo = orderGoodsPlatformRebateDao.getByRecId(orderGoodsRecord.getRecId());
                if(platformRebateDo==null){
                    continue;
                }
                //实际返利金额
                platformRebateDo.setPlatformRealRebateMoney(
                    BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
                        BigDecimalUtil.BigDecimalPlus.create(platformRebateDo.getPlatformRebateMoney(), BigDecimalUtil.Operator.multiply),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(rebateNumber), BigDecimalUtil.Operator.divide),
                        BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(orderGoodsRecord.getGoodsNumber())))
                );
                platformRebateDo.setStatus(OrderGoodsPlatformRebateConstant.REBATED);
                orderGoodsPlatformRebateDao.update(platformRebateDo);
                //统计平台返利
                PlatformTotalRebateDo platformTotalRebateDo=new PlatformTotalRebateDo();
                platformTotalRebateDo.setShopId(getShopId());
                platformTotalRebateDo.setTotalMoney(platformRebateDo.getPlatformRealRebateMoney());
                platformTotalRebateDo.setFinalMoney(platformRebateDo.getPlatformRealRebateMoney());
                platformTotalRebateDao.savePlatFormTotalRebate(platformTotalRebateDo);
            }

        }
    }
    /**
     * 订单完成时进行返利
     * @param order
     * @param isMp
     * @throws MpException
     */
    private BigDecimal finishRebate(OrderInfoRecord order, Byte isMp) throws MpException {
        if(!order.getFanliType().equals(DistributionConstant.REBATE_ORDER) ||
            order.getSettlementFlag().equals(OrderConstant.YES) ||
            !order.getTkOrderType().equals(OrderConstant.TK_NORMAL)) {
            logger().info("完成订单时不满足返利条件");
            return BIGDECIMAL_ZERO;
        }
        //goods
        Result<OrderGoodsRecord> goods = orderGoods.getByOrderId(order.getOrderId());
        //分销记录
        Map<Integer, Result<OrderGoodsRebateRecord>> rebateRecords = orderGoodsRebate.get(order.getOrderSn()).intoGroups(OrderGoodsRebateRecord::getRecId);
        //需要更新的记录
        ArrayList<OrderGoodsRebateRecord> updateRecords = new ArrayList<>();
        //商品返利统计
        ArrayList<FanliGoodsStatisticsRecord> statisticsRecords = new ArrayList<>();
        //返利汇总
        Map<Integer, BigDecimal> collect = new HashMap<>();
        //该订单返利总计
        BigDecimal total = BIGDECIMAL_ZERO;
        //
        Collection<Integer> updateLevel = new HashSet<>();
        for (OrderGoodsRecord one: goods) {
            if(OrderConstant.YES == one.getIsGift()) {
                //赠品不参与
                continue;
            }

            //返利记录
            Result<OrderGoodsRebateRecord> records = rebateRecords.get(one.getRecId());
            if(CollectionUtils.isEmpty(records)) {
                continue;
            }
            //该商品返利金额
            BigDecimal realRebateMoney = BIGDECIMAL_ZERO;
            //实际返利数量
            int rebateNumber = one.getGoodsNumber() - one.getReturnNumber();
            for (OrderGoodsRebateRecord record : records) {
                record.setRealRebateMoney(BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.HALF_DOWN,
                    BigDecimalUtil.BigDecimalPlus.create(record.getTotalRebateMoney(), BigDecimalUtil.Operator.multiply),
                    BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(rebateNumber), BigDecimalUtil.Operator.divide),
                    BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(one.getGoodsNumber())))
                );
                realRebateMoney = realRebateMoney.add(record.getRealRebateMoney());
                updateRecords.add(record);
                collect.putIfAbsent(record.getRebateUserId(), BIGDECIMAL_ZERO);
                collect.computeIfPresent(record.getRebateUserId(), (k, v) -> v.add(record.getRealRebateMoney()));
            }
            //总计
            total = total.add(realRebateMoney);
            //商品返利统
            statisticsRecords.add(fanliGoodsStatistics.createRecord(realRebateMoney, one, rebateNumber));

            for (Map.Entry<Integer, BigDecimal> entry: collect.entrySet()) {
                if(BigDecimalUtil.compareTo(entry.getValue(), BIGDECIMAL_ZERO) < 1) {
                    continue;
                }
                //重算等级
                updateLevel.add(entry.getKey());
                //判断当前分销等级
                Byte level = entry.getKey().equals(order.getUserId()) ? DistributionConstant.REBATE_LEVEL_0 : entry.getKey().equals(order.getFanliUserId()) ? DistributionConstant.REBATE_LEVEL_1 : DistributionConstant.REBATE_LEVEL_2;
                //更改分销员数据汇总表
                userFanliStatistics.update(order.getUserId(), entry.getKey(), level, entry.getValue(), total);
                //更新分销员统计信息表
                userTotalFanli.updateTotalRebate(entry.getKey(), entry.getValue(), user.getInviteCount(entry.getKey()));
                //返利增加余额
                addRebateAccount(entry.getKey(), order.getOrderSn(), entry.getValue(), order.getUserId());
            }
            db().batchUpdate(updateRecords).execute();

        }
        //更新
        db().batchUpdate(updateRecords).execute();
        fanliGoodsStatistics.batchUpdate(statisticsRecords);
        //更新分销员等级
        updateUserLevel(updateLevel);
        return total;
    }

    private void updateUserLevel(Collection<Integer> updateLevel) {
        if(CollectionUtils.isEmpty(updateLevel)) {
            return;
        }
        mpDistributionGoods.distributorLevel.updateUserLevel(Lists.newArrayList(updateLevel), "自动升级",0);
    }

    /**
     * 自动任务:完成订单
     */
    public void autoFinishOrders() {
        Result<OrderInfoRecord> orders = orderInfo.autoFinishOrders();
        for (OrderInfoRecord order : orders) {
            OrderOperateQueryParam param = new OrderOperateQueryParam();
            param.setAction(Integer.valueOf(OrderServiceCode.FINISH.ordinal()).byteValue());
            param.setIsMp(OrderConstant.IS_MP_AUTO);
            param.setOrderId(order.getOrderId());
            param.setOrderSn(order.getOrderSn());
            ExecuteResult result = execute(param);
            if (result == null || result.isSuccess()) {
                logger().info("订单自动任务,完成成功,orderSn:{}", order.getOrderSn());
            } else {
                logger().error("订单自动任务,完成失败,orderSn:{},错误信息{}{}", order.getOrderSn(), result.getErrorCode().toString(), result.getErrorParam());
            }
        }
    }

    private void orderSendScore(OrderInfoVo order) throws MpException {
        if (order.getMemberCardId() > 0) {
            MemberCardRecord card = userCard.memberCardService.getCardById(order.getMemberCardId());
            if (StringUtils.isBlank(card.getBuyScore())) {
                return;
            }
            ScoreJson scoreJson = Util.parseJson(card.getBuyScore(), ScoreJson.class);
            //订单金额
            BigDecimal payMoney = BigDecimalUtil.addOrSubtrac(BigDecimalUtil.BigDecimalPlus.create(order.getMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(order.getMemberCardBalance(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(order.getUseAccount(), BigDecimalUtil.Operator.subtrac),
                BigDecimalUtil.BigDecimalPlus.create(order.getShippingFee())
            );
            BigDecimal sendScore = null;
            //0：购物满多少送多少积分；1：购物每满多少送多少积分
            if (BYTE_ONE.equals(scoreJson.getOffset())) {
                if (scoreJson.getPerGetScores().compareTo(ZERO) > 0 && scoreJson.getPerGoodsMoney().compareTo(ZERO) > 0) {
                    sendScore = BigDecimalUtil.multiply(new BigDecimal(BigDecimalUtil.divide(payMoney, scoreJson.getPerGoodsMoney()).intValue()),
                        scoreJson.getPerGetScores());
                    logger().info("支付完成送积分:会员卡id[{}],每满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", order.getMemberCardId(), scoreJson.getPerGoodsMoney(), scoreJson.getPerGetScores(), payMoney, sendScore);
                }
            } else if (BYTE_ZERO.equals(scoreJson.getOffset())) {
                BigDecimal currentRule = scoreJson.getGoodsMoney().stream().filter(e -> e.compareTo(payMoney) <= 0).max(Comparators.comparable()).orElse(BIGDECIMAL_ZERO);
                int index = -1;
                for (int i = 0, length = scoreJson.getGoodsMoney().size(); i < length; i++) {
                    if (scoreJson.getGoodsMoney().get(i).compareTo(currentRule) == 0) {
                        index = i;
                        break;
                    }
                }
                if(index == -1) {
                    return;
                }
                sendScore = scoreJson.getGetScores().get(index);
                logger().debug("支付完成送积分:会员卡id[{}],满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", order.getMemberCardId(), currentRule, sendScore, payMoney, sendScore);
            }
            if (BigDecimalUtil.compareTo(sendScore, BIGDECIMAL_ZERO) > 0) {
                sendScore(order.getOrderSn(), sendScore.intValue(), order.getUserId());
            }
        }else {
            if(BYTE_ZERO.equals(scoreCfgService.getShoppingScore())) {
                return;
            }
            // 非会员卡送积分逻辑
            //订单金额
            BigDecimal payMoney = BigDecimalUtil.addOrSubtrac(BigDecimalUtil.BigDecimalPlus.create(order.getMoneyPaid(), BigDecimalUtil.Operator.add),
                BigDecimalUtil.BigDecimalPlus.create(order.getUseAccount(), BigDecimalUtil.Operator.subtrac),
                BigDecimalUtil.BigDecimalPlus.create(order.getShippingFee())
            );
            int sendScore = 0;
            //购物送积分类型： 0： 购物满；1：购物每满
            byte scoreType = scoreCfgService.getScoreType();
            //购物满
            if (scoreType == CONDITION_ZERO) {
                Result<Record2<String, String>> record2s = scoreCfgService.getValFromUserScoreSet(BUY, payMoney.toString());
                if(CollectionUtils.isEmpty(record2s)) {
                    return;
                }
                // 满...金额
                String setVal = record2s.getValue(0, USER_SCORE_SET.SET_VAL);
                // 送...积分
                String setVal2 = record2s.getValue(0, USER_SCORE_SET.SET_VAL2);
                if (org.apache.commons.lang3.StringUtils.isBlank(setVal2)) {
                    return;
                }
                sendScore = Integer.parseInt(setVal2);
                logger().info("支付完成送积分:非会员卡满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", setVal, setVal2, payMoney, sendScore);
            } else if (scoreType == CONDITION_ONE) {
                //购物每满
                Result<Record2<String, String>> record2s = scoreCfgService.getValFromUserScoreSet(BUY_EACH, payMoney.toString());
                if(CollectionUtils.isEmpty(record2s)) {
                    return;
                }
                // 每满...金额
                String setVal = record2s.getValue(0, USER_SCORE_SET.SET_VAL);
                // 送...积分
                String setVal2 = record2s.getValue(0, USER_SCORE_SET.SET_VAL2);
                if (org.apache.commons.lang3.StringUtils.isBlank(setVal) || org.apache.commons.lang3.StringUtils.isBlank(setVal2)) {
                    return;
                }
                sendScore = BigDecimalUtil.divide(payMoney, new BigDecimal(setVal)).intValue() * Integer.valueOf(setVal2);
                logger().debug("支付完成送积分:非会员卡每满[{}]元,送[{}]积分;订单金额[{}],赠送积分[{}]", setVal, setVal2, payMoney, sendScore);
            }
            sendScore(order.getOrderSn(), sendScore, order.getUserId());
        }
    }

    /**
     * 订单完成送积分
     * @param orderSn
     * @param score
     * @param userId
     * @throws MpException
     */
    public void sendScore(String orderSn, Integer score, Integer userId) throws MpException {
        if (score <= 0) {
            return;
        }
        ScoreData scoreData = ScoreData.newBuilder().
            userId(userId).
            orderSn(orderSn).
            //积分
            score(score).
            remarkCode(RemarkTemplate.ORDER_FINISH_SEND_SCORE.code).
            remarkData(orderSn).
            // remark("下单："+order.getOrderSn()).
            //后台处理时为操作人id为0
            adminUser(0).
            //积分
            tradeType(RecordTradeEnum.TYPE_SCORE_PAY.val()).
            //资金流量-收入
            tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).
            //积分变动是否来自退款
           isFromRefund(RecordTradeEnum.IS_FROM_REFUND_N.val()).build();
        //调用退积分接口
        recordMemberTrade.updateUserEconomicData(scoreData);
    }

    /**
     * 返利增加
     * @param userId
     * @param orderSn
     * @param money
     * @param orderUserId
     * @throws MpException 见具体方法
     */
    public void addRebateAccount(Integer userId, String orderSn, BigDecimal money, Integer orderUserId) throws MpException {
        logger().info("返利增加用户余额start:{}", money);
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        UserInfo userInfo = user.getUserInfo(orderUserId);
        AccountData accountData = AccountData.newBuilder().
            userId(userId).
            orderSn(orderSn).
            //返利金额
            amount(money).
            remarkCode(RemarkTemplate.ORDER_REBATE.code).
            remarkData(userInfo.getUsername()).
            payment("fanli").
            //支付类型
            isPaid(RecordTradeEnum.UACCOUNT_RECHARGE.val()).
            //后台处理时为操作人id为0
            adminUser(0).
            //用户余额支付
            tradeType(RecordTradeEnum.TYPE_CRASH_REBATE.val()).
            //资金流量-支出
            tradeFlow(RecordTradeEnum.TRADE_FLOW_OUT.val()).build();
        //调用退余额接口
        recordMemberTrade.updateUserEconomicData(accountData);
        logger().info("返利增加用户余额end:{}", money);
    }
}
