package com.meidianyi.shop.dao.main.order;

import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import static com.meidianyi.shop.db.main.Tables.ORDER_GOODS_BAK;


/**
 * @author 孔德成
 * @date 2020/8/24 10:40
 */
@Component
public class OrderGoodsBakDao extends MainBaseDao {


    /**
     * 	通过订单id[]查询其下商品
     * @param arrayToSearch
     * @return  Result<?>
     */
    public Result<?> getByOrderIds(Integer... arrayToSearch) {
        Result<?> goods = db().select(ORDER_GOODS_BAK.ORDER_ID,ORDER_GOODS_BAK.REC_ID,ORDER_GOODS_BAK.ORDER_SN,ORDER_GOODS_BAK.GOODS_ID,ORDER_GOODS_BAK.GOODS_NAME,ORDER_GOODS_BAK.GOODS_SN,ORDER_GOODS_BAK.GOODS_NUMBER,ORDER_GOODS_BAK.GOODS_PRICE,ORDER_GOODS_BAK.MARKET_PRICE,ORDER_GOODS_BAK.GOODS_ATTR,ORDER_GOODS_BAK.PRODUCT_SN,ORDER_GOODS_BAK.PRODUCT_ID,ORDER_GOODS_BAK.GOODS_IMG,ORDER_GOODS_BAK.MAIN_REC_ID,ORDER_GOODS_BAK.STRA_ID,ORDER_GOODS_BAK.PER_DISCOUNT,ORDER_GOODS_BAK.GOODS_SCORE,ORDER_GOODS_BAK.IS_GIFT,ORDER_GOODS_BAK.GIFT_ID,ORDER_GOODS_BAK.DISCOUNTED_GOODS_PRICE,ORDER_GOODS_BAK.ACTIVITY_TYPE,ORDER_GOODS_BAK.IS_CARD_EXCLUSIVE).from(ORDER_GOODS_BAK)
                .where(ORDER_GOODS_BAK.ORDER_ID.in(arrayToSearch))
                .orderBy(ORDER_GOODS_BAK.ORDER_ID.desc())
                .fetch();
        return goods;
    }
}
