package com.meidianyi.shop.dao.shop.rebate;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsPlatformRebateDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsPlatformRebateRecord;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS_PLATFORM_REBATE;

/**
 * @author yangpengcheng
 * @date 2020/9/27
 **/
@Repository
public class OrderGoodsPlatformRebateDao extends ShopBaseDao {
    /**
     * 保存
     * @param orderGoodsPlatformRebateDo
     */
    public void save(OrderGoodsPlatformRebateDo orderGoodsPlatformRebateDo){
        OrderGoodsPlatformRebateRecord record=db().newRecord(ORDER_GOODS_PLATFORM_REBATE);
        FieldsUtil.assign(orderGoodsPlatformRebateDo,record);
        record.insert();
    }

    /**
     * 更新
     * @param orderGoodsPlatformRebateDo
     */
    public void update(OrderGoodsPlatformRebateDo orderGoodsPlatformRebateDo){
        OrderGoodsPlatformRebateRecord record=db().newRecord(ORDER_GOODS_PLATFORM_REBATE);
        FieldsUtil.assign(orderGoodsPlatformRebateDo,record);
        db().executeUpdate(record);
    }
    public OrderGoodsPlatformRebateDo getByRecId(Integer recId){
        return db().selectFrom(ORDER_GOODS_PLATFORM_REBATE).where(ORDER_GOODS_PLATFORM_REBATE.REC_ID.eq(recId)).fetchAnyInto(OrderGoodsPlatformRebateDo.class);
    }
}
