package com.meidianyi.shop.service.shop.task.doctor;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticAllMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticParam;
import com.meidianyi.shop.service.shop.doctor.DoctorStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService.TYPE_LIST_1;

/**
 * @author chenjie
 * @date 2020年09月15日
 */
@Service
public class DoctorTaskService extends ShopBaseService {
    @Autowired
    public DoctorStatisticService doctorStatisticService;
    public void insertDoctorStatistic(Integer doctorId,Integer days) {
        for (int i = 0; i <= days; i++) {
            LocalDateTime today = LocalDate.now().atStartOfDay().minusDays(i);
            TYPE_LIST_1.forEach((e) -> {
                DoctorStatisticParam param = new DoctorStatisticParam();
                param.setDoctorId(doctorId);
                param.setStartTime(Timestamp.valueOf(today.minusDays(e)));
                param.setEndTime(Timestamp.valueOf(today));
                param.setType(e);
                param.setRefDate(Date.valueOf(today.minusDays(1).toLocalDate()));
                doctorStatisticService.statisticDoctor(param);
            });
        }
    }

    public void updateDoctorStatisticScore(Byte type,Date refDate, DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        doctorStatisticService.updateDoctorStatisticScore(type,refDate,doctorStatisticMinMax);
    }

    public DoctorStatisticMinMaxVo getMinMaxByType(DoctorStatisticAllMinMaxVo doctorStatisticAllMinMaxVo, Byte type){
        switch (type) {
            case (byte)1:
                return doctorStatisticAllMinMaxVo.getOneMinMax();
            case (byte)7:
                return doctorStatisticAllMinMaxVo.getWeekMinMax();
            case (byte)30:
                return doctorStatisticAllMinMaxVo.getMonthMinMax();
            case (byte)90:
                return doctorStatisticAllMinMaxVo.getSeasonMinMax();
            default:
                return doctorStatisticAllMinMaxVo.getOneMinMax();
        }
    }
}
