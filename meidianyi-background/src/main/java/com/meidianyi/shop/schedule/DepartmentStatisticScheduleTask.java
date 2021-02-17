package com.meidianyi.shop.schedule;

import com.meidianyi.shop.service.pojo.saas.shop.ShopListInfoVo;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticAllMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBasicVo;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.shop.ShopApplication;
import com.meidianyi.shop.service.shop.department.DepartmentStatisticService;
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
 * @date 2020年08月27日
 */
@Component
@ConditionalOnProperty(prefix = "schedule", name = "switch", havingValue = "on")
public class DepartmentStatisticScheduleTask {
    @Autowired
    private SaasApplication saas;

    public DepartmentStatisticService departmentStatisticService;
    /**
     * 门店订单统计
     * b2c_store_order_summary_trend 商品概况信息表
     * 每天凌晨零点过后8秒开始统计前一天的数据
     */
    @Scheduled(cron = "8 0 0 * * ?")
    public void departmentStatistics() {
        List<ShopListInfoVo> result = saas.shopService.getShopListInfo();
        result.forEach((r) -> {
            ShopApplication shop = saas.getShopApp(r.getShopId());
            List<DepartmentOneParam> allStore = shop.departmentService.getAllDepartment();
            allStore.forEach((d)->{
                shop.departmentTaskService.insertDepartmentStatistic(d.getId());
            });
            LocalDateTime today = LocalDate.now().atStartOfDay();
            Date refDate = Date.valueOf(today.minusDays(1).toLocalDate());
            DoctorStatisticAllMinMaxVo doctorStatisticAllMinMaxVo = new DoctorStatisticAllMinMaxVo();
            doctorStatisticAllMinMaxVo.setOneMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 1));
            doctorStatisticAllMinMaxVo.setWeekMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 7));
            doctorStatisticAllMinMaxVo.setMonthMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 30));
            doctorStatisticAllMinMaxVo.setSeasonMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 90));
            TYPE_LIST_1.forEach((t)->{
                DoctorStatisticMinMaxVo doctorStatisticMinMax = shop.doctorTaskService.getMinMaxByType(doctorStatisticAllMinMaxVo,t);
                shop.departmentTaskService.updateDepartmentStatisticScore(t,refDate,doctorStatisticMinMax);
            });
        });
    }
}
