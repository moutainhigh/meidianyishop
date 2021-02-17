package com.meidianyi.shop.dao.shop.order;

import com.meidianyi.shop.common.pojo.shop.table.ReturnOrderGoodsDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.RETURN_ORDER_GOODS;

/**
 * @author 孔德成
 * @date 2020/8/21 9:03
 */
@Component
public class ReturnOrderGoodsDao extends ShopBaseDao {

    public List<ReturnOrderGoodsDo> listCreateOrderGoodsByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(RETURN_ORDER_GOODS)
                .where(RETURN_ORDER_GOODS.CREATE_TIME.ge(beginTime)).and(RETURN_ORDER_GOODS.CREATE_TIME.le(endTime))
                .fetchInto(ReturnOrderGoodsDo.class);
    }


    public List<ReturnOrderGoodsDo> listUpdateOrderGoodsByYesterday(Timestamp beginTime, Timestamp endTime) {
        return  db().selectFrom(RETURN_ORDER_GOODS)
                .where(RETURN_ORDER_GOODS.UPDATE_TIME.ge(beginTime)).and(RETURN_ORDER_GOODS.UPDATE_TIME.le(endTime))
                .and(RETURN_ORDER_GOODS.CREATE_TIME.le(beginTime))
                .fetchInto(ReturnOrderGoodsDo.class);
    }

    public Map<String, List<ReturnOrderGoodsDo>> listReturnOrderGoods(List<Integer> returnOrderIds) {
        return db().selectFrom(RETURN_ORDER_GOODS)
                .where(RETURN_ORDER_GOODS.RET_ID.in(returnOrderIds))
                .fetchGroups(RETURN_ORDER_GOODS.ORDER_SN,ReturnOrderGoodsDo.class);

    }
}
