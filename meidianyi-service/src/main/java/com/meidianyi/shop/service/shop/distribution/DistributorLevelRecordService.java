package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.db.shop.tables.DistributorLevelRecord;
import com.meidianyi.shop.db.shop.tables.records.DistributorLevelRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.UpdateUserLevel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 分销等级变换记录
 * @author 王帅
 */
@Service
public class DistributorLevelRecordService extends ShopBaseService {

    DistributorLevelRecord table = DistributorLevelRecord.DISTRIBUTOR_LEVEL_RECORD;

    public void add(Collection<UpdateUserLevel> params) {
        ArrayList<DistributorLevelRecordRecord> records = new ArrayList<>(params.size());
        for (UpdateUserLevel param: params) {
            DistributorLevelRecordRecord record = db().newRecord(table, param);
            records.add(record);
        }
        db().batchInsert(records).execute();

    }
}
