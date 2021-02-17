package com.meidianyi.shop.service.shop.order.must;

import com.meidianyi.shop.db.shop.tables.OrderMust;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.must.OrderMustVo;
import com.meidianyi.shop.service.pojo.wxapp.order.must.OrderMustParam;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.tables.OrderMust.ORDER_MUST;

/**
 * @author: 王兵兵
 * @create: 2019-10-17 17:04
 * 下单必填信息
 **/
@Service
public class OrderMustService extends ShopBaseService {

    public final OrderMust TABLE = ORDER_MUST;
    /**
     * 按订单号查询下单必填信息
     * @param orderSn
     * @return PageResult
     */
    public OrderMustVo getOrderMustByOrderSn(String orderSn){
        Record record = db().select(TABLE.asterisk()).from(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchOne();
        if(record != null){
            return record.into(OrderMustVo.class);
        }else{
            return null;
        }
    }

    /**
     * 记录入库
     * @param must 订单必填信息
     */
    public void addRecord(OrderMustParam must){
        if(must == null) {
            return;
        }
        db().newRecord(TABLE, must).store();
    }

    /**
     * 按订单号查询下单必填信息
     * @param orderSns
     * @return PageResult
     */
    public List<OrderMustVo> getOrderMustByOrderSns(List<String> orderSns){
        if(CollectionUtils.isEmpty(orderSns)) {
            return null;
        }
        return db().select(TABLE.asterisk()).from(TABLE).where(TABLE.ORDER_SN.in(orderSns)).fetchInto(OrderMustVo.class);
    }
}
