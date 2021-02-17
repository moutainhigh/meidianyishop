package com.meidianyi.shop.schedule;

import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.saas.SaasApplication;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 商品相关监控定时任务
 *
 * @author wangbingbing
 */
@Component
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(prefix="schedule",name = "switch", havingValue = "on")
public class GoodsScheduleTask {

    @Autowired
    private  SaasApplication saas;


	/**
     * 监控秒杀，更新商品类型
	 * 每一分钟执行一次
	 */
	@Scheduled(cron = "0 */1 * * * ?")
    public void monitorSeckillGoods() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{saas.getShopApp(r.getShopId()).
            shopTaskService.seckillTaskService.monitorGoodsType();});
    }

    /**
     * 监控限时降价，更新商品类型
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorReducePriceGoods() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{saas.getShopApp(r.getShopId()).
            shopTaskService.reducePriceTaskService.monitorGoodsType();});
    }

    /**
     * 监控拼团，更新商品类型
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorGroupBuyGoods() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{saas.getShopApp(r.getShopId()).
            shopTaskService.groupBuyTaskService.monitorGoodsType();});
    }

    /**
     * 1.监控砍价，更新商品类型
     * 2.关闭失效的砍价发起记录
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorBargainGoods() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{
            saas.getShopApp(r.getShopId()).shopTaskService.bargainTaskService.monitorGoodsType();
            saas.getShopApp(r.getShopId()).shopTaskService.bargainTaskService.closeBargainRecord();
            saas.getShopApp(r.getShopId()).shopTaskService.bargainTaskService.sendBargainProgress();
        });
    }

    /**
     * 监控预售，更新商品类型
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void monitorPreSaleGoods() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{saas.getShopApp(r.getShopId()).
            shopTaskService.preSaleTaskService.monitorGoodsType();});
    }

    /**
     * 删除用户足迹
     * 每三个月执行一次
     */
    @Scheduled(cron = "0 0 0 1 */3 ?")
    public void deleteFootprint() {
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)->{saas.getShopApp(r.getShopId()).
            shopTaskService.footprintDeleteTaskService.deleteFootprint();});
    }

    /**
     * 商品自动上架
     * 每一分钟执行一次
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoOnSaleGoods(){
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)-> saas.getShopApp(r.getShopId()).goods.onSaleGoods());
    }

    /**
     * 更新商品PV字段
     * 每天执行一次
     */
    @Scheduled(cron = "0 15 2 * * ? ")
    public void updateGoodsPv(){
        Result<ShopRecord> result = saas.shop.getAll();
        result.forEach((r)-> saas.getShopApp(r.getShopId()).
            shopTaskService.goodsPvUpdateTaskService.updateGoodsPv());
    }

}
