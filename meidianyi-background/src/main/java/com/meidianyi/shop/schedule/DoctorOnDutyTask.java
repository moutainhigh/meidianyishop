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
 * @author chenjie
 * @date 2020年08月12日
 */
@Component
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(prefix="schedule",name = "switch", havingValue = "on")
public class DoctorOnDutyTask {
    @Autowired
    private SaasApplication saas;
    /**
     * 过期的处方
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void onDutyDoctor(){
        Result<ShopRecord> shops = saas.shop.getAll();
        shops.forEach(shopRecord -> {
            saas.getShopApp(shopRecord.getShopId()).doctorService.onDutyDoctorTask();
        });
    }
}
