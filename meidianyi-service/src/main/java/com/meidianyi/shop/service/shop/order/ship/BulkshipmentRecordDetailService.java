package com.meidianyi.shop.service.shop.order.ship;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.db.shop.tables.BulkshipmentRecordDetail;
import com.meidianyi.shop.db.shop.tables.records.BulkshipmentRecordDetailRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipFailModel;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipPojo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * table = BULKSHIPMENT_RECORD_DETAIL
 * @author 王帅
 */
@Service
public class BulkshipmentRecordDetailService extends ShopBaseService {
    public final BulkshipmentRecordDetail TABLE = BulkshipmentRecordDetail.BULKSHIPMENT_RECORD_DETAIL;
    public final byte sucess = 0;
    public final byte fail = 1;
    public BulkshipmentRecordDetailRecord createRecord(BatchShipPojo shipPojo, Integer batchId) {
        BulkshipmentRecordDetailRecord record = db().newRecord(TABLE);
        record.setBatchId(batchId);
        record.setStatus(sucess);
        record.setOrderSn(shipPojo.getOrderSn());
        record.setShippingName(shipPojo.getExpress());
        record.setShippingNo(shipPojo.getTrackingNo());
        if(StringUtils.isBlank(shipPojo.getOrderSn())) {
            record.setStatus(fail);
            record.setFailReason(JsonResultMessage.MSG_ORDER_ORDER_SN_NOT_NULL);
        } else if(StringUtils.isBlank(shipPojo.getExpress())) {
            record.setStatus(fail);
            record.setFailReason(JsonResultMessage.MSG_ORDER_SHIPPING_SHIPPINGID_NOT_NULL);
        } else if(StringUtils.isBlank(shipPojo.getTrackingNo())) {
            record.setStatus(fail);
            record.setFailReason(JsonResultMessage.MSG_ORDER_SHIPPING_SHIPPINGNO_NOT_NULL);
        }
        return record;
    }

    public List<BatchShipFailModel> getFailDataByBatchId(Integer batchId) {
        return db().select(TABLE.ORDER_SN, TABLE.SHIPPING_NAME, TABLE.SHIPPING_NO, TABLE.FAIL_REASON).from(TABLE).where(TABLE.BATCH_ID.eq(batchId).and(TABLE.STATUS.eq(fail))).fetchInto(BatchShipFailModel.class);
    }
}
