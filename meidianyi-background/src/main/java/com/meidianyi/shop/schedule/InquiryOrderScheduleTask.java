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
 * @author yangpengcheng
 * @date 2020/7/23
 **/
@Component
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(prefix="schedule",name = "switch", havingValue = "on")
public class InquiryOrderScheduleTask {
    @Autowired
    private SaasApplication saas;

    /**
     * 关闭超时待接诊订单
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeToWaitingInquiryOrder(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach((shop)->{
            saas.getShopApp(shop.getShopId()).inquiryOrderTaskService.closeToWaitingInquiryOrder();
            saas.getShopApp(shop.getShopId()).inquiryOrderTaskService.finishedCloseOrder();
            saas.getShopApp(shop.getShopId()).inquiryOrderTaskService.close();
            // 咨询会话关闭可继续问诊订单
            saas.getShopApp(shop.getShopId()).imSessionService.timingDeadReadyToContinueSession();
        });
    }
}
