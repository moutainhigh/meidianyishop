package com.meidianyi.shop.service.shop.order.action;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.AbstractExcelDisposer;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalGateConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiJsonResult;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsPlatformRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.rebate.OrderGoodsPlatformRebateDao;
import com.meidianyi.shop.dao.shop.rebate.PrescriptionRebateDao;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.trade.ReturnBusinessAddressParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.api.ApiReturnParam;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam.ReturnGoods;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundVo.RefundVoGoods;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.OrderGoodsPlatformRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateConstant;
import com.meidianyi.shop.service.shop.activity.factory.OrderCreateMpProcessorFactory;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardExchangeService;
import com.meidianyi.shop.service.shop.config.ShopReturnConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import com.meidianyi.shop.service.shop.market.groupdraw.GroupDrawService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperationJudgment;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import com.meidianyi.shop.service.shop.order.record.ReturnStatusChangeService;
import com.meidianyi.shop.service.shop.order.refund.ReturnMethodService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.shop.order.refund.goods.ReturnOrderGoodsService;
import com.meidianyi.shop.service.shop.order.refund.record.OrderRefundRecordService;
import com.meidianyi.shop.service.shop.order.refund.record.RefundAmountRecordService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.RT_ONLY_MONEY;

/**
 * @author 王帅
 */
