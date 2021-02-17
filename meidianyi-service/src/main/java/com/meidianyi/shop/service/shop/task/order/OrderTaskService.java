package com.meidianyi.shop.service.shop.task.order;

import com.meidianyi.shop.db.shop.tables.records.ServiceOrderRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.store.ReservationDetail;
import com.meidianyi.shop.service.shop.order.action.AuditService;
import com.meidianyi.shop.service.shop.order.action.CloseService;
import com.meidianyi.shop.service.shop.order.action.FinishService;
import com.meidianyi.shop.service.shop.order.action.PayService;
import com.meidianyi.shop.service.shop.order.action.ReceiveService;
import com.meidianyi.shop.service.shop.order.action.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王帅
 * 订单定时任务
 */
@Service
public class OrderTaskService extends ShopBaseService {

    @Autowired
    private CloseService close;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ReceiveService receive;

    @Autowired
    private FinishService finish;

    @Autowired
    private PayService pay;

    @Autowired
    private ReturnService refund;

    /**
     * 订单自动关闭
     */
    public void close(){
        logger().info("订单关闭定时任务start,shop:{}", getShopId());
        close.autoCloseOrders();
        //TODO 再写一个定时任务  后台定时关闭超时未领取商品
        logger().info("订单关闭定时任务end");
    }

    public void unAudit(){
        logger().info("订单退款未审核订单start,shop:{}", getShopId());
        auditService.autoUnAuditOrders();
        logger().info("订单退款未审核订单定时任务end");
    }

    /**
     * 收货
     */
    public void receive(){
        logger().info("订单自动收货定时任务start,shop:{}", getShopId());
        receive.autoReceiveOrders();
        //TODO 换货自动收货
        //autoReceiveOrders
        logger().info("订单自动收货定时任务end");
    }

    /**
     * 自动完成订单
     */
    public void finish(){
        logger().info("订单自动完成定时任务start,shop:{}", getShopId());
        finish.autoFinishOrders();
        logger().info("订单自动完成定时任务end");
    }

    /**
     * 订单未支付通知
     */
    public void expiringNoPayOrderNotify(){
        logger().info("订单未支付通知定时任务start,shop:{}", getShopId());
        pay.autoExpiringNoPayOrderNotify();
        logger().info("订单未支付通知定时任务end");
    }

    /**
     * 退货退款
     */
    public void autoReturnOrder(){
        logger().info("退货退款定时任务start,shop:{}", getShopId());
        refund.autoReturnOrder();
        logger().info("退货退款定时任务end");
    }

    /**
     * 门店服务预约订单自动关闭
     */
    public void serviceOrderClose(){

        List<ServiceOrderRecord> orders = saas.getShopApp(getShopId()).store.reservation.getExpiredUnpaidOrders();
        ReservationDetail param = new ReservationDetail();
        param.setCancelReason("定时任务自动取消");
        logger().info("门店服务订单自动关闭定时任务start,shop:{},orderIds:{}", getShopId(),orders);
        orders.forEach(order->{
            param.setOrderId(order.getOrderId());
            param.setOrderSn(order.getOrderSn());
            saas.getShopApp(getShopId()).store.reservation.cancelWaitToPayReservation(param);
        });
    }
}
