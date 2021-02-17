package com.meidianyi.shop.dao.shop.order;

import com.meidianyi.shop.common.pojo.shop.table.OrderMedicalHistoryDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.OrderMedicalHistoryRecord;
import com.meidianyi.shop.service.pojo.wxapp.order.medical.OrderMedicalHistoryBo;
import org.springframework.stereotype.Repository;


import static com.meidianyi.shop.db.shop.Tables.ORDER_MEDICAL_HISTORY;

/**
 * 订单历史诊断
 * @author 孔德成
 * @date 2020/7/28 14:43
 */
@Repository
public class OrderMedicalHistoryDao extends ShopBaseDao {

    public OrderMedicalHistoryDo getByOrderId(Integer orderId){
        return db().select().from(ORDER_MEDICAL_HISTORY).where(ORDER_MEDICAL_HISTORY.ORDER_ID.eq(orderId)).fetchOneInto(OrderMedicalHistoryDo.class);
    }

    /**
     * 保存
     * @param patientDiagnose
     * @return
     */
    public int save(OrderMedicalHistoryBo patientDiagnose) {
        OrderMedicalHistoryRecord orderMedicalHistoryRecord = db().newRecord(ORDER_MEDICAL_HISTORY, patientDiagnose);
       return orderMedicalHistoryRecord.insert();
    }
}
