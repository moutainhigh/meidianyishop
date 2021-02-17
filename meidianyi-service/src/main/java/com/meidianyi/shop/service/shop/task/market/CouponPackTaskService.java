package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.db.shop.tables.records.VirtualOrderRecord;
import com.meidianyi.shop.service.shop.market.couponpack.CouponPackService;
import com.meidianyi.shop.service.shop.order.virtual.CouponPackOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-03-04 18:36
 **/
@Service
public class CouponPackTaskService {
    @Autowired
    private CouponPackOrderService couponPackOrderService;
    @Autowired
    private CouponPackService couponPackService;

    /**
     *
     */
    public void monitorCouponPackOrders(){
        List<VirtualOrderRecord> orderRecordList = couponPackOrderService.getCanGrantCouponOrderList();
        couponPackService.sendCouponPack(orderRecordList);
    }
}
