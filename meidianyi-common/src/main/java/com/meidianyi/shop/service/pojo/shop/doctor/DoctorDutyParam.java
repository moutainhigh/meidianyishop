package com.meidianyi.shop.service.pojo.shop.doctor;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author chenjie
 * @date 2020年08月11日
 */
@Data
public class DoctorDutyParam {
    private Integer doctorId;
    private Byte onDutyStatus;
    private Timestamp onDutyTime;
    private Byte type=1;
}
