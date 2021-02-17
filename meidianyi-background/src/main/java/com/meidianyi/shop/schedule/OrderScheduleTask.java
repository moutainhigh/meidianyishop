package com.meidianyi.shop.schedule;

import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.saas.SaasApplication;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单处理定时任务
 *
 * @author wangbingbing
 */
@Component
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(prefix="schedule",name = "switch", havingValue = "on")
public class OrderScheduleTask {
    @Autowired
    private SaasApplication saas;
    @Autowired
    private JedisManager jedisManager;


    /**
     * 拼团订单处理（自动成团、退款等）
     * 每一分钟执行一次
     */
    /**      *

    public void monitorGroupBuyOrders() {
     *Result<ShopRecord> result = saas.shop.getAll();
     *result.forEach((r) -> {
     *String uuid = Util.randomId();
     *String key = JedisKeyConstant.TASK_JOB_LOCK_ORDER_GROUP_BUY + r.getShopId();
     *saas.getShopApp(r.getShopId()).groupIntegration.updateState();
     *             //订单处理时间可能较长，加锁防止重入
     *if (jedisManager.addLock(key, uuid, 1000 * 90)) {
     *saas.getShopApp(r.getShopId()).shopTaskService.groupBuyTaskService.monitorOrder();
     *
     *saas.getShopApp(r.getShopId()).groupDraw.groupDrawUser.dealOpenGroupDraw();
     *jedisManager.releaseLock(key, uuid);
     *}
     *});
     *}
      */


    /**
     * 预售订单自动关闭
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorPreSaleOrders() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{
                saas.getShopApp(r.getShopId()).shopTaskService.preSaleTaskService.monitorOrder();
        });
    }

    /**
     * 订单关闭
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void close(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.close();
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.serviceOrderClose();
        });
    }

    /**
     * 24小时未审核
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void unAudit(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.unAudit();
        });
    }

    /**
     * 收货
     * 每天00：05执行
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void receive(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.receive();
        });
    }

    /**
     * 完成订单
     * 每十分钟一次
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void finish(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.finish();
        });
    }

    /**
     * 订单未支付通知
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void expiringNoPayOrderNotify(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.expiringNoPayOrderNotify();
        });
    }

    /**
     * 自动处理退款退货
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCloseReturnOrder(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.orderTaskService.autoReturnOrder();
        });
    }

    /**
     * 奖品 定时过期
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closePrizeGoods(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).shopTaskService.prizeTaskService.closePrizeGoods();
        });
    }

    /**
     * 优惠券礼包订单自动发券
     * 每一天执行一次
     */
    @Scheduled(cron = "0 01 01 * * ?")
    public void monitorCouponPackOrders() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r) -> {
            saas.getShopApp(r.getShopId()).shopTaskService.couponPackTaskService.monitorCouponPackOrders();
        });
    }


}
