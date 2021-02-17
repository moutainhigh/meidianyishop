package com.meidianyi.shop.dao.shop.rebate;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorTotalRebateRecord;
import com.meidianyi.shop.service.pojo.shop.rebate.DoctorTotalRebateVo;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR_TOTAL_REBATE;

/**
 * @author yangpengcheng
 * @date 2020/8/25
 **/
@Repository
public class DoctorTotalRebateDao extends ShopBaseDao {

    /**
     * 更新
     * @param doctorId
     * @param money
     */
    public void updateDoctorTotalRebate(Integer doctorId, BigDecimal money){
        DoctorTotalRebateRecord record=db().selectFrom(DOCTOR_TOTAL_REBATE).where(DOCTOR_TOTAL_REBATE.DOCTOR_ID.eq(doctorId)).fetchOne();
        if(record==null){
            DoctorTotalRebateRecord recordInsert=db().newRecord(DOCTOR_TOTAL_REBATE);
            recordInsert.setDoctorId(doctorId);
            recordInsert.setTotalMoney(money);
            recordInsert.setFinalMoney(money);
            recordInsert.insert();
        }else {
            record.setTotalMoney(BigDecimalUtil.add(record.getTotalMoney(),money));
            record.setFinalMoney(BigDecimalUtil.add(record.getFinalMoney(),money));
            record.setUpdateTime(DateUtils.getLocalDateTime());
            record.update();
        }
    }

    /**
     * 根据医师id查询
     * @param doctorId
     * @return
     */
    public DoctorTotalRebateVo getRebateByDoctorId(Integer doctorId){
        DoctorTotalRebateVo doctorTotalRebateVo=db().select().from(DOCTOR_TOTAL_REBATE).where(DOCTOR_TOTAL_REBATE.DOCTOR_ID.eq(doctorId))
            .fetchAnyInto(DoctorTotalRebateVo.class);
        return doctorTotalRebateVo;
    }

    /**
     * 更新总金额和冻结金额
     * @param doctorId
     * @param totalMoney
     * @param blockMoney
     */
    public void updateTotalMoneyBlockedMoney(Integer doctorId,BigDecimal totalMoney,BigDecimal blockMoney){
        db().update(DOCTOR_TOTAL_REBATE).set(DOCTOR_TOTAL_REBATE.TOTAL_MONEY,totalMoney)
            .set(DOCTOR_TOTAL_REBATE.BLOCKED_MONEY,blockMoney)
            .where(DOCTOR_TOTAL_REBATE.DOCTOR_ID.eq(doctorId)).execute();

    }

    /**
     * 更新冻结金额
     * @param doctorId
     * @param blockMoney
     */
    public void updateBlockMoney(Integer doctorId,BigDecimal blockMoney){
        db().update(DOCTOR_TOTAL_REBATE).set(DOCTOR_TOTAL_REBATE.BLOCKED_MONEY,blockMoney)
            .where(DOCTOR_TOTAL_REBATE.DOCTOR_ID.eq(doctorId)).execute();
    }
    /**
     * 更新总金额
     * @param doctorId
     * @param totalMoney
     */
    public void updateTotalMoney(Integer doctorId,BigDecimal totalMoney){
        db().update(DOCTOR_TOTAL_REBATE).set(DOCTOR_TOTAL_REBATE.TOTAL_MONEY,totalMoney)
            .where(DOCTOR_TOTAL_REBATE.DOCTOR_ID.eq(doctorId)).execute();
    }
}
