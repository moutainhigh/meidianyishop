package com.meidianyi.shop.dao.shop.medical;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.MedicalAdviceRecord;
import com.meidianyi.shop.service.pojo.shop.medical.FetchMedicalAdviceVo;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.tables.MedicalAdvice.MEDICAL_ADVICE;


/**
 * @author 赵晓东
 * @description
 * @create 2020-07-24 09:48
 **/

@Repository
public class MedicalAdviceDao extends ShopBaseDao {

    /**
     * 新增医嘱
     * @param fetchMedicalAdviceVo 医嘱入参
     */
    public void addHisMedicalAdvice(FetchMedicalAdviceVo fetchMedicalAdviceVo){
        MedicalAdviceRecord medicalAdviceRecord = db().newRecord(MEDICAL_ADVICE, fetchMedicalAdviceVo);
        medicalAdviceRecord.insert();
    }

    /**
     * 医嘱详情查询
     *
     * @param posCode 医嘱单号
     * @return Integer
     */
    public Integer getMedicalAdviceByCode(String posCode) {
        return db().select(MEDICAL_ADVICE.ID).from(MEDICAL_ADVICE)
            .where(MEDICAL_ADVICE.POS_CODE.eq(posCode)
                .and(MEDICAL_ADVICE.IS_DELETE.eq(DelFlag.NORMAL_VALUE))).fetchOneInto(Integer.class);
    }

    /**
     * 更新医嘱
     * @param fetchMedicalAdviceVo 处方入参
     */
    public void updateHisMedicalAdvice(FetchMedicalAdviceVo fetchMedicalAdviceVo){
        MedicalAdviceRecord medicalAdviceRecord = db().select().from(MEDICAL_ADVICE)
            .where(MEDICAL_ADVICE.POS_CODE.eq(fetchMedicalAdviceVo.getPosCode()))
            .fetchOneInto(MedicalAdviceRecord.class);
        FieldsUtil.assign(fetchMedicalAdviceVo, medicalAdviceRecord);
        medicalAdviceRecord.update();
        fetchMedicalAdviceVo.setId(medicalAdviceRecord.getId());
    }


}
