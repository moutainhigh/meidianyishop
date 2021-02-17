package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.FanliGoodsStatistics;
import com.meidianyi.shop.db.shop.tables.records.FanliGoodsStatisticsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 商品返利统计表
 * @author 王帅
 */
@Service
public class FanliGoodsStatisticsService extends ShopBaseService {
    private static final FanliGoodsStatistics TABLE = FanliGoodsStatistics.FANLI_GOODS_STATISTICS;

    /**
     * 删除record，并不入库
     * @param realRebateMoney
     * @param goods
     * @param rebateNumber
     * @return
     */
    public FanliGoodsStatisticsRecord createRecord(BigDecimal realRebateMoney, OrderGoodsRecord goods, Integer rebateNumber) {
        FanliGoodsStatisticsRecord record = db().newRecord(TABLE);
        record.setPrdId(goods.getProductId());
        record.setPrdSn(goods.getProductSn());
        record.setGoodsId(goods.getGoodsId());
        record.setSaleNumber(rebateNumber);
        record.setPrdTotalFanli(realRebateMoney);
        //TODO 无需要
        record.setCatId(0);
        return record;
    }
    public void batchUpdate(ArrayList<FanliGoodsStatisticsRecord> statisticsRecords) {
        if(CollectionUtils.isEmpty(statisticsRecords)) {
            return;
        }
        for (FanliGoodsStatisticsRecord record : statisticsRecords) {
            FanliGoodsStatisticsRecord byPrdId = getByPrdId(record.getPrdId());
            if(byPrdId == null) {
                record.insert();
            }else {
                byPrdId.setSaleNumber(record.getSaleNumber() + byPrdId.getSaleNumber());
                byPrdId.setPrdTotalFanli(BigDecimalUtil.add(record.getPrdTotalFanli(), byPrdId.getPrdTotalFanli()));
                byPrdId.update();
            }
        }
    }
    private FanliGoodsStatisticsRecord getByPrdId(Integer prdId) {
        return db().selectFrom(TABLE).where(TABLE.PRD_ID.eq(prdId)).fetchOne();
    }
}
