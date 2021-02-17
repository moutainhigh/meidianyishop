package com.meidianyi.shop.service.shop.goods.mp;

import com.meidianyi.shop.db.shop.tables.records.ShareRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.share.ShareRecordParam;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Date;

import static com.meidianyi.shop.db.shop.Tables.SHARE_RECORD;

/**
 * @author 李晓冰
 * @date 2019年12月26日
 */
@Service
public class GoodsActivityShareRecordService extends ShopBaseService {
    public void addShareRecord(ShareRecordParam param){

        ShareRecordRecord shareRecordRecord = db().selectFrom(SHARE_RECORD).where(SHARE_RECORD.ACTIVITY_ID.eq(param.getActivityId())
            .and(SHARE_RECORD.USER_ID.eq(param.getUserId())).and(SHARE_RECORD.ACTIVITY_TYPE.eq(param.getActivityType()))
            .and(DSL.date(SHARE_RECORD.CREATE_TIME).eq(new Date(System.currentTimeMillis())))).fetchAny();

        if (shareRecordRecord == null) {
            shareRecordRecord = db().newRecord(SHARE_RECORD);
            shareRecordRecord.setActivityId(param.getActivityId());
            shareRecordRecord.setUserId(param.getUserId());
            shareRecordRecord.setActivityType(param.getActivityType());
            shareRecordRecord.setCount(0);
            shareRecordRecord.insert();
        } else {
            shareRecordRecord.setCount(shareRecordRecord.getCount()+1);
            shareRecordRecord.update();
        }

    }
}
