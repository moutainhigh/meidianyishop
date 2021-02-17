package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/9/14 16:55
 */
@Data
public class DoctorAttendanceVo {

    /**
     * 接诊量
     */
    private Integer receivingNumber;
    /**
     * 出勤率
     */
    private String doctorAttendanceRate;
    private Integer attendanceDay;
    private Integer dueAttendanceDay;
    /**
     * 服务费
     */
    private String serviceCharge;
    /**
     * 处方数
     */
    private Integer prescriptionNum;

}
