package com.meidianyi.shop.schedule;

import com.meidianyi.shop.service.pojo.saas.shop.ShopListInfoVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticAllMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.shop.ShopApplication;
import com.meidianyi.shop.service.shop.doctor.DoctorStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService.TYPE_LIST_1;

/**
 * @author chenjie
 * @date 2020年09月15日
 */
@Component
@ConditionalOnProperty(prefix = "schedule", name = "switch", havingValue = "on")
public class DoctorStatisticScheduleTask {
    @Autowired
    private SaasApplication saas;

    @Autowired
    public DoctorStatisticService doctorStatisticService;
    /**
     * 医师业绩统计
     * b2c_doctor_summary_trend医师业绩统计表
     * 每天凌晨零点过后8秒开始统计前一天的数据
     */
    @Scheduled(cron = "8 0 0 * * ?")
    public void doctorStatistics() {
        List<ShopListInfoVo> result = saas.shopService.getShopListInfo();
        result.forEach((r) -> {
            ShopApplication shop = saas.getShopApp(r.getShopId());
            List<DoctorOneParam> allDoctors = shop.doctorService.getAllDoctor(0);
            allDoctors.forEach((d)->{
                shop.doctorTaskService.insertDoctorStatistic(d.getId(),0);
            });
            if (allDoctors.size() > 0) {
                LocalDateTime today = LocalDate.now().atStartOfDay();
                Date refDate = Date.valueOf(today.minusDays(1).toLocalDate());
                DoctorStatisticAllMinMaxVo doctorStatisticAllMinMaxVo = new DoctorStatisticAllMinMaxVo();
                doctorStatisticAllMinMaxVo.setOneMinMax(doctorStatisticService.getMinMaxStatisticData(refDate,(byte) 1));
                doctorStatisticAllMinMaxVo.setWeekMinMax(doctorStatisticService.getMinMaxStatisticData(refDate,(byte) 7));
                doctorStatisticAllMinMaxVo.setMonthMinMax(doctorStatisticService.getMinMaxStatisticData(refDate,(byte) 30));
                doctorStatisticAllMinMaxVo.setSeasonMinMax(doctorStatisticService.getMinMaxStatisticData(refDate,(byte) 90));
                TYPE_LIST_1.forEach((t)->{
                    DoctorStatisticMinMaxVo doctorStatisticMinMax = shop.doctorTaskService.getMinMaxByType(doctorStatisticAllMinMaxVo,t);
                    shop.doctorTaskService.updateDoctorStatisticScore(t,refDate,doctorStatisticMinMax);
                });
            }
        });
    }
}
