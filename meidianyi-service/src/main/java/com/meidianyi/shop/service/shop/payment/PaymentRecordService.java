package com.meidianyi.shop.service.shop.payment;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.image.ImageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentRecordParam;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentRecordVo;
import org.jooq.SelectWhereStep;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.tables.PaymentRecord.PAYMENT_RECORD;

/**
 * @author lixinguo
 */
@Service
public class PaymentRecordService extends ShopBaseService {

	/**
	 * 添加PaymentRecord记录
	 * @param param
	 * @return
	 */
	public PaymentRecordRecord addPaymentRecord(PaymentRecordParam param) {
		param.setPaySn(IncrSequenceUtil.generateOrderSn(OrderConstant.PAY_SN_PREFIX));
		PaymentRecordRecord record = db().newRecord(PAYMENT_RECORD,param);
		record.insert();
        record.refresh();
		return record;
	}

	/**
	 * 通过支付单号得到支付记录
	 * @param paySn
	 * @return
	 */
	public PaymentRecordRecord getPaymentRecordByPaySn(String paySn) {
		return db().fetchAny(PAYMENT_RECORD,PAYMENT_RECORD.PAY_SN.eq(paySn));
	}

	/**
	 * 通过商家单号得到支付记录
	 * @param orderSn
	 * @return
	 */
	public PaymentRecordRecord getPaymentRecordByOrderSn(String orderSn) {
		return db().fetchAny(PAYMENT_RECORD,PAYMENT_RECORD.ORDER_SN.eq(orderSn));
	}

	/**
	 * 通过交易单号得到支付记录
	 * @param tradeNo
	 * @return
	 */
	public PaymentRecordRecord getPaymentRecordByTradeNo(String tradeNo) {
		return db().fetchAny(PAYMENT_RECORD,PAYMENT_RECORD.TRADE_NO.eq(tradeNo));
	}

    /**
     * 得到分页记录
     * @param param
     * @return
     */
    public PageResult<PaymentRecordVo> getPageList(ImageListQueryParam param) {
        SelectWhereStep<PaymentRecordRecord> select = db().selectFrom(PAYMENT_RECORD);
        select.orderBy(PAYMENT_RECORD.ID.desc());
        return this.getPageResult(select, param.getPage(),param.getPageRows(), PaymentRecordVo.class);
    }
}
