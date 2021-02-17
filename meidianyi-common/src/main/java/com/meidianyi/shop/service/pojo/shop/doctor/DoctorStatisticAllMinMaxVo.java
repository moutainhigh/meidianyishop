package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Data
public class DoctorStatisticAllMinMaxVo {
    private DoctorStatisticMinMaxVo oneMinMax;
    private DoctorStatisticMinMaxVo weekMinMax;
    private DoctorStatisticMinMaxVo monthMinMax;
    private DoctorStatisticMinMaxVo seasonMinMax;
}
