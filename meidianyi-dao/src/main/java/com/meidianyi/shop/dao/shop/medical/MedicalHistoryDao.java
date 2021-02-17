package com.meidianyi.shop.dao.shop.medical;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.MedicalHistoryRecord;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.*;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.tables.MedicalHistory.MEDICAL_HISTORY;


/**
 * @author 赵晓东
 * @description
 * @create 2020-07-07 10:40
 */
@Repository
public class MedicalHistoryDao extends ShopBaseDao {

    /**
     * 病历详情分页查询
     *
     * @param medicalHistoryListParam 病历详情入参
     * @return PageResult<MedicalHistoryListVo>
     */
    public MedicalHistoryListVo getMedicalHistoryDetail(MedicalHistoryListParam medicalHistoryListParam) {
        return db().select().from(MEDICAL_HISTORY)
            .where(MEDICAL_HISTORY.ID.eq(medicalHistoryListParam.getId())
                .and(MEDICAL_HISTORY.IS_DELETE.eq(DelFlag.NORMAL_VALUE))).fetchOneInto(MedicalHistoryListVo.class);
    }

    /**
     * 病历表列表查询
     *
     * @param medicalHistoryPageInfoParam 病历表列表入参
     * @return PageResult<MedicalHistoryPageInfoVo>
     */
    public PageResult<MedicalHistoryPageInfoVo> getMedicalHistoryPageInfo(MedicalHistoryPageInfoParam medicalHistoryPageInfoParam) {
        SelectConditionStep<Record> and = db().select().from(MEDICAL_HISTORY)
            .where(MEDICAL_HISTORY.PATIENT_ID.eq(medicalHistoryPageInfoParam.getPatientId())
                .and(MEDICAL_HISTORY.IS_DELETE.eq(DelFlag.NORMAL_VALUE)));
        return getPageResult(and, medicalHistoryPageInfoParam, MedicalHistoryPageInfoVo.class);
    }

    /**
     * 新增病历
     * @param fetchMedicalHistoryVo 病历入参
     */
    public void addHisMedicalHistory(FetchMedicalHistoryVo fetchMedicalHistoryVo){
        MedicalHistoryRecord medicalHistoryRecord = db().newRecord(MEDICAL_HISTORY, fetchMedicalHistoryVo);
        medicalHistoryRecord.insert();
    }

    /**
     * 更新处方
     * @param fetchMedicalHistoryVo 处方入参
     */
    public void updateHisMedicalHistory(FetchMedicalHistoryVo fetchMedicalHistoryVo){
        MedicalHistoryRecord medicalHistoryRecord = db().select().from(MEDICAL_HISTORY)
            .where(MEDICAL_HISTORY.ID.eq(fetchMedicalHistoryVo.getId()))
            .fetchOneInto(MedicalHistoryRecord.class);
        FieldsUtil.assign(fetchMedicalHistoryVo, medicalHistoryRecord);
        medicalHistoryRecord.update();
        fetchMedicalHistoryVo.setId(medicalHistoryRecord.getId());
    }

    /**
     * 病历详情查询
     *
     * @param posCode 医嘱编码
     * @return Integer
     */
    public Integer getMedicalHistoryDetailByCode(String posCode) {
        return db().select(MEDICAL_HISTORY.ID).from(MEDICAL_HISTORY)
            .where(MEDICAL_HISTORY.POS_CODE.eq(posCode)
                .and(MEDICAL_HISTORY.IS_DELETE.eq(DelFlag.NORMAL_VALUE))).fetchOneInto(Integer.class);
    }
}
