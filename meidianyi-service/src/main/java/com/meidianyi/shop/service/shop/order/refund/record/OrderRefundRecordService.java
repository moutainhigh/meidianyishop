package com.meidianyi.shop.service.shop.order.refund.record;

import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.OrderRefundRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderRefundRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.OrderRefundRecord.ORDER_REFUND_RECORD;

/**
 * 	非系统金额退款记录表
 * @author 王帅
 *
 */
@Service
public class OrderRefundRecordService extends ShopBaseService{

	public final OrderRefundRecord TABLE = ORDER_REFUND_RECORD;
    public static final Byte SUCCESS = 1;
    public static final Byte FAIL = 2;
    /**
     * 非系统金额退款记录
     * @param refundSn 退款流水号
     * @param refundResult 退款结果（失败==null）
     * @param retId 退订单号
     */
	public void addRecord(String refundSn, PaymentRecordRecord payRecord, WxPayRefundResult refundResult, Integer retId, BigDecimal money, String errorDesc) {
        OrderRefundRecordRecord record = db().newRecord(TABLE);
        record.setRefundSn(refundSn);
        record.setPaySn(payRecord.getPaySn());
        record.setOrderSn(payRecord.getOrderSn());
        record.setPayCode(payRecord.getPayCode());
        record.setRefundTime(DateUtils.getSqlTimestamp());
        record.setRetId(retId);
        record.setTransSn(payRecord.getTradeNo());
        record.setRefundAmount(money);
        if(refundResult != null) {
            //成功
            record.setDealStatus(SUCCESS);
            record.setDealStatusName("非系统金额退款成功");
            record.setDealRemark(refundResult.toString());
        }else {
            //失败
            record.setDealStatus(FAIL);
            record.setDealStatusName("非系统金额退款失败");
            record.setRemark1(errorDesc);
        }
        record.insert();
    }

    /**
     * 判断该退款订单是否存在非系统金额退款失败情况
     * @param retId
     * @return
     */
    public boolean isExistFail(Integer retId){
        if(retId == null) {
            return false;
        }
        OrderRefundRecordRecord count = db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId).and(TABLE.DEAL_STATUS.eq(FAIL))).fetchAny();
        if(count != null) {
            return true;
        }
        return false;
	}

    /**
     * 判断该退款订单是否存退款成功情况
     * @param retId
     * @return
     */
    public boolean isExistSucess(Integer retId){
        OrderRefundRecordRecord count = db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId).and(TABLE.DEAL_STATUS.eq(SUCCESS))).fetchAny();
        if(count != null) {
            return true;
        }
        return false;
    }

    /**
     * 判断该次退款成功记录
     * @param retId
     * @param paySn
     * @param money
     */
    public OrderRefundRecordRecord getReturnRecord(Integer retId, String paySn, BigDecimal money) {
        return db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId).and(TABLE.PAY_SN.eq(paySn)).and(TABLE.REFUND_AMOUNT.eq(money))).fetchAny();
    }

    /**
     * 更新退款订单状态
     * @param returnRecord
     * @param status
     */
    public void updateStatus(OrderRefundRecordRecord returnRecord, Byte status, String desc) {
        if(returnRecord == null) {
            return;
        }
        db().update(TABLE).set(TABLE.DEAL_STATUS, status).set(TABLE.REMARK1, desc).where(TABLE.ID.eq(returnRecord.getId())).execute();
    }

    /**
     * 查询该订单是否存在正在处理中的退款订单（且微信退款失败）
     * @param orderSn
     * @return
     */
    public Integer getFailReturnOrder(String orderSn) {
        OrderRefundRecordRecord record = db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchAny();
        if(record == null) {
            return null;
        }
        return record.getRetId();
    }

    /**
     * 查询该订单失败数据
     * @param retId
     * @return
     */
    public List<OrderRefundRecordRecord> getSuccessRecord(Integer retId) {
        return db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId).and(TABLE.DEAL_STATUS.eq(SUCCESS))).fetch();
    }

    /**
     * 查询该订单失败数据
     * @param retId
     * @return
     */
    public OrderRefundRecordRecord getFailRecord(Integer retId) {
        return db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId).and(TABLE.DEAL_STATUS.eq(FAIL))).fetchAny();
    }
}
