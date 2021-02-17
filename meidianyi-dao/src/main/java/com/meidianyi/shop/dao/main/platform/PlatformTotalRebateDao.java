package com.meidianyi.shop.dao.main.platform;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.main.table.PlatformTotalRebateDo;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.db.main.tables.records.PlatformTotalRebateRecord;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.main.Tables.PLATFORM_TOTAL_REBATE;

/**
 * @author yangpengcheng
 * @date 2020/9/27
 **/
@Repository
public class PlatformTotalRebateDao extends MainBaseDao {

    /**
     * 新增
     * @param platformTotalRebateDo
     */
    public void savePlatFormTotalRebate(PlatformTotalRebateDo platformTotalRebateDo){
        PlatformTotalRebateRecord record=db().selectFrom(PLATFORM_TOTAL_REBATE).where(PLATFORM_TOTAL_REBATE.SHOP_ID.eq(platformTotalRebateDo.getShopId())).fetchOne();
        if(record==null){
            PlatformTotalRebateRecord recordInsert=db().newRecord(PLATFORM_TOTAL_REBATE);
            FieldsUtil.assign(platformTotalRebateDo,recordInsert);
            recordInsert.insert();
        }else {
            record.setTotalMoney(BigDecimalUtil.add(record.getTotalMoney(),platformTotalRebateDo.getTotalMoney()));
            record.setFinalMoney(BigDecimalUtil.add(record.getFinalMoney(),platformTotalRebateDo.getFinalMoney()));
            record.update();
        }

    }

}
