package com.meidianyi.shop.service.shop.order.record;

import static com.meidianyi.shop.db.shop.tables.ReturnStatusChange.RETURN_STATUS_CHANGE;

import java.util.List;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.shop.tables.ReturnStatusChange;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnStatusChangeRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.refund.OperatorRecord;

/**
 * 	订单退货/退款状态变化记录
 * @author 王帅
 *
 */
@Service
public class ReturnStatusChangeService extends ShopBaseService{
	public final ReturnStatusChange TABLE = RETURN_STATUS_CHANGE;
	
	/**
	 * 记录
	 * @param returnOrder
	 * @param isMp
	 * @param desc
	 */
	public void addRecord(ReturnOrderRecord returnOrder , Byte isMp , String desc) {
		ReturnStatusChangeRecord record = db().newRecord(TABLE);
		record.setRetId(returnOrder.getRetId());
		record.setUserId(returnOrder.getUserId());
		record.setType(isMp);
		record.setStatus(returnOrder.getRefundStatus());
		record.setOrderSn(returnOrder.getOrderSn());
		record.setDesc(desc);
		record.insert();
	}
	
	/**
	 * 	获取最后操作
	 */
	public ReturnStatusChangeRecord getLastOperator(Integer retId) {
		return db().selectFrom(TABLE).where(TABLE.RET_ID.eq(retId)).orderBy(TABLE.ID.desc()).limit(1).fetchAny();
	}
	
	/**
	 * 	退款订单操作记录
	 */
	public List<OperatorRecord> getOperatorRecord(Integer retId) {
		return db().select(TABLE.TYPE,TABLE.STATUS,TABLE.CREATE_TIME).from(TABLE).where(TABLE.RET_ID.eq(retId)).orderBy(TABLE.ID.desc()).fetchInto(OperatorRecord.class);
	}
}
