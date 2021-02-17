package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.audit.AuditExternalParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.refund.RefundParam;
import com.meidianyi.shop.service.pojo.shop.prescription.FetchPrescriptionOneParam;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.refund.ReturnOrderService;
import com.meidianyi.shop.service.shop.order.refund.goods.ReturnOrderGoodsService;
import com.meidianyi.shop.service.shop.order.refund.record.OrderRefundRecordService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核
 * @author 孔德成
 * @date 2020/7/17 15:30
 */
@Service
@Slf4j
public class AuditService extends ShopBaseService implements IorderOperate<AuditExternalParam,AuditExternalParam> {

    @Autowired
    private OrderRefundRecordService orderRefundRecord;
    @Autowired
    private ReturnOrderService returnOrder;

    @Autowired
    private OrderInfoService orderInfo;
    @Autowired
    private OrderGoodsService orderGoods;
    @Autowired
    private ReturnOrderGoodsService returnOrderGoods;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private PrescriptionService prescriptionService;

    @Override
    public OrderServiceCode getServiceCode() {
        return null;
    }

    @Override
    public Object query(AuditExternalParam param) throws MpException {
        return null;
    }

    /**
     * 审核订单
     * @param param
     * @return
     */
    @Override
    public ExecuteResult execute(AuditExternalParam param) {
        log.info("审核订单(AuditService)-开始");
        OrderInfoRecord orderRecord = orderInfo.getOrderByOrderSn(param.getOrderSn());
        Result<OrderGoodsRecord> oGoods = orderGoods.getByOrderId(orderRecord.getOrderId());
        FetchPrescriptionOneParam fetchparam =new FetchPrescriptionOneParam();
        fetchparam.setPrescriptionCode(param.getPrescriptionCode());
        prescriptionService.pullExternalOnePrescriptionInfo(fetchparam);
        for (OrderGoodsRecord goods: oGoods){
            goods.setPrescriptionCode(param.getPrescriptionCode());
            goods.setAuditTime(DateUtils.getLocalDateTime());
            if (param.getIsAudit().equals(BaseConstant.YES)){
                goods.setMedicalAuditStatus((byte)1);
            }else {
                goods.setMedicalAuditStatus((byte)2);
            }
            goods.update();
        }
        orderRecord.setPrescriptionCodeList(param.getPrescriptionCode());
        if (param.getIsAudit().equals(BaseConstant.YES)){
            orderRecord.setOrderAuditStatus((byte)1);
        }else {
            orderRecord.setOrderAuditStatus((byte)2);
        }
        orderRecord.setAuditTime(DateUtils.getLocalDateTime());
        orderRecord.update();
        if (param.getIsAudit().equals(BaseConstant.YES)){

        }else {
            returnOrderOption(param.getOrderSn());
        }
        log.info("审核订单(AuditService)-结束");
        return null;
    }

    /**
     * 退款订单
     * @param orderSn 订单号
     */
    private void returnOrderOption(String orderSn) {
        OrderInfoRecord orderRecord = orderInfo.getOrderByOrderSn(orderSn);
        Result<OrderGoodsRecord> oGoods = orderGoods.getByOrderId(orderRecord.getOrderId());
        //组装退款param
        RefundParam param = new RefundParam();
        //1是退款
        param.setAction((byte) OrderServiceCode.RETURN.ordinal());
        param.setIsMp(OrderConstant.IS_MP_DOCTOR);
        param.setReturnSourceType(OrderConstant.RS_AUTO_COMMUNITY_GROUP);
        param.setOrderSn(orderSn);
        param.setOrderId(orderRecord.getOrderId());
        param.setReturnType(OrderConstant.RT_ONLY_MONEY);
        param.setReturnMoney(orderRecord.getMoneyPaid().add(orderRecord.getScoreDiscount()).add(orderRecord.getUseAccount()).add(orderRecord.getMemberCardBalance()).subtract(orderRecord.getShippingFee()));
        param.setShippingFee(orderRecord.getShippingFee());

        List<RefundParam.ReturnGoods> returnGoodsList = new ArrayList<>();
        oGoods.forEach(orderGoods->{
            RefundParam.ReturnGoods returnGoods = new RefundParam.ReturnGoods();
            returnGoods.setRecId(orderGoods.getRecId());
            returnGoods.setReturnNumber(orderGoods.getGoodsNumber());

            returnGoodsList.add(returnGoods);
        });
        param.setReturnGoods(returnGoodsList);
        returnService.execute(param);
    }

    /**
     * 订单未审核自动退款
     */
    public void autoUnAuditOrders() {
        Result<OrderInfoRecord> orders = orderInfo.getCanAutoUnAuditOrders();
        orders.forEach(orderRecord->{
            Result<OrderGoodsRecord> oGoods = orderGoods.getByOrderId(orderRecord.getOrderId());
            RefundParam param = new RefundParam();
            param.setAction((byte) OrderServiceCode.RETURN.ordinal());
            param.setIsMp(OrderConstant.IS_MP_AUTO);
            param.setOrderSn(orderRecord.getOrderSn());
            param.setOrderId(orderRecord.getOrderId());
            param.setReturnType(OrderConstant.RT_ONLY_MONEY);
            param.setReasonType(OrderConstant.RETRURN_REASON_TYPE_DOCTOR_AUDIT);
            param.setReturnSourceType(OrderConstant.RS_AUTO_AUDIT);
            param.setReasonDesc("订单24小时内医师未审核");
            param.setReturnMoney(orderRecord.getMoneyPaid().add(orderRecord.getScoreDiscount()).add(orderRecord.getUseAccount()).add(orderRecord.getMemberCardBalance()).subtract(orderRecord.getShippingFee()));
            param.setShippingFee(orderRecord.getShippingFee());
            List<RefundParam.ReturnGoods> returnGoodsList = new ArrayList<>();
            oGoods.forEach(orderGoods->{
                RefundParam.ReturnGoods returnGoods = new RefundParam.ReturnGoods();
                returnGoods.setRecId(orderGoods.getRecId());
                returnGoods.setReturnNumber(orderGoods.getGoodsNumber());
                returnGoodsList.add(returnGoods);
            });
            param.setReturnGoods(returnGoodsList);
            returnService.execute(param);
        });
    }
}
