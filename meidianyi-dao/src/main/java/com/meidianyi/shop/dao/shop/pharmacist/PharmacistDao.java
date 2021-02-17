package com.meidianyi.shop.dao.shop.pharmacist;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.PharmacistDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.PharmacistRecord;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.tables.Pharmacist.PHARMACIST;

/**
 * @author yangpengcheng
 * @date 2020/9/16
 **/
@Repository
public class PharmacistDao extends ShopBaseDao {

    public  int savePharmacist(PharmacistDo pharmacistDo){
        PharmacistRecord record=db().newRecord(PHARMACIST);
        FieldsUtil.assign(pharmacistDo,record);
        record.insert();
        pharmacistDo.setId(record.getId());
        return record.getId();
    }

}
