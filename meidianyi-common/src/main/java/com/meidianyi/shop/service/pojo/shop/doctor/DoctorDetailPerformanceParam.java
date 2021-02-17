package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author yangpengcheng
 * @date 2020/9/23
 **/
@Data
public class DoctorDetailPerformanceParam {
    private Integer doctorId;
    private Timestamp startTime;
    private Timestamp endTime;
}
