package com.meidianyi.shop.dao.shop.store;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.StoreOrderSummaryTrendDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.StoreOrderSummaryTrendRecord;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticParam;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.Tables.*;
/**
 * @author chenjie
 * @date 2020年08月27日
 */
@Repository
public class StoreOrderSummaryTrendDao extends ShopBaseDao {
    /**
     * 添加记录
     *
     * @param param
     * @return
     */
    public void insertStoreStatistic(StoreOrderSummaryTrendDo param) {
        StoreOrderSummaryTrendRecord record = db().newRecord(STORE_ORDER_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.insert();
    }

    /**
     * 更新记录
     *
     * @param param
     * @return
     */
    public void updateStoreStatistic(StoreOrderSummaryTrendDo param) {
        StoreOrderSummaryTrendRecord record = db().newRecord(STORE_ORDER_SUMMARY_TREND);
        FieldsUtil.assign(param, record);
        record.update();
    }

    /**
     * 查询记录
     *
     * @param param
     * @return
     */
    public StoreOrderSummaryTrendDo getStoreStatistic(StatisticParam param) {
        return db().selectFrom(STORE_ORDER_SUMMARY_TREND)
            .where(STORE_ORDER_SUMMARY_TREND.STORE_ID.eq(param.getStoreId()))
            .and(STORE_ORDER_SUMMARY_TREND.TYPE.eq(param.getType()))
            .and(STORE_ORDER_SUMMARY_TREND.REF_DATE.eq(param.getRefDate()))
            .fetchAnyInto(StoreOrderSummaryTrendDo.class);
    }
}
