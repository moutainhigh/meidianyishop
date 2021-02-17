package com.meidianyi.shop.dao.shop.doctor;

import com.meidianyi.shop.common.pojo.shop.table.DoctorDutyRecordDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorDutyRecordRecord;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR_DUTY_RECORD;

/**
 * @author chenjie
 * @date 2020年08月12日
 */
@Repository
public class DoctorDutyRecordDao  extends ShopBaseDao {
    /**
     * 新增医师上下班记录
     * @param
     * @return
     */
    public int insertDoctorDutyRecord(DoctorDutyRecordDo doctorDutyRecordDo){
        DoctorDutyRecordRecord record=db().newRecord(DOCTOR_DUTY_RECORD,doctorDutyRecordDo);
        record.insert();
        doctorDutyRecordDo.setId(record.getId());
        return record.getId();
    }
}
