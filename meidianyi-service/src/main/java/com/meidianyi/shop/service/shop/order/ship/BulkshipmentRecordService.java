package com.meidianyi.shop.service.shop.order.ship;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.ShopAccount;
import com.meidianyi.shop.db.main.tables.ShopChildAccount;
import com.meidianyi.shop.db.shop.tables.BulkshipmentRecord;
import com.meidianyi.shop.db.shop.tables.records.BulkshipmentRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipListParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipListVo;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

/**
 * table = BULKSHIPMENT_RECORD
 * @author 王帅
 */
@Service
public class BulkshipmentRecordService extends ShopBaseService {
    public final BulkshipmentRecord TABLE = BulkshipmentRecord.BULKSHIPMENT_RECORD;
    public final ShopAccount MAIN_TABLE_ACCOUNT = ShopAccount.SHOP_ACCOUNT;
    public final ShopChildAccount MAIN_TABLE_CHILD_ACCOUNT= ShopChildAccount.SHOP_CHILD_ACCOUNT;
    public BulkshipmentRecordRecord addRecord(int size, AdminTokenAuthInfo adminInfo) {
        BulkshipmentRecordRecord record = db().newRecord(TABLE);
        record.setSysId(adminInfo.getSysId());
        record.setAccountId(adminInfo.getSubAccountId());
        record.setTotalNum(size);
        record.insert();
        return record;
    }

    public PageResult<BatchShipListVo> batchShipList(BatchShipListParam param) {
        SelectJoinStep<?> select = db().select(TABLE.ID, TABLE.SYS_ID, TABLE.ACCOUNT_ID, TABLE.TOTAL_NUM, TABLE.SUCCESS_NUM, TABLE.CREATE_TIME).from(TABLE);
        if(param.getBatchId() != null) {
            select.where(TABLE.ID.eq(param.getBatchId()));
        }
        if(param.getStart() != null) {
            select.where(TABLE.CREATE_TIME.ge(param.getStart()));
        }
        if(param.getEnd() != null) {
            select.where(TABLE.CREATE_TIME.le(param.getEnd()));
        }
        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), BatchShipListVo.class);
    }
}
