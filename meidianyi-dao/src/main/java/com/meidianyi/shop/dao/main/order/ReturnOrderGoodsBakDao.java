package com.meidianyi.shop.dao.main.order;

import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import static com.meidianyi.shop.db.main.Tables.RETURN_ORDER_GOODS_BAK;


/**
 * @author 孔德成
 * @date 2020/8/20 17:11
 */
@Component
public class ReturnOrderGoodsBakDao extends MainBaseDao {

    /**
     * 	通过订单sn[]查询其下退货商品信息
     * @param arrayToSearch
     * @return  Result<Record>
     */
    public Result<Record> getByOrderSn(String... arrayToSearch) {
        Result<Record> goods = db().select(RETURN_ORDER_GOODS_BAK.asterisk()).from(RETURN_ORDER_GOODS_BAK)
                .where(RETURN_ORDER_GOODS_BAK.ORDER_SN.in(arrayToSearch))
                .orderBy(RETURN_ORDER_GOODS_BAK.ID)
                .fetch();
        return goods;
    }
}