@Component
public class ReturnService extends ShopBaseService implements IorderOperate<OrderOperateQueryParam, RefundParam> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderInfoService orderInfo;
    @Autowired
    private RefundAmountRecordService refundAmountRecord;
    @Autowired
    private ReturnOrderService returnOrder;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private ReturnOrderGoodsService returnOrderGoods;
    @Autowired
    private OrderActionService orderAction;
    @Autowired
    private ReturnMethodService returnMethod;
    @Autowired
    private GoodsService goods;
    @Autowired
    private GoodsSpecProductService goodsSpecProduct;
    @Autowired
    private ReturnStatusChangeService returnStatusChange;
    @Autowired
    public RecordAdminActionService record;
    @Autowired
    public ShopReturnConfigService shopReturncfg;
    @Autowired
    public OrderCreateMpProcessorFactory orderCreateMpProcessorFactory;
    @Autowired
    public GroupBuyService groupBuyService;
    @Autowired
    public GroupDrawService groupDraw;
    @Autowired
    private OrderOperateSendMessage sendMessage;
    @Autowired
    private CouponService coupon;
    @Autowired
    private OrderRefundRecordService orderRefundRecord;
    @Autowired
    private ShopReturnConfigService returnCfg;
    @Autowired
    private AtomicOperation atomicOperation;
    @Autowired
    private WxCardExchangeService wxCardExchange;
    @Autowired
    private Calculate calculate;
    @Autowired
    private PrescriptionRebateDao prescriptionRebateDao;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private OrderGoodsPlatformRebateDao orderGoodsPlatformRebateDao;

    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.RETURN;
    }

    /**
     * 退款、退货查询
     *
     * @param param param
     * @return ExecuteResult RefundVo
     * @throws MpException 见代码
     */
    @Override
    public ExecuteResult execute(RefundParam param) {
        logger.info("退款退货执行start(ReturnService)");
        ExecuteResult returnOrder1 = checkRefundInfo(param);
        if (returnOrder1 != null) {
            return returnOrder1;
        }
        //获取订单详情
        OrderInfoVo order = orderInfo.getByOrderId(param.getOrderId(), OrderInfoVo.class);
        if (order == null) {
            logger.error("退款退货执行异常，订单不存在");
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_NOT_EXIST, "订单不存在", null);
        }
        //result
        ExecuteResult result = ExecuteResult.create();
        //微信退款错误信息
        AtomicReference<String[]> wxRefundErrorInfo = new AtomicReference<String[]>();
        ExecuteResult e = refund(param, order, result, wxRefundErrorInfo);
        if (e != null) {
            return e;
        }
        //操作记录
        record.insertRecord(Arrays.asList(new Integer[]{RecordContentTemplate.ORDER_RETURN.code}), new String[]{param.getOrderSn()});
        ReturnOrderRecord rOrder = (ReturnOrderRecord) result.getResult();

        //判断
        if (!orderRefundRecord.isExistFail(rOrder.getRetId())) {
            //消息推送
            sendMessage.send(rOrder, returnOrderGoods.getReturnGoods(rOrder.getOrderSn(), rOrder.getRetId()));
            if(param.getIsMp().equals(OrderConstant.IS_MP_Y)){
                sendMessage.sendWaitSaleAfterMessage(order,rOrder);
            }
            result.setResult(rOrder.getReturnOrderSn());
            return result;
        } else {
            result.setErrorCode(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_ERROR);
            result.setErrorParam(wxRefundErrorInfo.get());
            result.setResult(null);
            return result;
        }

    }

    /**
     * 更改处方返利信息
     * @param returnGoods
     * @param order
     */
    public void updatePrescriptionRebateStatus(List<OrderReturnGoodsVo> returnGoods, OrderInfoVo order){
        if(CollectionUtils.isEmpty(returnGoods)) {
            return;
        }

        List<String> preCodeList=orderGoodsDao.getPrescriptionCodeListByOrderSn(order.getOrderSn());
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
            List<OrderGoodsDo> orderGoodsDoList=orderGoodsDao.getByPrescription(preCode);
            int returnNums=orderGoodsDoList.stream().mapToInt(OrderGoodsDo::getReturnNumber).sum();
            int goodsNums=orderGoodsDoList.stream().mapToInt(OrderGoodsDo::getGoodsNumber).sum();
            //商品退完
            if(goodsNums==returnNums){
                //更改处方返利状态
                prescriptionRebateDao.updateStatus(preCode, PrescriptionRebateConstant.REBATE_FAIL,PrescriptionRebateConstant.REASON_RETURNED);
                prescriptionDao.updateSettlementFlag(preCode,PrescriptionConstant.SETTLEMENT_NOT);
            }

        }

    }

    /**
     * 平台返利
     * @param returnGoods
     */
    public void updatePlatformRebate(List<OrderReturnGoodsVo> returnGoods){
        for(OrderReturnGoodsVo returnGoodsVo:returnGoods){
            OrderGoodsRecord goodsRecord = orderGoodsDao.getByRecId(returnGoodsVo.getRecId());
            //不审核的药品,平台单独返利
            if(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT.equals(goodsRecord.getMedicalAuditType())){
                //实际返利数量
                int rebateNumber = goodsRecord.getGoodsNumber() - goodsRecord.getReturnNumber();
                OrderGoodsPlatformRebateDo platformRebateDo = orderGoodsPlatformRebateDao.getByRecId(goodsRecord.getRecId());
                if(platformRebateDo!=null){
                    //实际返利金额
                    platformRebateDo.setPlatformRealRebateMoney(
                        BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
                            BigDecimalUtil.BigDecimalPlus.create(platformRebateDo.getPlatformRebateMoney(), BigDecimalUtil.Operator.multiply),
                            BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(rebateNumber), BigDecimalUtil.Operator.divide),
                            BigDecimalUtil.BigDecimalPlus.create(BigDecimalUtil.valueOf(goodsRecord.getGoodsNumber())))
                    );
                    if(rebateNumber==0){
                        platformRebateDo.setStatus(OrderGoodsPlatformRebateConstant.REBATE_FAIL);
                    }
                }
                orderGoodsPlatformRebateDao.update(platformRebateDo);
            }
        }
    }
    /**
     * 审核失败退款
     * @param orderSn
     * @return
     */
    public ExecuteResult auditNotPassRefund(String orderSn,Byte reasonType,String reasonDesc){
        OrderInfoRecord orderRecord = orderInfo.getOrderByOrderSn(orderSn);
        Result<OrderGoodsRecord> oGoods = orderGoods.getByOrderId(orderRecord.getOrderId());
        //组装退款param
        RefundParam param = new RefundParam();
        //1是退款
        param.setAction((byte) OrderServiceCode.RETURN.ordinal());
        param.setIsMp(OrderConstant.IS_MP_DOCTOR);
        param.setOrderSn(orderSn);
        param.setOrderId(orderRecord.getOrderId());
        param.setReturnType(OrderConstant.RT_ONLY_MONEY);
        param.setReturnMoney(orderRecord.getMoneyPaid().add(orderRecord.getScoreDiscount()).add(orderRecord.getUseAccount()).add(orderRecord.getMemberCardBalance()).subtract(orderRecord.getShippingFee()));
        param.setShippingFee(orderRecord.getShippingFee());
        param.setReasonType(reasonType);
        param.setReasonDesc(reasonDesc);
        List<RefundParam.ReturnGoods> returnGoodsList = new ArrayList<>();
        oGoods.forEach(orderGoods -> {
            RefundParam.ReturnGoods returnGoods = new RefundParam.ReturnGoods();
            returnGoods.setRecId(orderGoods.getRecId());
            returnGoods.setReturnNumber(orderGoods.getGoodsNumber());
            returnGoodsList.add(returnGoods);
        });
        param.setReturnGoods(returnGoodsList);
        return execute(param);
    }
    /**
     * 审核失败退款
     * @param orderSn
     * @return
     */
    public ExecuteResult storeAllRefund(String orderSn){
        OrderInfoRecord orderRecord = orderInfo.getOrderByOrderSn(orderSn);
        Result<OrderGoodsRecord> oGoods = orderGoods.getByOrderId(orderRecord.getOrderId());
        //组装退款param
        RefundParam param = new RefundParam();
        //1是退款
        param.setAction((byte) OrderServiceCode.RETURN.ordinal());
        param.setIsMp(OrderConstant.IS_MP_STORE_CLERK);
        param.setOrderSn(orderSn);
        param.setOrderId(orderRecord.getOrderId());
        param.setReturnType(OrderConstant.RT_ONLY_MONEY);
        param.setReturnMoney(orderRecord.getMoneyPaid().add(orderRecord.getScoreDiscount()).add(orderRecord.getUseAccount()).add(orderRecord.getMemberCardBalance()).subtract(orderRecord.getShippingFee()));
        param.setShippingFee(orderRecord.getShippingFee());
        param.setReasonType(OrderConstant.RETRURN_REASON_TYPE_DOCTOR_AUDIT);
//        param.setReasonDesc("");
        List<RefundParam.ReturnGoods> returnGoodsList = new ArrayList<>();
        oGoods.forEach(orderGoods -> {
            RefundParam.ReturnGoods returnGoods = new RefundParam.ReturnGoods();
            returnGoods.setRecId(orderGoods.getRecId());
            returnGoods.setReturnNumber(orderGoods.getGoodsNumber());
            returnGoodsList.add(returnGoods);
        });
        param.setReturnGoods(returnGoodsList);
        return execute(param);
    }

    /**
     * 退款
     *
     * @param param
     * @param order
     * @param result
     * @param wxRefundErrorInfo
     * @return
     */
    private ExecuteResult refund(RefundParam param, OrderInfoVo order, ExecuteResult result, AtomicReference<String[]> wxRefundErrorInfo) {
        try {
            transaction(() -> {
                try {
                    InnerProcessReturnOrder innerProcessReturnOrder = new InnerProcessReturnOrder(param, order, result).invoke();
                    ReturnOrderRecord rOrder = innerProcessReturnOrder.getrOrder();
                    List<ReturnOrderGoodsRecord> returnGoods = innerProcessReturnOrder.getReturnGoods();

                    /**
                     * 买家发起：
                     * 		不需走退款逻辑：0退货提交物流、1撤销退款
                     * 商家发起：
                     * 		不需走退款逻辑：2拒绝仅退款请求与买家提交物流商家拒绝退款、3同意退货请求、4拒绝退货申请、
                     * 		走退款逻辑（param.returnOperate == null）：商家同意买家退款、商家确认收货并退款、后台手动退款
                     * 自动任务：
                     *      买家发起仅退款申请后，商家在returnMoneyDays日内未处理，系统将自动退款。
                     *      商家已发货，买家发起退款退货申请，商家在 returnAddressDays日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收获地址
                     *      买家已提交物流信息，商家在 returnShoppingDays 日内未处理，系统将默认同意退款退货，并自动退款给买家
                     *      商家同意退款退货，买家在 returnPassDays 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
                     *
                     */
                    if (processRefundLogic(param, order, rOrder, returnGoods)) {
                        return;
                    }
                } catch (MpException e) {
                    if (e.getErrorCode().equals(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_ERROR)) {
                        wxRefundErrorInfo.set(e.getCodeParam());
                        if (!orderRefundRecord.isExistSucess(((ReturnOrderRecord) result.getResult()).getRetId())) {
                            throw e;
                        }
                    } else {
                        throw new MpException(e.getErrorCode());
                    }
                } catch (DataAccessException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof MpException) {
                        throw cause;
                    } else {
                        throw new MpException(JsonResultCode.CODE_ORDER_RETURN_ROLLBACK_NO_MPEXCEPTION, e.getMessage());
                    }
                }
            });
        } catch (DataAccessException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MpException) {
                return ExecuteResult.create(((MpException) cause).getErrorCode(), ((MpException) cause).getCodeParam());
            } else {
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_RETURN_ROLLBACK_NO_MPEXCEPTION, e.getMessage());
            }
        } catch (Exception e) {
            logger.error("退款捕获mp异常", e);
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_RETURN_ROLLBACK_NO_MPEXCEPTION, null);
        }
        return null;
    }

    /**
     * 买家发起：
     * 不需走退款逻辑：0退货提交物流、1撤销退款
     * 商家发起：
     * 不需走退款逻辑：2拒绝仅退款请求与买家提交物流商家拒绝退款、3同意退货请求、4拒绝退货申请、
     * 走退款逻辑（param.returnOperate == null）：商家同意买家退款、商家确认收货并退款、后台手动退款
     * 自动任务：
     * 买家发起仅退款申请后，商家在returnMoneyDays日内未处理，系统将自动退款。
     * 商家已发货，买家发起退款退货申请，商家在 returnAddressDays日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收获地址
     * 买家已提交物流信息，商家在 returnShoppingDays 日内未处理，系统将默认同意退款退货，并自动退款给买家
     * 商家同意退款退货，买家在 returnPassDays 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
     *
     * @param param
     * @param order
     * @param rOrder
     * @param returnGoods
     * @return
     * @throws MpException
     */
    private boolean processRefundLogic(RefundParam param, OrderInfoVo order, ReturnOrderRecord rOrder,
                                       List<ReturnOrderGoodsRecord> returnGoods) throws MpException {
        if (param.getReturnOperate() != null) {
            //响应订单操作
            returnOrder.responseReturnOperate(param, rOrder);
            //订单状态记录
            orderAction.addRecord(order, param, order.getOrderStatus(), "保存订单操作之前状态，" + OrderConstant.RETURN_TYPE_CN[param.getReturnType()] + "_" + OrderConstant.RETURN_OPERATE[param.getReturnOperate()] + "之前订单状态");
            // 更新订单信息
            orderInfo.updateInReturn(rOrder, null, null);
            //更新orderGoods表
            orderGoods.updateInReturn(order.getOrderSn(), returnGoods, rOrder);
            //更新退款商品行success状态
            returnOrderGoods.updateSucess(rOrder.getRefundStatus(), returnGoods);
            //退款订单记录
            returnStatusChange.addRecord(rOrder, param.getIsMp(), OrderConstant.RETURN_OPERATE[param.getReturnOperate()]);
            return true;
        } else if (!param.getIsMp().equals(OrderConstant.IS_MP_Y)
            && Byte.valueOf(OrderConstant.RT_GOODS).equals(param.getReturnType())) {
            //后台直接发起退货退款时设置退款状态为4以便于后续操作
            rOrder.setRefundStatus(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
        }
        if (param.getIsMp() == OrderConstant.IS_MP_Y) {
            logger.info("买家操作完成");
            return true;
        }
        //退款逻辑
        boolean isExecute = refundLogic(order, rOrder, BigDecimalUtil.compareTo(param.getReturnMoney(), null) > 0 ? param.getReturnMoney() : BigDecimal.ZERO, param);
        if (isExecute) {
            //需要执行 完成后更新信息
            finishUpdateInfo(order, rOrder, param);
        }
        return false;
    }

    private ExecuteResult checkRefundInfo(RefundParam param) {
        //判断该订单是否存在微信退款失败的退款单
        Integer failRetId = orderRefundRecord.getFailReturnOrder(param.getOrderSn());
        if (failRetId != null && !orderRefundRecord.isExistFail(param.getRetId())) {
            ReturnOrderRecord returnOrder = this.returnOrder.getByRetId(param.getRetId());
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_RETURN_EXIST_WX_REFUND_FAIL_ORDER, returnOrder == null ? null : returnOrder.getReturnOrderSn(), null);
        }
        //校验
        if (!Byte.valueOf(OrderConstant.RETURN_OPERATE_MP_REVOKE).equals(param.getReturnOperate()) &&
            !Byte.valueOf(OrderConstant.RETURN_OPERATE_MP_SUBMIT_SHIPPING).equals(param.getReturnOperate()) &&
            !Byte.valueOf(OrderConstant.RETURN_OPERATE_ADMIN_REFUSE).equals(param.getReturnOperate()) &&
            !Byte.valueOf(OrderConstant.RETURN_OPERATE_ADMIN_REFUSE_RETURN_GOODS_APPLY).equals(param.getReturnOperate())&&
            !Byte.valueOf(OrderConstant.RETURN_OPERATE_STORE_ALL_RETURN).equals(param.getReturnOperate())) {
            //非提交物流、非撤销校验
            if (param.getReturnMoney() == null) {
                logger.info("退款时未输入退商品金额");
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_RETURN_NOT_NULL_RETURNMONEY, null);
            }
            if (param.getShippingFee() == null) {
                logger.info("退款时未输入退运费金额");
                return ExecuteResult.create(JsonResultCode.CODE_ORDER_RETURN_NOT_NULL_SHIPPINGFEE, null);
            }
        }
        return null;
    }

    @Override
    public Object query(OrderOperateQueryParam param) throws MpException {
        logger.info("获取可退款、退货信息参数为:" + param.toString());
        //判断该订单是否存在微信退款失败的退款单
        Integer failRetId = orderRefundRecord.getFailReturnOrder(param.getOrderSn());
        if (failRetId != null) {
            ReturnOrderRecord returnOrder = this.returnOrder.getByRetId(failRetId);
            throw MpException.initErrorResult(JsonResultCode.CODE_ORDER_RETURN_EXIST_WX_REFUND_FAIL_ORDER, returnOrder == null ? null : returnOrder.getReturnOrderSn(), null);

        }
        Byte isMp = param.getIsMp();
        RefundVo vo = new RefundVo();

        OrderListInfoVo currentOrder = getOrderListInfoVo(param, isMp, vo);

        processManualRefund(isMp, vo, currentOrder);

        //无可退类型则返回
        if (vo.isReturn(isMp).equals(Boolean.FALSE)) {
            return vo;
        }
        //是否为拆单下的主订单
        Boolean isMain = orderInfo.isMainOrder(currentOrder);
        //如果当前订单为子订单,把订单中金额与用户信息替换为主订单的信息
        orderInfo.replaceOrderInfo(currentOrder);
        //当前订单最终支付金额(包含运费)
        final BigDecimal amount = orderInfo.getOrderFinalAmount(currentOrder, Boolean.TRUE);
        //如果该订单为主订单则查询子订单
        List<OrderListInfoVo> subOrders = orderInfo.getChildOrders(currentOrder, isMain);
        List<String> cOrderSns = subOrders.stream().map(OrderListInfoVo::getOrderSn).collect(Collectors.toList());
        //初始化订单sn数组
        List<String> sns = new ArrayList<String>(subOrders.size() + 1);
        sns.add(currentOrder.getOrderSn());
        if (subOrders.size() > 0) {
            sns.addAll(cOrderSns);
        }
        //退款数据汇总(该汇总信息会在'构造优先级退款信息'复用)
        LinkedHashMap<String, BigDecimal> returnAmountMap = refundAmountRecord.getReturnAmountMap(sns, null, null);
        //构造优先级退款信息
        Map<String, BigDecimal> canReturn = orderInfo.getCanReturn(currentOrder, amount, returnAmountMap);
        //查询订单下商品(如果为主订单则包含子订单商品)
        Map<String, List<RefundVoGoods>> goods = orderGoods.getByOrderSns(sns);
        //计算子订单商品数量(主订单返回的map->size=0)
        HashMap<Integer, Integer> subOrderGoodsSum = orderGoods.countSubOrderGoods(goods, currentOrder, isMain);
        //查询该订单退货中的商品
        Map<Integer, Integer> refundingGoods = returnOrderGoods.getRefundingGoodsNumberMap(param.getOrderSn(), OrderConstant.SUCCESS_RETURNING);
        //设置可退商品行信息
        Iterator<RefundVoGoods> currentGoods = goods.get(currentOrder.getOrderSn()).iterator();
        //如果后台查询且满足手动退款则需查询已退金额
        Map<Integer, BigDecimal> manualReturnMoney = null;
        if ((isMp != OrderConstant.IS_MP_Y) && vo.getReturnType()[OrderConstant.RT_MANUAL]) {
            manualReturnMoney = returnOrderGoods.getManualReturnMoney(param.getOrderSn());
        }

        processCurrentGoods(isMp, vo, isMain, subOrderGoodsSum, refundingGoods, currentGoods, manualReturnMoney);

        checkReturnType(goods.get(currentOrder.getOrderSn()), vo.getReturnType());
        vo.setRefundGoods(goods.get(currentOrder.getOrderSn()));
        vo.setReturnAmountMap(canReturn);
        logger.info("获取可退货信息完成");
        return vo;
    }

    private void processCurrentGoods(Byte isMp, RefundVo vo, Boolean isMain, HashMap<Integer, Integer> subOrderGoodsSum,
                                     Map<Integer, Integer> refundingGoods,
                                     Iterator<RefundVoGoods> currentGoods, Map<Integer, BigDecimal> manualReturnMoney) {
        while (currentGoods.hasNext()) {
            RefundVoGoods oneGoods = (RefundVoGoods) currentGoods.next();
            //主订单需要减去子订单的商品数量
            if (isMain) {
                //总数 = 总数 - 子订单数量
                oneGoods.setTotal((oneGoods.getGoodsNumber() - ((subOrderGoodsSum.get(oneGoods.getRecId())) == null ? 0 : subOrderGoodsSum.get(oneGoods.getRecId()))));
            } else {
                //总数 = 总数
                oneGoods.setTotal(oneGoods.getGoodsNumber());
            }
            //已提交=退中+退完成
            Integer submitted = (refundingGoods.get(oneGoods.getRecId()) == null ? 0 : refundingGoods.get(oneGoods.getRecId())) + oneGoods.getReturnNumber();
            oneGoods.setSubmitted(submitted);
            // 可退
            Integer returnable = oneGoods.getTotal() - oneGoods.getSubmitted();
            oneGoods.setReturnable(returnable);
            //处理前后端不同逻辑
            if (isMp == OrderConstant.IS_MP_Y) {
                if (oneGoods.getReturnable() <= 0) {
                    //前端可退数量为0不展示
                    currentGoods.remove();
                    continue;
                }
            } else {
                //后台商家配置的不可退款的可退
                oneGoods.setIsCanReturn(OrderConstant.IS_CAN_RETURN_Y);
            }
            //如果后台查询且满足手动退款则需查询已退金额
            if (isMp != OrderConstant.IS_MP_Y && vo.getReturnType()[3]) {
                oneGoods.setReturnMoney(manualReturnMoney.get(oneGoods.getRecId()) == null ? BigDecimal.ZERO : manualReturnMoney.get(oneGoods.getRecId()));
            }
        }
    }

    private void processManualRefund(Byte isMp, RefundVo vo, OrderListInfoVo currentOrder) {
        //手动退款校验,已退金额<sum(已退R商品数量*折后单价)
        if ((isMp != OrderConstant.IS_MP_Y)) {
            //是否退过商品（数量角度）
            List<OrderGoodsVo> returnGoods = orderGoods.getReturnGoods(currentOrder.getOrderSn());
            //判断金额
            if (returnGoods.size() != 0) {
                BigDecimal returnMoney = returnOrder.getReturnMoney(currentOrder.getOrderSn());
                //已退商品可退最大金额
                BigDecimal returnGoodsMaxMoney = BigDecimal.ZERO;
                for (OrderGoodsVo goods : returnGoods) {
                    returnGoodsMaxMoney = returnGoodsMaxMoney.add(
                        BigDecimalUtil.multiplyOrDivide(
                            BigDecimalPlus.create(new BigDecimal(goods.getReturnNumber()), Operator.multiply),
                            BigDecimalPlus.create(goods.getDiscountedGoodsPrice(), null)));
                }
                if (BigDecimalUtil.compareTo(returnGoodsMaxMoney, returnMoney) > 0) {
                    vo.getReturnType()[OrderConstant.RT_MANUAL] = true;
                }
            }
        }
    }

    private OrderListInfoVo getOrderListInfoVo(OrderOperateQueryParam param, Byte isMp, RefundVo vo) throws MpException {
        //获取当前订单
        OrderListInfoVo currentOrder = orderInfo.getByOrderId(param.getOrderId(), OrderListInfoVo.class);
        if (currentOrder == null) {
            throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST);
        }
        vo.setOrderType(OrderInfoService.orderTypeToArray(currentOrder.getGoodsType()));
        //退款校验
        if (OrderOperationJudgment.isReturnMoney(currentOrder, isMp)) {
            vo.getReturnType()[RT_ONLY_MONEY] = true;
        }
        //退货校验
        if (OrderOperationJudgment.isReturnGoods(currentOrder, isMp)) {
            vo.getReturnType()[OrderConstant.RT_GOODS] = true;
        }
        //获取已退运费
        BigDecimal returnShipingFee = returnOrder.getReturnShippingFee(currentOrder.getOrderSn());
        //退运费校验
        if (OrderOperationJudgment.adminIsReturnShipingFee(currentOrder.getShippingFee(), returnShipingFee, true)) {
            vo.getReturnType()[OrderConstant.RT_ONLY_SHIPPING_FEE] = true;
            //设置
            vo.setReturnShippingFee(currentOrder.getShippingFee().subtract(returnShipingFee));
        } else {
            vo.setReturnShippingFee(BigDecimal.ZERO);
        }
        return currentOrder;
    }

    /**
     * 仅退运费生成退款订单及校验
     *
     * @param param
     * @param order
     * @return
     * @throws MpException
     */
    private ReturnOrderRecord returnShippingFee(RefundParam param, OrderInfoVo order) throws MpException {
        // 只退运费必须金额>0
        if (BigDecimalUtil.compareTo(param.getShippingFee(), BigDecimal.ZERO) < 1) {
            logger.error("订单sn:{}，仅退运费时退运费金额必须大于0", param.getOrderSn());
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_RETURN_SHIPPING_FEE_NOT_ZERO);
        }
        // 获取已退运费
        BigDecimal returnShipingFee = returnOrder.getReturnShippingFee(param.getOrderSn());
        if (!OrderOperationJudgment.adminIsReturnShipingFee(order.getShippingFee(), returnShipingFee.add(param.getShippingFee()), false)) {
            logger.error("订单sn:{}，该订单运费已经退完或超额", param.getOrderSn());
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_RETURN_SHIPPING_FEE_EXCESS);
        }
        logger.info("退运费创建退款订单开始");
        // 增加退款/退货记录，形成货退款订单
        ReturnOrderRecord rOrder = returnOrder.addRecord(param, order, null);
        logger.info("退运费创建退款订单结束");
        return rOrder;
    }

    /**
     * 退款方法集合
     *
     * @param order
     * @param returnOrder
     * @param currentMoney
     * @param param
     * @return 是否需要后续处理状态改变（好友代付存在微信退款不需要）
     * @throws MpException
     */
    public boolean refundLogic(OrderInfoVo order, ReturnOrderRecord returnOrder, BigDecimal currentMoney, RefundParam param) throws MpException {
        logger.info("退款refundLogic start");
        if (OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING != returnOrder.getRefundStatus()) {
            logger().info("退款订单sn:" + returnOrder.getReturnOrderSn() + ",refundStatus" + returnOrder.getRefundStatus() + "不符合完成退款条件。");
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_STATUS_NOT_SATISFIED);
        }
        if (BigDecimalUtil.compareTo(returnOrder.getMoney(), currentMoney) < 0) {
            logger().info("退款订单sn:" + returnOrder.getReturnOrderSn() + ",退款金额超过该退款订单最大金额");
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_MONEY_EXCEEDED);
        }
        //当前退款金额大于等于零,进行退款金额参数构造
        if (BigDecimalUtil.compareTo(currentMoney, BigDecimal.ZERO) > -1 ||
            BigDecimalUtil.compareTo(param.getShippingFee(), currentMoney) > -1) {
            returnOrder.setMoney(currentMoney);
            returnOrder.setShippingFee(param.getShippingFee());
            returnOrder.update();
        }
        //当前订单为子订单需要替换支付信息与用户信息(子订单无补款信息,不需复制)
        orderInfo.replaceOrderInfo(order);
        boolean priorityRefund = priorityRefund(order, returnOrder);
        //计算退款商品行退款金额
        returnOrderGoods.calculateGoodsReturnMoney(returnOrder, param.getReturnGoods());
        //TODO 好友代付且存在微信退款队列处理
        if (OrderConstant.PAY_WAY_FRIEND_PAYMENT == order.getOrderPayWay() && priorityRefund) {
            return false;
        }
        logger.info("退款refundLogic end");
        return true;
    }

    /**
     * 优先级退款
     *
     * @param order
     * @param returnOrder
     * @return 是否需要后续处理状态改变（好友代付存在微信退款不需要）
     * @throws MpException
     */
    public boolean priorityRefund(OrderInfoVo order, ReturnOrderRecord returnOrder) throws MpException {
        logger.info("优先级退款start");
        //是否微信退款（好友代付没有补款）
        boolean flag = false;
        //此次退款金额
        BigDecimal returnAmount = returnOrder.getMoney().add(returnOrder.getShippingFee());
        logger.info("此次退款金额{}", returnAmount);
        // 是否为主订单
        Boolean isMain = orderInfo.isMainOrder(order);
        // 如果该订单为主订单则查询子订单
        List<OrderListInfoVo> subOrders = orderInfo.getChildOrders(order, isMain);
        List<String> cOrderSns = subOrders.stream().map(OrderListInfoVo::getOrderSn).collect(Collectors.toList());
        // 初始化订单sn数组
        List<String> sns = new ArrayList<String>(subOrders.size() + 1);
        sns.add(order.getOrderSn());
        if (subOrders.size() > 0) {
            sns.addAll(cOrderSns);
        }
        //优先级退款数据汇总
        LinkedHashMap<String, BigDecimal> returnAmountMap = refundAmountRecord.getReturnAmountMap(sns, null, returnOrder.getRetId());
        //初始化执行退款数据（先退第三方（微信））
        LinkedHashMap<String, BigDecimal> executeRefundRecord = refundAmountRecord.executeRefundRecord();
        //构造优先级退款数据
        for (Entry<String, BigDecimal> entry : returnAmountMap.entrySet()) {
            logger.info("{}优先级退款", entry.getKey());
            //当前优先级名称
            String key = entry.getKey();
            //有补款支付才退
            if (key.equals(orderInfo.PS_BK_ORDER_MONEY) && BigDecimalUtil.compareTo(order.getBkOrderMoney(), BigDecimal.ZERO) < 1) {
                continue;
            }
            //当前优先级已退款金额
            BigDecimal value = entry.getValue();
            BigDecimal keyCanReturn = BigDecimalUtil.subtrac(FieldsUtil.getFieldValueByFieldName(key, order, BigDecimal.class), value);
            if (BigDecimalUtil.compareTo(keyCanReturn, BigDecimal.ZERO) < 1) {
                //跳出可退<=0
                continue;
            }
            //此优先级此次退款金额
            BigDecimal currentReturn;
            if (BigDecimalUtil.compareTo(returnAmount, keyCanReturn) < 0) {
                currentReturn = returnAmount;
                returnAmount = BigDecimal.ZERO;
            } else {
                currentReturn = keyCanReturn;
                returnAmount = returnAmount.subtract(keyCanReturn);
            }
            //构造执行退款数据
            executeRefundRecord.put(entry.getKey(), currentReturn);
            //微信退款后续处理标识
            if (key.equals(orderInfo.PS_MONEY_PAID)) {
                flag = true;
            }
            if (returnAmount.compareTo(BigDecimal.ZERO) < 1) {
                //此次退款金额已经退完
                break;
            }
        }
        if (returnAmount.compareTo(BigDecimal.ZERO) > 0) {
            logger.error("优先级退款完成后本次退款金额>0,orderSn:{},retId:{}", order.getOrderSn(), returnOrder.getRetId());
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_AFTER_RETURNAMOUNT_GREAT_THAN_ZERO);
        }
        //优先级退款执行
        for (Entry<String, BigDecimal> entry : executeRefundRecord.entrySet()) {
            if (BigDecimalUtil.compareTo(entry.getValue(), null) < 1) {
                continue;
            }
            logger.info("{}优先级退款执行", entry.getKey());
            returnMethod.refundMethods(entry.getKey(), order, returnOrder.getRetId(), entry.getValue());
        }
        logger.info("优先级退款end");
        return flag;
    }


    /**
     * 退款完成变更相关信息
     *
     * @param order
     * @param returnOrderRecord
     * @param param
     * @return
     * @throws MpException
     */
    public void finishUpdateInfo(OrderInfoVo order, ReturnOrderRecord returnOrderRecord, RefundParam param) throws MpException {
        logger.info("退款完成变更相关信息start");
        Result<ReturnOrderGoodsRecord> returnGoodsRecord = returnOrderGoods.getReturnGoods(returnOrderRecord.getOrderSn(), returnOrderRecord.getRetId());
        List<OrderReturnGoodsVo> returnGoods = returnGoodsRecord.into(OrderReturnGoodsVo.class);
        returnGoods.forEach(g -> g.setIsGift(orderGoods.isGift(g.getRecId())));
        //库存更新
        updateStockAndSales(order, returnGoods, returnOrderRecord);
        //退款退货订单完成更新
        returnOrder.finishReturn(returnOrderRecord);
        //更新ReturnOrderGoods-success
        returnOrderGoods.updateSucess(returnOrderRecord.getRefundStatus(), returnGoodsRecord);
        //更新orderGoods表
        orderGoods.updateInReturn(order.getOrderSn(), returnGoodsRecord, returnOrderRecord);
        //可退款退货商品数量是否为0(有状态依赖于ordergoods表的商品数量与已经退货退款数量)
        boolean canReturnGoodsNumber = orderGoods.canReturnGoodsNumber(order.getOrderSn());
        //更新ordrinfo主表信息
        orderInfo.updateInReturn(returnOrderRecord, order, canReturnGoodsNumber);
        //退优惠券
        returnCoupon(order);
        //TODO 拆单逻辑特殊处理

        //部分发货退款完成,检查是否需要设置状态为已发货
        if (order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY && order.getPartShipFlag() == OrderConstant.PART_SHIP && canReturnGoodsNumber) {
            //TODO 目前发货只支持按商品行发货，若支持数量发货需要修改
            if (!orderGoods.isCanDeliverOrder(order.getOrderSn())) {
                orderInfo.setOrderstatus(order.getOrderSn(), OrderConstant.ORDER_SHIPPED);
            }
        }
        //限次卡退次数
        wxCardExchange.limitCardRefundTimes(order, returnGoods);
        //返利金额重新计算
        calculate.recalculationRebate(returnGoods, orderGoods.getOrderGoods(order.getOrderSn(), returnGoods.stream().map(OrderReturnGoodsVo::getRecId).collect(Collectors.toList())), order.getOrderSn());
        //医师处方返利金额重新计算
        updatePrescriptionRebateStatus(returnGoods,order);
        //平台返利
        updatePlatformRebate(returnGoods);
        // 发送退款成功模板消息
        // 自动同步订单微信购物单
        returnStatusChange.addRecord(returnOrderRecord, param.getIsMp(), "当前退款订单正常结束：" + OrderConstant.RETURN_TYPE_CN[returnOrderRecord.getReturnType()]);
        logger.info("退款完成变更相关信息end");
    }

    private void updateStockAndSales(OrderInfoVo order, List<OrderReturnGoodsVo> returnGoods, ReturnOrderRecord returnOrderRecord) throws MpException {
        List<Byte> goodsType = Lists.newArrayList(OrderInfoService.orderTypeToByte(order.getGoodsType()));
        //售后商品库存配置
        Byte autoReturnGoodsStock = returnCfg.getAutoReturnGoodsStock();
        //货到付款 、拼团抽奖未中奖、退运费、手动退款
        boolean noModifySkuSaleNum = OrderConstant.IS_COD_YES.equals(order.getIsCod()) ||
            (goodsType.contains(BaseConstant.ACTIVITY_TYPE_GROUP_DRAW) && !groupDraw.isWinDraw(order.getOrderSn())) ||
            returnOrderRecord.getReturnType().equals(OrderConstant.RT_ONLY_SHIPPING_FEE) ||
            returnOrderRecord.getReturnType().equals(OrderConstant.RT_MANUAL);
        if (noModifySkuSaleNum) {
            //不进行修改库存销量操作
            return;
        }
        //是否恢复库存（仅限普通商品与赠品）->(配置可退 && (退款退货 || 换货)) || (退款待发货)）
        boolean isRestore = (
            (autoReturnGoodsStock == OrderConstant.YES && (returnOrderRecord.getReturnType().equals(OrderConstant.RT_GOODS) || returnOrderRecord.getReturnType().equals(OrderConstant.RT_CHANGE))) ||
                (returnOrderRecord.getReturnType().equals(RT_ONLY_MONEY) && order.getOrderStatus().equals(OrderConstant.ORDER_WAIT_DELIVERY)));
        //修改商品库存-销量
        atomicOperation.updateStockAndSales(returnGoods, order, isRestore);
        //处理活动库存等
        processAct(returnOrderRecord, order, returnGoods, goodsType, isRestore);
    }

    /**
     * @param returnOrderRecord
     * @param order             订单
     * @param returnGoods       退款商品
     * @param goodsType         订单类型
     * @param isRestore         是否可退
     * @throws MpException
     */
    private void processAct(ReturnOrderRecord returnOrderRecord, OrderInfoVo order, List<OrderReturnGoodsVo> returnGoods, List<Byte> goodsType, boolean isRestore) throws MpException {
        if (!isRestore) {
            return;
        }
        orderCreateMpProcessorFactory.processReturnOrder(returnOrderRecord, BaseConstant.ACTIVITY_TYPE_PRESCRIPTION, null, returnGoods);
        //获取退款活动(goodsType.retainAll后最多会出现一个单一营销+赠品活动)
        goodsType.retainAll(OrderCreateMpProcessorFactory.RETURN_ACTIVITY);
        for (Byte type : goodsType) {
            if (BaseConstant.ACTIVITY_TYPE_GIFT.equals(type)) {
                //赠品修改活动库存
                orderCreateMpProcessorFactory.processReturnOrder(returnOrderRecord, BaseConstant.ACTIVITY_TYPE_GIFT, null, returnGoods.stream().filter(x -> OrderConstant.IS_GIFT_Y.equals(x.getIsGift())).collect(Collectors.toList()));
            } else {
                //修改活动库存
                orderCreateMpProcessorFactory.processReturnOrder(returnOrderRecord, type, order.getActivityId(), returnGoods.stream().filter(x -> OrderConstant.IS_GIFT_N.equals(x.getIsGift())).collect(Collectors.toList()));
            }
        }
    }

    /**
     * 非仅退运费生成退款订单及校验
     *
     * @param param
     * @param order
     * @return
     * @throws MpException
     */
    private ReturnOrderRecord notOnlyReturnShippingFee(RefundParam param, OrderInfoVo order, RefundVo check) throws MpException {
        //通用是否支持该退款退货类型
        if (!check.getReturnType()[param.getReturnType()]) {
            logger.error("订单sn:{},{}时通用校验失败，不支持该类型退款退货", param.getOrderSn(), OrderConstant.RETURN_TYPE_CN[param.getReturnType()]);
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_NOT_SUPPORT_RETURN_TYPE);
        }
        //查询参数校验
        if (CollectionUtils.isEmpty(check.getRefundGoods())) {
            logger.error("订单sn:{},{}时，已无可退商品", param.getOrderSn(), OrderConstant.RETURN_TYPE_CN[param.getReturnType()]);
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_GOODS_RETURN_COMPLETED);
        }

        //输入商品简单校验
        if (CollectionUtils.isEmpty(param.getReturnGoods())) {
            logger.error("订单sn:{},{}时，未选择商品", param.getOrderSn(), OrderConstant.RETURN_TYPE_CN[param.getReturnType()]);
            throw new MpException(JsonResultCode.CODE_ORDER_RETURN_NO_SELECT_GOODS);
        }
        // 退运费判断
        if (BigDecimalUtil.compareTo(param.getShippingFee(), null) > 0) {
            BigDecimal returnShipingFee = returnOrder.getReturnShippingFee(param.getOrderSn());
            if (!OrderOperationJudgment.adminIsReturnShipingFee(order.getShippingFee(), returnShipingFee.add(param.getShippingFee()), false)) {
                logger.error("订单sn:{}，该订单运费已经退完或超额", param.getOrderSn());
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_RETURN_SHIPPING_FEE_EXCESS);
            }
        }
        //手动退款商品行金额和与输入金额比较
        if (param.getReturnType() == OrderConstant.RT_MANUAL) {
            BigDecimal sum = param.getReturnGoods().stream().map(ReturnGoods::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (BigDecimalUtil.compareTo(sum, param.getReturnMoney()) != 0) {
                logger.error("订单sn:{}，手动退款商品行金额和与输入金额不一致", param.getOrderSn());
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_MANUAL_INCONSISTENT_AMOUNT);
            }
        }
        //构造退款订单及复杂校验
        ReturnOrderRecord rOrder = returnOrder.checkAndCreateOrder(param, order, check);
        return rOrder;
    }

    /**
     * 根据实际退款数量确定是否支持退款类型（退款、退货）
     *
     * @param refundVoGoods
     * @param returnTypes
     */
    public void checkReturnType(List<RefundVoGoods> refundVoGoods, boolean[] returnTypes) {
        if (returnTypes[0] || returnTypes[1]) {
            if (refundVoGoods.stream().mapToInt(RefundVoGoods::getReturnNumber).sum() == refundVoGoods.stream().mapToInt(RefundVoGoods::getTotal).sum()) {
                returnTypes[0] = false;
                returnTypes[1] = false;
            }
        }
    }

    /**
     * 自动处理退货退款订单
     */
    public void autoReturnOrder() {
        if (OrderConstant.YES == shopReturncfg.getAutoReturn()) {
            //买家发起仅退款申请后，商家在returnMoneyDays日内未处理，系统将自动退款。
            Byte returnMoneyDays = shopReturncfg.getReturnMoneyDays();
            //商家已发货，买家发起退款退货申请，商家在 returnAddressDays日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收获地址
            Byte returnAddressDays = shopReturncfg.getReturnAddressDays();
            //买家已提交物流信息，商家在 returnShoppingDays 日内未处理，系统将默认同意退款退货，并自动退款给买家
            Byte returnShoppingDays = shopReturncfg.getReturnShippingDays();
            //商家同意退款退货，买家在 returnPassDays 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
            Byte returnPassDays = shopReturncfg.getReturnPassDays();
            Result<ReturnOrderRecord> autoReturnOrder = returnOrder.getAutoReturnOrder(returnMoneyDays, returnAddressDays, returnShoppingDays, returnPassDays);
            autoReturnOrder.forEach(order -> {
                RefundParam param = new RefundParam();
                param.setAction(Integer.valueOf(OrderServiceCode.RETURN.ordinal()).byteValue());
                param.setIsMp(OrderConstant.IS_MP_AUTO);
                param.setOrderId(order.getOrderId());
                param.setOrderSn(order.getOrderSn());
                param.setReturnType(order.getReturnType());
                param.setReturnMoney(order.getMoney());
                param.setShippingFee(order.getShippingFee());
                if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING) && order.getReturnType().equals(RT_ONLY_MONEY)) {
                    //买家发起退款申请后，商家在 returnMoneyDays 日内未处理，系统将自动退款
                    param.setReturnOperate(null);
                } else if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDITING)) {
                    if (order.getReturnType().equals(OrderConstant.RT_GOODS) || order.getReturnType().equals(OrderConstant.RT_CHANGE)) {
                        //商家已发货，买家发起退款退货申请，商家在 ? 日内未处理，系统将默认同意退款退货，并自动向买家发送商家的默认收获地址
                        param.setReturnOperate(OrderConstant.RETURN_OPERATE_ADMIN_AGREE_RETURN);
                        ReturnBusinessAddressParam defaultAddress = shopReturncfg.getDefaultAddress();
                        if (defaultAddress != null) {
                            param.setConsignee(defaultAddress.getConsignee());
                            param.setReturnAddress(defaultAddress.getReturnAddress());
                            param.setMerchantTelephone(defaultAddress.getMerchantTelephone());
                            param.setZipCode(defaultAddress.getZipCode());
                        }
                    } else if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING) && order.getReturnType().equals(OrderConstant.RT_GOODS)) {
                        //买家已提交物流信息，商家在 ? 日内未处理，系统将默认同意退款退货，并自动退款给买家
                        param.setReturnOperate(null);
                    } else if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDIT_PASS) && order.getReturnType().equals(OrderConstant.RT_GOODS)) {
                        //商家同意退款退货，买家在 ? 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
                    }
                } else if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING) && order.getReturnType().equals(OrderConstant.RT_GOODS)) {
                    //买家已提交物流信息，商家在 ? 日内未处理，系统将默认同意退款退货，并自动退款给买家
                    param.setReturnOperate(null);
                } else if (order.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDIT_PASS) && order.getReturnType().equals(OrderConstant.RT_GOODS)) {
                    //商家同意退款退货，买家在 ? 日内未提交物流信息，且商家未确认收货并退款，退款申请将自动完成。
                }
                ExecuteResult result = execute(param);
                if (result == null || result.isSuccess()) {
                    logger().info("订单自动任务,完成成功,orderSn:{}", order.getOrderSn());
                } else {
                    logger().error("订单自动任务,完成失败,orderSn:{},错误信息{}{}", order.getOrderSn(), result.getErrorCode().toString(), result.getErrorParam());
                }
            });
        }
    }

    /**
     * 退款时释放优惠券
     *
     * @param order
     */
    private void returnCoupon(OrderInfoVo order) {
        if (order.getOrderStatus().equals(OrderConstant.ORDER_FINISHED)) {
            logger.info("订单完成，不可退优惠券");
            return;
        }
        OrderInfoRecord orderInfoRecord = orderInfo.getOrderByOrderSn(order.getOrderSn());
        if (BigDecimalUtil.compareTo(order.getDiscount(), BigDecimal.ZERO) > 0
                && orderInfoRecord.getIsRefundCoupon().equals(OrderConstant.YES)) {
            if (orderInfoRecord.getOrderStatus().equals(OrderConstant.ORDER_REFUND_FINISHED) || orderInfoRecord.getOrderStatus().equals(OrderConstant.ORDER_RETURN_FINISHED)) {
                coupon.releaserCoupon(order.getOrderSn());
            }
        }
    }

    public ApiJsonResult returnOrderApi(ApiReturnParam param) {
        ApiJsonResult result = new ApiJsonResult();
        if (param == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数content为空");
            return result;
        }
        if (StringUtils.isBlank(param.getOrderSn())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数order_sn为空");
            return result;
        }
        if (StringUtils.isBlank(param.getRefundType())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数refund_type为空");
            return result;
        }
        if (OrderConstant.API_RETURN_REFUSE.equals(param.getRefundType()) && StringUtils.isBlank(param.getRefuseReason())) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("参数refuse_reason为空");
            return result;
        }
        ReturnOrderRecord rOrder = returnOrder.getByReturnOrderSn(param.getReturnOrderSn());
        if (rOrder == null) {
            result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
            result.setMsg("退款订单不存在");
            return result;
        }
        RefundParam executeParam = new RefundParam();
        executeParam.setAction(Integer.valueOf(OrderServiceCode.RETURN.ordinal()).byteValue());
        executeParam.setIsMp(OrderConstant.IS_MP_ERP_OR_EKB);
        executeParam.setOrderId(rOrder.getOrderId());
        executeParam.setOrderSn(rOrder.getOrderSn());
        executeParam.setRetId(rOrder.getRetId());
        executeParam.setReturnType(rOrder.getReturnType());
        executeParam.setReturnMoney(rOrder.getMoney());
        executeParam.setShippingFee(rOrder.getShippingFee());
        //获取当前操作
        if (returnOrderApiBuild(param, result, rOrder, executeParam)){
            return result;
        }
        return result;
    }

    private boolean returnOrderApiBuild(ApiReturnParam param, ApiJsonResult result, ReturnOrderRecord rOrder, RefundParam executeParam) {
        if (OrderConstant.API_RETURN_AGREE.equals(param.getRefundType())) {
            //同意
            if (rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)) {
                if (rOrder.getReturnType().equals(RT_ONLY_MONEY)) {
                    //同意买家仅退款申请
                    executeParam.setReturnOperate(null);
                } else if (rOrder.getReturnType().equals(OrderConstant.RT_GOODS) && rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDITING)) {
                    //同意买家退货申请
                    executeParam.setReturnOperate(OrderConstant.RETURN_OPERATE_ADMIN_AGREE_RETURN);
                    ReturnBusinessAddressParam defaultAddress = shopReturncfg.getDefaultAddress();
                    if (defaultAddress != null) {
                        executeParam.setConsignee(defaultAddress.getConsignee());
                        executeParam.setReturnAddress(defaultAddress.getReturnAddress());
                        executeParam.setMerchantTelephone(defaultAddress.getMerchantTelephone());
                        executeParam.setZipCode(defaultAddress.getZipCode());
                    }
                } else if (rOrder.getReturnType().equals(OrderConstant.RT_GOODS)) {
                    if (rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDIT_PASS) || rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)) {
                        //卖家同意退货申请或者买家提交物流后可以完成退款
                        executeParam.setReturnOperate(null);
                    } else {
                        result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                        result.setMsg("当前退款订单无法执行当前操作");
                        return true;
                    }
                } else {
                    result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                    result.setMsg("当前退款订单无法执行当前操作");
                    return true;
                }
            } else if (rOrder.getReturnType().equals(OrderConstant.RT_GOODS) && rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDITING)) {
                //同意买家退货申请
                executeParam.setReturnOperate(OrderConstant.RETURN_OPERATE_ADMIN_AGREE_RETURN);
                ReturnBusinessAddressParam defaultAddress = shopReturncfg.getDefaultAddress();
                if (defaultAddress != null) {
                    executeParam.setConsignee(defaultAddress.getConsignee());
                    executeParam.setReturnAddress(defaultAddress.getReturnAddress());
                    executeParam.setMerchantTelephone(defaultAddress.getMerchantTelephone());
                    executeParam.setZipCode(defaultAddress.getZipCode());
                }
            } else if (rOrder.getReturnType().equals(OrderConstant.RT_GOODS)) {
                if (rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDIT_PASS) || rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING)) {
                    //卖家同意退货申请或者买家提交物流后可以完成退款
                    executeParam.setReturnOperate(null);
                } else {
                    result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                    result.setMsg("当前退款订单无法执行当前操作");
                    return true;
                }
            } else {
                result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                result.setMsg("当前退款订单无法执行当前操作");
                return true;
            }
        } else {
            //拒绝
            if (rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING) || rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDIT_PASS)) {
                executeParam.setRefundRefuseReason(param.getRefuseReason());
                executeParam.setReturnOperate(OrderConstant.RETURN_OPERATE_ADMIN_REFUSE);
            } else if (rOrder.getRefundStatus().equals(OrderConstant.REFUND_STATUS_AUDITING)) {
                executeParam.setReturnOperate(OrderConstant.RETURN_OPERATE_ADMIN_REFUSE_RETURN_GOODS_APPLY);
                executeParam.setApplyNotPassReason(param.getRefuseReason());
            } else {
                result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                result.setMsg("当前退款订单无法执行当前操作");
                return true;
            }
            ExecuteResult executeResult = saas().getShopApp(getShopId()).orderActionFactory.orderOperate(executeParam);
            if (executeResult == null || executeResult.isSuccess()) {
                result.setCode(ApiExternalGateConstant.ERROR_CODE_SUCCESS);
            } else {
                logger.error("外服系统调用退款接口失败，executeResult：{}", executeResult);
                result.setCode(ApiExternalGateConstant.ERROR_LACK_PARAM);
                result.setMsg(Util.translateMessage(AbstractExcelDisposer.DEFAULT_LANGUAGE, executeResult.getErrorCode().getMessage(), JsonResult.LANGUAGE_TYPE_MSG, executeResult.getErrorParam()));
            }
        }
        return false;
    }

    private class InnerProcessReturnOrder {
        private RefundParam param;
        private OrderInfoVo order;
        private ExecuteResult result;
        private ReturnOrderRecord rOrder;
        private List<ReturnOrderGoodsRecord> returnGoods;

        public InnerProcessReturnOrder(RefundParam param, OrderInfoVo order, ExecuteResult result) {
            this.param = param;
            this.order = order;
            this.result = result;
        }

        public ReturnOrderRecord getrOrder() {
            return rOrder;
        }

        public List<ReturnOrderGoodsRecord> getReturnGoods() {
            return returnGoods;
        }

        public InnerProcessReturnOrder invoke() throws MpException {
            //退款订单
            rOrder = null;
            //退款商品
            returnGoods = null;
            if (param.getRetId() != null) {
                rOrder = returnOrder.getByRetId(param.getRetId());
                if (rOrder == null) {
                    logger.error("退款退货执行异常，退订单不存在");
                    throw new MpException(JsonResultCode.CODE_ORDER_NOT_EXIST);
                }
            }
            if (rOrder == null) {
                //订单状态记录
                orderAction.addRecord(order, param, order.getOrderStatus(), "保存" + OrderConstant.RETURN_TYPE_CN[param.getReturnType()] + "之前订单状态");
                //设置是否自动退款
                param.setIsAutoReturn(shopReturncfg.getAutoReturn());
                //if仅退运费 else非仅退运费
                if (OrderConstant.RT_ONLY_SHIPPING_FEE == param.getReturnType()) {
                    //生成退款订单
                    rOrder = returnShippingFee(param, order);
                } else {
                    //通过退款查询获取可退信息
                    RefundVo check = (RefundVo) query(param);
                    //校验  生成退款订单
                    rOrder = notOnlyReturnShippingFee(param, order, check);
                    //生成退款商品
                    returnGoods = returnOrderGoods.add(param, rOrder, check);
                    //更新orderGoods表
                    orderGoods.updateInReturn(order.getOrderSn(), returnGoods, rOrder);
                }
                // 更新订单信息
                orderInfo.updateInReturn(rOrder, null, null);
                //退款订单记录
                returnStatusChange.addRecord(rOrder, param.getIsMp(), "生成退款退货订单信息：" + OrderConstant.RETURN_TYPE_CN[param.getReturnType()]);
            }
            //暂存退款订单
            result.setResult(rOrder);
            //退款商品为空则初始化
            if (CollectionUtils.isEmpty(returnGoods)) {
                logger.info("退款商品为空则初始化");
                returnGoods = returnOrderGoods.getReturnGoods(order.getOrderSn(), rOrder.getRetId());
            }
            return this;
        }
    }
}
